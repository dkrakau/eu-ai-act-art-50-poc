package io.krakau.genaifinderdbmanager.service;

import com.google.protobuf.ProtocolStringList;
import io.milvus.client.MilvusClient;
import io.milvus.client.MilvusServiceClient;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.grpc.DescribeCollectionResponse;
import io.milvus.grpc.GetCollectionStatisticsResponse;
import io.milvus.grpc.QueryResults;
import io.milvus.grpc.SearchResults;
import io.milvus.grpc.ShowCollectionsResponse;
import io.milvus.grpc.ShowPartitionsResponse;
import io.milvus.param.ConnectParam;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import io.milvus.param.R;
import io.milvus.param.collection.CreateCollectionParam;
import io.milvus.param.collection.CreateDatabaseParam;
import io.milvus.param.collection.DescribeCollectionParam;
import io.milvus.param.collection.DropCollectionParam;
import io.milvus.param.collection.DropDatabaseParam;
import io.milvus.param.collection.FieldType;
import io.milvus.param.collection.FlushParam;
import io.milvus.param.collection.GetCollectionStatisticsParam;
import io.milvus.param.collection.HasCollectionParam;
import io.milvus.param.collection.LoadCollectionParam;
import io.milvus.param.collection.ReleaseCollectionParam;
import io.milvus.param.collection.ShowCollectionsParam;
import io.milvus.param.dml.DeleteParam;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.QueryParam;
import io.milvus.param.dml.SearchParam;
import io.milvus.param.index.CreateIndexParam;
import io.milvus.param.index.DropIndexParam;
import io.milvus.param.partition.CreatePartitionParam;
import io.milvus.param.partition.DropPartitionParam;
import io.milvus.param.partition.HasPartitionParam;
import io.milvus.param.partition.LoadPartitionsParam;
import io.milvus.param.partition.ReleasePartitionsParam;
import io.milvus.param.partition.ShowPartitionsParam;
import io.milvus.response.DescCollResponseWrapper;
import io.milvus.response.GetCollStatResponseWrapper;
import io.milvus.response.QueryResultsWrapper;
import io.milvus.response.SearchResultsWrapper;
import io.milvus.response.SearchResultsWrapper.IDScore;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Dominik
 */
public class MilvusService {

    private final MilvusServiceClient milvusClient;

    public MilvusService(String url, int port) {
       
        this.milvusClient = new MilvusServiceClient(
                ConnectParam.newBuilder()
                        .withHost(url)
                        .withPort(port)
                        .build()
        );
    }
    
    public void createDatabse(String databaseName) {
        CreateDatabaseParam createDatabaseParam = CreateDatabaseParam.newBuilder()
                .withDatabaseName(databaseName)
                .build();
        this.milvusClient.createDatabase(createDatabaseParam);
        System.out.println("Milvus database " + databaseName + " created successfully.");
    }
    
    public void dropDatabse(String databaseName) {
        DropDatabaseParam dropDatabaseParam = DropDatabaseParam.newBuilder()
                .withDatabaseName(databaseName)
                .build();
        this.milvusClient.dropDatabase(dropDatabaseParam);
        System.out.println("Milvus database " + databaseName + " dropped successfully.");
    }

    public void createCollection(String databaseName, String collectionName, String description, List<FieldType> fields, int shardsNum, ConsistencyLevelEnum consistency) {

        CreateCollectionParam createCollectionReq = CreateCollectionParam.newBuilder()
                .withDatabaseName(databaseName)
                .withCollectionName(collectionName)
                .withDescription(description)
                .withShardsNum(shardsNum)
                .withConsistencyLevel(consistency)
                .withEnableDynamicField(false)
                .withFieldTypes(fields)
                .build();

        this.milvusClient.createCollection(createCollectionReq);
    }

    public void createPartition(String databaseName, String collectionName, String partitionName) {
        this.milvusClient.createPartition(
                CreatePartitionParam.newBuilder()
                        .withDatabaseName(databaseName)
                        .withCollectionName(collectionName)
                        .withPartitionName(partitionName)
                        .build()
        );
    }

    public void createIndex(String indexName, String databaseName, String collectionName, String field, IndexType indexType, MetricType metricType, String indexParam, Boolean syncMode) {
        this.milvusClient.createIndex(
                CreateIndexParam.newBuilder()
                        .withDatabaseName(databaseName)
                        .withCollectionName(collectionName)
                        .withIndexName(indexName)
                        .withFieldName(field)
                        .withIndexType(indexType)
                        .withMetricType(metricType)
                        .withExtraParam(indexParam)
                        .withSyncMode(syncMode)
                        .build()
        );
    }

    public void loadCollection(String databaseName, String collectionName) {
        this.milvusClient.loadCollection(
                LoadCollectionParam.newBuilder()
                        .withDatabaseName(databaseName)
                        .withCollectionName(collectionName)
                        .build()
        );
    }

    public void releaseCollection(String databaseName, String collectionName) {
        this.milvusClient.releaseCollection(
                ReleaseCollectionParam.newBuilder()
                        .withDatabaseName(databaseName)
                        .withCollectionName(collectionName)
                        .build());
    }

    public void loadPartition(String databaseName, String collectionName, String partitionName) {
        this.milvusClient.loadPartitions(
                LoadPartitionsParam.newBuilder()
                        .withDatabaseName(partitionName)
                        .withCollectionName(collectionName)
                        .withPartitionNames(Arrays.asList(partitionName))
                        .build());
    }

    public void releasePartition(String databaseName, String collectionName, String partitionName) {
        this.milvusClient.releasePartitions(
                ReleasePartitionsParam.newBuilder()
                        .withDatabaseName(databaseName)
                        .withCollectionName(collectionName)
                        .withPartitionNames(Arrays.asList(partitionName))
                        .build()
        );
    }

    public void insert(String databaseName, String collectionName, List<InsertParam.Field> fields) {

        InsertParam insertParam = InsertParam.newBuilder()
                .withDatabaseName(databaseName)
                .withCollectionName(collectionName)
                .withFields(fields)
                .build();

        this.milvusClient.insert(insertParam);
    }

    public void insert(String databaseName, String collectionName, String partitionName, List<InsertParam.Field> fields) {

        InsertParam insertParam = InsertParam.newBuilder()
                .withDatabaseName(databaseName)
                .withCollectionName(collectionName)
                .withPartitionName(partitionName)
                .withFields(fields)
                .build();

        this.milvusClient.insert(insertParam);
    }

    public void query(String databaseName, String collectionName, String partitionName, String queryField, String query) {

        List<String> searchPartitions = Arrays.asList(partitionName);
        List<String> queryOutputFields = Arrays.asList(queryField);

        loadPartition(databaseName, collectionName, partitionName);

        QueryParam queryParam = QueryParam.newBuilder()
                .withDatabaseName(databaseName)
                .withCollectionName(collectionName)
                .withPartitionNames(searchPartitions)
                .withExpr(query)
                .withOutFields(queryOutputFields)
                .build();

        R<QueryResults> respQuery = this.milvusClient.query(queryParam);
        QueryResultsWrapper wrapperQuery = new QueryResultsWrapper(respQuery.getData());

        for (String field : queryOutputFields) {
            System.out.println(wrapperQuery.getFieldWrapper(field).getFieldData());
        }

        releasePartition(databaseName, collectionName, partitionName);

        // To Do: return implementation
    }

    public List<?> queryById(String databaseName, String collectionName, String queryField, String query) {
        List<?> result = new ArrayList<>();

        List<String> queryOutputFields = Arrays.asList(queryField);

        QueryParam queryParam = QueryParam.newBuilder()
                .withDatabaseName(databaseName)
                .withCollectionName(collectionName)
                .withConsistencyLevel(ConsistencyLevelEnum.STRONG)
                .withExpr(query)
                .withOutFields(queryOutputFields)
                .build();

        R<QueryResults> respQuery = this.milvusClient.query(queryParam);

        QueryResultsWrapper wrapperQuery = new QueryResultsWrapper(respQuery.getData());
        result = wrapperQuery.getFieldWrapper("id").getFieldData();

        return result;
    }

    public void deleteCollections(String databaseName) {

        R<ShowCollectionsResponse> r = listCollections(databaseName);
        ShowCollectionsResponse response = r.getData();
        ProtocolStringList collectionList = response.getCollectionNamesList();

        for (String collectionName : collectionList) {
            this.milvusClient.dropCollection(
                    DropCollectionParam.newBuilder()
                            .withDatabaseName(databaseName)
                            .withCollectionName(collectionName)
                            .build()
            );
            System.out.println("Milvus collection " + collectionName + " dropped successfully.");
        }
    }

    public void deleteCollection(String databaseName, String collectionName) {
        this.milvusClient.dropCollection(
                DropCollectionParam.newBuilder()
                        .withDatabaseName(databaseName)
                        .withCollectionName(collectionName)
                        .build()
        );

        System.out.println("Milvus collection " + collectionName + " dropped successfully.");
    }

    public void deletePartition(String collectionName, String partitionName) {
        this.milvusClient.dropPartition(
                DropPartitionParam.newBuilder()
                        .withCollectionName(collectionName)
                        .withPartitionName(partitionName)
                        .build()
        );

        System.out.println("Milvus partition " + partitionName + " of collection " + collectionName + " dropped successfully.");
    }

    public void deleteIndex(String databaseName, String collectionName, String indexName) {
        this.milvusClient.dropIndex(
                DropIndexParam.newBuilder()
                        .withDatabaseName(databaseName)
                        .withCollectionName(collectionName)
                        .withIndexName(indexName)
                        .build()
        );

        System.out.println("Milvus index " + indexName + " of collection " + collectionName + " dropped successfully.");
    }

    public void deleteColumns(String databaseName, String collectionName, String deleteExpr) {
        this.milvusClient.delete(
                DeleteParam.newBuilder()
                        .withDatabaseName(databaseName)
                        .withCollectionName(collectionName)
                        .withExpr(deleteExpr)
                        .build()
        );
    }

    public void deleteColumns(String databaseName, String collectionName, String deleteExpr, String partitionName) {
        this.milvusClient.delete(
                DeleteParam.newBuilder()
                        .withDatabaseName(databaseName)
                        .withCollectionName(collectionName)
                        .withExpr(deleteExpr)
                        .withPartitionName(partitionName)
                        .build()
        );
    }

    public boolean hasCollection(String databaseName, String collectionName) {

        boolean hasCollection = false;

        R<Boolean> respHasCollection = this.milvusClient.hasCollection(
                HasCollectionParam.newBuilder()
                        .withDatabaseName(databaseName)
                        .withCollectionName(collectionName)
                        .build()
        );
        if (respHasCollection.getData() == Boolean.TRUE) {
            hasCollection = true;
        }

        return hasCollection;
    }

    public boolean hasPartition(String databaseName, String collectionName, String partitionName) {
        boolean hasPartition = false;

        R<Boolean> respHasPartition = this.milvusClient.hasPartition(
                HasPartitionParam.newBuilder()
                        .withDatabaseName(databaseName)
                        .withCollectionName(collectionName)
                        .withPartitionName(partitionName)
                        .build()
        );
        if (respHasPartition.getData() == Boolean.TRUE) {
            hasPartition = true;
        }

        return hasPartition;
    }

    public R<ShowCollectionsResponse> listCollections(String databaseName) {

        R<ShowCollectionsResponse> respShowCollections = this.milvusClient.showCollections(
                ShowCollectionsParam.newBuilder().withDatabaseName(databaseName).build()
        );
        System.out.println("########## Milvus collections ##########");
        System.out.println(respShowCollections);

        return respShowCollections;
    }

    public void collectionInfo(String databaseName, String collectionName) {

        R<DescribeCollectionResponse> respDescribeCollection = this.milvusClient.describeCollection(
                // Return the name and schema of the collection.
                DescribeCollectionParam.newBuilder()
                        .withDatabaseName(databaseName)
                        .withCollectionName(collectionName)
                        .build()
        );
        DescCollResponseWrapper wrapperDescribeCollection = new DescCollResponseWrapper(respDescribeCollection.getData());

        R<GetCollectionStatisticsResponse> respCollectionStatistics = this.milvusClient.getCollectionStatistics(
                // Return the statistics information of the collection.
                GetCollectionStatisticsParam.newBuilder()
                        .withDatabaseName(databaseName)
                        .withCollectionName(collectionName)
                        .build()
        );
        GetCollStatResponseWrapper wrapperCollectionStatistics = new GetCollStatResponseWrapper(respCollectionStatistics.getData());

//        System.out.println("########## Milvus Collections for [" + collectionName + "] ##########");
        System.out.println(wrapperDescribeCollection);
        System.out.println("Collection row count: " + wrapperCollectionStatistics.getRowCount());
    }

    public void listPartitions(String databaseName, String collectionName) {

        R<ShowPartitionsResponse> respShowPartitions = this.milvusClient.showPartitions(
                ShowPartitionsParam.newBuilder()
                        .withDatabaseName(databaseName)
                        .withCollectionName(collectionName)
                        .withPartitionNames(Arrays.asList("content"))
                        .build()
        );

        System.out.println("########## Milvus List of Partitions for [" + collectionName + "] ##########");
        System.out.println(respShowPartitions);
    }

    public MilvusClient getClient() {
        return this.milvusClient;
    }

    public void close() {
        this.milvusClient.close();
    }

    public Map<Long, Float> similaritySearch(String databaseName, String collectionName, String partitionName, String searchField, String vectorFieldName, List<String> searchVectors) {

        List<String> searchPartitions = Arrays.asList(partitionName);
        List<String> searchOutputFields = Arrays.asList(searchField);
        List<ByteBuffer> binarySearchVectors = new ArrayList<>();

        for (String vector : searchVectors) {
            binarySearchVectors.add(buildSearchVektor64(vector));
        }

        loadPartition(databaseName, collectionName, partitionName);

        final Integer SEARCH_K = 10;                       // TopK - Limits search result
        final String SEARCH_PARAM = "{\"nprobe\":10}";    // Params

        SearchParam searchParam = SearchParam.newBuilder()
                .withDatabaseName(databaseName)
                .withCollectionName(collectionName)
                //.withConsistencyLevel(ConsistencyLevelEnum.STRONG)
                .withPartitionNames(searchPartitions)
                .withMetricType(MetricType.HAMMING)
                .withOutFields(searchOutputFields)
                .withTopK(SEARCH_K)
                .withVectors(binarySearchVectors)
                .withVectorFieldName(vectorFieldName)
                .withParams(SEARCH_PARAM)
                .build();

        R<SearchResults> respSearch = this.milvusClient.search(searchParam);

        releasePartition(databaseName, collectionName, partitionName);

        SearchResultsWrapper wrapperSearch = new SearchResultsWrapper(respSearch.getData().getResults());
        List<IDScore> searchResult = wrapperSearch.getIDScore(0);
        System.out.println(wrapperSearch.getIDScore(0));
        System.out.println(wrapperSearch.getFieldData("id", 0));

        Map<Long, Float> result = new LinkedHashMap<>();
        for (int i = 0; i < searchResult.size(); i++) {
            result.put(searchResult.get(i).getLongID(), searchResult.get(i).getScore());
        }
        System.out.println(result);

        return result;
    }

    public ByteBuffer buildSearchVektor64(String vectorString) {

        byte[] bytes = new byte[8];
        int byteIndex = 0;

        int i = 0;
        while (i < vectorString.length()) {
            bytes[byteIndex] = (byte) Short.parseShort(vectorString.substring(i, i + 8), 2);
            byteIndex++;
            i = i + 8;
        }

        return ByteBuffer.allocate(8).put(bytes);
    }

    public void flush(FlushParam requestParam) {
        this.milvusClient.flush(requestParam);
    }
}