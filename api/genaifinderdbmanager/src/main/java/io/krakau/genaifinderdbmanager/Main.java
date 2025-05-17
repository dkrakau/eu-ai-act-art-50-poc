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

        initArgsReader(args);

        // #####################################################################
        EnvironmentVariables env = new EnvironmentVariables("applicatoin.properties");
        databaseManager = new DatabaseManager(env);
        importService = new ImportService(env, "ai-images/data-images.json");

//        databaseManager.create();
//        databaseManager.drop();
        importService.importData();
    }

    public static void initArgsReader(String[] args) throws IOException {
        if (args.length > 0) {
            if (args[0].equals("-c") || args[0].equals("--create")) {
                databaseManager.create();
            } else if (args[0].equals("-d") || args[0].equals("--drop")) {
                databaseManager.drop();
            } else if (args[0].equals("-i") || args[0].equals("--import")) {
                if (args.length > 1) {
                    String filePath = args[1];
                    importService = new ImportService(new EnvironmentVariables("applicatoin.properties"), filePath);
                    importService.importData();
                } else {
                    System.out.println("Path to file is missing. See help.");
                    printHelp();
                }
            } else if (args[0].equals("-s") || args[0].equals("--stats")) {
               databaseManager.stats();
            } else if (args[0].equals("-h") || args[0].equals("--help")) {
                printHelp();
            } else {
                System.out.println("Wrong arguments are used. See help.");
                printHelp();
            }
        } else {
            System.out.println("No arguments provided. See help.");
            printHelp();
        }
    }

    public static void printHelp() {
        System.out.println("Usage: DatabaseManager <options>");
        System.out.println("  -c, --create\t\tCreate milvus vector and mongodb database");
        System.out.println("  -d, --drop\t\tDrop milvus vector and mongodb database");
        System.out.println("  -i, --import\t\tImport data into milvus vector and mongodb database using http post requests");
        System.out.println("  -s, --stats\t\tShow information of milvus and mongodb database");
        System.out.println("  -h, --help\t\tShow help information of DatabaseManager");
    }

}
