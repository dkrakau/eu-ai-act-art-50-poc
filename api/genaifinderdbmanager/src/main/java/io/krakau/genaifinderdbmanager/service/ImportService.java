package io.krakau.genaifinderdbmanager.service;

import io.krakau.genaifinderdbmanager.config.EnvironmentVariables;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Dominik
 */
public class ImportService {
    
    private EnvironmentVariables env;

    private String filePath;
    private JSONArray assets;

    public ImportService(EnvironmentVariables env, String filePath) throws IOException {
        this.env = env;
        this.filePath = filePath;
        this.assets = readData(filePath);
    }

    public void importData() throws FileNotFoundException, IOException {
        System.out.println("Importing image data from " + this.filePath + " ...");
        for (int i = 0; i < assets.length(); i++) {
            JSONObject asset = (JSONObject) assets.get(i);
            JSONObject responseJson = sendCreateImageRequest(asset);
            System.out.println("{ \"nnsId\" : " + responseJson.getLong("nnsId") + " }");
        }
    }

    private JSONObject sendCreateImageRequest(JSONObject asset) throws FileNotFoundException, IOException {
        // Get values from JSONObejct
        String provider = asset.getString("provider");
        String prompt = asset.getString("prompt");
        Long timestamp = asset.getLong("timestamp");
        Path path = Path.of(convertFileNameUTF8ToISO88591(asset.getString("file")));
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String requestParams = "?provider=" + provider + "&prompt=" + transformSpacesForUrl(prompt) + "&timestamp=" + timestamp;
        HttpPost createImagePostRequest = new HttpPost("http://localhost/create/image" + requestParams);
        // Attach file to POST request body
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addBinaryBody("file", path.toFile());
        HttpEntity multipart = builder.build();
        // Add muiltipart to POST request
        createImagePostRequest.setEntity(multipart);
        // Send POST request
        CloseableHttpResponse response = httpClient.execute(createImagePostRequest);
        // return response body as json obejct
        return new JSONObject(new String(response.getEntity().getContent().readAllBytes()));
    }

    private String transformSpacesForUrl(String s) {
        return s.replaceAll(" ", "%20");
    }

    private String convertFileNameUTF8ToISO88591(String fileName) {
        // Convert the UTF-16 Java String to ISO-8859-1 bytes
        byte[] filenameBytes = fileName.getBytes(StandardCharsets.ISO_8859_1);
        // Create a new String using the ISO-8859-1 bytes
        return new String(filenameBytes, StandardCharsets.UTF_8);
    }

    private JSONArray readData(String filePath) throws FileNotFoundException, IOException {
        return new JSONArray(new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.ISO_8859_1));
    }
}