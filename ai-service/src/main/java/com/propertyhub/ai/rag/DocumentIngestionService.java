package com.propertyhub.ai.rag;

import com.propertyhub.ai.dto.response.IngestionResponse;
import com.propertyhub.ai.exception.AiServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentIngestionService {

    private static final Logger log = LoggerFactory.getLogger(DocumentIngestionService.class);

    private static final String[] KNOWLEDGE_FILES = {
            "pune-locality-guide.md",
            "hinjewadi-guide.md",
            "wakad-guide.md",
            "property-buying-faq.md",
            "home-loan-faq.md",
            "property-documentation-guide.md"
    };

    private final VectorStore vectorStore;
    private final JdbcTemplate jdbcTemplate;

    public DocumentIngestionService(VectorStore vectorStore, JdbcTemplate jdbcTemplate) {
        this.vectorStore = vectorStore;
        this.jdbcTemplate = jdbcTemplate;
    }

    public IngestionResponse ingest() {
        log.info("Document ingestion requested");

        Integer existingCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM vector_store", Integer.class);
        if (existingCount != null && existingCount > 0) {
            log.info("Knowledge base already ingested, skipping");
            return new IngestionResponse(0);
        }

        try {
            List<Document> documents = new ArrayList<>();
            for (String file : KNOWLEDGE_FILES) {
                TextReader reader = new TextReader(new ClassPathResource("knowledge/" + file));
                reader.getCustomMetadata().put("source", file);
                documents.addAll(reader.get());
            }

            vectorStore.add(documents);
            log.info("Documents embedded and stored: count={}", documents.size());

            return new IngestionResponse(documents.size());
        } catch (Exception ex) {
            log.error("Document ingestion failed", ex);
            throw new AiServiceException("Failed to ingest knowledge documents", ex);
        }
    }

}
