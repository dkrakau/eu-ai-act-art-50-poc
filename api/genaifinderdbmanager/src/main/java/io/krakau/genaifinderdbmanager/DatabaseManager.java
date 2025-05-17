package io.krakau.genaifinderdbmanager;

import io.krakau.genaifinderdbmanager.config.EnvironmentVariables;
import io.krakau.genaifinderdbmanager.service.MilvusService;
import java.io.IOException;

/**
 *
 * @author Dominik
 */
public class DatabaseManager {

    private EnvironmentVariables env;

    private MilvusService milvusService;

    public DatabaseManager(EnvironmentVariables env) throws IOException {
        this.env = env;
        this.milvusService = new MilvusService(env);
    }

    public void create() {
        this.milvusService.createDatabase();
        this.milvusService.createCollections();
        this.milvusService.createPartition();
        this.milvusService.createIndexes();
    }

    public void drop() {
        this.milvusService.drop();
    }

    public void stats() {
        this.milvusService.getInfo();
    }

}
