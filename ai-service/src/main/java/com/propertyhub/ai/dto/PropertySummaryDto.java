package com.propertyhub.ai.dto;

import java.math.BigDecimal;

public record PropertySummaryDto(

        Long id,
        String title,
        String city,
        BigDecimal price,
        Integer bhk,
        BigDecimal area,
        String propertyType,
        String furnishing,
        boolean parking

) {
}
