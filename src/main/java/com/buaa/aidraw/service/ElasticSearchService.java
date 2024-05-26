package com.buaa.aidraw.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.indices.*;
import co.elastic.clients.json.jackson.JacksonJsonpGenerator;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.buaa.aidraw.model.domain.Element;
import jakarta.annotation.Resource;
import org.elasticsearch.client.RequestOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class ElasticSearchService {
    @Resource
    private final ElasticsearchClient elasticsearchClient;

    @Autowired
    public ElasticSearchService(ElasticsearchClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }

    private String getMappingConfig(String name){
        return "classpath:es/"+name+".json";
    }

    public void CreateIndex(String indexName) throws IOException {
        BooleanResponse exists = elasticsearchClient.indices().exists(r -> r.index(indexName));
        if (exists.value()) {
            // 删除表
            elasticsearchClient.indices().delete(d -> d.index(indexName));
        }

        // 创建
        // >> 获取mapping配置文件
        File file = ResourceUtils.getFile(getMappingConfig(indexName));
        FileReader fileReader = new FileReader(file);
        // >> 创建
        CreateIndexResponse indexResponse = elasticsearchClient.indices()
                .create(c -> c
                        .index(indexName)
                        .withJson(fileReader)
                );
    }

    public void getIndexProperties(String indexName) {
        try {
            // Create the get index request
            GetIndexRequest getIndexRequest = GetIndexRequest.of(g -> g
                    .index(indexName)
            );

            // Execute the get index request
            GetIndexResponse getIndexResponse = elasticsearchClient.indices().get(getIndexRequest);

            // Get index state
            Map<String, IndexState> indices = getIndexResponse.result();
            IndexState indexState = indices.get(indexName);

            if (indexState != null) {
                System.out.println(indexState.mappings().properties().keySet());
            } else {
                System.out.println("Index not found");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void insertElement(Element element) {
        try {
            IndexRequest<Element> indexRequest = IndexRequest.of(i -> i
                    .index("element")
                    .id(element.getId())
                    .document(element)
            );

            IndexResponse indexResponse = elasticsearchClient.index(indexRequest);

            System.out.println("Indexed document with id: " + indexResponse.id());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteElement(String id) {
        try {
            // Create the delete request
            DeleteRequest deleteRequest = DeleteRequest.of(d -> d
                    .index("element") // Replace with your index name
                    .id(id)
            );

            // Execute the delete request
            DeleteResponse deleteResponse = elasticsearchClient.delete(deleteRequest);

            // Print response
            System.out.println("Deleted document with id: " + deleteResponse.id());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void searchElement(String keyword) {
        try {
            Query elementNameQuery = MatchQuery.of(m -> m
                    .field("elementName")
                    .query(keyword)
            )._toQuery();

            Query promptQuery = MatchQuery.of(m -> m
                    .field("prompt")
                    .query(keyword)
            )._toQuery();

            Query combinedQuery = Query.of(q -> q
                    .bool(b -> b
                            .should(elementNameQuery)
                            .should(promptQuery)
                    )
            );

            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index("element")
                    .query(combinedQuery)
            );

            SearchResponse<Object> searchResponse = elasticsearchClient.search(searchRequest, Object.class);

            List<Hit<Object>> hits = searchResponse.hits().hits();
            for (Hit<Object> hit : hits) {
                System.out.println("Found document with id: " + hit.id());
                System.out.println("Source: " + hit.source());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
