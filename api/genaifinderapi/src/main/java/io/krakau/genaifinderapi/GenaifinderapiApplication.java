package io.krakau.genaifinderapi;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class GenaifinderapiApplication {

    public static Environment env;

    @Autowired
    public GenaifinderapiApplication(Environment env) {
        GenaifinderapiApplication.env = env;
    }

    public static void main(String[] args) {

        SpringApplication.run(GenaifinderapiApplication.class, args);

        Logger.getLogger(GenaifinderapiApplication.class.getName()).log(Level.INFO, "Mongo:\t\t\t" + env.getProperty("spring.data.mongodb.host") + ":" + env.getProperty("spring.data.mongodb.port"));
        Logger.getLogger(GenaifinderapiApplication.class.getName()).log(Level.INFO, "Milvus:\t\t\t" + env.getProperty("spring.data.milvus.host") + ":" + env.getProperty("spring.data.milvus.port")
                + ", distance: " + env.getProperty("spring.data.milvus.distance")
                + ", topK: " + env.getProperty("spring.data.milvus.topK")
                + ", nlist: " + env.getProperty("spring.data.milvus.nlist")
                + ", nprobe: " + env.getProperty("spring.data.milvus.nprobe"));
        Logger.getLogger(GenaifinderapiApplication.class.getName()).log(Level.INFO, "ISCC-Web create:\t" + env.getProperty("spring.api.iscc-web-create"));
        Logger.getLogger(GenaifinderapiApplication.class.getName()).log(Level.INFO, "ISCC-Web explain:\t" + env.getProperty("spring.api.iscc-web-explain"));
    }
}
