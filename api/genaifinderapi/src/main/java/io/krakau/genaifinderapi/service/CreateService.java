package io.krakau.genaifinderapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Dominik
 */
@Service
public class CreateService {
    
    private String helloWorld;
    
    @Autowired
    public CreateService() {
        this.helloWorld = "Hello World!";
    }
    
    public String getHelloWorld() {
        return this.helloWorld;
    }
    
    public String createImage() {
        return "createImage";
    }
    
}
