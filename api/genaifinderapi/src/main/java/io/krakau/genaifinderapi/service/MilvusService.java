package io.krakau.genaifinderapi.service;

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
import io.milvus.param.dml.SearchParam;
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
    }

    public R<MutationResult> insertIntoImageCollection() {
        return this.milvusServiceClient.insert(InsertParam.newBuilder()
                .withDatabaseName("default")
                .withCollectionName("test")
                .withFields(null)
                .withRows(null)
                .build());
    }

    public R<SearchResults> searchOnImageCollection() {
        return this.milvusServiceClient.search(SearchParam.newBuilder()
                .withDatabaseName("default")
                .withCollectionName("test")
                .withConsistencyLevel(ConsistencyLevelEnum.BOUNDED)
                .withMetricType(MetricType.HAMMING)
                .withBinaryVectors(null)
                .withOutFields(null)
                .withTopK(100)
//                .withExpr("")
                .build());
    }

    public R<RpcStatus> loadImageCollection() {
        return this.milvusServiceClient.loadCollection(
                LoadCollectionParam.newBuilder()
                        .withDatabaseName("default")
                        .withCollectionName("test")
                        .build());
    }

    public R<RpcStatus> releaseImageCollection() {
        return this.milvusServiceClient.releaseCollection(
                ReleaseCollectionParam.newBuilder()
                        .withDatabaseName("default")
                        .withCollectionName("test")
                        .build());
    }
}
