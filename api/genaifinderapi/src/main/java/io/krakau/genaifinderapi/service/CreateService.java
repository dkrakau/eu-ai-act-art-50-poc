package io.krakau.genaifinderapi.service;

import java.io.IOException;
import java.io.InputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    
    public String createImage(MultipartFile imageFile, String prividerName, String prompt, Long timestamp) {
        
        
        
       
        //    1. Send image to iscc-web to create iscc
        
        //    2. Send iscc to iscc-web to explain iscc
        //    3. Insert units to milvus collection
        //    4. Encrypt privider.name + iscc + timestamp with private key
        //    5. Insert asset into mongodb
        //    6. Return asset that was inserted into mongodb
       
        
        
        return "createImage";
    }
    
}
