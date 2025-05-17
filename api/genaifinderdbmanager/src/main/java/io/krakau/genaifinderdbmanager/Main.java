package io.krakau.genaifinderdbmanager;

import io.krakau.genaifinderdbmanager.config.EnvironmentVariables;
import io.krakau.genaifinderdbmanager.service.ImportService;
import java.io.IOException;

/**
 *
 * @author Dominik
 */
public class Main {
    
    private static DatabaseManager databaseManager;
    private static ImportService importService;
    
    public static void main(String[] args) throws IOException {
        
        EnvironmentVariables env = new EnvironmentVariables("applicatoin.properties");
        
        databaseManager = new DatabaseManager(env);
        importService = new ImportService(env, "ai-images/data-images.json"); 
       
        importService.importData();
        
//        databaseManager.create();
//        databaseManager.drop();
    }
    
    public static void printHelp() {
        System.out.println("Usage: DatabaseManager <options>");
        System.out.println("  -c, --create\t\tCreate milvus vector database");
        System.out.println("  -d, --drop\t\tDrop milvus vector database");
        System.out.println("  -i, --insert\t\tInserts entries into milvus vector database");
        System.out.println("  -s, --stats\t\tShow information of milvus vector database collections");
        System.out.println("      --stats <name>\tShow information of milvus vector database collection");
        System.out.println("  -h, --help\t\tShow help information of DatabaseManager");
    }
    
}