package io.krakau.genaifinderapi.component;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 *
 * @author Dominik
 */
@Component
public class VectorConverter {
    
    public VectorConverter() {
    
    }   
    
    public static List<ByteBuffer> buildSearchVectors(List<String> searchVectors) {
        
        List<ByteBuffer> binarySearchVectors = new ArrayList<>();
        
        for(String vector : searchVectors) {
            binarySearchVectors.add(buildSearchVector64(vector));
        }
        
        return binarySearchVectors;
    }
    
    public static ByteBuffer buildSearchVector64(String vectorString) {
        
        byte[] bytes = new byte[8];
        int byteIndex = 0;
        
        int i = 0;
        while(i < vectorString.length()) {
            bytes[byteIndex] = (byte) Short.parseShort(vectorString.substring(i, i + 8), 2);
            byteIndex++;
            i = i + 8;
        }
        
        return ByteBuffer.allocate(8).put(bytes);
    }
    
    public static JSONArray getFeatures(JSONObject iscc) {
        return ((JSONObject) iscc.getJSONArray("features").get(0)).getJSONArray("features");
    }

    public static String getFeatureAsBinaryString(String feature) throws UnsupportedEncodingException {

        String binaryString = "";

        byte[] bytes = Base64.getUrlDecoder().decode(feature); // using getDecoder -> error

        for (int i = 0; i < bytes.length; i++) {
            binaryString = binaryString + String.format("%8s", Integer.toBinaryString(bytes[i] & 0xFF)).replace(' ', '0');
        }

//        System.out.println(binaryString);
        return binaryString.equals("") ? null : binaryString;
    }

    public static String getUintBits(JSONObject unit) {
        return unit.getString("hash_bits");
    }
    
    public static String toString(byte[] bytes) {
        String result = "";
        for(byte b : bytes) {
            result = result + b + ", ";
        }
        return result;
    }
    
}
