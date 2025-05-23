package io.krakau.genaifinderapi;

import io.krakau.genaifinderapi.component.EnvironmentVariables;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GenaifinderapiApplication {

    public static EnvironmentVariables env;

    @Autowired
    public GenaifinderapiApplication(EnvironmentVariables env) {
        GenaifinderapiApplication.env = env;
    }

    public static void main(String[] args) {

        SpringApplication.run(GenaifinderapiApplication.class, args);

        Logger.getLogger(GenaifinderapiApplication.class.getName()).log(Level.INFO, "Mongo:\t\t" + env.MONGODB_HOST + ":" + env.MONGODB_PORT);
        Logger.getLogger(GenaifinderapiApplication.class.getName()).log(Level.INFO, "Milvus:\t\t" + env.MILVUS_URL
                + ", distance: " + env.MILVUS_DISTANCE
                + ", topK: " + env.MILVUS_TOP_K
                + ", nlist: " + env.MILVUS_NLIST
                + ", nprobe: " + env.MILVUS_NPROBE);
        Logger.getLogger(GenaifinderapiApplication.class.getName()).log(Level.INFO, "ISCC-Web create:\t" + env.API_ISCCWEB_CREATE);
        Logger.getLogger(GenaifinderapiApplication.class.getName()).log(Level.INFO, "ISCC-Web explain:\t" + env.API_ISCCWEB_EXPLAIN);
    }
}
