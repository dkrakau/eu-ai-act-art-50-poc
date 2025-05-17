package io.krakau.genaifinderdbmanager.service;

import io.krakau.genaifinderdbmanager.config.EnvironmentVariables;
import io.krakau.genaifinderdbmanager.wrapper.MilvusWrapper;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.grpc.DataType;
import io.milvus.grpc.ShowCollectionsResponse;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import io.milvus.param.R;
import io.milvus.param.collection.FieldType;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Dominik
 */
public class MilvusService {
    
    private EnvironmentVariables env;

    private MilvusWrapper milvusService;
    
    public MilvusService(EnvironmentVariables env) {
        this.env = env;
        this.milvusService = new MilvusWrapper(env.MILVUS_URL, env.MILVUS_PORT);
    }
    
    public void drop() {
        if (collectionsExists()) {
            System.out.println("Milvus: Dropping collections ...");
            this.milvusService.deleteCollection(env.MILVUS_DATABASE, env.MILVUS_COLLECTION_UNIT_META);
            this.milvusService.deleteCollection(env.MILVUS_DATABASE, env.MILVUS_COLLECTION_UNIT_CONTENT);
            this.milvusService.deleteCollection(env.MILVUS_DATABASE, env.MILVUS_COLLECTION_UNIT_DATA);
            this.milvusService.deleteCollection(env.MILVUS_DATABASE, env.MILVUS_COLLECTION_UNIT_INSTANCE);
            this.milvusService.dropDatabse(env.MILVUS_DATABASE);
        } else {
            System.out.println("Milvus: Drop collections failed: Milvus collections do not exists.");
        }
    }

    public void createDatabase() {
        System.out.println("Milvus: Creating database ...");
        this.milvusService.createDatabse(env.MILVUS_DATABASE);
    }

    public void createCollections() {
        if (!collectionsExists()) {
            System.out.println("Milvus: Creating collections ...");
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
            System.out.println("Milvus: Create collections failed: Milvus collections with partitions already exists.");
        }
    }

    public void createPartition() {
        if (!partitionsExists(env.MILVUS_COLLECTION_UNIT_META)
                && !partitionsExists(env.MILVUS_COLLECTION_UNIT_CONTENT)
                && !partitionsExists(env.MILVUS_COLLECTION_UNIT_DATA)
                && !partitionsExists(env.MILVUS_COLLECTION_UNIT_INSTANCE)) {
            System.out.println("Milvus: Creating partitions ...");
            String[] mediaType = {env.MILVUS_PARTITION_TEXT, env.MILVUS_PARTITION_IMAGE, env.MILVUS_PARTITION_AUDIO, env.MILVUS_PARTITION_VIDEO};
            for (int i = 0; i < mediaType.length; i++) {
                this.milvusService.createPartition(env.MILVUS_DATABASE, env.MILVUS_COLLECTION_UNIT_META, mediaType[i]);
                this.milvusService.createPartition(env.MILVUS_DATABASE, env.MILVUS_COLLECTION_UNIT_CONTENT, mediaType[i]);
                this.milvusService.createPartition(env.MILVUS_DATABASE, env.MILVUS_COLLECTION_UNIT_DATA, mediaType[i]);
                this.milvusService.createPartition(env.MILVUS_DATABASE, env.MILVUS_COLLECTION_UNIT_INSTANCE, mediaType[i]);
            }
        } else {
            System.out.println("Milvus: Create partitions failed: Milvus partitions already exists.");
        }
    }

    public void createIndexes() {
        if (collectionsExists()) {
            System.out.println("Milvus: Creating indexes for collections ...");
            this.milvusService.createIndex(env.MILVUS_INDEX_NAME, env.MILVUS_DATABASE, env.MILVUS_COLLECTION_UNIT_META, env.MILVUS_FIELD_VECTOR, IndexType.BIN_IVF_FLAT, MetricType.HAMMING, env.MILVUS_INDEX_PARAM, Boolean.FALSE);
            this.milvusService.createIndex(env.MILVUS_INDEX_NAME, env.MILVUS_DATABASE, env.MILVUS_COLLECTION_UNIT_CONTENT, env.MILVUS_FIELD_VECTOR, IndexType.BIN_IVF_FLAT, MetricType.HAMMING, env.MILVUS_INDEX_PARAM, Boolean.FALSE);
            this.milvusService.createIndex(env.MILVUS_INDEX_NAME, env.MILVUS_DATABASE, env.MILVUS_COLLECTION_UNIT_DATA, env.MILVUS_FIELD_VECTOR, IndexType.BIN_IVF_FLAT, MetricType.HAMMING, env.MILVUS_INDEX_PARAM, Boolean.FALSE);
            this.milvusService.createIndex(env.MILVUS_INDEX_NAME, env.MILVUS_DATABASE, env.MILVUS_COLLECTION_UNIT_INSTANCE, env.MILVUS_FIELD_VECTOR, IndexType.BIN_IVF_FLAT, MetricType.HAMMING, env.MILVUS_INDEX_PARAM, Boolean.FALSE);
        } else {
            System.out.println("Milvus: Create indexes failed: Milvus collections already exists.");
        }
    }

    private boolean collectionsExists() {
        return this.milvusService.hasCollection(env.MILVUS_DATABASE, env.MILVUS_COLLECTION_UNIT_META)
                && this.milvusService.hasCollection(env.MILVUS_DATABASE, env.MILVUS_COLLECTION_UNIT_CONTENT)
                && this.milvusService.hasCollection(env.MILVUS_DATABASE, env.MILVUS_COLLECTION_UNIT_DATA)
                && this.milvusService.hasCollection(env.MILVUS_DATABASE, env.MILVUS_COLLECTION_UNIT_INSTANCE);
    }
    
    private boolean partitionsExists(String collectionName) {
        return this.milvusService.hasPartition(env.MILVUS_DATABASE, collectionName, env.MILVUS_PARTITION_AUDIO)
                && this.milvusService.hasPartition(env.MILVUS_DATABASE, collectionName, env.MILVUS_PARTITION_IMAGE)
                && this.milvusService.hasPartition(env.MILVUS_DATABASE, collectionName, env.MILVUS_PARTITION_TEXT)
                && this.milvusService.hasPartition(env.MILVUS_DATABASE, collectionName, env.MILVUS_PARTITION_VIDEO);
    }

    public void getInfo() {
        R<ShowCollectionsResponse> respShowCollections = this.milvusService.listCollections(env.MILVUS_DATABASE);
        System.out.println("########## Milvus collections ##########");
        System.out.println(respShowCollections);
    }

    public void getCollectionInfo(String name) {
        if (collectionsExists()) {
            if (name.equals(env.MILVUS_COLLECTION_UNIT_META)
                    || name.equals(env.MILVUS_COLLECTION_UNIT_CONTENT)
                    || name.equals(env.MILVUS_COLLECTION_UNIT_DATA)
                    || name.equals(env.MILVUS_COLLECTION_UNIT_INSTANCE)) {
                this.milvusService.collectionInfo(env.MILVUS_DATABASE, name);
            } else {
                System.out.println("Milvus: Collection name [" + name + "] not found.");
            }
        } else {
            System.out.println("Milvus: Collection info failed: Milvus collections do not exist.");
        }
    }
    
}
