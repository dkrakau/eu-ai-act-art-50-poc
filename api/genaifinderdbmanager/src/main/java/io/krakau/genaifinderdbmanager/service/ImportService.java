package io.krakau.genaifinderdbmanager.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Dominik
 */
public class ImportService {

    private JSONArray assets;

    public ImportService(String file) throws IOException {
        this.assets = readData(file);
    }

    public void importData() throws FileNotFoundException, IOException {
        for (int i = 0; i < assets.length(); i++) {
            JSONObject asset = (JSONObject) assets.get(i);
            System.out.println(sendCreateImageRequest(asset));
        }
    }

    private String sendCreateImageRequest(JSONObject asset) throws FileNotFoundException, IOException {
        // Get values from JSONObejct
        String provider = asset.getString("provider");
        String prompt = asset.getString("prompt");
        Long timestamp = asset.getLong("timestamp");
        File file = new File(asset.getString("file"));
        String requestParams = "?provider=" + provider + "&prompt=" + prompt + "&timestamp=" + timestamp;
        // Send create image request with post to api
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost uploadFile = new HttpPost("http://localhost/create/image" + requestParams);
        
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        // This attaches the file to the POST:
        builder.addBinaryBody(
                "file",
                new FileInputStream(file)
        );

        HttpEntity multipart = builder.build();
        uploadFile.setEntity(multipart);
        CloseableHttpResponse response = httpClient.execute(uploadFile);
        HttpEntity responseEntity = response.getEntity();
        return provider + prompt + timestamp + file.getName();
    }

    private JSONArray readData(String filePath) throws FileNotFoundException, IOException {
        return new JSONArray(new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.ISO_8859_1));
    }
}
