package com.propertyhub.ai.rag;

import com.propertyhub.ai.dto.response.KnowledgeSearchResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VectorSearchServiceTest {

    @Mock
    private VectorStore vectorStore;

    @Test
    void mapsSimilaritySearchResultsToKnowledgeSearchResults() {
        VectorSearchService service = new VectorSearchService(vectorStore);
        Document doc = new Document("Hinjewadi is a major IT hub in Pune.", Map.of("source", "hinjewadi-guide.md"));
        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(List.of(doc));

        List<KnowledgeSearchResult> results = service.search("Is Hinjewadi good for IT professionals?", 3);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).content()).contains("Hinjewadi");
        assertThat(results.get(0).source()).isEqualTo("hinjewadi-guide.md");
    }

}
