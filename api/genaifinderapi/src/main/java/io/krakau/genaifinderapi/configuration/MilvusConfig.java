package io.krakau.genaifinderapi.configuration;

import io.krakau.genaifinderapi.GenaifinderapiApplication;
import io.milvus.client.MilvusServiceClient;
import io.milvus.param.ConnectParam;
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
                .withAuthorization(GenaifinderapiApplication.env.getProperty("spring.data.milvus.auth.username"), GenaifinderapiApplication.env.getProperty("spring.data.milvus.auth.password"))
                .withUri(GenaifinderapiApplication.env.getProperty("spring.data.milvus.uri"))
                .build());
    }

}
