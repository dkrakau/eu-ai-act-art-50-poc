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
    
}
