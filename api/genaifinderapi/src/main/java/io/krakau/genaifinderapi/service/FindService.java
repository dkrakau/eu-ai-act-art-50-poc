package io.krakau.genaifinderapi.service;

import io.krakau.genaifinderapi.GenaifinderapiApplication;
import io.krakau.genaifinderapi.component.VectorConverter;
import io.krakau.genaifinderapi.schema.iscc.ExplainedISCC;
import io.krakau.genaifinderapi.schema.mongodb.Asset;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.grpc.SearchResults;
import io.milvus.param.MetricType;
import io.milvus.param.R;
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Dominik
 */
@Service
public class FindService {

    private AssetService assetService;
    private DownloadService downloadService;
    private IsccWebService isccWebService;
    private VectorConverter vectorConverter;
    private MilvusService milvusService;

    @Autowired
    public FindService(
            AssetService assetService,
            DownloadService downloadService,
            IsccWebService isccWebService,
            VectorConverter vectorConverter,
            MilvusService milvusService
    ) {
        this.assetService = assetService;
        this.downloadService = downloadService;
        this.isccWebService = isccWebService;
        this.vectorConverter = vectorConverter;
        this.milvusService = milvusService;
    }

    public List<Asset> findImage(String url) {
        
        List<Asset> assets = new ArrayList<>();

        File downloadedFile = null;
        Document iscc = null;
        ExplainedISCC explainedIscc = null;

        try {
            // 1. Download asset by url link
            downloadedFile = this.downloadService.download(url);
            // 2. Send asset to iscc-web to create iscc
            iscc = this.isccWebService.createISCC(new FileInputStream(downloadedFile), downloadedFile.getName());
            // 3. Send iscc to iscc-web to explain iscc
            explainedIscc = this.isccWebService.explainISCC(iscc.getString("iscc"));
            // 3. Nearest neighbour search for content unit on assets iscc code
            List<ByteBuffer> vector = Arrays.asList(this.vectorConverter.buildSearchVector64(explainedIscc.getUnits().get(1).getHash_bits()));
            List<String> outFields = Arrays.asList("nnsId");
            R<SearchResults> nnsResult = this.milvusService.search(
                    GenaifinderapiApplication.env.getProperty("spring.data.milvus.database"),
                    GenaifinderapiApplication.env.getProperty("spring.data.milvus.collection.name.units.content"),
                    Arrays.asList(GenaifinderapiApplication.env.getProperty("spring.data.milvus.partition.name.image")),
                    ConsistencyLevelEnum.BOUNDED,
                    MetricType.HAMMING,
                    vector,
                    outFields,
                    Integer.parseInt(GenaifinderapiApplication.env.getProperty("spring.data.milvus.topK")),
                    "");
            // 4. Get nnsIds from search result
            // 5. Sort by distance < 16
            // 6. Return List of assets by nnsIds

        } catch (Exception ex) {
            Logger.getLogger(FindService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return assets;
    }

}
