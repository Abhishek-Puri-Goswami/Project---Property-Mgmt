package com.propertyhub.ai.client;

import com.propertyhub.ai.dto.PropertyDto;
import com.propertyhub.ai.dto.PropertySummaryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@FeignClient(name = "property-service")
public interface PropertyFeignClient {

    @GetMapping("/api/properties/{id}")
    PropertyDto getProperty(@PathVariable("id") Long id);

    @GetMapping("/api/properties")
    List<PropertySummaryDto> searchProperties(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Integer bhk,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice
    );

    @GetMapping("/api/properties/batch")
    List<PropertySummaryDto> getPropertiesByIds(@RequestParam("ids") List<Long> ids);

}
