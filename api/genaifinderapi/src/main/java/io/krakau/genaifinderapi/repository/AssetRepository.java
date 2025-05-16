package io.krakau.genaifinderapi.repository;

import io.krakau.genaifinderapi.schema.mongodb.Asset;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 *
 * @author Dominik
 */
public interface AssetRepository extends MongoRepository<Asset, String> {

    @Aggregation(pipeline = {
        "{$match:{}}"
    })
    public List<Asset> findAll();
    
    @Aggregation(pipeline = {
        "{$match:{}}"
    })
    public Slice<Asset> findAllPageable(Pageable page);

    @Aggregation(pipeline = {
        "{$match: {\"nnsId\": {$in: ?0}}}"
    })
    public List<Asset> findByNnsId(Iterable<Long> nnsIds);

}
