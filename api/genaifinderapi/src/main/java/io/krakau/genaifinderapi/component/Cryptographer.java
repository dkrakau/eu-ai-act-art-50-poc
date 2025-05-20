package io.krakau.genaifinderapi.component;

import io.krakau.genaifinderapi.schema.mongodb.Credentials;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

/**
 *
 * @author Dominik
 */
@Component
public class Cryptographer {

    private KeyPairGenerator keyPairGenerator;
    private Cipher cipher;

    private HashMap<String, PrivateKey> privateKeys;
    private HashMap<String, PublicKey> publicKeys;
    
    private ResourceLoader resourceLoader;

    @Autowired
    public Cryptographer(EnviromentVariables env, ResourceLoader resourceLoader) throws NoSuchAlgorithmException, NoSuchPaddingException {
        
        this.privateKeys = new HashMap<String, PrivateKey>();
        this.publicKeys = new HashMap<String, PublicKey>();
        
        this.resourceLoader = resourceLoader;

        this.keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        this.keyPairGenerator.initialize(2048);
        this.cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

        try {
            this.loadKeyPair(
                    env.CRYPTOGRAPHER_OPENAI,
                    this.resourceLoader.getResource(env.CRYPTOGRAPHER_OPENAI_KEY_PRIVATE).getFile(),
                    this.resourceLoader.getResource(env.CRYPTOGRAPHER_OPENAI_KEY_PUBLIC).getFile()
            );
            this.loadKeyPair(
                    env.CRYPTOGRAPHER_LEONARDOAI,
                    this.resourceLoader.getResource(env.CRYPTOGRAPHER_LEONARDOAI_KEY_PRIVATE).getFile(),
                    this.resourceLoader.getResource(env.CRYPTOGRAPHER_LEONARDOAI_KEY_PUBLIC).getFile()
            );
        } catch (IOException ex) {
            Logger.getLogger(Cryptographer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeySpecException ex) {
            Logger.getLogger(Cryptographer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void loadKeyPair(String provider, File privateKeyFile, File publicKeyFile) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {

        String privateKeyBytes = new String(Files.readAllBytes(privateKeyFile.toPath()), Charset.defaultCharset());
        String privateKeyPEM = privateKeyBytes
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END PRIVATE KEY-----", "");
        // load private key
        PKCS8EncodedKeySpec specPrivateKey = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyPEM));

        String publicKeyBytes = new String(Files.readAllBytes(publicKeyFile.toPath()), Charset.defaultCharset());
        String publicKeyPEM = publicKeyBytes
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END PUBLIC KEY-----", "");
        // load public key
        X509EncodedKeySpec specPublicKey = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyPEM));

        // extract private and public key
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = kf.generatePrivate(specPrivateKey);
        PublicKey publicKey = kf.generatePublic(specPublicKey);

        // save keys in mapping
        this.privateKeys.put(provider, privateKey);
        this.publicKeys.put(provider, publicKey);
    }

    private String encrypt(String provider, String message) throws IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException {

        PrivateKey privateKey = this.privateKeys.get(provider);

        byte[] byteMessage = message.getBytes();
        this.cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        this.cipher.update(byteMessage);
        byte[] ciphertext = this.cipher.doFinal();

        return Base64.getEncoder().encodeToString(ciphertext);
    }

    private String decrypt(String provider, String encryptedMessage) throws IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException {

        PublicKey publicKey = this.publicKeys.get(provider);

        byte[] byteEncryptedMessage = Base64.getDecoder().decode(encryptedMessage);
        this.cipher.init(Cipher.DECRYPT_MODE, publicKey);
        this.cipher.update(byteEncryptedMessage);
        byte[] decrypted = this.cipher.doFinal();

        return new String(decrypted, "UTF8");
    }

    public Credentials getCredentials(String provider, String message) throws IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException {
        PublicKey publicKey = this.publicKeys.get(provider);
        String encryptedMessage = encrypt(provider, message);
        return new Credentials(message, encryptedMessage, Base64.getEncoder().encodeToString(publicKey.getEncoded()));
    }

}
