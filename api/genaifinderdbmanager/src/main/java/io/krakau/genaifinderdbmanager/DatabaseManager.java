package io.krakau.genaifinderdbmanager;

import io.krakau.genaifinderdbmanager.config.EnvironmentVariables;
import io.krakau.genaifinderdbmanager.service.MilvusService;
import io.krakau.genaifinderdbmanager.service.MongoService;
import java.io.IOException;

/**
 *
 * @author Dominik
 */
public class DatabaseManager {

    private MongoService mongoService;
    private MilvusService milvusService;

    public DatabaseManager(EnvironmentVariables env) throws IOException {
        this.mongoService = new MongoService(env);
        this.milvusService = new MilvusService(env);
    }

    public void create() {
        this.milvusService.createDatabase();
        this.milvusService.createCollections();
        this.milvusService.createPartition();
        this.milvusService.createIndexes();
        
        this.mongoService.createDatabse();
        this.mongoService.createCollection();
    }

    public void drop() {
        this.milvusService.drop();
        
        this.mongoService.dropCollection();
        this.mongoService.dropDatabase();
    }

    public void stats() {
        this.milvusService.getInfo();
        this.mongoService.getInfo();
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
