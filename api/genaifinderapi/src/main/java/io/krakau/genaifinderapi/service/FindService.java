package io.krakau.genaifinderapi.service;

import io.krakau.genaifinderapi.component.Cryptographer;
import io.krakau.genaifinderapi.component.EnvironmentVariables;
import io.krakau.genaifinderapi.component.VectorConverter;
import io.krakau.genaifinderapi.schema.iscc.ExplainedISCC;
import io.krakau.genaifinderapi.schema.mongodb.Asset;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.grpc.SearchResults;
import io.milvus.param.MetricType;
import io.milvus.param.R;
import io.milvus.response.SearchResultsWrapper;
import io.milvus.response.SearchResultsWrapper.IDScore;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Dominik
 */
@Service
public class FindService {

    private static Logger logger = Logger.getLogger(FindService.class.getName());

    private EnvironmentVariables env;

    private AssetService assetService;
    private IsccWebService isccWebService;
    private VectorConverter vectorConverter;
    private MilvusService milvusService;
    private Cryptographer cryptographer;

    @Autowired
    public FindService(
            EnvironmentVariables env,
            AssetService assetService,
            IsccWebService isccWebService,
            VectorConverter vectorConverter,
            MilvusService milvusService,
            Cryptographer cryptographer
    ) {
        this.env = env;
        this.assetService = assetService;
        this.isccWebService = isccWebService;
        this.vectorConverter = vectorConverter;
        this.milvusService = milvusService;
        this.cryptographer = cryptographer;
    }

    public List<Asset> findImage(String isccString) {

        List<Asset> sortedFoundAssets = new ArrayList<>();
        List<Asset> foundAssets = new ArrayList<>();

        ExplainedISCC explainedISCC = null;

        try {
            // 1. Send iscc to iscc-web to explain iscc
            explainedISCC = this.isccWebService.explainISCC(isccString);
            // 2. Nearest neighbour vector search for content unit on assets iscc code
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
                    "");
            // 3. Get response from vector search
            SearchResultsWrapper nnsReponseWrapper = new SearchResultsWrapper(nnsResponse.getData().getResults());
            // 4. Map nnsIds to distance
            HashMap<Long, Float> nnsResultMap = new HashMap<>();
            List<Long> fieldDataNnsId = (List<Long>) nnsReponseWrapper.getFieldData(env.MILVUS_COLLECTION_FIELD_NNSID, 0);
            List<IDScore> idScores = nnsReponseWrapper.getIDScore(0);
            for (int i = 0; i < idScores.size() && (idScores.get(i).getScore() <= Float.parseFloat(env.MILVUS_DISTANCE)); i++) { // distance filter
                Long nnsId = fieldDataNnsId.get(i);
                Float distance = idScores.get(i).getScore();
                nnsResultMap.put(nnsId, distance);
                logger.log(Level.INFO, "nnsId: " + nnsId + ", " + "distance: " + distance);
            }
            logger.log(Level.INFO, nnsResultMap.toString());
            logger.log(Level.INFO, "Found " + nnsResultMap.size() + " nnsIds in milvus with distance " + env.MILVUS_DISTANCE);
            // 5. Find assets by nnsIds from mongodb
            List<Long> nnsIds = new ArrayList<>();
            nnsIds.addAll(nnsResultMap.keySet());
            foundAssets = this.assetService.findByNnsId(nnsIds);
            logger.log(Level.INFO, "Found " + foundAssets.size() + " assets in mongodb");
            // 6. Add distance to found assets
            for (Asset asset : foundAssets) {
                asset.setDistance(nnsResultMap.get(asset.getNnsId()).intValue());
            }
            // 7. Sort found assets by distance
            sortedFoundAssets = foundAssets.stream()
                    .sorted(Comparator.comparingInt(Asset::getDistance))
                    .collect(Collectors.toList());
            // 8. Add credentials to assets
            for (Asset asset : sortedFoundAssets) {
                asset
                        .getMetadata()
                        .getProvider()
                        .setCredentials(
                                this.cryptographer.getCredentials(
                                        asset.getMetadata().getProvider().getName(),
                                        asset.getMetadata().getProvider().getName() + "-" + asset.getMetadata().getIscc().getData().getIscc() + "-" + asset.getMetadata().getProvider().getTimestamp()
                                ));
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        // 9. Return assets
        return sortedFoundAssets;
    }

}
