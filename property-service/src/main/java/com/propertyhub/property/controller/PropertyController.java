package com.propertyhub.property.controller;

import com.propertyhub.property.dto.request.CreatePropertyRequest;
import com.propertyhub.property.dto.request.UpdatePropertyRequest;
import com.propertyhub.property.dto.response.PropertyResponse;
import com.propertyhub.property.dto.response.PropertySummaryResponse;
import com.propertyhub.property.entity.Furnishing;
import com.propertyhub.property.entity.PropertyType;
import com.propertyhub.property.service.PropertyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    private final PropertyService propertyService;

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @GetMapping
    public ResponseEntity<List<PropertySummaryResponse>> search(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Integer bhk,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) BigDecimal minArea,
            @RequestParam(required = false) BigDecimal maxArea,
            @RequestParam(required = false) PropertyType propertyType,
            @RequestParam(required = false) Furnishing furnishing,
            @RequestParam(required = false) Boolean parking
    ) {
        return ResponseEntity.ok(propertyService.search(
                city, bhk, minPrice, maxPrice, minArea, maxArea, propertyType, furnishing, parking
        ));
    }

    @PostMapping
    public ResponseEntity<PropertyResponse> create(@Valid @RequestBody CreatePropertyRequest request) {
        PropertyResponse response = propertyService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropertyResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(propertyService.get(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PropertyResponse> update(@PathVariable Long id, @Valid @RequestBody UpdatePropertyRequest request) {
        return ResponseEntity.ok(propertyService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        propertyService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
