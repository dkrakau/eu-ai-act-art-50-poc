package io.krakau.genaifinderapi.schema.mongodb;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 *
 * @author Dominik
 */
@Document
public class Credentials {
    
    @Field("message")
    private String message;
    
    @Field("encryptedMessage")
    private String encryptedMessage;
    
    @Field("publicKey")
    private String publicKey;
    
    public Credentials(String message, String encryptedMessage, String publicKey) {
        this.message = message;
        this.encryptedMessage = encryptedMessage;
        this.publicKey = publicKey;
    }

    @Override
    public String toString() {
        return "Credentials{" + "message=" + message + ", encryptedMessage=" + encryptedMessage + ", publicKey=" + publicKey + '}';
    }
    
}
