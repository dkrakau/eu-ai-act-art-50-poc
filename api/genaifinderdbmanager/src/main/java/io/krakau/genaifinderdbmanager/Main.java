package io.krakau.genaifinderdbmanager;

import java.io.IOException;

/**
 *
 * @author Dominik
 */
public class Main {
    
    private static DatabaseManager databaseManager;
    
    public static void main(String[] args) throws IOException {
        
        databaseManager = new DatabaseManager();
        
        databaseManager.create();
        
//        databaseManager.drop();
    }
    
}