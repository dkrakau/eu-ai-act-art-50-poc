package io.krakau.genaifinderapi.component;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Dominik
 */
@Component
public class Cryptographer {

    private KeyPairGenerator keyPairGenerator;
    private Cipher cipher;
    private HashMap<String, KeyPair> keyPairs;

    @Autowired
    public Cryptographer() throws NoSuchAlgorithmException, NoSuchPaddingException {
        this.keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        this.keyPairGenerator.initialize(512);
        this.cipher = Cipher.getInstance("RSA/ECB/NoPadding");
    }

    public void loadKeyPair(String provider, File privateKeyFile, File publicKeyFile) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {

        String privateKeyBytes = new String(Files.readAllBytes(privateKeyFile.toPath()), Charset.defaultCharset());
        String privateKeyPEM = privateKeyBytes
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END PRIVATE KEY-----", "");
        // load private key
        PKCS8EncodedKeySpec specPrivateKey = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKeyPEM));        
        
        String publicKeyBytes = new String(Files.readAllBytes(privateKeyFile.toPath()), Charset.defaultCharset());
        String publicKeyPEM = publicKeyBytes
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END PUBLIC KEY-----", "");
        // load public key
        X509EncodedKeySpec specPublicKey = new X509EncodedKeySpec(Base64.decodeBase64(publicKeyPEM));

        // extract private and public key
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = kf.generatePrivate(specPrivateKey);
        PublicKey publicKey = kf.generatePublic(specPublicKey);

        // save keypair in mapping
        this.keyPairs.put(provider, new KeyPair(publicKey, privateKey));
    }

    public String encrypt(String provider, String message) throws IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException {

        KeyPair keyPair = this.keyPairs.get(provider);

        byte[] byteMessage = message.getBytes();
        Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPrivate());
        cipher.update(byteMessage);
        byte[] ciphertext = cipher.doFinal();

        return new String(ciphertext, "UTF8");
    }

    public String decrypt(String provider, String encryptedMessage) throws IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException {

        KeyPair keyPair = this.keyPairs.get(provider);

        byte[] byteEncryptedMessage = encryptedMessage.getBytes();
        cipher.init(Cipher.DECRYPT_MODE, keyPair.getPublic());
        cipher.update(byteEncryptedMessage);
        byte[] decrypted = cipher.doFinal();

        return new String(decrypted, "UTF8");
    }

}
