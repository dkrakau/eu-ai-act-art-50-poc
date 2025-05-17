package io.krakau.genaifinderdbmanager;

import io.krakau.genaifinderdbmanager.config.EnvironmentVariables;
import io.krakau.genaifinderdbmanager.service.MilvusService;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.grpc.DataType;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import io.milvus.param.collection.FieldType;
import io.milvus.param.collection.FlushParam;
import io.milvus.param.dml.InsertParam;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Dominik
 */
public class DatabaseManager {

    private EnvironmentVariables env;
    
    private MilvusService milvusService;
    private boolean collectionsExist;
    
    public DatabaseManager(EnvironmentVariables env) throws IOException {
        this.env = env;
        this.milvusService = new MilvusService(env.MILVUS_URL, env.MILVUS_PORT);
        this.collectionsExist = this.milvusService.hasCollection(env.MILVUS_DATABASE, env.MILVUS_COLLECTION_UNIT_META)
                && this.milvusService.hasCollection(env.MILVUS_DATABASE, env.MILVUS_COLLECTION_UNIT_CONTENT)
                && this.milvusService.hasCollection(env.MILVUS_DATABASE, env.MILVUS_COLLECTION_UNIT_DATA)
                && this.milvusService.hasCollection(env.MILVUS_DATABASE, env.MILVUS_COLLECTION_UNIT_INSTANCE);
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
            this.milvusService.deleteCollection(env.MILVUS_DATABASE, env.MILVUS_COLLECTION_UNIT_META);
            this.milvusService.deleteCollection(env.MILVUS_DATABASE, env.MILVUS_COLLECTION_UNIT_CONTENT);
            this.milvusService.deleteCollection(env.MILVUS_DATABASE, env.MILVUS_COLLECTION_UNIT_DATA);
            this.milvusService.deleteCollection(env.MILVUS_DATABASE, env.MILVUS_COLLECTION_UNIT_INSTANCE);
            
            this.milvusService.dropDatabse(env.MILVUS_DATABASE);
        } else {
            System.out.println("Drop collections failed: Milvus collections do not exist.");
        }
    }
    
    private void createDatabase() {
        System.out.println("Creating database ...");
        this.milvusService.createDatabse(env.MILVUS_DATABASE);
    }
    
    private void createCollections() {
        if (!this.collectionsExist) {
            System.out.println("Creating collections ...");
            FieldType id = FieldType.newBuilder()
                    .withName(env.MILVUS_FIELD_ID)
                    .withDataType(DataType.Int64)
                    .withPrimaryKey(true)
                    .withAutoID(true)
                    .build();

            FieldType nnsId = FieldType.newBuilder()
                    .withName(env.MILVUS_FIELD_NNSID)
                    .withDataType(DataType.Int64)
                    .build();

            FieldType vec = FieldType.newBuilder()
                    .withName(env.MILVUS_FIELD_VECTOR)
                    .withDataType(DataType.BinaryVector)
                    .withDimension(env.MILVUS_VECTOR_DIM)
                    .build();

            List<FieldType> fieldList = new ArrayList<>();
            fieldList.add(id);
            fieldList.add(vec);
            fieldList.add(nnsId);

            this.milvusService.createCollection(env.MILVUS_DATABASE, env.MILVUS_COLLECTION_UNIT_META, env.MILVUS_COLLECTION_DESCRIPTION, fieldList, env.MILVUS_SHARDS, ConsistencyLevelEnum.STRONG);
            this.milvusService.createCollection(env.MILVUS_DATABASE, env.MILVUS_COLLECTION_UNIT_CONTENT, env.MILVUS_COLLECTION_DESCRIPTION, fieldList, env.MILVUS_SHARDS, ConsistencyLevelEnum.STRONG);
            this.milvusService.createCollection(env.MILVUS_DATABASE, env.MILVUS_COLLECTION_UNIT_DATA, env.MILVUS_COLLECTION_DESCRIPTION, fieldList, env.MILVUS_SHARDS, ConsistencyLevelEnum.STRONG);
            this.milvusService.createCollection(env.MILVUS_DATABASE, env.MILVUS_COLLECTION_UNIT_INSTANCE, env.MILVUS_COLLECTION_DESCRIPTION, fieldList, env.MILVUS_SHARDS, ConsistencyLevelEnum.STRONG);
        } else {
            System.out.println("Create collections failed: Milvus collections with partitions already exitst.");
        }
    }
    
    private void createPartition() {
        System.out.println("Creating partitions ...");
        String[] mediaType = {env.MILVUS_PARTITION_TEXT, env.MILVUS_PARTITION_IMAGE, env.MILVUS_PARTITION_AUDIO, env.MILVUS_PARTITION_VIDEO};
        for (int i = 0; i < mediaType.length; i++) {
            this.milvusService.createPartition(env.MILVUS_DATABASE, env.MILVUS_COLLECTION_UNIT_META, mediaType[i]);
            this.milvusService.createPartition(env.MILVUS_DATABASE, env.MILVUS_COLLECTION_UNIT_CONTENT, mediaType[i]);
            this.milvusService.createPartition(env.MILVUS_DATABASE, env.MILVUS_COLLECTION_UNIT_DATA, mediaType[i]);
            this.milvusService.createPartition(env.MILVUS_DATABASE, env.MILVUS_COLLECTION_UNIT_INSTANCE, mediaType[i]);
        }
    }
    
    private void createIndexes() {
//        if (this.collectionsExist) {
            System.out.println("Creating indexes for collections ...");
            this.milvusService.createIndex(env.MILVUS_INDEX_NAME, env.MILVUS_DATABASE, env.MILVUS_COLLECTION_UNIT_META, env.MILVUS_FIELD_VECTOR, IndexType.BIN_IVF_FLAT, MetricType.HAMMING, env.MILVUS_INDEX_PARAM, Boolean.FALSE);
            this.milvusService.createIndex(env.MILVUS_INDEX_NAME, env.MILVUS_DATABASE, env.MILVUS_COLLECTION_UNIT_CONTENT, env.MILVUS_FIELD_VECTOR, IndexType.BIN_IVF_FLAT, MetricType.HAMMING, env.MILVUS_INDEX_PARAM, Boolean.FALSE);
            this.milvusService.createIndex(env.MILVUS_INDEX_NAME, env.MILVUS_DATABASE, env.MILVUS_COLLECTION_UNIT_DATA, env.MILVUS_FIELD_VECTOR, IndexType.BIN_IVF_FLAT, MetricType.HAMMING, env.MILVUS_INDEX_PARAM, Boolean.FALSE);
            this.milvusService.createIndex(env.MILVUS_INDEX_NAME, env.MILVUS_DATABASE, env.MILVUS_COLLECTION_UNIT_INSTANCE, env.MILVUS_FIELD_VECTOR, IndexType.BIN_IVF_FLAT, MetricType.HAMMING, env.MILVUS_INDEX_PARAM, Boolean.FALSE);
//        } else {
//            System.out.println("Create indexes failed: Milvus collections already exitst.");
//        }
    }
    
    
    
    
    public void dropDatabase() {
        System.out.println("Dropping database ...");
        this.milvusService.dropDatabse(env.MILVUS_DATABASE);
    }

    

    

    
    /*
    * Old method for insertion of testdata
    */
    private void insertColumns(int count, String partitionName, String origin) {
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
                fields.add(new InsertParam.Field(env.MILVUS_FIELD_VECTOR, buffer));
                fields.add(new InsertParam.Field(env.MILVUS_FIELD_NNSID, nss));
                fields.add(new InsertParam.Field(env.MILVUS_FIELD_ORIGINID, originList));

//                boolean partitionExist = false;
//                String[] mediaType = {this.COLLECTION_PARTITION_TEXT, this.COLLECTION_PARTITION_IMAGE, this.COLLECTION_PARTITION_AUDIO, this.COLLECTION_PARTITION_VIDEO};
//                int i = 0;
//                while (i < mediaType.length && !partitionExist) {
//                    partitionExist = partitionName.equals(mediaType[i]);
//                    i++;
//                }
                if (partitionExist(partitionName)) {
                    milvusService.insert(env.MILVUS_COLLECTION_UNIT_META, partitionName, fields);
                    milvusService.insert(env.MILVUS_COLLECTION_UNIT_CONTENT, partitionName, fields);
                    milvusService.insert(env.MILVUS_COLLECTION_UNIT_DATA, partitionName, fields);
                    milvusService.insert(env.MILVUS_COLLECTION_UNIT_INSTANCE, partitionName, fields);

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
        String[] mediaType = {env.MILVUS_PARTITION_TEXT, env.MILVUS_PARTITION_IMAGE, env.MILVUS_PARTITION_AUDIO, env.MILVUS_PARTITION_VIDEO};
        int i = 0;
        while (i < mediaType.length && !partitionExist) {
            partitionExist = partitionName.equals(mediaType[i]);
            i++;
        }
        return partitionExist;
    }

    /*
    * Old method for insertion of testdata
    */
    private void insertColumns(int count, String origin) {
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
                fields.add(new InsertParam.Field(env.MILVUS_FIELD_VECTOR, buffer));
                fields.add(new InsertParam.Field(env.MILVUS_FIELD_NNSID, nss));
                fields.add(new InsertParam.Field(env.MILVUS_FIELD_ORIGINID, originList));

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
                String[] mediaType = {env.MILVUS_PARTITION_TEXT, env.MILVUS_PARTITION_IMAGE, env.MILVUS_PARTITION_AUDIO, env.MILVUS_PARTITION_VIDEO};
                while (i < mediaType.length) {
                    milvusService.insert(env.MILVUS_COLLECTION_UNIT_META, mediaType[i], fields);
                    milvusService.insert(env.MILVUS_COLLECTION_UNIT_CONTENT, mediaType[i], fields);
                    milvusService.insert(env.MILVUS_COLLECTION_UNIT_DATA, mediaType[i], fields);
                    milvusService.insert(env.MILVUS_COLLECTION_UNIT_INSTANCE, mediaType[i], fields);
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
        this.milvusService.listCollections(env.MILVUS_DATABASE);
    }

    public void getCollectionInfo(String name) {
        if (this.collectionsExist) {
            if (name.equals(env.MILVUS_COLLECTION_UNIT_META)
                    || name.equals(env.MILVUS_COLLECTION_UNIT_CONTENT)
                    || name.equals(env.MILVUS_COLLECTION_UNIT_DATA)
                    || name.equals(env.MILVUS_COLLECTION_UNIT_INSTANCE)) {
                this.milvusService.collectionInfo(env.MILVUS_DATABASE, name);
            } else {
                System.out.println("Milvus collection name [" + name + "] not found.");
            }
        } else {
            System.out.println("Collection info failed: Milvus collections does not exist.");
        }
    }

    public void flush() {
        this.milvusService.flush(FlushParam.newBuilder().addCollectionName(env.MILVUS_COLLECTION_UNIT_META).build());
        this.milvusService.flush(FlushParam.newBuilder().addCollectionName(env.MILVUS_COLLECTION_UNIT_CONTENT).build());
        this.milvusService.flush(FlushParam.newBuilder().addCollectionName(env.MILVUS_COLLECTION_UNIT_DATA).build());
        this.milvusService.flush(FlushParam.newBuilder().addCollectionName(env.MILVUS_COLLECTION_UNIT_INSTANCE).build());

    }

    
}