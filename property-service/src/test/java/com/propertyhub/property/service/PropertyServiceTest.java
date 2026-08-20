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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.record.RecordModule;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PropertyServiceTest {

    @Mock
    private PropertyRepository propertyRepository;

    private final ModelMapper modelMapper = new ModelMapper();
    private final PropertyMapper propertyMapper = new PropertyMapper(modelMapper);
    private PropertyService propertyService;

    {
        modelMapper.registerModule(new RecordModule());
    }

    private CreatePropertyRequest createRequest() {
        return new CreatePropertyRequest(
                "2BHK in Hinjewadi", "Spacious flat", "Pune",
                new BigDecimal("7200000"), 2, new BigDecimal("1150"),
                PropertyType.APARTMENT, Furnishing.SEMI_FURNISHED, true, 1L
        );
    }

    private Property existingProperty() {
        return new Property(
                "2BHK in Hinjewadi", "Spacious flat", "Pune",
                new BigDecimal("7200000"), 2, new BigDecimal("1150"),
                PropertyType.APARTMENT, Furnishing.SEMI_FURNISHED, true, 1L
        );
    }

    @Test
    void createsAndReturnsProperty() {
        propertyService = new PropertyService(propertyRepository, propertyMapper);
        when(propertyRepository.save(any(Property.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PropertyResponse response = propertyService.create(createRequest());

        assertThat(response.title()).isEqualTo("2BHK in Hinjewadi");
        assertThat(response.city()).isEqualTo("Pune");
        assertThat(response.agentId()).isEqualTo(1L);
    }

    @Test
    void getReturnsPropertyWhenFound() {
        propertyService = new PropertyService(propertyRepository, propertyMapper);
        Property property = existingProperty();
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));

        PropertyResponse response = propertyService.get(1L);

        assertThat(response.city()).isEqualTo("Pune");
    }

    @Test
    void getThrowsWhenNotFound() {
        propertyService = new PropertyService(propertyRepository, propertyMapper);
        when(propertyRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> propertyService.get(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateModifiesExistingProperty() {
        propertyService = new PropertyService(propertyRepository, propertyMapper);
        Property property = existingProperty();
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));

        UpdatePropertyRequest request = new UpdatePropertyRequest(
                "Updated Title", "Updated desc", "Mumbai",
                new BigDecimal("8000000"), 3, new BigDecimal("1400"),
                PropertyType.VILLA, Furnishing.FURNISHED, false, 2L
        );

        PropertyResponse response = propertyService.update(1L, request);

        assertThat(response.title()).isEqualTo("Updated Title");
        assertThat(response.city()).isEqualTo("Mumbai");
        assertThat(response.bhk()).isEqualTo(3);
    }

    @Test
    void updateThrowsWhenNotFound() {
        propertyService = new PropertyService(propertyRepository, propertyMapper);
        when(propertyRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> propertyService.update(99L, createUpdateRequest()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteRemovesExistingProperty() {
        propertyService = new PropertyService(propertyRepository, propertyMapper);
        Property property = existingProperty();
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));

        propertyService.delete(1L);

        verify(propertyRepository).delete(property);
    }

    @Test
    void deleteThrowsWhenNotFound() {
        propertyService = new PropertyService(propertyRepository, propertyMapper);
        when(propertyRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> propertyService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(propertyRepository, never()).delete(any(Property.class));
    }

    private UpdatePropertyRequest createUpdateRequest() {
        return new UpdatePropertyRequest(
                "Title", "Desc", "Pune",
                new BigDecimal("100"), 1, new BigDecimal("100"),
                PropertyType.APARTMENT, Furnishing.FURNISHED, false, 1L
        );
    }

    @Test
    void searchReturnsMappedResultsForMatchingFilters() {
        propertyService = new PropertyService(propertyRepository, propertyMapper);
        Property property = existingProperty();
        when(propertyRepository.search(eq("Pune"), eq(2), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull()))
                .thenReturn(List.of(property));

        List<PropertySummaryResponse> results = propertyService.search("Pune", 2, null, null, null, null, null, null, null);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).city()).isEqualTo("Pune");
    }

    @Test
    void searchReturnsEmptyListWhenNoFiltersMatch() {
        propertyService = new PropertyService(propertyRepository, propertyMapper);
        when(propertyRepository.search(isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull()))
                .thenReturn(List.of());

        List<PropertySummaryResponse> results = propertyService.search(null, null, null, null, null, null, null, null, null);

        assertThat(results).isEmpty();
    }

    @Test
    void searchThrowsWhenMinPriceGreaterThanMaxPrice() {
        propertyService = new PropertyService(propertyRepository, propertyMapper);

        assertThatThrownBy(() -> propertyService.search(
                null, null, new BigDecimal("9000000"), new BigDecimal("1000000"), null, null, null, null, null
        )).isInstanceOf(InvalidSearchException.class);
    }

    @Test
    void searchThrowsWhenMinAreaGreaterThanMaxArea() {
        propertyService = new PropertyService(propertyRepository, propertyMapper);

        assertThatThrownBy(() -> propertyService.search(
                null, null, null, null, new BigDecimal("2000"), new BigDecimal("500"), null, null, null
        )).isInstanceOf(InvalidSearchException.class);
    }

    @Test
    void searchThrowsWhenPriceIsNegative() {
        propertyService = new PropertyService(propertyRepository, propertyMapper);

        assertThatThrownBy(() -> propertyService.search(
                null, null, new BigDecimal("-1"), null, null, null, null, null, null
        )).isInstanceOf(InvalidSearchException.class);
    }

    @Test
    void searchThrowsWhenBhkNotPositive() {
        propertyService = new PropertyService(propertyRepository, propertyMapper);

        assertThatThrownBy(() -> propertyService.search(
                null, 0, null, null, null, null, null, null, null
        )).isInstanceOf(InvalidSearchException.class);
    }

    @Test
    void getByIdsReturnsMatchingSummaries() {
        propertyService = new PropertyService(propertyRepository, propertyMapper);
        Property property = existingProperty();
        when(propertyRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(property));

        List<PropertySummaryResponse> results = propertyService.getByIds(List.of(1L, 2L));

        assertThat(results).hasSize(1);
        assertThat(results.get(0).city()).isEqualTo("Pune");
    }

    @Test
    void getByIdsReturnsEmptyListWhenNoneFound() {
        propertyService = new PropertyService(propertyRepository, propertyMapper);
        when(propertyRepository.findAllById(List.of(99L))).thenReturn(List.of());

        List<PropertySummaryResponse> results = propertyService.getByIds(List.of(99L));

        assertThat(results).isEmpty();
    }

}
