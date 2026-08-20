package com.propertyhub.property.service;

import com.propertyhub.property.dto.request.CreatePropertyRequest;
import com.propertyhub.property.dto.request.UpdatePropertyRequest;
import com.propertyhub.property.dto.response.PropertyResponse;
import com.propertyhub.property.dto.response.PropertySummaryResponse;
import com.propertyhub.property.entity.Furnishing;
import com.propertyhub.property.entity.Property;
import com.propertyhub.property.entity.PropertyType;
import com.propertyhub.property.exception.InvalidSearchException;
import com.propertyhub.property.exception.ResourceNotFoundException;
import com.propertyhub.property.mapper.PropertyMapper;
import com.propertyhub.property.repository.PropertyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

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

    public List<PropertySummaryResponse> search(String city, Integer bhk, BigDecimal minPrice, BigDecimal maxPrice,
                                                  BigDecimal minArea, BigDecimal maxArea, PropertyType propertyType,
                                                  Furnishing furnishing, Boolean parking) {
        log.info("Property search started");

        validateSearchParams(bhk, minPrice, maxPrice, minArea, maxArea);

        List<Property> results = propertyRepository.search(
                city, bhk, minPrice, maxPrice, minArea, maxArea, propertyType, furnishing, parking
        );

        log.info("Property search completed: resultCount={}", results.size());

        return results.stream().map(PropertyMapper::toSummary).toList();
    }

    public List<PropertySummaryResponse> getByIds(List<Long> ids) {
        return propertyRepository.findAllById(ids).stream()
                .map(PropertyMapper::toSummary)
                .toList();
    }

    private void validateSearchParams(Integer bhk, BigDecimal minPrice, BigDecimal maxPrice,
                                       BigDecimal minArea, BigDecimal maxArea) {
        if (bhk != null && bhk <= 0) {
            throw new InvalidSearchException("bhk must be positive");
        }
        if (minPrice != null && minPrice.signum() < 0) {
            throw new InvalidSearchException("minPrice must not be negative");
        }
        if (maxPrice != null && maxPrice.signum() < 0) {
            throw new InvalidSearchException("maxPrice must not be negative");
        }
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new InvalidSearchException("minPrice must not be greater than maxPrice");
        }
        if (minArea != null && minArea.signum() < 0) {
            throw new InvalidSearchException("minArea must not be negative");
        }
        if (maxArea != null && maxArea.signum() < 0) {
            throw new InvalidSearchException("maxArea must not be negative");
        }
        if (minArea != null && maxArea != null && minArea.compareTo(maxArea) > 0) {
            throw new InvalidSearchException("minArea must not be greater than maxArea");
        }
    }

    private Property findOrThrow(Long id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Property not found");
                    return new ResourceNotFoundException("Property with id " + id + " was not found");
                });
    }

}
