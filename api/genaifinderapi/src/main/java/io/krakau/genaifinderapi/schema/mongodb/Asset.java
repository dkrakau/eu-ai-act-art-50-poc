package io.krakau.genaifinderapi.schema.mongodb;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

/**
 *
 * @author Dominik
 */
@Document
public class Asset {
    
    @MongoId
    @Field(targetType = FieldType.OBJECT_ID)
    private String id;
    
    @Field("metadata")
    private Metadata metadata;
    
    @Field("nnsId")
    private Long nnsId;
    
    private Float distance;
    
}
