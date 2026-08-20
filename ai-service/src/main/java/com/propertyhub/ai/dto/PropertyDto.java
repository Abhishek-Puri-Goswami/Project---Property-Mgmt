package com.propertyhub.ai.dto;

import java.math.BigDecimal;

public record PropertyDto(

        Long id,
        String title,
        String description,
        String city,
        BigDecimal price,
        Integer bhk,
        BigDecimal area,
        String propertyType,
        String furnishing,
        boolean parking,
        Long agentId

) {
}
