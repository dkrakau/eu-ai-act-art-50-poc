package io.krakau.genaifinderdbmanager.service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoIterable;
import io.krakau.genaifinderdbmanager.config.EnvironmentVariables;

/**
 *
 * @author Dominik
 */
public class MongoService {

    private EnvironmentVariables env;
    private MongoClient mongoClient;

    public MongoService(EnvironmentVariables env) {
        this.env = env;
        this.mongoClient = MongoClients.create("mongodb://" + env.MONGODB_AUTH_USERNAME + ":" + env.MONGODB_AUTH_PASSWORD
                + "@" + env.MONGODB_HOST + ":" + env.MONGODB_PORT 
                + "/?authSource=" + env.MONGODB_AUTH_DB);
    }    

    public void createDatabse() {
        System.out.println("Mongo: Creating database ...");
        this.mongoClient.getDatabase(env.MONGODB_DATABASE);
        System.out.println("Mongo: Database created successfully.");
    }

    public void dropDatabase() {
        System.out.println("Mongo: Dropping database ...");
        this.mongoClient.getDatabase(env.MONGODB_DATABASE).drop();
        System.out.println("Mongo: Database dropped successfully.");
    }

    public void createCollection() {
        System.out.println("Mongo: Creating collection ...");
        this.mongoClient.getDatabase(env.MONGODB_DATABASE).createCollection(env.MONGODB_COLLECTION);
        System.out.println("Mongo: Collection created successfully.");
    }

    public void dropCollection() {
        System.out.println("Mongo: Dropping collection ...");
        this.mongoClient.getDatabase(env.MONGODB_DATABASE).getCollection(env.MONGODB_COLLECTION).drop();
        System.out.println("Mongo: Collection dropped successfully.");
    }
    
    public void getInfo() {
        System.out.println("########## Mongodb collections ##########");
        MongoIterable<String> collectionNames = this.mongoClient.getDatabase(env.MONGODB_DATABASE).listCollectionNames();
        System.out.println("Mongo: List of collections:");
        for (String name : collectionNames) {
            System.out.println(name);
        }
    }

}
