package io.krakau.genaifinderapi.service;

import io.krakau.genaifinderapi.GenaifinderapiApplication;
import io.krakau.genaifinderapi.component.Cryptographer;
import io.krakau.genaifinderapi.component.Snowflaker;
import io.krakau.genaifinderapi.component.VectorConverter;
import io.krakau.genaifinderapi.schema.dto.ProviderDto;
import io.krakau.genaifinderapi.schema.iscc.ExplainedISCC;
import io.krakau.genaifinderapi.schema.mongodb.Asset;
import io.krakau.genaifinderapi.schema.mongodb.IsccData;
import io.krakau.genaifinderapi.schema.mongodb.Metadata;
import io.krakau.genaifinderapi.schema.mongodb.Provider;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.InsertParam.Field;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    private Snowflaker snowflaker;

    @Autowired
    public CreateService(
            AssetService assetService,
            IsccWebService isccWebService,
            VectorConverter vectorConverter,
            MilvusService milvusService,
            Cryptographer cryptographer,
            Snowflaker snowflaker
    ) {
        this.assetService = assetService;
        this.isccWebService = isccWebService;
        this.vectorConverter = vectorConverter;
        this.milvusService = milvusService;
        this.cryptographer = cryptographer;
        this.snowflaker = snowflaker;
    }

    public Asset createImage(ProviderDto provider, MultipartFile imageFile) {

        Asset asset = null;
        Document iscc = null;
        ExplainedISCC explainedISCC = null;
        Long snowflakeId = this.snowflaker.id();

        try {
            // 1. Send image to iscc-web to create iscc
            iscc = this.isccWebService.createISCC(imageFile.getInputStream(), imageFile.getName());
            // 2. Send iscc to iscc-web to explain iscc
            explainedISCC = this.isccWebService.explainISCC(iscc.getString("iscc"));
            // 3. Insert units to milvus collection
            List<Field> metaFields = new ArrayList<>();
            metaFields.add(new InsertParam.Field("vector", Arrays.asList(this.vectorConverter.buildSearchVector64(explainedISCC.getUnits().get(0).getHash_bits()))));
            metaFields.add(new InsertParam.Field("nnsId", Arrays.asList(snowflakeId)));
            this.milvusService.insert(
                    GenaifinderapiApplication.env.getProperty("spring.data.milvus.database"),
                    GenaifinderapiApplication.env.getProperty("spring.data.milvus.collection.name.units.meta"),
                    GenaifinderapiApplication.env.getProperty("spring.data.milvus.partition.name.image"),
                    metaFields);
            
            List<Field> contentFields = new ArrayList<>();
            contentFields.add(new InsertParam.Field("vector", Arrays.asList(this.vectorConverter.buildSearchVector64(explainedISCC.getUnits().get(1).getHash_bits()))));
            contentFields.add(new InsertParam.Field("nnsId", Arrays.asList(snowflakeId)));
            this.milvusService.insert(
                    GenaifinderapiApplication.env.getProperty("spring.data.milvus.database"),
                    GenaifinderapiApplication.env.getProperty("spring.data.milvus.collection.name.units.content"),
                    GenaifinderapiApplication.env.getProperty("spring.data.milvus.partition.name.image"),
                    contentFields);
            
            List<Field> dataFields = new ArrayList<>();
            dataFields.add(new InsertParam.Field("vector", Arrays.asList(this.vectorConverter.buildSearchVector64(explainedISCC.getUnits().get(2).getHash_bits()))));
            dataFields.add(new InsertParam.Field("nnsId", Arrays.asList(snowflakeId)));
            this.milvusService.insert(
                    GenaifinderapiApplication.env.getProperty("spring.data.milvus.database"),
                    GenaifinderapiApplication.env.getProperty("spring.data.milvus.collection.name.units.data"),
                    GenaifinderapiApplication.env.getProperty("spring.data.milvus.partition.name.image"),
                    dataFields);
            
            List<Field> instanceFields = new ArrayList<>();
            instanceFields.add(new InsertParam.Field("vector", Arrays.asList(this.vectorConverter.buildSearchVector64(explainedISCC.getUnits().get(3).getHash_bits()))));
            instanceFields.add(new InsertParam.Field("nnsId", Arrays.asList(snowflakeId)));
            this.milvusService.insert(
                    GenaifinderapiApplication.env.getProperty("spring.data.milvus.database"),
                    GenaifinderapiApplication.env.getProperty("spring.data.milvus.collection.name.units.instance"),
                    GenaifinderapiApplication.env.getProperty("spring.data.milvus.partition.name.image"),
                    instanceFields);
            // 4. Append cryptographic credentials to asset and insert asset into mongodb
            asset = new Asset(
                    new Metadata(
                            new Provider(
                                    provider.getName(),
                                    provider.getPrompt(),
                                    provider.getTimestamp(),
                                    this.cryptographer.getCredentials( 
                                            provider.getName(),
                                            provider.getName() + "-" + iscc.getString("iscc") + "-" + provider.getTimestamp()
                                    )
                            ),
                            new IsccData(
                                    iscc,
                                    explainedISCC
                            )
                    ),
                    snowflakeId);
            // 5. Insert asset into mongodb
            this.assetService.insert(asset);

        } catch (IOException ioe) {
            Logger.getLogger(CreateService.class.getName()).log(Level.SEVERE, null, ioe);
        } catch (Exception ex) {
            Logger.getLogger(CreateService.class.getName()).log(Level.SEVERE, null, ex);
        }
        // 6. Return asset that was inserted into mongodb
        return asset;
    }

}
