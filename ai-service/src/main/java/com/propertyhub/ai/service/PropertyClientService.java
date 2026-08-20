package com.propertyhub.ai.service;

import com.propertyhub.ai.client.PropertyFeignClient;
import com.propertyhub.ai.dto.PropertyDto;
import com.propertyhub.ai.dto.PropertySummaryDto;
import com.propertyhub.ai.exception.AiServiceException;
import com.propertyhub.ai.exception.PropertyNotFoundException;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PropertyClientService {

    private static final Logger log = LoggerFactory.getLogger(PropertyClientService.class);

    private final PropertyFeignClient propertyFeignClient;

    public PropertyClientService(PropertyFeignClient propertyFeignClient) {
        this.propertyFeignClient = propertyFeignClient;
    }

    public PropertyDto getProperty(Long id) {
        try {
            return propertyFeignClient.getProperty(id);
        } catch (FeignException.NotFound ex) {
            throw new PropertyNotFoundException("Property with id " + id + " was not found");
        } catch (FeignException ex) {
            log.error("Property service call failed");
            throw new AiServiceException("Failed to retrieve property " + id, ex);
        }
    }

    public List<PropertySummaryDto> searchProperties(String city, Integer bhk, BigDecimal minPrice, BigDecimal maxPrice) {
        try {
            return propertyFeignClient.searchProperties(city, bhk, minPrice, maxPrice);
        } catch (FeignException ex) {
            log.error("Property service call failed");
            throw new AiServiceException("Failed to search properties", ex);
        }
    }

    public List<PropertySummaryDto> getPropertiesByIds(List<Long> ids) {
        try {
            return propertyFeignClient.getPropertiesByIds(ids);
        } catch (FeignException ex) {
            log.error("Property service call failed");
            throw new AiServiceException("Failed to retrieve properties by ids", ex);
        }
    }

}
