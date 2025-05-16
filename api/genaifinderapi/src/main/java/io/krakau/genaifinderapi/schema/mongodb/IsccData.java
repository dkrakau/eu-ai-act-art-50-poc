package io.krakau.genaifinderapi.schema.mongodb;

import io.krakau.genaifinderapi.schema.iscc.ExplainedISCC;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 *
 * @author Dominik
 */
@Document
public class IsccData {
    
    @Field("data")
    private org.bson.Document data;
    
    @Field("explained")
    private ExplainedISCC explained;
    
    public IsccData() {
    }

    public IsccData(org.bson.Document data, ExplainedISCC explained) {
        this.data = data;
        this.explained = explained;
    }

    public org.bson.Document getData() {
        return data;
    }

    public ExplainedISCC getExplained() {
        return explained;
    }

    public void setData(org.bson.Document data) {
        this.data = data;
    }

    public void setExplained(ExplainedISCC explained) {
        this.explained = explained;
    }    
    
}
