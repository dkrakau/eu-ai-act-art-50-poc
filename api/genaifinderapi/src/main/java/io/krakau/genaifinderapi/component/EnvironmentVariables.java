package io.krakau.genaifinderapi.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author Dominik
 */
@Component
public class EnvironmentVariables {
    
    // ### OPENAPI ###
    @Value("${springdoc.api-docs.path}")
    public String OPENAPI_PAHT;
    @Value("${springdoc.api-docs.server.url}")
    public String OPENAPI_SERVER_URL;
    @Value("${springdoc.api-docs.server.description}")
    public String OPENAPI_SERVER_DESCRIPTION;
    @Value("${springdoc.api-docs.contact.name}")
    public String OPENAPI_CONTRACT_NAME;
    @Value("${springdoc.api-docs.contact.email}")
    public String OPENAPI_CONTRACT_EMAIL;
    @Value("${springdoc.api-docs.contact.url}")
    public String OPENAPI_CONTRACT_URL;
    @Value("${springdoc.api-docs.licence.name}")
    public String OPENAPI_LICENCE_NAME;
    @Value("${springdoc.api-docs.licence.identifier}")
    public String OPENAPI_LICENCE_IDENTIFIER;
    @Value("${springdoc.api-docs.licence.url}")
    public String OPENAPI_LICENCE_URL;
    @Value("${springdoc.api-docs.info.title}")
    public String OPENAPI_INFO_TITLE;
    @Value("${springdoc.api-docs.info.description}")
    public String OPENAPI_INFO_DESCRIPTION;
    @Value("${springdoc.api-docs.info.summary}")
    public String OPENAPI_INFO_SUMMARY;
    @Value("${springdoc.api-docs.info.vision}")
    public String OPENAPI_INFO_VERSION;
    @Value("${springdoc.api-docs.info.terms-of-service}")
    public String OPENAPI_INFO_TOS;
    
    
    // ### APPLICATION ###
    @Value("${spring.storage.dir}")
    public String RESOURCE_DIR;
    @Value("${spring.storage.dir.images}")
    public String RESOURCE_IMAGE_DIR;
    
    
    // ### SECURITY ###
    @Value("${spring.security.allowed.localhost}")
    public String SECURITY_LOCALHOST;
    @Value("${spring.security.allowed.ip}")
    public String SECURITY_IP;
    
    
    // ### MONGODB ###
    @Value("${spring.data.mongodb.host}")
    public String MONGODB_HOST;
    @Value("${spring.data.mongodb.port}")
    public String MONGODB_PORT;
    @Value("${spring.data.mongodb.authentication-database}")
    public String MONGODB_AUTH_DB;
    @Value("${spring.data.mongodb.username}")
    public String MONGODB_AUTH_USERNAME;
    @Value("${spring.data.mongodb.password}")
    public String MONGODB_AUTH_PASSWORD;
    @Value("${spring.data.mongodb.database}")
    public String MONGODB_DATABASE;
    
    
    // ### MILVUS ###
    @Value("${spring.data.milvus.uri}")
    public String MILVUS_URL;
    @Value("${spring.data.milvus.auth.username}")
    public String MILVUS_AUTH_USERNAME;
    @Value("${spring.data.milvus.auth.password}")
    public String MILVUS_AUTH_PASSWORD;
    @Value("${spring.data.milvus.distance}")
    public String MILVUS_DISTANCE;
    @Value("${spring.data.milvus.topK}")
    public String MILVUS_TOP_K;
    @Value("${spring.data.milvus.nlist}")
    public String MILVUS_NLIST;
    @Value("${spring.data.milvus.nprobe}")
    public String MILVUS_NPROBE;
    @Value("${spring.data.milvus.database}")
    public String MILVUS_DATABASE;
    @Value("${spring.data.milvus.collection.description}")
    public String MILVUS_COLLECTION_DESCRIPTION;
    @Value("${spring.data.milvus.collection.name.units.meta}")
    public String MILVUS_COLLECTION_UNIT_META;
    @Value("${spring.data.milvus.collection.name.units.content}")
    public String MILVUS_COLLECTION_UNIT_CONTENT;
    @Value("${spring.data.milvus.collection.name.units.data}")
    public String MILVUS_COLLECTION_UNIT_DATA;
    @Value("${spring.data.milvus.collection.name.units.instance}")
    public String MILVUS_COLLECTION_UNIT_INSTANCE;
    @Value("${spring.data.milvus.collection.field.id}")
    public String MILVUS_COLLECITON_FIELD_ID;
    @Value("${spring.data.milvus.collection.field.vector}")
    public String MILVUS_COLLECTION_FIELD_VECTOR;
    @Value("${spring.data.milvus.collection.field.nnsId}")
    public String MILVUS_COLLECTION_FIELD_NNSID;
    @Value("${spring.data.milvus.partition.name.audio}")
    public String MILVUS_PARTITION_AUDIO;
    @Value("${spring.data.milvus.partition.name.image}")
    public String MILVUS_PARTITION_IMAGE;
    @Value("${spring.data.milvus.partition.name.text}")
    public String MILVUS_PARTITION_TEXT;
    @Value("${spring.data.milvus.partition.name.video}")
    public String MILVUS_PARTITION_VIDEO;
    
   
    // ### Cryptographer ###
    @Value("${spring.data.cryptographer.proivider.openai}")
    public String CRYPTOGRAPHER_OPENAI;
    @Value("${spring.data.cryptographer.proivider.openai.key.private}")
    public String CRYPTOGRAPHER_OPENAI_KEY_PRIVATE;
    @Value("${spring.data.cryptographer.proivider.openai.key.public}")
    public String CRYPTOGRAPHER_OPENAI_KEY_PUBLIC;
    @Value("${spring.data.cryptographer.proivider.leonardoai}")
    public String CRYPTOGRAPHER_LEONARDOAI;
    @Value("${spring.data.cryptographer.proivider.leonardoai.key.private}")
    public String CRYPTOGRAPHER_LEONARDOAI_KEY_PRIVATE;
    @Value("${spring.data.cryptographer.proivider.leonardoai.key.public}")
    public String CRYPTOGRAPHER_LEONARDOAI_KEY_PUBLIC;
    
    // ### APIs ###
    @Value("${spring.api.iscc-web-create}")
    public String API_ISCCWEB_CREATE;
    @Value("${spring.api.iscc-web-explain}")
    public String API_ISCCWEB_EXPLAIN;
    
}
