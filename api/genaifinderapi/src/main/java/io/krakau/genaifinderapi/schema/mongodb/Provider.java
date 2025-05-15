package io.krakau.genaifinderapi.schema.mongodb;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 *
 * @author Dominik
 */
@Document
public class Provider {
    
    @Field("name")
    private String name;
    
    @Field("prompt")
    private String prompt;
    
    @Field("timestamp")
    private Long timestamp;
    
    @Field("credentials")
    private Credentials credentials;
    
    public Provider() {
    }

    public Provider(String name, String prompt, Long timestamp, Credentials credentials) {
        this.name = name;
        this.prompt = prompt;
        this.timestamp = timestamp;
        this.credentials = credentials;
    }

    public String getName() {
        return name;
    }

    public String getPrompt() {
        return prompt;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    @Override
    public String toString() {
        return "Provider{" + "name=" + name + ", prompt=" + prompt + ", timestamp=" + timestamp + ", credentials=" + credentials + '}';
    }
    
}
