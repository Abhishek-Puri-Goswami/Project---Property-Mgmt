package com.propertyhub.ai.controller;

import com.propertyhub.ai.dto.response.IngestionResponse;
import com.propertyhub.ai.dto.response.KnowledgeSearchResult;
import com.propertyhub.ai.rag.DocumentIngestionService;
import com.propertyhub.ai.rag.VectorSearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ai/knowledge")
public class KnowledgeController {

    private final DocumentIngestionService documentIngestionService;
    private final VectorSearchService vectorSearchService;

    public KnowledgeController(DocumentIngestionService documentIngestionService,
                                VectorSearchService vectorSearchService) {
        this.documentIngestionService = documentIngestionService;
        this.vectorSearchService = vectorSearchService;
    }

    @PostMapping("/ingest")
    public ResponseEntity<IngestionResponse> ingest() {
        return ResponseEntity.ok(documentIngestionService.ingest());
    }

    @GetMapping("/search")
    public ResponseEntity<List<KnowledgeSearchResult>> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "3") int topK
    ) {
        return ResponseEntity.ok(vectorSearchService.search(query, topK));
    }

}
