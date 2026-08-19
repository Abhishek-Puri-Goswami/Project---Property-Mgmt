package com.propertyhub.property.dto.response;

import com.propertyhub.property.entity.Furnishing;
import com.propertyhub.property.entity.PropertyType;

import java.math.BigDecimal;

public record PropertySummaryResponse(

        Long id,
        String title,
        String city,
        BigDecimal price,
        Integer bhk,
        BigDecimal area,
        PropertyType propertyType,
        Furnishing furnishing,
        boolean parking

) {
}
