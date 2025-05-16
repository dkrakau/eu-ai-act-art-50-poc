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
    private IsccData iscc;
    
    public Metadata() {
    }

    public Metadata(Provider provider, IsccData iscc) {
        this.provider = provider;
        this.iscc = iscc;
    }

    public Provider getProvider() {
        return provider;
    }

    public IsccData getIscc() {
        return iscc;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public void setIscc(IsccData iscc) {
        this.iscc = iscc;
    }

    @Override
    public String toString() {
        return "Metadata{" + "provider=" + provider + ", iscc=" + iscc + '}';
    }
    
}
