package io.krakau.genaifinderapi.service;

import io.krakau.genaifinderapi.component.MimeTypes;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Dominik
 */
@Service
public class DownloadService {
    
    private static Logger logger = Logger.getLogger(DownloadService.class.getName());
    
    private MimeTypes mimeTypes;
    
    @Autowired
    public DownloadService(MimeTypes mimeTypes) {
        this.mimeTypes = mimeTypes;
    }

    private String getFilename(String line) {
        String fileName = null;

//        System.out.println("MAIN LINE = " + line);
        if (line.contains("filename*=UTF-8\"")) {
            String keyName = line.substring(0, 16);
            if (keyName.equals("filename*=UTF-8\"")) {
                fileName = line.substring(16, line.length());
                fileName = fileName.replaceAll("\"", "");
            }
        } else {
            if (line.contains("filename=")) {
                String keyName = line.substring(0, 9);
//                System.out.println("keyNAME " + keyName);
                if (keyName.equals("filename=")) {
//                    System.out.println("TEST line = " + line);
                    fileName = line.substring(9, line.length());
//                    System.out.println("TEST F = " + fileName);
                    fileName = fileName.replaceAll("\"", "");

                }
            }
        }

        if (fileName != null) {
//            System.out.println("FILENAME b ->  " + fileName);
            fileName = fileName.replaceAll("%20", " ");
//            System.out.println("FILENAME a ->  " + fileName);
        }

        return fileName;
    }

    public File download(String sourceUrl) throws MalformedURLException, IOException, Exception {
        
        logger.log(Level.INFO, "Source URL: " + sourceUrl);

        File file = null;

//        logger.log(Level.INFO, "Downloading: " + fileURL);
        String saveFilePath = null;

        URL url = new URL(sourceUrl);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setDoOutput(true);
//        httpConn.setConnectTimeout(180000);
        httpConn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");
        int responseCode = httpConn.getResponseCode();
        String responseMessage = httpConn.getResponseMessage();

        // always check HTTP response code first
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String contentType = httpConn.getHeaderField("content-type");
            String fileName = "" + Instant.now().toEpochMilli();   
            String fileExtension = this.mimeTypes.getDefaultExt(contentType);
            saveFilePath = fileName + "." + fileExtension;

            InputStream inputStream = httpConn.getInputStream();

            // opens an output stream to save into file
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);

            int bytesRead = -1;
            byte[] buffer = new byte[2048];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

            file = new File(saveFilePath);
            if (file.length() == 0) {
                file.delete();
                file = null;
            }

        } else {
//            System.out.println("No file to download. Server replied HTTP code: " + responseCode + ", " + responseMessage);
            throw new Exception("download:" + responseCode + ":" + responseMessage);

        }
        httpConn.disconnect();
        
        logger.log(Level.INFO, "Downloaded file: " + file.getAbsolutePath());

        return file;
    }

    public void deleteFile(File file) {
        if (file != null && file.exists()) {
            file.delete();
        } else {
            logger.log(Level.INFO, "No file named " + file.getName() + " found.");
        }
    }

}
