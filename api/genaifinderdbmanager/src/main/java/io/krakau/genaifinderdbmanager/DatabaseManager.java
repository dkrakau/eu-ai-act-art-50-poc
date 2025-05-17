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
    
    public void help() {
        System.out.println("Usage: DatabaseManager <options>");
        System.out.println("  -c, --create\t\tCreate milvus vector and mongodb database");
        System.out.println("  -d, --drop\t\tDrop milvus vector and mongodb database");
        System.out.println("  -i, --import\t\tImport data into milvus vector and mongodb database using http post requests");
        System.out.println("  -s, --stats\t\tShow information of milvus and mongodb database");
        System.out.println("  -h, --help\t\tShow help information of DatabaseManager");
    }

}
