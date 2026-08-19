package com.propertyhub.property.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.propertyhub.property.dto.response.PropertyResponse;
import com.propertyhub.property.dto.response.PropertySummaryResponse;
import com.propertyhub.property.entity.Furnishing;
import com.propertyhub.property.entity.PropertyType;
import com.propertyhub.property.exception.InvalidSearchException;
import com.propertyhub.property.exception.ResourceNotFoundException;
import com.propertyhub.property.service.PropertyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PropertyController.class)
class PropertyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PropertyService propertyService;

    private PropertyResponse sampleResponse() {
        return new PropertyResponse(
                1L, "2BHK in Hinjewadi", "Spacious flat", "Pune",
                new BigDecimal("7200000"), 2, new BigDecimal("1150"),
                PropertyType.APARTMENT, Furnishing.SEMI_FURNISHED, true, 1L,
                LocalDateTime.now(), LocalDateTime.now()
        );
    }

    private String validCreateJson() {
        return """
                {"title":"2BHK in Hinjewadi","city":"Pune","price":7200000,"bhk":2,"area":1150,
                 "propertyType":"APARTMENT","furnishing":"SEMI_FURNISHED","parking":true,"agentId":1}
                """;
    }

    @Test
    void returns201OnValidCreate() throws Exception {
        when(propertyService.create(any())).thenReturn(sampleResponse());

        mockMvc.perform(post("/api/properties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validCreateJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("2BHK in Hinjewadi"));
    }

    @Test
    void returns400WhenPriceNotPositive() throws Exception {
        mockMvc.perform(post("/api/properties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Title","city":"Pune","price":-1,"bhk":2,"area":1150,
                                 "propertyType":"APARTMENT","furnishing":"SEMI_FURNISHED","parking":true,"agentId":1}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fieldErrors.price").exists());
    }

    @Test
    void returns400WhenTitleBlank() throws Exception {
        mockMvc.perform(post("/api/properties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"","city":"Pune","price":100,"bhk":2,"area":1150,
                                 "propertyType":"APARTMENT","furnishing":"SEMI_FURNISHED","parking":true,"agentId":1}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.title").exists());
    }

    @Test
    void returns200OnGetFound() throws Exception {
        when(propertyService.get(1L)).thenReturn(sampleResponse());

        mockMvc.perform(get("/api/properties/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value("Pune"));
    }

    @Test
    void returns404OnGetNotFound() throws Exception {
        when(propertyService.get(99L)).thenThrow(new ResourceNotFoundException("Property with id 99 was not found"));

        mockMvc.perform(get("/api/properties/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("RESOURCE_NOT_FOUND"));
    }

    @Test
    void returns200OnUpdate() throws Exception {
        when(propertyService.update(eq(1L), any())).thenReturn(sampleResponse());

        mockMvc.perform(put("/api/properties/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validCreateJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void returns404OnUpdateNotFound() throws Exception {
        when(propertyService.update(eq(99L), any())).thenThrow(new ResourceNotFoundException("Property with id 99 was not found"));

        mockMvc.perform(put("/api/properties/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validCreateJson()))
                .andExpect(status().isNotFound());
    }

    @Test
    void returns204OnDelete() throws Exception {
        doNothing().when(propertyService).delete(1L);

        mockMvc.perform(delete("/api/properties/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void returns404OnDeleteNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Property with id 99 was not found")).when(propertyService).delete(99L);

        mockMvc.perform(delete("/api/properties/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void returns200OnSearchWithFilters() throws Exception {
        PropertySummaryResponse summary = new PropertySummaryResponse(
                1L, "2BHK in Hinjewadi", "Pune", new BigDecimal("7200000"), 2,
                new BigDecimal("1150"), PropertyType.APARTMENT, Furnishing.SEMI_FURNISHED, true
        );
        when(propertyService.search(eq("Pune"), eq(2), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(summary));

        mockMvc.perform(get("/api/properties").param("city", "Pune").param("bhk", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].city").value("Pune"));
    }

    @Test
    void returns400OnInvalidSearchRange() throws Exception {
        when(propertyService.search(any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenThrow(new InvalidSearchException("minPrice must not be greater than maxPrice"));

        mockMvc.perform(get("/api/properties").param("minPrice", "9000000").param("maxPrice", "1000000"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

}
