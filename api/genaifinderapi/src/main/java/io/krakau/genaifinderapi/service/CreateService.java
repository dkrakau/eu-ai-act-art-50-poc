package io.krakau.genaifinderapi.service;

import io.krakau.genaifinderapi.component.Cryptographer;
import io.krakau.genaifinderapi.component.EnvironmentVariables;
import io.krakau.genaifinderapi.component.Snowflaker;
import io.krakau.genaifinderapi.component.VectorConverter;
import io.krakau.genaifinderapi.schema.dto.ProviderDto;
import io.krakau.genaifinderapi.schema.iscc.ExplainedISCC;
import io.krakau.genaifinderapi.schema.iscc.ISCC;
import io.krakau.genaifinderapi.schema.mongodb.Asset;
import io.krakau.genaifinderapi.schema.mongodb.IsccData;
import io.krakau.genaifinderapi.schema.mongodb.Metadata;
import io.krakau.genaifinderapi.schema.mongodb.Provider;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.InsertParam.Field;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Dominik
 */
@Service
public class CreateService {
    
    private static Logger logger = Logger.getLogger(CreateService.class.getName());

    private EnvironmentVariables env;
    
    private AssetService assetService;
    private IsccWebService isccWebService;
    private VectorConverter vectorConverter;
    private MilvusService milvusService;
    private Snowflaker snowflaker;

    @Autowired
    public CreateService(
            EnvironmentVariables env,
            AssetService assetService,
            IsccWebService isccWebService,
            VectorConverter vectorConverter,
            MilvusService milvusService,
            Snowflaker snowflaker
    ) {
        this.env = env;
        this.assetService = assetService;
        this.isccWebService = isccWebService;
        this.vectorConverter = vectorConverter;
        this.milvusService = milvusService;
        this.snowflaker = snowflaker;
    }

    public Asset createImage(ProviderDto provider, MultipartFile imageFile) {

        Asset asset = null;
        ISCC iscc = null;
        ExplainedISCC explainedISCC = null;
        Long snowflakeId = this.snowflaker.id();        

        try {
            // 1. Send image to iscc-web to create iscc
            iscc = this.isccWebService.createISCC(imageFile.getInputStream(), imageFile.getOriginalFilename());
            // 2. Send iscc to iscc-web to explain iscc
            explainedISCC = this.isccWebService.explainISCC(iscc.getIscc());
            // 3. Save image file
            saveImage(imageFile);
            // 4. Insert units to milvus collection
            List<Field> metaFields = new ArrayList<>();
            metaFields.add(new InsertParam.Field(env.MILVUS_COLLECTION_FIELD_VECTOR, Arrays.asList(this.vectorConverter.buildSearchVector64(explainedISCC.getUnits().get(0).getHash_bits()))));
            metaFields.add(new InsertParam.Field(env.MILVUS_COLLECTION_FIELD_NNSID, Arrays.asList(snowflakeId)));
            this.milvusService.insert(
                    env.MILVUS_DATABASE,
                    env.MILVUS_COLLECTION_UNIT_META,
                    env.MILVUS_PARTITION_IMAGE,
                    metaFields);
            List<Field> contentFields = new ArrayList<>();
            contentFields.add(new InsertParam.Field(env.MILVUS_COLLECTION_FIELD_VECTOR, Arrays.asList(this.vectorConverter.buildSearchVector64(explainedISCC.getUnits().get(1).getHash_bits()))));
            contentFields.add(new InsertParam.Field(env.MILVUS_COLLECTION_FIELD_NNSID, Arrays.asList(snowflakeId)));
            this.milvusService.insert(
                    env.MILVUS_DATABASE,
                    env.MILVUS_COLLECTION_UNIT_CONTENT,
                    env.MILVUS_PARTITION_IMAGE,
                    contentFields);
            List<Field> dataFields = new ArrayList<>();
            dataFields.add(new InsertParam.Field(env.MILVUS_COLLECTION_FIELD_VECTOR, Arrays.asList(this.vectorConverter.buildSearchVector64(explainedISCC.getUnits().get(2).getHash_bits()))));
            dataFields.add(new InsertParam.Field(env.MILVUS_COLLECTION_FIELD_NNSID, Arrays.asList(snowflakeId)));
            this.milvusService.insert(
                    env.MILVUS_DATABASE,
                    env.MILVUS_COLLECTION_UNIT_DATA,
                    env.MILVUS_PARTITION_IMAGE,
                    dataFields);
            List<Field> instanceFields = new ArrayList<>();
            instanceFields.add(new InsertParam.Field(env.MILVUS_COLLECTION_FIELD_VECTOR, Arrays.asList(this.vectorConverter.buildSearchVector64(explainedISCC.getUnits().get(3).getHash_bits()))));
            instanceFields.add(new InsertParam.Field(env.MILVUS_COLLECTION_FIELD_NNSID, Arrays.asList(snowflakeId)));
            this.milvusService.insert(
                    env.MILVUS_DATABASE,
                    env.MILVUS_COLLECTION_UNIT_INSTANCE,
                    env.MILVUS_PARTITION_IMAGE,
                    instanceFields);
            // 5. Append cryptographic credentials to asset and insert asset into mongodb
            asset = new Asset(
                    new Metadata(
                            new Provider(
                                    provider.getName(),
                                    provider.getPrompt(),
                                    provider.getTimestamp(),
                                    null
                            ),
                            new IsccData(
                                    iscc,
                                    explainedISCC
                            )
                    ),
                    snowflakeId);
            // 6. Insert asset into mongodb
            this.assetService.insert(asset);

        } catch (IOException ioe) {
            logger.log(Level.SEVERE, null, ioe);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        // 7. Return asset that was inserted into mongodb
        return asset;
    }
    
    
    private String saveImage(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(env.RESOURCE_DIR + "/" + env.RESOURCE_IMAGE_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        Path filePath = uploadPath.resolve(file.getOriginalFilename());
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        Logger.getLogger(CreateService.class.getName()).log(Level.INFO, "File " + filePath + " saved.");

        return filePath.toString();
    }

}
