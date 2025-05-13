package io.krakau.genaifinderapi.configuration;

import io.milvus.client.MilvusServiceClient;
import io.milvus.param.ConnectParam;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.TokenCountBatchingStrategy;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.milvus.MilvusVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author Dominik
 */
@Configuration
public class MilvusConfig {

    @Bean
    public MilvusServiceClient milvusClient() {
        return new MilvusServiceClient(ConnectParam.newBuilder()
                .withAuthorization("minioadmin", "minioadmin")
                .withUri("http://localhost:19530")
                .build());
    }

}
