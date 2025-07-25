package io.krakau.genaifinderapi.configuration;

import io.krakau.genaifinderapi.component.EnvironmentVariables;
import io.milvus.client.MilvusServiceClient;
import io.milvus.param.ConnectParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author Dominik
 */
@Configuration
public class MilvusConfig {

    private EnvironmentVariables env;
    
    @Autowired
    public MilvusConfig(EnvironmentVariables env) {
        this.env = env;
    }

    @Bean
    public MilvusServiceClient milvusClient() {
        return new MilvusServiceClient(ConnectParam.newBuilder()
                .withAuthorization(env.MILVUS_AUTH_USERNAME, env.MILVUS_AUTH_PASSWORD)
                .withUri(env.MILVUS_URL)
                .build());
    }

}
