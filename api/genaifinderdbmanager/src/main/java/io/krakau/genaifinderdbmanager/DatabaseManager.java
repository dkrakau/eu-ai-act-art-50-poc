package io.krakau.genaifinderdbmanager;

import io.krakau.genaifinderdbmanager.service.MilvusService;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.grpc.DataType;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import io.milvus.param.collection.FieldType;
import io.milvus.param.collection.FlushParam;
import io.milvus.param.dml.InsertParam;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;

/**
 *
 * @author Dominik
 */
public class DatabaseManager {

    private MilvusService milvusService;
    private boolean collectionsExist;

    private Map<String, String> applicationProperties;

    //Milvus
    private final String MILVUS_URL;
    private final int MILVUS_PORT;
    private final String DATABASE_NAME;
    private final String COLLECTION_UNIT_NAME_META;
    private final String COLLECTION_UNIT_NAME_CONTENT;
    private final String COLLECTION_UNIT_NAME_DATA;
    private final String COLLECTION_UNIT_NAME_INSTANCE;
    private final String COLLECTION_DESCRIPTION_META;
    private final String COLLECTION_DESCRIPTION_CONTENT;
    private final String COLLECTION_DESCRIPTION_DATA;
    private final String COLLECTION_DESCRIPTION_INSTANCE;
    private final String COLLECTION_PARTITION_TEXT;
    private final String COLLECTION_PARTITION_IMAGE;
    private final String COLLECTION_PARTITION_AUDIO;
    private final String COLLECTION_PARTITION_VIDEO;
    private final int SHARDS;
    private final String FIELD_ID;
    private final String FIELD_NNSID;
    private final String FIELD_VECTOR;
    private final String FIELD_ORIGINID;
    private final int VECTOR_DIM;
    private final String INDEX_NAME;
    private final String INDEX_PARAM;
    
    public DatabaseManager() throws IOException {
        // loading application.properties file
        this.applicationProperties = loadApplicationProperties("application.properties");
        // read values into variables
        this.MILVUS_URL = this.applicationProperties.get("genaifinder.dbmanager.milvus.url");
        this.MILVUS_PORT = Integer.parseInt(this.applicationProperties.get("genaifinder.dbmanager.milvus.port"));
        this.DATABASE_NAME = this.applicationProperties.get("genaifinder.dbmanager.milvus.database");
        this.COLLECTION_UNIT_NAME_META = this.applicationProperties.get("genaifinder.dbmanager.milvus.collection.name.meta");
        this.COLLECTION_UNIT_NAME_CONTENT = this.applicationProperties.get("genaifinder.dbmanager.milvus.collection.name.content");
        this.COLLECTION_UNIT_NAME_DATA = this.applicationProperties.get("genaifinder.dbmanager.milvus.collection.name.data");
        this.COLLECTION_UNIT_NAME_INSTANCE = this.applicationProperties.get("genaifinder.dbmanager.milvus.collection.name.instance");
        this.COLLECTION_DESCRIPTION_META = this.applicationProperties.get("genaifinder.dbmanager.milvus.collection.descripion.meta");
        this.COLLECTION_DESCRIPTION_CONTENT = this.applicationProperties.get("genaifinder.dbmanager.milvus.collection.descripion.content");
        this.COLLECTION_DESCRIPTION_DATA = this.applicationProperties.get("genaifinder.dbmanager.milvus.collection.descripion.data");
        this.COLLECTION_DESCRIPTION_INSTANCE = this.applicationProperties.get("genaifinder.dbmanager.milvus.collection.descripion.instance");
        this.COLLECTION_PARTITION_TEXT = this.applicationProperties.get("genaifinder.dbmanager.milvus.partition.name.text");
        this.COLLECTION_PARTITION_IMAGE = this.applicationProperties.get("genaifinder.dbmanager.milvus.partition.name.image");
        this.COLLECTION_PARTITION_AUDIO = this.applicationProperties.get("genaifinder.dbmanager.milvus.partition.name.audio");
        this.COLLECTION_PARTITION_VIDEO = this.applicationProperties.get("genaifinder.dbmanager.milvus.partition.name.video");
        this.SHARDS = Integer.parseInt(this.applicationProperties.get("genaifinder.dbmanager.milvus.shards"));
        this.FIELD_ID = this.applicationProperties.get("genaifinder.dbmanager.milvus.field.id");
        this.FIELD_NNSID = this.applicationProperties.get("genaifinder.dbmanager.milvus.field.nnsId");
        this.FIELD_ORIGINID = this.applicationProperties.get("genaifinder.dbmanager.milvus.field.originId");
        this.FIELD_VECTOR = this.applicationProperties.get("genaifinder.dbmanager.milvus.field.vector");
        this.VECTOR_DIM = Integer.parseInt(this.applicationProperties.get("genaifinder.dbmanager.milvus.vectorDim"));
        this.INDEX_NAME = this.applicationProperties.get("genaifinder.dbmanager.milvus.index.name");
        this.INDEX_PARAM = "{\"nlist\":" + this.applicationProperties.get("genaifinder.dbmanager.milvus.index.param") + "}";
        this.milvusService = new MilvusService(this.MILVUS_URL, this.MILVUS_PORT);
        this.collectionsExist = this.milvusService.hasCollection(this.DATABASE_NAME, this.COLLECTION_UNIT_NAME_META)
                && this.milvusService.hasCollection(this.DATABASE_NAME, this.COLLECTION_UNIT_NAME_CONTENT)
                && this.milvusService.hasCollection(this.DATABASE_NAME, this.COLLECTION_UNIT_NAME_DATA)
                && this.milvusService.hasCollection(this.DATABASE_NAME, this.COLLECTION_UNIT_NAME_INSTANCE);
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
    
    public void createDatabase() {
        System.out.println("Creating database ...");
        this.milvusService.createDatabse(this.DATABASE_NAME);
    }
    
    public void dropDatabase() {
        System.out.println("Dropping database ...");
        this.milvusService.dropDatabse(this.DATABASE_NAME);
    }

    public void createCollections() {
        if (!this.collectionsExist) {
            System.out.println("Creating collections ...");
            FieldType id = FieldType.newBuilder()
                    .withName(this.FIELD_ID)
                    .withDataType(DataType.Int64)
                    .withPrimaryKey(true)
                    .withAutoID(true)
                    .build();

            FieldType nnsId = FieldType.newBuilder()
                    .withName(this.FIELD_NNSID)
                    .withDataType(DataType.Int64)
                    .build();

            FieldType vec = FieldType.newBuilder()
                    .withName(this.FIELD_VECTOR)
                    .withDataType(DataType.BinaryVector)
                    .withDimension(this.VECTOR_DIM)
                    .build();

            List<FieldType> fieldList = new ArrayList<>();
            fieldList.add(id);
            fieldList.add(vec);
            fieldList.add(nnsId);

            this.milvusService.createCollection(this.DATABASE_NAME, this.COLLECTION_UNIT_NAME_META, this.COLLECTION_DESCRIPTION_META, fieldList, this.SHARDS, ConsistencyLevelEnum.STRONG);
            this.milvusService.createCollection(this.DATABASE_NAME, this.COLLECTION_UNIT_NAME_CONTENT, this.COLLECTION_DESCRIPTION_CONTENT, fieldList, this.SHARDS, ConsistencyLevelEnum.STRONG);
            this.milvusService.createCollection(this.DATABASE_NAME, this.COLLECTION_UNIT_NAME_DATA, this.COLLECTION_DESCRIPTION_DATA, fieldList, this.SHARDS, ConsistencyLevelEnum.STRONG);
            this.milvusService.createCollection(this.DATABASE_NAME, this.COLLECTION_UNIT_NAME_INSTANCE, this.COLLECTION_DESCRIPTION_INSTANCE, fieldList, this.SHARDS, ConsistencyLevelEnum.STRONG);
        } else {
            System.out.println("Create collections failed: Milvus collections with partitions already exitst.");
        }
    }

    public void createIndexes() {
//        if (this.collectionsExist) {
            System.out.println("Creating indexes for collections ...");
            this.milvusService.createIndex(this.INDEX_NAME, this.DATABASE_NAME, this.COLLECTION_UNIT_NAME_META, this.FIELD_VECTOR, IndexType.BIN_IVF_FLAT, MetricType.HAMMING, this.INDEX_PARAM, Boolean.FALSE);
            this.milvusService.createIndex(this.INDEX_NAME, this.DATABASE_NAME, this.COLLECTION_UNIT_NAME_CONTENT, this.FIELD_VECTOR, IndexType.BIN_IVF_FLAT, MetricType.HAMMING, this.INDEX_PARAM, Boolean.FALSE);
            this.milvusService.createIndex(this.INDEX_NAME, this.DATABASE_NAME, this.COLLECTION_UNIT_NAME_DATA, this.FIELD_VECTOR, IndexType.BIN_IVF_FLAT, MetricType.HAMMING, this.INDEX_PARAM, Boolean.FALSE);
            this.milvusService.createIndex(this.INDEX_NAME, this.DATABASE_NAME, this.COLLECTION_UNIT_NAME_INSTANCE, this.FIELD_VECTOR, IndexType.BIN_IVF_FLAT, MetricType.HAMMING, this.INDEX_PARAM, Boolean.FALSE);
//        } else {
//            System.out.println("Create indexes failed: Milvus collections already exitst.");
//        }
    }

    public void createPartition() {
        System.out.println("Creating partitions ...");
        String[] mediaType = {this.COLLECTION_PARTITION_TEXT, this.COLLECTION_PARTITION_IMAGE, this.COLLECTION_PARTITION_AUDIO, this.COLLECTION_PARTITION_VIDEO};
        for (int i = 0; i < mediaType.length; i++) {
            this.milvusService.createPartition(this.DATABASE_NAME, this.COLLECTION_UNIT_NAME_META, mediaType[i]);
            this.milvusService.createPartition(this.DATABASE_NAME, this.COLLECTION_UNIT_NAME_CONTENT, mediaType[i]);
            this.milvusService.createPartition(this.DATABASE_NAME, this.COLLECTION_UNIT_NAME_DATA, mediaType[i]);
            this.milvusService.createPartition(this.DATABASE_NAME, this.COLLECTION_UNIT_NAME_INSTANCE, mediaType[i]);
        }
    }
    
    public void create() {
        createDatabase();
        createCollections();
        createPartition();
        createIndexes();
    }

    public void drop() {
        if (this.collectionsExist) {
            System.out.println("Dropping collections ...");
            this.milvusService.deleteCollection(this.DATABASE_NAME, this.COLLECTION_UNIT_NAME_META);
            this.milvusService.deleteCollection(this.DATABASE_NAME, this.COLLECTION_UNIT_NAME_CONTENT);
            this.milvusService.deleteCollection(this.DATABASE_NAME, this.COLLECTION_UNIT_NAME_DATA);
            this.milvusService.deleteCollection(this.DATABASE_NAME, this.COLLECTION_UNIT_NAME_INSTANCE);
            
            this.milvusService.dropDatabse(this.DATABASE_NAME);
        } else {
            System.out.println("Drop collections failed: Milvus collections do not exist.");
        }
    }

    public void insertColumns(int count, String partitionName, String origin) {
        if (this.collectionsExist) {
            int step = 0;
            if (count > 0) {
                if (count > 10000) {
                    step = 10000;
                } else {
                    step = count;
                }
                System.out.println("Inserting test entries ...");

                List<Long> nss = new ArrayList<>();
                Long id = 1700405424941L;
                List<Integer> originList = new ArrayList<>();
//                int originDefaultValue = getOriginId(origin);
                int originDefaultValue = 2;
                for (int i = 0; i < step; ++i) {
                    nss.add(id);
                    originList.add(originDefaultValue);
                }

                List<ByteBuffer> buffer = generateBinVectors(step);
                List<InsertParam.Field> fields = new ArrayList<>();
                fields.add(new InsertParam.Field(this.FIELD_VECTOR, buffer));
                fields.add(new InsertParam.Field(this.FIELD_NNSID, nss));
                fields.add(new InsertParam.Field(this.FIELD_ORIGINID, originList));

//                boolean partitionExist = false;
//                String[] mediaType = {this.COLLECTION_PARTITION_TEXT, this.COLLECTION_PARTITION_IMAGE, this.COLLECTION_PARTITION_AUDIO, this.COLLECTION_PARTITION_VIDEO};
//                int i = 0;
//                while (i < mediaType.length && !partitionExist) {
//                    partitionExist = partitionName.equals(mediaType[i]);
//                    i++;
//                }
                if (partitionExist(partitionName)) {
                    milvusService.insert(this.COLLECTION_UNIT_NAME_META, partitionName, fields);
                    milvusService.insert(this.COLLECTION_UNIT_NAME_CONTENT, partitionName, fields);
                    milvusService.insert(this.COLLECTION_UNIT_NAME_DATA, partitionName, fields);
                    milvusService.insert(this.COLLECTION_UNIT_NAME_INSTANCE, partitionName, fields);

                } else {
                    System.out.println("Insert failed: Milvus partition do net exist");
                }

                insertColumns(count - step, partitionName, origin);

            } else {
                System.out.println("Insert done.");
            }
        } else {
            System.out.println("Insert failed: Milvus collections do not exist.");
        }
    }

    public boolean partitionExist(String partitionName) {
        boolean partitionExist = false;
        String[] mediaType = {this.COLLECTION_PARTITION_TEXT, this.COLLECTION_PARTITION_IMAGE, this.COLLECTION_PARTITION_AUDIO, this.COLLECTION_PARTITION_VIDEO};
        int i = 0;
        while (i < mediaType.length && !partitionExist) {
            partitionExist = partitionName.equals(mediaType[i]);
            i++;
        }
        return partitionExist;
    }

    public void insertColumns(int count, String origin) {
        if (this.collectionsExist) {
            int step = 0;
//            int rest = 0;
            if (count > 0) {
                System.out.println("Inserting test entries ...");
                if (count >= 10000) {
                    step = 10000;
//                } else if(count > 4){
//                    step = count / 4;
//                    rest = count % 4;
                } else {
                    step = count;
//                    step = 1;
                }
                List<Long> nss = new ArrayList<>();
                Long id = 1700405424941L;
                List<Integer> originList = new ArrayList<>();
//                int originDefaultValue = getOriginId(origin);
                int originDefaultValue = 2;
                for (int i = 0; i < step; ++i) {
                    nss.add(id);
                    originList.add(originDefaultValue);
                }

                List<ByteBuffer> buffer = generateBinVectors(step);
                List<InsertParam.Field> fields = new ArrayList<>();
                fields.add(new InsertParam.Field(this.FIELD_VECTOR, buffer));
                fields.add(new InsertParam.Field(this.FIELD_NNSID, nss));
                fields.add(new InsertParam.Field(this.FIELD_ORIGINID, originList));

                int i = 0;
//                if (step == 1) {
//                    if (count == 3) {
//                        i = 1;
//                    }
//                    if (count == 2) {
//                        i = 2;
//                    }
//                    if (count == 1) {
//                        i = 3;
//                    }
//                }
                String[] mediaType = {this.COLLECTION_PARTITION_TEXT, this.COLLECTION_PARTITION_IMAGE, this.COLLECTION_PARTITION_AUDIO, this.COLLECTION_PARTITION_VIDEO};
                while (i < mediaType.length) {
                    milvusService.insert(this.COLLECTION_UNIT_NAME_META, mediaType[i], fields);
                    milvusService.insert(this.COLLECTION_UNIT_NAME_CONTENT, mediaType[i], fields);
                    milvusService.insert(this.COLLECTION_UNIT_NAME_DATA, mediaType[i], fields);
                    milvusService.insert(this.COLLECTION_UNIT_NAME_INSTANCE, mediaType[i], fields);
                    i++;
                }
//                if (rest == 0) {
                insertColumns(count - step, origin);
//                } else {
//                    insertColumns(rest);
//                }
            } else {
                System.out.println("Insert done.");
            }
        } else {
            System.out.println("Insert failed: Milvus collections do not exist.");
        }
    }

    private List<ByteBuffer> generateBinVectors(int count) {

        List<ByteBuffer> vectors = new ArrayList<>();

        for (int n = 0; n < count; ++n) {
            ByteBuffer buffer = ByteBuffer.allocate(8);
            byte bytes[] = new byte[8];
            new Random().nextBytes(bytes);
            buffer.put(bytes);
            vectors.add(buffer);
        }
        return vectors;
    }

    public void getInfo() {
        this.milvusService.listCollections(this.DATABASE_NAME);
    }

    public void getCollectionInfo(String name) {
        if (this.collectionsExist) {
            if (name.equals(this.COLLECTION_UNIT_NAME_META)
                    || name.equals(this.COLLECTION_UNIT_NAME_CONTENT)
                    || name.equals(this.COLLECTION_UNIT_NAME_DATA)
                    || name.equals(this.COLLECTION_UNIT_NAME_INSTANCE)) {
                this.milvusService.collectionInfo(this.DATABASE_NAME, name);
            } else {
                System.out.println("Milvus collection name [" + name + "] not found.");
            }
        } else {
            System.out.println("Collection info failed: Milvus collections does not exist.");
        }
    }

    public void flush() {
        this.milvusService.flush(FlushParam.newBuilder().addCollectionName(this.COLLECTION_UNIT_NAME_META).build());
        this.milvusService.flush(FlushParam.newBuilder().addCollectionName(this.COLLECTION_UNIT_NAME_CONTENT).build());
        this.milvusService.flush(FlushParam.newBuilder().addCollectionName(this.COLLECTION_UNIT_NAME_DATA).build());
        this.milvusService.flush(FlushParam.newBuilder().addCollectionName(this.COLLECTION_UNIT_NAME_INSTANCE).build());

    }

    public void help() {
        System.out.println("Usage: DatabaseManager <options>");
        System.out.println("  -c, --create\t\tCreate milvus vector database");
        System.out.println("  -d, --drop\t\tDrop milvus vector database");
        System.out.println("  -i, --insert\t\tInserts entries into milvus vector database");
        System.out.println("  -s, --stats\t\tShow information of milvus vector database collections");
        System.out.println("      --stats <name>\tShow information of milvus vector database collection");
        System.out.println("  -h, --help\t\tShow help information of DatabaseManager");
    }
}