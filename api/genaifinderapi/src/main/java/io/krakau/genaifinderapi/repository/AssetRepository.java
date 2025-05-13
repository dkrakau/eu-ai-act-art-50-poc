package io.krakau.genaifinderapi.repository;

import io.krakau.genaifinderapi.schema.mongodb.Asset;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 *
 * @author Dominik
 */
public interface AssetRepository extends MongoRepository<Asset, String>{
    
     Slice<Asset> findAllAssets(Pageable page);
    
}
