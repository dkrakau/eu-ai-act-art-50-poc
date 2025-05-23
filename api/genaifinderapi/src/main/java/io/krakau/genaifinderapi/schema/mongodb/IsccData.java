package io.krakau.genaifinderapi.schema.mongodb;

import io.krakau.genaifinderapi.schema.iscc.ExplainedISCC;
import io.krakau.genaifinderapi.schema.iscc.ISCC;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 *
 * @author Dominik
 */
@Document
public class IsccData {
    
    @Field("data")
    private ISCC data;
    
    @Field("explained")
    private ExplainedISCC explained;
    
    public IsccData() {
    }

    public IsccData(ISCC data, ExplainedISCC explained) {
        this.data = data;
        this.explained = explained;
    }

    public ISCC getData() {
        return data;
    }

    public ExplainedISCC getExplained() {
        return explained;
    }

    public void setData(ISCC data) {
        this.data = data;
    }

    public void setExplained(ExplainedISCC explained) {
        this.explained = explained;
    }    
    
}