package com.propertyhub.property.dto.response;

import com.propertyhub.property.entity.Furnishing;
import com.propertyhub.property.entity.PropertyType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PropertyResponse(

        Long id,
        String title,
        String description,
        String city,
        BigDecimal price,
        Integer bhk,
        BigDecimal area,
        PropertyType propertyType,
        Furnishing furnishing,
        boolean parking,
        Long agentId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}
