package io.krakau.genaifinderapi.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 *
 * @author Dominik
 */
@Component
public class EnviromentVariables {
    
    // ### SECURITY ###
    public String SECURITY_LOCALHOST;
    public String SECURITY_DOMAIN;
    public String SECURITY_IP;
    
    // ### OPENAPI ###
    public String OPENAPI_PAHT;
    public String OPENAPI_SERVER_URL;
    public String OPENAPI_SERVER_DESCRIPTION;
    public String OPENAPI_CONTRACT_EMAIL;
    public String OPENAPI_CONTRACT_NAME;
    public String OPENAPI_CONTRACT_URL;
    public String OPENAPI_LICENCE_NAME;
    public String OPENAPI_LICENCE_IDENTIFIER;
    public String OPENAPI_LICENCE_URL;
    public String OPENAPI_INFO_TITLE;
    public String OPENAPI_INFO_SUMMARY;
    public String OPENAPI_INFO_VERSION;
    public String OPENAPI_INFO_TOS;
    public String OPENAPI_INFO_DESCRIPTION;
    
    // ### MONGODB ###
    public String MONGODB_HOST;
    public String MONGODB_PORT;
    public String MONGODB_AUTH_DB;
    public String MONGODB_AUTH_USERNAME;
    public String MONGODB_AUTH_PASSWORD;
    public String MONGODB_DATABASE;
    
    // ### MILVUS ###
    public String MILVUS_URL;
    public String MILVUS_AUTH_USERNAME;
    public String MILVUS_AUTH_PASSWORD;
    public String MILVUS_DISTANCE;
    public String MILVUS_TOP_K;
    public String MILVUS_NLIST;
    public String MILVUS_NPROBE;
    public String MILVUS_DATABASE;
    public String MILVUS_COLLECTION_DESCRIPTION;
    public String MILVUS_COLLECTION_UNIT_META;
    public String MILVUS_COLLECTION_UNIT_CONTENT;
    public String MILVUS_COLLECTION_UNIT_DATA;
    public String MILVUS_COLLECTION_UNIT_INSTANCE;
    public String MILVUS_COLLECITON_FIELD_ID;
    public String MILVUS_COLLECTION_FIELD_VECTOR;
    public String MILVUS_COLLECTION_FIELD_NNSID;
    public String MILVUS_PARTITION_AUDIO;
    public String MILVUS_PARTITION_IMAGE;
    public String MILVUS_PARTITION_TEXT;
    public String MILVUS_PARTITION_VIDEO;
   
    // ### Cryptographer ###
    public String CRYPTOGRAPHER_OPENAI;
    public String CRYPTOGRAPHER_OPENAI_KEY_PRIVATE;
    public String CRYPTOGRAPHER_OPENAI_KEY_PUBLIC;
    public String CRYPTOGRAPHER_LEONARDOAI;
    public String CRYPTOGRAPHER_LEONARDOAI_KEY_PRIVATE;
    public String CRYPTOGRAPHER_LEONARDOAI_KEY_PUBLIC;
    
    // ### APIs ###
    public String API_ISCCWEB_CREATE;
    public String API_ISCCWEB_EXPLAIN;
    
    @Autowired
    public EnviromentVariables(Environment env) {
        
        // ### SECURITY ###
        this.SECURITY_LOCALHOST = env.getProperty("spring.security.allowed.localhost");
        this.SECURITY_DOMAIN = env.getProperty("spring.security.allowed.domain");
        this.SECURITY_IP = env.getProperty("spring.security.allowed.ip");
        
        // ### OPENAPI ###
        this.OPENAPI_PAHT = env.getProperty("springdoc.api-docs.path");
        this.OPENAPI_SERVER_URL = env.getProperty("springdoc.api-docs.server.url");
        this.OPENAPI_SERVER_DESCRIPTION = env.getProperty("springdoc.api-docs.server.description");
        this.OPENAPI_CONTRACT_NAME = env.getProperty("springdoc.api-docs.contact.name");
        this.OPENAPI_CONTRACT_EMAIL = env.getProperty("springdoc.api-docs.contact.email");
        this.OPENAPI_CONTRACT_URL = env.getProperty("springdoc.api-docs.contact.url");
        this.OPENAPI_LICENCE_NAME = env.getProperty("springdoc.api-docs.licence.name");
        this.OPENAPI_LICENCE_IDENTIFIER = env.getProperty("springdoc.api-docs.licence.identifier");
        this.OPENAPI_LICENCE_URL = env.getProperty("springdoc.api-docs.licence.url");
        this.OPENAPI_INFO_TITLE = env.getProperty("springdoc.api-docs.info.title");
        this.OPENAPI_INFO_DESCRIPTION = env.getProperty("springdoc.api-docs.info.description");
        this.OPENAPI_INFO_SUMMARY = env.getProperty("springdoc.api-docs.info.summary");
        this.OPENAPI_INFO_VERSION = env.getProperty("springdoc.api-docs.info.vision");
        this.OPENAPI_INFO_TOS = env.getProperty("springdoc.api-docs.info.terms-of-service");
        
        
        // ### MONGODB ###
        this.MONGODB_HOST = env.getProperty("spring.data.mongodb.host");
        this.MONGODB_PORT = env.getProperty("spring.data.mongodb.port");
        this.MONGODB_AUTH_DB = env.getProperty("spring.data.mongodb.authentication-database");
        this.MONGODB_AUTH_USERNAME = env.getProperty("spring.data.mongodb.username");
        this.MONGODB_AUTH_PASSWORD = env.getProperty("spring.data.mongodb.password");
        this.MONGODB_DATABASE = env.getProperty("spring.data.mongodb.database");
        
        
        // ### MILVUS ###
        this.MILVUS_URL = env.getProperty("spring.data.milvus.uri");
        this.MILVUS_AUTH_USERNAME = env.getProperty("spring.data.milvus.auth.username");
        this.MILVUS_AUTH_PASSWORD = env.getProperty("spring.data.milvus.auth.password");
        this.MILVUS_DISTANCE = env.getProperty("spring.data.milvus.distance");
        this.MILVUS_TOP_K = env.getProperty("spring.data.milvus.topK");
        this.MILVUS_NLIST = env.getProperty("spring.data.milvus.nlist");
        this.MILVUS_NPROBE = env.getProperty("spring.data.milvus.nprobe");
        this.MILVUS_DATABASE = env.getProperty("spring.data.milvus.database");
        this.MILVUS_COLLECTION_DESCRIPTION = env.getProperty("spring.data.milvus.collection.description");
        this.MILVUS_COLLECTION_UNIT_META = env.getProperty("spring.data.milvus.collection.name.units.meta");
        this.MILVUS_COLLECTION_UNIT_CONTENT = env.getProperty("spring.data.milvus.collection.name.units.content");
        this.MILVUS_COLLECTION_UNIT_DATA = env.getProperty("spring.data.milvus.collection.name.units.data");
        this.MILVUS_COLLECTION_UNIT_INSTANCE = env.getProperty("spring.data.milvus.collection.name.units.instance");
        this.MILVUS_COLLECITON_FIELD_ID = env.getProperty("spring.data.milvus.collection.field.id");
        this.MILVUS_COLLECTION_FIELD_VECTOR = env.getProperty("spring.data.milvus.collection.field.vector");
        this.MILVUS_COLLECTION_FIELD_NNSID = env.getProperty("spring.data.milvus.collection.field.nnsId");
        this.MILVUS_PARTITION_AUDIO = env.getProperty("spring.data.milvus.partition.name.audio");
        this.MILVUS_PARTITION_IMAGE = env.getProperty("spring.data.milvus.partition.name.image");
        this.MILVUS_PARTITION_TEXT = env.getProperty("spring.data.milvus.partition.name.text");
        this.MILVUS_PARTITION_VIDEO = env.getProperty("spring.data.milvus.partition.name.video");
        
        
        // ### Cryptographer ###
        this.CRYPTOGRAPHER_OPENAI = env.getProperty("spring.data.cryptographer.proivider.openai");
        this.CRYPTOGRAPHER_OPENAI_KEY_PRIVATE = env.getProperty("spring.data.cryptographer.proivider.openai.key.private");
        this.CRYPTOGRAPHER_OPENAI_KEY_PUBLIC = env.getProperty("spring.data.cryptographer.proivider.openai.key.public");
        this.CRYPTOGRAPHER_LEONARDOAI = env.getProperty("spring.data.cryptographer.proivider.leonardoai");
        this.CRYPTOGRAPHER_LEONARDOAI_KEY_PRIVATE = env.getProperty("spring.data.cryptographer.proivider.leonardoai.key.private");
        this.CRYPTOGRAPHER_LEONARDOAI_KEY_PUBLIC = env.getProperty("spring.data.cryptographer.proivider.leonardoai.key.public");
        
        
        // ### APIs ###
        this.API_ISCCWEB_CREATE = env.getProperty("spring.api.iscc-web-create");
        this.API_ISCCWEB_EXPLAIN = env.getProperty("spring.api.iscc-web-explain");
    }
    
}
