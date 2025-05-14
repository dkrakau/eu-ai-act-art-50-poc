package io.krakau.genaifinderapi.service;

import io.krakau.genaifinderapi.component.Cryptographer;
import io.krakau.genaifinderapi.component.VectorConverter;
import io.krakau.genaifinderapi.schema.iscc.ExplainedISCC;
import io.milvus.param.R;
import io.milvus.param.RpcStatus;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Dominik
 */
@Service
public class CreateService {

    private AssetService assetService;
    private IsccWebService isccWebService;
    private VectorConverter vectorConverter;
    private MilvusService milvusService;
    private Cryptographer cryptographer;

    @Autowired
    public CreateService(
            AssetService assetService,
            IsccWebService isccWebService,
            VectorConverter vectorConverter,
            MilvusService milvusService,
            Cryptographer cryptographer
    ) {
        this.assetService = assetService;
        this.isccWebService = isccWebService;
        this.vectorConverter = vectorConverter;
        this.milvusService = milvusService;
        this.cryptographer = cryptographer;
    }

    public String createImage(MultipartFile imageFile, String prividerName, String prompt, Long timestamp) {

        Document iscc = null;
                
        try {
            //    1. Send image to iscc-web to create iscc
            iscc = this.isccWebService.createISCC(imageFile.getInputStream(), imageFile.getName());
            //    2. Send iscc to iscc-web to explain iscc
            ExplainedISCC explainedISCC = this.isccWebService.explainISCC(iscc.getString("iscc"));
           
            //    3. Insert units to milvus collection
            R<RpcStatus> status = this.milvusService.loadImageCollection();
            Logger.getLogger(CreateService.class.getName()).log(Level.INFO, status.toString());
            //    4. Encrypt privider.name + iscc + timestamp with private key
            Logger.getLogger(CreateService.class.getName()).log(Level.INFO, this.cryptographer.getCredentials("OpenAI", "test 1 test 2 test 3?").toString());
            //    5. Insert asset into mongodb
            //    6. Return asset that was inserted into mongodb
        } catch (IOException ioe) {
            Logger.getLogger(CreateService.class.getName()).log(Level.SEVERE, null, ioe);
        } catch (Exception ex) {
            Logger.getLogger(CreateService.class.getName()).log(Level.SEVERE, null, ex);
        }

        return iscc.toString();
    }

}
