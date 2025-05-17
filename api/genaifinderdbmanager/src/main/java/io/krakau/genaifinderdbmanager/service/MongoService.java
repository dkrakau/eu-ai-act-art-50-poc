package io.krakau.genaifinderdbmanager.service;

import com.mongodb.MongoClient;
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
        this.mongoClient = new MongoClient(env.MONGODB_HOST, env.MONGODB_PORT);
    }    

    public void createDatabse() {
        this.mongoClient.getDatabase(env.MONGODB_DATABASE);
    }

    public void dropDatabase() {
        this.mongoClient.getDatabase(env.MONGODB_DATABASE).drop();
    }

    public void createCollection() {
        this.mongoClient.getDatabase(env.MONGODB_DATABASE).createCollection(env.MONGODB_COLLECTION);
    }

    public void dropCollection() {
        this.mongoClient.getDatabase(env.MONGODB_DATABASE).getCollection(env.MONGODB_COLLECTION).drop();
    }
    
    public void getInfo() {
        MongoIterable<String> collectionNames = this.mongoClient.getDatabase(env.MONGODB_DATABASE).listCollectionNames();
        System.out.println("List of collections:");
        for (String name : collectionNames) {
            System.out.println(name);
        }
    }

}
