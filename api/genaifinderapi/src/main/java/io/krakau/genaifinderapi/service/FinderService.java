package io.krakau.genaifinderapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Dominik
 */
@Service
public class FinderService {
    
    private String helloWorld;
    
    @Autowired
    public FinderService() {
        this.helloWorld = "Hello World!";
    }
    
    public String getHelloWorld() {
        return this.helloWorld;
    }
    
}
