package com.propertyhub.ai.controller;

import com.propertyhub.ai.dto.response.IngestionResponse;
import com.propertyhub.ai.dto.response.KnowledgeSearchResult;
import com.propertyhub.ai.rag.DocumentIngestionService;
import com.propertyhub.ai.rag.VectorSearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(KnowledgeController.class)
class KnowledgeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DocumentIngestionService documentIngestionService;

    @MockitoBean
    private VectorSearchService vectorSearchService;

    @Test
    void returns200OnIngest() throws Exception {
        when(documentIngestionService.ingest()).thenReturn(new IngestionResponse(6));

        mockMvc.perform(post("/api/ai/knowledge/ingest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.documentsIngested").value(6));
    }

    @Test
    void returns200OnSearch() throws Exception {
        when(vectorSearchService.search("Is Hinjewadi good for IT professionals?", 3))
                .thenReturn(List.of(new KnowledgeSearchResult("Hinjewadi is a major IT hub.", "hinjewadi-guide.md", 0.9)));

        mockMvc.perform(get("/api/ai/knowledge/search")
                        .param("query", "Is Hinjewadi good for IT professionals?"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].source").value("hinjewadi-guide.md"));
    }

}
