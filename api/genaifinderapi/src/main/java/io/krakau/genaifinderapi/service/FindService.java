package io.krakau.genaifinderapi.service;

import io.krakau.genaifinderapi.component.EnviromentVariables;
import io.krakau.genaifinderapi.component.VectorConverter;
import io.krakau.genaifinderapi.schema.iscc.ExplainedISCC;
import io.krakau.genaifinderapi.schema.mongodb.Asset;
import io.krakau.genaifinderapi.schema.mongodb.IsccData;
import io.krakau.genaifinderapi.schema.mongodb.Metadata;
import io.krakau.genaifinderapi.schema.mongodb.Provider;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.grpc.SearchResults;
import io.milvus.param.MetricType;
import io.milvus.param.R;
import io.milvus.response.SearchResultsWrapper;
import io.milvus.response.SearchResultsWrapper.IDScore;
import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

    private EnviromentVariables env;
    
    private AssetService assetService;
    private DownloadService downloadService;
    private IsccWebService isccWebService;
    private VectorConverter vectorConverter;
    private MilvusService milvusService;

    @Autowired
    public FindService(
            EnviromentVariables env,
            AssetService assetService,
            DownloadService downloadService,
            IsccWebService isccWebService,
            VectorConverter vectorConverter,
            MilvusService milvusService
    ) {
        this.env = env;
        this.assetService = assetService;
        this.downloadService = downloadService;
        this.isccWebService = isccWebService;
        this.vectorConverter = vectorConverter;
        this.milvusService = milvusService;
    }

    public List<Asset> findImage(String url) {

        Instant instant = Instant.now();
        Long timestamp = instant.toEpochMilli();

        List<Asset> assets = new ArrayList<>();
        List<Asset> foundAssets = new ArrayList<>();
        
        File downloadedFile = null;
        Document iscc = null;
        ExplainedISCC explainedISCC = null;

        try {
            // 1. Download asset by url link
            downloadedFile = this.downloadService.download(url);
            // 2. Send asset to iscc-web to create iscc
            iscc = this.isccWebService.createISCC(new FileInputStream(downloadedFile), downloadedFile.getName());
            // 3. Send iscc to iscc-web to explain iscc
            explainedISCC = this.isccWebService.explainISCC(iscc.getString("iscc"));
            // 4. Delete downloaded file
            downloadedFile.delete();
            // 5. Nearest neighbour vector search for content unit on assets iscc code
            List<ByteBuffer> searchVector = Arrays.asList(this.vectorConverter.buildSearchVector64(explainedISCC.getUnits().get(1).getHash_bits()));
            List<String> outFields = Arrays.asList(env.MILVUS_COLLECTION_FIELD_NNSID);
            R<SearchResults> nnsResponse = this.milvusService.search(
                    env.MILVUS_DATABASE,
                    env.MILVUS_COLLECTION_UNIT_CONTENT,
                    Arrays.asList(env.MILVUS_PARTITION_IMAGE),
                    ConsistencyLevelEnum.BOUNDED,
                    MetricType.HAMMING,
                    searchVector,
                    env.MILVUS_COLLECTION_FIELD_VECTOR,
                    outFields,
                    Integer.parseInt(env.MILVUS_TOP_K),
                    //Integer.parseInt(GenaifinderapiApplication.env.getProperty("spring.data.milvus.topK")),
                    "");
            // 6. Get response from vector search
            SearchResultsWrapper nnsReponseWrapper = new SearchResultsWrapper(nnsResponse.getData().getResults());
            // 7. Map nnsIds to distance
            HashMap<Long, Float> nnsResultMap = new HashMap<>();
            List<Long> fieldDataNnsId = (List<Long>) nnsReponseWrapper.getFieldData(env.MILVUS_COLLECTION_FIELD_NNSID, 0);
            List<IDScore> idScores = nnsReponseWrapper.getIDScore(0);
            for (int i = 0; i < idScores.size() && (idScores.get(i).getScore() <= Float.parseFloat(env.MILVUS_DISTANCE)); i++) { // distance filter
                Long nnsId = fieldDataNnsId.get(i);
                Float distance = idScores.get(i).getScore();
                nnsResultMap.put(nnsId, distance);
                Logger.getLogger(FindService.class.getName()).log(Level.INFO, "nnsId: " + nnsId + ", " + "distance: " + distance);
            }
            Logger.getLogger(FindService.class.getName()).log(Level.INFO, nnsResultMap.toString());
            Logger.getLogger(FindService.class.getName()).log(Level.INFO, "Found " + nnsResultMap.size() + " nnsIds in milvus with distance " + env.MILVUS_DISTANCE);
            // 8. Find assets by nnsIds from mongodb
            List<Long> nnsIds = new ArrayList<>();
            nnsIds.addAll(nnsResultMap.keySet());
            foundAssets = this.assetService.findByNnsId(nnsIds);
            Logger.getLogger(FindService.class.getName()).log(Level.INFO, "Found " + foundAssets.size() + " assets in mongodb");
            // 9. Add distance to assets
            for(Asset asset : assets) {
                asset.setDistance(nnsResultMap.get(asset.getNnsId()));
            }
            // 10. Create asset for url
            URI uri = new URI(url);
            Asset assetForUrl = new Asset(
                    new Metadata(
                            new Provider(
                                    uri.getHost(),
                                    null,
                                    timestamp,
                                    null
                            ),
                            new IsccData(
                                    iscc,
                                    explainedISCC
                            )
                    ),
                    null);
            // 11. Add url asset and found assets to assets
            assets.add(assetForUrl);
            assets.addAll(foundAssets);
        } catch (Exception ex) {
            Logger.getLogger(FindService.class.getName()).log(Level.SEVERE, null, ex);
        }
        // 12. Return assets
        return assets;
    }

}
