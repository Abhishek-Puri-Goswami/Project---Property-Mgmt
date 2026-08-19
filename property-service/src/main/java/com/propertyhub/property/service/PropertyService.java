package com.propertyhub.property.service;

import com.propertyhub.property.dto.request.CreatePropertyRequest;
import com.propertyhub.property.dto.request.UpdatePropertyRequest;
import com.propertyhub.property.dto.response.PropertyResponse;
import com.propertyhub.property.entity.Property;
import com.propertyhub.property.exception.ResourceNotFoundException;
import com.propertyhub.property.mapper.PropertyMapper;
import com.propertyhub.property.repository.PropertyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PropertyService {

    private static final Logger log = LoggerFactory.getLogger(PropertyService.class);

    private final PropertyRepository propertyRepository;

    public PropertyService(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    public PropertyResponse create(CreatePropertyRequest request) {
        log.info("Property creation requested");

        Property property = PropertyMapper.toEntity(request);
        Property saved = propertyRepository.save(property);

        log.info("Property created successfully");

        return PropertyMapper.toResponse(saved);
    }

    public PropertyResponse get(Long id) {
        Property property = findOrThrow(id);
        return PropertyMapper.toResponse(property);
    }

    public PropertyResponse update(Long id, UpdatePropertyRequest request) {
        Property property = findOrThrow(id);

        property.update(
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

        return PropertyMapper.toResponse(property);
    }

    public void delete(Long id) {
        Property property = findOrThrow(id);
        propertyRepository.delete(property);
    }

    private Property findOrThrow(Long id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Property not found");
                    return new ResourceNotFoundException("Property with id " + id + " was not found");
                });
    }

}
