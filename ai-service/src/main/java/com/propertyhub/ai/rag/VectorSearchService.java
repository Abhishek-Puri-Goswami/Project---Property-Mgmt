package com.propertyhub.ai.rag;

import com.propertyhub.ai.dto.response.KnowledgeSearchResult;
import com.propertyhub.ai.exception.AiServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VectorSearchService {

    private static final Logger log = LoggerFactory.getLogger(VectorSearchService.class);

    private final VectorStore vectorStore;

    public VectorSearchService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public List<KnowledgeSearchResult> search(String query, int topK) {
        log.info("pgvector search started");

        try {
            var results = vectorStore.similaritySearch(
                    SearchRequest.builder().query(query).topK(topK).build()
            );

            log.info("Relevant documents retrieved: count={}", results.size());

            return results.stream()
                    .map(d -> new KnowledgeSearchResult(
                            d.getText(),
                            String.valueOf(d.getMetadata().get("source")),
                            d.getScore()
                    ))
                    .toList();
        } catch (Exception ex) {
            log.error("Vector search failed", ex);
            throw new AiServiceException("Failed to search the knowledge base", ex);
        }
    }

}
