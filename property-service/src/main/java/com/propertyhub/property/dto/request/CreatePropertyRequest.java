package com.propertyhub.property.dto.request;

import com.propertyhub.property.entity.Furnishing;
import com.propertyhub.property.entity.PropertyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreatePropertyRequest(

        @NotBlank(message = "Title is required")
        String title,

        String description,

        @NotBlank(message = "City is required")
        String city,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be positive")
        BigDecimal price,

        @NotNull(message = "BHK is required")
        @Positive(message = "BHK must be positive")
        Integer bhk,

        @NotNull(message = "Area is required")
        @Positive(message = "Area must be positive")
        BigDecimal area,

        @NotNull(message = "Property type is required")
        PropertyType propertyType,

        @NotNull(message = "Furnishing is required")
        Furnishing furnishing,

        boolean parking,

        @NotNull(message = "Agent id is required")
        Long agentId

) {
}
