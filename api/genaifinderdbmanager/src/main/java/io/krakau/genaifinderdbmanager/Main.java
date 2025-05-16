package io.krakau.genaifinderdbmanager;

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
        
        databaseManager = new DatabaseManager();
        importService = new ImportService("ai-images/data-images.json"); 
        
        importService.importData();
        
//        databaseManager.create();
//        databaseManager.drop();
    }
    
}