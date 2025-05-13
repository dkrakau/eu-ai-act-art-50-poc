package io.krakau.genaifinderapi.schema.mongodb;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 *
 * @author Dominik
 */
@Document
public class Metadata {
    
    @Field("provider")
    private Provider provider;
    
    @Field("iscc")
    private ISCC iscc;
    
}
