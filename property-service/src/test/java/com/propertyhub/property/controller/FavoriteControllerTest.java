package com.propertyhub.property.controller;

import com.propertyhub.property.dto.response.FavoriteResponse;
import com.propertyhub.property.dto.response.PropertySummaryResponse;
import com.propertyhub.property.entity.Furnishing;
import com.propertyhub.property.entity.PropertyStatus;
import com.propertyhub.property.entity.PropertyType;
import com.propertyhub.property.exception.DuplicateResourceException;
import com.propertyhub.property.exception.ResourceNotFoundException;
import com.propertyhub.property.service.FavoriteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FavoriteController.class)
class FavoriteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FavoriteService favoriteService;

    private PropertySummaryResponse sampleSummary() {
        return new PropertySummaryResponse(1L, "2BHK in Hinjewadi", "Pune", new BigDecimal("7200000"), 2,
                new BigDecimal("1150"), PropertyType.APARTMENT, Furnishing.SEMI_FURNISHED, true, PropertyStatus.ACTIVE);
    }

    @Test
    void returns201OnAdd() throws Exception {
        when(favoriteService.add(1L, 5L)).thenReturn(new FavoriteResponse(10L, sampleSummary(), LocalDateTime.now()));

        mockMvc.perform(post("/api/properties/1/favorites").param("userId", "5"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.property.city").value("Pune"));
    }

    @Test
    void returns409OnDuplicateAdd() throws Exception {
        when(favoriteService.add(eq(1L), eq(5L)))
                .thenThrow(new DuplicateResourceException("Property with id 1 is already favorited by user 5"));

        mockMvc.perform(post("/api/properties/1/favorites").param("userId", "5"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("DUPLICATE_RESOURCE"));
    }

    @Test
    void returns404OnAddForMissingProperty() throws Exception {
        when(favoriteService.add(eq(99L), eq(5L)))
                .thenThrow(new ResourceNotFoundException("Property with id 99 was not found"));

        mockMvc.perform(post("/api/properties/99/favorites").param("userId", "5"))
                .andExpect(status().isNotFound());
    }

    @Test
    void returns204OnRemove() throws Exception {
        mockMvc.perform(delete("/api/properties/1/favorites").param("userId", "5"))
                .andExpect(status().isNoContent());
    }

    @Test
    void returns404OnRemoveWhenNotFavorited() throws Exception {
        doThrow(new ResourceNotFoundException("Property with id 1 is not favorited by user 5"))
                .when(favoriteService).remove(1L, 5L);

        mockMvc.perform(delete("/api/properties/1/favorites").param("userId", "5"))
                .andExpect(status().isNotFound());
    }

    @Test
    void returns200OnListByUser() throws Exception {
        when(favoriteService.listByUser(5L)).thenReturn(List.of(new FavoriteResponse(10L, sampleSummary(), LocalDateTime.now())));

        mockMvc.perform(get("/api/properties/favorites").param("userId", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].property.city").value("Pune"));
    }

}
