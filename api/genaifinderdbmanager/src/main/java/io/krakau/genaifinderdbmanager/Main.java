package io.krakau.genaifinderdbmanager;

import io.krakau.genaifinderdbmanager.service.ImportService;
import java.io.File;
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
        
//        File f = new File("ai-images/chatgpt.com/Zerstörtes Haus in Berlin.png");
//        System.out.println(f.getName());

        importService.importData();
        
//        databaseManager.create();
//        databaseManager.drop();
    }
    
}