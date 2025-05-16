package io.krakau.genaifinderdbmanager.service;

import java.io.FileReader;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author Dominik
 */
public class ImportService {

    public ImportService() {

    }

    public String getData() {

        JSONParser parser = new JSONParser();
        JSONArray jsonArray = null;
        try {
            jsonArray = (JSONArray) parser.parse(new FileReader("ai-images/data-images.json"));
        } catch (Exception e) {
            System.out.println(e);
        }

        return jsonArray.toJSONString();
    }

}
