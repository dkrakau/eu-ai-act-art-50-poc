package io.krakau.genaifinderapi.service;

import io.krakau.genaifinderapi.component.EnvironmentVariables;
import io.krakau.genaifinderapi.schema.iscc.ExplainedISCC;
import io.krakau.genaifinderapi.schema.iscc.Unit;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Dominik
 */
@Service
public class IsccWebService {
    
    private static Logger logger = Logger.getLogger(IsccWebService.class.getName());
    
    private EnvironmentVariables env;
    
    @Autowired
    public IsccWebService(EnvironmentVariables env) {
        this.env = env;
    }
    
    
    public Document createISCC(InputStream binaryData, String filename) throws IOException, Exception {

        CloseableHttpClient client = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost(env.API_ISCCWEB_CREATE);
        httpPost.addHeader("accept", "application/json");
        httpPost.addHeader("Content-Type", "application/octet-stream");
        httpPost.addHeader("X-Upload-Filename", getBase64EncodedFilename(filename));
        HttpEntity entityRequest = new InputStreamEntity(binaryData, ContentType.APPLICATION_OCTET_STREAM);

        httpPost.setEntity(entityRequest);

        logger.log(Level.INFO, httpPost.getFirstHeader("X-Upload-Filename").toString() + " <- " + filename);

        String response = null;
        HttpResponse httpresponse = client.execute(httpPost); // still errors
        HttpEntity entityResponse = httpresponse.getEntity();

        Document iscc = null;
        if (httpresponse.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_CREATED) {
            response = EntityUtils.toString(entityResponse);
            EntityUtils.consume(httpresponse.getEntity());
            iscc = Document.parse(new JSONObject(response).toString());

        } else {
//            System.out.println("Error createISCC code -> " + httpresponse.getStatusLine().getStatusCode() + " " + httpresponse.getStatusLine().getReasonPhrase());
            throw new Exception("createISCC:" + httpresponse.getStatusLine().getStatusCode() + ":" + httpresponse.getStatusLine().getReasonPhrase());
        }

        client.close();
        
        logger.log(Level.INFO, "ISCC created: " + iscc.toString());

        return iscc;
    }

    public String getBase64EncodedFilename(String filename) {
        return Base64.getEncoder().encodeToString(filename.getBytes());
    }

    public ExplainedISCC explainISCC(String iscc) throws IOException, Exception {

        CloseableHttpClient client = HttpClients.createDefault();

        HttpGet httpGet = new HttpGet(env.API_ISCCWEB_EXPLAIN + "/" + iscc);

        String response = null;
        ExplainedISCC explainedISCC = null;
        CloseableHttpResponse httpresponse = null;

        httpresponse = client.execute(httpGet); // still errors
        int httpStatusCode = httpresponse.getStatusLine().getStatusCode();

        // always check HTTP response code first
        if (httpStatusCode == HttpURLConnection.HTTP_OK) {
            HttpEntity entityResponse = httpresponse.getEntity();

//            System.out.println(httpresponse.getStatusLine());
            response = EntityUtils.toString(entityResponse);
            EntityUtils.consume(httpresponse.getEntity());

            JSONObject json = new JSONObject(response);

            JSONArray unitsJson = json.getJSONArray("units");

            List<Unit> units = new ArrayList<>();
            for (int i = 0; i < unitsJson.length(); i++) {
                units.add(
                        new Unit(
                                unitsJson.getJSONObject(i).getString("readable"),
                                unitsJson.getJSONObject(i).getString("hash_hex"),
                                unitsJson.getJSONObject(i).getString("iscc_unit"),
                                unitsJson.getJSONObject(i).getString("hash_bits"),
                                unitsJson.getJSONObject(i).getString("hash_uint")
                        )
                );
            }

            explainedISCC = new ExplainedISCC(
                    json.getString("iscc"),
                    json.getString("readable"),
                    json.getString("multiformat"),
                    json.getString("decomposed"),
                    units);

        } else {
//            System.out.println("ISCC Explain ERROR: " + httpStatusCode);
            throw new Exception("explainISCC:" + httpStatusCode + " " + httpresponse.getStatusLine());
        }

        client.close();
        
        logger.log(Level.INFO, explainedISCC.toString());

        return explainedISCC;
    }
    
}
