package io.krakau.genaifinderapi.service;

import io.krakau.genaifinderapi.repository.AssetRepository;
import io.krakau.genaifinderapi.schema.mongodb.Asset;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

/**
 *
 * @author Dominik
 */
@Service
public class AssetService {
    
    private AssetRepository assetRepository;
    
    @Autowired
    public AssetService(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }
    
    public List<Asset> findAll() {
        return this.assetRepository.findAll();
    }
    
    public Slice<Asset> findAllPageable(Pageable pageable) {
        return this.assetRepository.findAllPageable(pageable);
    }
    
    public List<Asset> findByNnsId(List<Long> nnsIds) {
        return this.assetRepository.findByNnsId(nnsIds);
    }
    
    public Asset insert(Asset asset) {
        return this.assetRepository.insert(asset);
    }
    
}
