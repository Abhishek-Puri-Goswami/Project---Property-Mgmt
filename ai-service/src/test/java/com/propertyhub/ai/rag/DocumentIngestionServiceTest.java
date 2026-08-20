package com.propertyhub.ai.rag;

import com.propertyhub.ai.dto.response.IngestionResponse;
import com.propertyhub.ai.exception.VectorSearchException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentIngestionServiceTest {

    @Mock
    private VectorStore vectorStore;

    @Mock
    private JdbcTemplate jdbcTemplate;

    private DocumentIngestionService documentIngestionService;

    @Test
    void ingestsAllSixDocumentsWhenStoreIsEmpty() {
        documentIngestionService = new DocumentIngestionService(vectorStore, jdbcTemplate);
        when(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM vector_store", Integer.class)).thenReturn(0);

        IngestionResponse response = documentIngestionService.ingest();

        assertThat(response.documentsIngested()).isEqualTo(6);

        ArgumentCaptor<List<Document>> captor = ArgumentCaptor.forClass(List.class);
        verify(vectorStore).add(captor.capture());
        assertThat(captor.getValue()).hasSize(6);
    }

    @Test
    void skipsIngestionWhenStoreAlreadyPopulated() {
        documentIngestionService = new DocumentIngestionService(vectorStore, jdbcTemplate);
        when(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM vector_store", Integer.class)).thenReturn(6);

        IngestionResponse response = documentIngestionService.ingest();

        assertThat(response.documentsIngested()).isZero();
        verify(vectorStore, never()).add(any());
    }

    @Test
    void throwsVectorSearchExceptionWhenStoreFails() {
        documentIngestionService = new DocumentIngestionService(vectorStore, jdbcTemplate);
        when(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM vector_store", Integer.class)).thenReturn(0);
        org.mockito.Mockito.doThrow(new RuntimeException("store unavailable")).when(vectorStore).add(any());

        assertThatThrownBy(() -> documentIngestionService.ingest())
                .isInstanceOf(VectorSearchException.class);
    }

}
