package io.krakau.genaifinderapi.service;

import com.google.gson.JsonObject;
import io.krakau.genaifinderapi.GenaifinderapiApplication;
import io.milvus.client.MilvusServiceClient;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.grpc.MutationResult;
import io.milvus.grpc.SearchResults;
import io.milvus.param.MetricType;
import io.milvus.param.R;
import io.milvus.param.RpcStatus;
import io.milvus.param.collection.LoadCollectionParam;
import io.milvus.param.collection.ReleaseCollectionParam;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.InsertParam.Field;
import io.milvus.param.dml.SearchParam;
import java.nio.ByteBuffer;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Dominik
 */
@Service
public class MilvusService {

    private MilvusServiceClient milvusServiceClient;

    @Autowired
    public MilvusService(MilvusServiceClient milvusServiceClient) {
        this.milvusServiceClient = milvusServiceClient;
        loadCollection(GenaifinderapiApplication.env.getProperty("spring.data.milvus.database"), GenaifinderapiApplication.env.getProperty("spring.data.milvus.collection.name.units.meta"));
        loadCollection(GenaifinderapiApplication.env.getProperty("spring.data.milvus.database"), GenaifinderapiApplication.env.getProperty("spring.data.milvus.collection.name.units.content"));
        loadCollection(GenaifinderapiApplication.env.getProperty("spring.data.milvus.database"), GenaifinderapiApplication.env.getProperty("spring.data.milvus.collection.name.units.data"));
        loadCollection(GenaifinderapiApplication.env.getProperty("spring.data.milvus.database"), GenaifinderapiApplication.env.getProperty("spring.data.milvus.collection.name.units.instance"));
    }
    

    public R<RpcStatus> loadCollection(String databaseName, String collectionName) {
        return this.milvusServiceClient.loadCollection(
                LoadCollectionParam.newBuilder()
                        .withDatabaseName(databaseName)
                        .withCollectionName(collectionName)
                        .build());
    }
    
    public R<RpcStatus> releaseCollection(String databaseName, String collectionName) {
        return this.milvusServiceClient.releaseCollection(
                ReleaseCollectionParam.newBuilder()
                        .withDatabaseName(databaseName)
                        .withCollectionName(collectionName)
                        .build());
    }

    public R<MutationResult> insert(String databaseName, String collectionName, String partitionName, List<Field> fields) {
        return this.milvusServiceClient.insert(InsertParam.newBuilder()
                .withDatabaseName(databaseName)
                .withCollectionName(collectionName)
                .withPartitionName(partitionName)
                .withFields(fields)
                .build());
    }

    public R<SearchResults> search(String databaseName, String collectionName, List<String> partitionNames, ConsistencyLevelEnum consistencyLevel, MetricType metricType, List<ByteBuffer> vectors, List<String> outFields, Integer topK, String expression) {
        return this.milvusServiceClient.search(SearchParam.newBuilder()
                .withDatabaseName(databaseName)
                .withCollectionName(collectionName)
                .withPartitionNames(partitionNames)
                .withConsistencyLevel(consistencyLevel)
                .withMetricType(metricType)
                .withBinaryVectors(vectors)
                .withOutFields(outFields)
                .withTopK(topK)
                .withExpr(expression)
                .build());
    }

}
