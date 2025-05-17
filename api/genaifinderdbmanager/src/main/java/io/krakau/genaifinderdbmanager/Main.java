package io.krakau.genaifinderdbmanager;

import io.krakau.genaifinderdbmanager.config.EnvironmentVariables;
import io.krakau.genaifinderdbmanager.service.ImportService;
import java.io.IOException;

/**
 *
 * @author Dominik
 */
public class Main {

    private static final String APPLICATION_PROPOERTIES = "application.properties";
    private static DatabaseManager databaseManager;
    private static ImportService importService;

    public static void main(String[] args) throws IOException {
        // ###########################  PRODUCTION  ############################
        EnvironmentVariables env = new EnvironmentVariables(APPLICATION_PROPOERTIES);
        databaseManager = new DatabaseManager(env);
        initArgsReader(args);
        // ##########################  DEVELOPMENT  ############################
//        EnvironmentVariables env = new EnvironmentVariables(APPLICATION_PROPOERTIES);
//        databaseManager = new DatabaseManager(env);
//        databaseManager.create();
//        databaseManager.drop();
//        importService = new ImportService(env, "ai-images/data-images.json");
//        importService.importData();
        // #####################################################################
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
                    importService = new ImportService(new EnvironmentVariables(APPLICATION_PROPOERTIES), filePath);
                    importService.importData();
                } else {
                    System.out.println("Path to file is missing. See help.");
                    databaseManager.help();
                }
            } else if (args[0].equals("-s") || args[0].equals("--stats")) {
                databaseManager.stats();
            } else if (args[0].equals("-h") || args[0].equals("--help")) {
                databaseManager.help();
            } else {
                System.out.println("Wrong arguments are used. See help.");
                databaseManager.help();
            }
        } else {
            System.out.println("No arguments provided. See help.");
            databaseManager.help();
        }
    }
}
