package com.propertyhub.property.mapper;

import com.propertyhub.property.dto.request.CreatePropertyRequest;
import com.propertyhub.property.dto.response.PropertyResponse;
import com.propertyhub.property.dto.response.PropertySummaryResponse;
import com.propertyhub.property.entity.Property;

public final class PropertyMapper {

    private PropertyMapper() {
    }

    public static Property toEntity(CreatePropertyRequest request) {
        return new Property(
                request.title(),
                request.description(),
                request.city(),
                request.price(),
                request.bhk(),
                request.area(),
                request.propertyType(),
                request.furnishing(),
                request.parking(),
                request.agentId()
        );
    }

    public static PropertyResponse toResponse(Property property) {
        return new PropertyResponse(
                property.getId(),
                property.getTitle(),
                property.getDescription(),
                property.getCity(),
                property.getPrice(),
                property.getBhk(),
                property.getArea(),
                property.getPropertyType(),
                property.getFurnishing(),
                property.isParking(),
                property.getAgentId(),
                property.getCreatedAt(),
                property.getUpdatedAt()
        );
    }

    public static PropertySummaryResponse toSummary(Property property) {
        return new PropertySummaryResponse(
                property.getId(),
                property.getTitle(),
                property.getCity(),
                property.getPrice(),
                property.getBhk(),
                property.getArea(),
                property.getPropertyType(),
                property.getFurnishing(),
                property.isParking()
        );
    }

}
