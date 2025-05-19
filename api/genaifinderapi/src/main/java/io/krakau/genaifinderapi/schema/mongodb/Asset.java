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
    
    private Integer distance;
    
    public Asset() {
    }

    public Asset(Metadata metadata, Long nnsId) {
        this.metadata = metadata;
        this.nnsId = nnsId;
    }

    public String getId() {
        return id;
    }
    
    public Metadata getMetadata() {
        return metadata;
    }

    public Long getNnsId() {
        return nnsId;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public void setNnsId(Long nnsId) {
        this.nnsId = nnsId;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "Asset{" + "id=" + id + ", metadata=" + metadata + ", nnsId=" + nnsId + ", distance=" + distance + '}';
    }
    
}
