package io.krakau.genaifinderdbmanager;

import io.krakau.genaifinderdbmanager.config.EnvironmentVariables;
import io.krakau.genaifinderdbmanager.service.MilvusService;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.grpc.DataType;
import io.milvus.grpc.ShowCollectionsResponse;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import io.milvus.param.R;
import io.milvus.param.collection.FieldType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Dominik
 */
public class DatabaseManager {

    private EnvironmentVariables env;

    private MilvusService milvusService;

    public DatabaseManager(EnvironmentVariables env) throws IOException {
        this.env = env;
        this.milvusService = new MilvusService(env.MILVUS_URL, env.MILVUS_PORT);
    }

    public void create() {
        createDatabase();
        createCollections();
        createPartition();
        createIndexes();
    }

    public void drop() {
        if (collectionsExists()) {
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
        if (!collectionsExists()) {
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
        if (collectionsExists()) {
            System.out.println("Creating partitions ...");
            String[] mediaType = {env.MILVUS_PARTITION_TEXT, env.MILVUS_PARTITION_IMAGE, env.MILVUS_PARTITION_AUDIO, env.MILVUS_PARTITION_VIDEO};
            for (int i = 0; i < mediaType.length; i++) {
                this.milvusService.createPartition(env.MILVUS_DATABASE, env.MILVUS_COLLECTION_UNIT_META, mediaType[i]);
                this.milvusService.createPartition(env.MILVUS_DATABASE, env.MILVUS_COLLECTION_UNIT_CONTENT, mediaType[i]);
                this.milvusService.createPartition(env.MILVUS_DATABASE, env.MILVUS_COLLECTION_UNIT_DATA, mediaType[i]);
                this.milvusService.createPartition(env.MILVUS_DATABASE, env.MILVUS_COLLECTION_UNIT_INSTANCE, mediaType[i]);
            }
        } else {
            System.out.println("Create partitions failed: Milvus collections does not exitst.");
        }
    }

    private void createIndexes() {
        if (collectionsExists()) {
            System.out.println("Creating indexes for collections ...");
            this.milvusService.createIndex(env.MILVUS_INDEX_NAME, env.MILVUS_DATABASE, env.MILVUS_COLLECTION_UNIT_META, env.MILVUS_FIELD_VECTOR, IndexType.BIN_IVF_FLAT, MetricType.HAMMING, env.MILVUS_INDEX_PARAM, Boolean.FALSE);
            this.milvusService.createIndex(env.MILVUS_INDEX_NAME, env.MILVUS_DATABASE, env.MILVUS_COLLECTION_UNIT_CONTENT, env.MILVUS_FIELD_VECTOR, IndexType.BIN_IVF_FLAT, MetricType.HAMMING, env.MILVUS_INDEX_PARAM, Boolean.FALSE);
            this.milvusService.createIndex(env.MILVUS_INDEX_NAME, env.MILVUS_DATABASE, env.MILVUS_COLLECTION_UNIT_DATA, env.MILVUS_FIELD_VECTOR, IndexType.BIN_IVF_FLAT, MetricType.HAMMING, env.MILVUS_INDEX_PARAM, Boolean.FALSE);
            this.milvusService.createIndex(env.MILVUS_INDEX_NAME, env.MILVUS_DATABASE, env.MILVUS_COLLECTION_UNIT_INSTANCE, env.MILVUS_FIELD_VECTOR, IndexType.BIN_IVF_FLAT, MetricType.HAMMING, env.MILVUS_INDEX_PARAM, Boolean.FALSE);
        } else {
            System.out.println("Create indexes failed: Milvus collections already exitst.");
        }
    }

    private boolean collectionsExists() {
        return this.milvusService.hasCollection(env.MILVUS_DATABASE, env.MILVUS_COLLECTION_UNIT_META)
                && this.milvusService.hasCollection(env.MILVUS_DATABASE, env.MILVUS_COLLECTION_UNIT_CONTENT)
                && this.milvusService.hasCollection(env.MILVUS_DATABASE, env.MILVUS_COLLECTION_UNIT_DATA)
                && this.milvusService.hasCollection(env.MILVUS_DATABASE, env.MILVUS_COLLECTION_UNIT_INSTANCE);
    }

    private void getInfo() {
        R<ShowCollectionsResponse> respShowCollections = this.milvusService.listCollections(env.MILVUS_DATABASE);
        System.out.println("########## Milvus collections ##########");
        System.out.println(respShowCollections);
    }

    private void getCollectionInfo(String name) {
        if (collectionsExists()) {
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

}
