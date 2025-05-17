package io.krakau.genaifinderdbmanager.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 *
 * @author Dominik
 */
public class EnvironmentVariables {

    // Mongodb
    public final String MONGODB_URL;
    public final String MONGODB_HOST;
    public final int MONGODB_PORT;
    public final String MONGODB_AUTH_DB;
    public final String MONGODB_AUTH_USERNAME;
    public final String MONGODB_AUTH_PASSWORD;
    public final String MONGODB_DATABASE;
    public final String MONGODB_COLLECTION;

    // Milvus
    public final String MILVUS_URL;
    public final int MILVUS_PORT;
    public final String MILVUS_DATABASE;
    public final String MILVUS_COLLECTION_UNIT_META;
    public final String MILVUS_COLLECTION_UNIT_CONTENT;
    public final String MILVUS_COLLECTION_UNIT_DATA;
    public final String MILVUS_COLLECTION_UNIT_INSTANCE;
    public final String MILVUS_COLLECTION_DESCRIPTION;
    public final String MILVUS_PARTITION_TEXT;
    public final String MILVUS_PARTITION_IMAGE;
    public final String MILVUS_PARTITION_AUDIO;
    public final String MILVUS_PARTITION_VIDEO;
    public final int MILVUS_SHARDS;
    public final String MILVUS_FIELD_ID;
    public final String MILVUS_FIELD_NNSID;
    public final String MILVUS_FIELD_VECTOR;
    public final String MILVUS_FIELD_ORIGINID;
    public final int MILVUS_VECTOR_DIM;
    public final String MILVUS_INDEX_NAME;
    public final String MILVUS_INDEX_PARAM;

    // Importer
    public final String IMPORTER_API_ENDPOINT_CREATE_IMAGE;
    public final String IMPORTER_KEY_PROVIDER;
    public final String IMPORTER_KEY_PROMPT;
    public final String IMPORTER_KEY_TIMESTAMP;

    private Map<String, String> applicationProperties;

    public EnvironmentVariables(String fileName) throws IOException {
        // Loading application.properties file
        this.applicationProperties = loadApplicationProperties(fileName);
        // Read values into variables
        // MONGODB
        this.MONGODB_URL = this.applicationProperties.get("genaifinder.dbmanager.mongo.url");
        this.MONGODB_HOST = this.applicationProperties.get("genaifinder.dbmanager.mongo.host");
        this.MONGODB_PORT = Integer.parseInt(this.applicationProperties.get("genaifinder.dbmanager.mongo.port"));
        this.MONGODB_AUTH_DB = this.applicationProperties.get("genaifinder.dbmanager.mongo.authentication");
        this.MONGODB_AUTH_USERNAME = this.applicationProperties.get("genaifinder.dbmanager.mongo.user");
        this.MONGODB_AUTH_PASSWORD = this.applicationProperties.get("genaifinder.dbmanager.mongo.passwort");
        this.MONGODB_DATABASE = this.applicationProperties.get("genaifinder.dbmanager.mongo.db.name");
        this.MONGODB_COLLECTION = this.applicationProperties.get("genaifinder.dbmanager.mongo.collection.name");
        // MILVUS
        this.MILVUS_URL = this.applicationProperties.get("genaifinder.dbmanager.milvus.url");
        this.MILVUS_PORT = Integer.parseInt(this.applicationProperties.get("genaifinder.dbmanager.milvus.port"));
        this.MILVUS_DATABASE = this.applicationProperties.get("genaifinder.dbmanager.milvus.database");
        this.MILVUS_COLLECTION_UNIT_META = this.applicationProperties.get("genaifinder.dbmanager.milvus.collection.name.meta");
        this.MILVUS_COLLECTION_UNIT_CONTENT = this.applicationProperties.get("genaifinder.dbmanager.milvus.collection.name.content");
        this.MILVUS_COLLECTION_UNIT_DATA = this.applicationProperties.get("genaifinder.dbmanager.milvus.collection.name.data");
        this.MILVUS_COLLECTION_UNIT_INSTANCE = this.applicationProperties.get("genaifinder.dbmanager.milvus.collection.name.instance");
        this.MILVUS_COLLECTION_DESCRIPTION = this.applicationProperties.get("genaifinder.dbmanager.milvus.collection.descripion");
        this.MILVUS_PARTITION_TEXT = this.applicationProperties.get("genaifinder.dbmanager.milvus.partition.name.text");
        this.MILVUS_PARTITION_IMAGE = this.applicationProperties.get("genaifinder.dbmanager.milvus.partition.name.image");
        this.MILVUS_PARTITION_AUDIO = this.applicationProperties.get("genaifinder.dbmanager.milvus.partition.name.audio");
        this.MILVUS_PARTITION_VIDEO = this.applicationProperties.get("genaifinder.dbmanager.milvus.partition.name.video");
        this.MILVUS_SHARDS = Integer.parseInt(this.applicationProperties.get("genaifinder.dbmanager.milvus.shards"));
        this.MILVUS_FIELD_ID = this.applicationProperties.get("genaifinder.dbmanager.milvus.field.id");
        this.MILVUS_FIELD_NNSID = this.applicationProperties.get("genaifinder.dbmanager.milvus.field.nnsId");
        this.MILVUS_FIELD_VECTOR = this.applicationProperties.get("genaifinder.dbmanager.milvus.field.vector");
        this.MILVUS_FIELD_ORIGINID = this.applicationProperties.get("genaifinder.dbmanager.milvus.field.originId");
        this.MILVUS_VECTOR_DIM = Integer.parseInt(this.applicationProperties.get("genaifinder.dbmanager.milvus.vectorDim"));
        this.MILVUS_INDEX_NAME = this.applicationProperties.get("genaifinder.dbmanager.milvus.index.name");
        this.MILVUS_INDEX_PARAM = "{\"nlist\":" + this.applicationProperties.get("genaifinder.dbmanager.milvus.index.param") + "}";
        // IMPORTER
        this.IMPORTER_API_ENDPOINT_CREATE_IMAGE = this.applicationProperties.get("genaifinder.dbmanager.importer.api.endpoint.create.image");
        this.IMPORTER_KEY_PROVIDER = this.applicationProperties.get("genaifinder.dbmanager.importer.key.provider");
        this.IMPORTER_KEY_PROMPT = this.applicationProperties.get("genaifinder.dbmanager.importer.key.prompt");
        this.IMPORTER_KEY_TIMESTAMP = this.applicationProperties.get("genaifinder.dbmanager.importer.key.timestamp");
    }

    public Map<String, String> loadApplicationProperties(String fileName) throws FileNotFoundException, IOException {
        Map<String, String> applicationProperties = new HashMap<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(fileName))));
        String line = reader.readLine();
        while (line != null) {
            if (line.isEmpty() || line.charAt(0) == '#') {
                line = reader.readLine();
            } else {
                StringTokenizer st = new StringTokenizer(line, "=");
                String key = st.nextToken();
                String value = st.nextToken();
                applicationProperties.put(key, value);
            }
            line = reader.readLine();

        }
        reader.close();
        return applicationProperties;
    }

}
