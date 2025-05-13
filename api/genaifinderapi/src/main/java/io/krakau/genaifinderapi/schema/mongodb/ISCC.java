package io.krakau.genaifinderapi.schema.mongodb;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 *
 * @author Dominik
 */
@Document
public class ISCC {
    
    @Field("iscc")
    private org.bson.Document data;
    
    @Field("explained")
    private org.bson.Document explained; 
    
}
