package com.propertyhub.property.mapper;

import com.propertyhub.property.dto.request.CreatePropertyRequest;
import com.propertyhub.property.dto.response.PropertyResponse;
import com.propertyhub.property.dto.response.PropertySummaryResponse;
import com.propertyhub.property.entity.Property;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class PropertyMapper {

    private final ModelMapper modelMapper;

    public PropertyMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Property toEntity(CreatePropertyRequest request) {
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

    public PropertyResponse toResponse(Property property) {
        return modelMapper.map(property, PropertyResponse.class);
    }

    public PropertySummaryResponse toSummary(Property property) {
        return modelMapper.map(property, PropertySummaryResponse.class);
    }

}
