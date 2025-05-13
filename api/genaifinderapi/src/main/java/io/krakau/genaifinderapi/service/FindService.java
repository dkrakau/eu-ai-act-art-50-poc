package io.krakau.genaifinderapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Dominik
 */
@Service
public class FindService {
    
    private String url;
    
    private AssetService assetService;
    private DownloadService downloadService;
    private IsccWebService isccWebService;
    
    @Autowired
    public FindService(
            AssetService assetService,
            DownloadService downloadService,
            IsccWebService isccWebService
    ) {
        this.url = null;
        this.assetService = assetService;
        this.downloadService = downloadService;
        this.isccWebService = isccWebService;
    }
    
    public String findImage(String url) {
        
        this.url = url;
        
        /*
            1. Download asset by url link
            2. Send asset to iscc-web to create iscc
            3. Send iscc to iscc-web to explain iscc
            3. Nearest neighbour search for content unit on assets iscc code
            4. Decrypt encryptedMessage with publicKey
            5. Compare message and decryptedMessage
            6. Return List of assets
        */
        
        return this.url;
    }
    
}
