package com.propertyhub.property.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.propertyhub.property.dto.response.VisitResponse;
import com.propertyhub.property.entity.VisitStatus;
import com.propertyhub.property.exception.ResourceNotFoundException;
import com.propertyhub.property.service.VisitService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VisitController.class)
class VisitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VisitService visitService;

    private VisitResponse sampleResponse() {
        return new VisitResponse(10L, 1L, "2BHK in Hinjewadi", 5L, LocalDateTime.now().plusDays(1), VisitStatus.PENDING, null, LocalDateTime.now());
    }

    @Test
    void returns201OnSchedule() throws Exception {
        when(visitService.schedule(eq(1L), any())).thenReturn(sampleResponse());

        mockMvc.perform(post("/api/properties/1/visits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "userId", 5,
                                "scheduledAt", LocalDateTime.now().plusDays(1).toString()
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void returns400WhenScheduledAtInPast() throws Exception {
        mockMvc.perform(post("/api/properties/1/visits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "userId", 5,
                                "scheduledAt", LocalDateTime.now().minusDays(1).toString()
                        ))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.scheduledAt").exists());
    }

    @Test
    void returns404OnScheduleForMissingProperty() throws Exception {
        when(visitService.schedule(eq(99L), any()))
                .thenThrow(new ResourceNotFoundException("Property with id 99 was not found"));

        mockMvc.perform(post("/api/properties/99/visits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "userId", 5,
                                "scheduledAt", LocalDateTime.now().plusDays(1).toString()
                        ))))
                .andExpect(status().isNotFound());
    }

    @Test
    void returns200OnListByUserId() throws Exception {
        when(visitService.listByUser(5L)).thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get("/api/properties/visits").param("userId", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(5));
    }

    @Test
    void returns200OnListByAgentId() throws Exception {
        when(visitService.listByAgent(1L)).thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get("/api/properties/visits").param("agentId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].propertyId").value(1));
    }

    @Test
    void returns400WhenNeitherUserIdNorAgentIdProvided() throws Exception {
        mockMvc.perform(get("/api/properties/visits"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

}
