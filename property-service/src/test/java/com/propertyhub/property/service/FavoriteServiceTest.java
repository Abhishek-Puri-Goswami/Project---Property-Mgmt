package com.propertyhub.property.service;

import com.propertyhub.property.dto.response.FavoriteResponse;
import com.propertyhub.property.entity.Favorite;
import com.propertyhub.property.entity.Furnishing;
import com.propertyhub.property.entity.Property;
import com.propertyhub.property.entity.PropertyType;
import com.propertyhub.property.exception.DuplicateResourceException;
import com.propertyhub.property.exception.ResourceNotFoundException;
import com.propertyhub.property.mapper.FavoriteMapper;
import com.propertyhub.property.mapper.PropertyMapper;
import com.propertyhub.property.repository.FavoriteRepository;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private PropertyRepository propertyRepository;

    private final ModelMapper modelMapper = new ModelMapper();
    private final PropertyMapper propertyMapper = new PropertyMapper(modelMapper);
    private final FavoriteMapper favoriteMapper = new FavoriteMapper(propertyMapper);
    private FavoriteService favoriteService;

    {
        modelMapper.registerModule(new RecordModule());
    }

    private Property sampleProperty() {
        return new Property("2BHK in Hinjewadi", "Spacious flat", "Pune", new BigDecimal("7200000"),
                2, new BigDecimal("1150"), PropertyType.APARTMENT, Furnishing.SEMI_FURNISHED, true, 1L);
    }

    @Test
    void addCreatesFavoriteWhenNotAlreadyFavorited() {
        favoriteService = new FavoriteService(favoriteRepository, propertyRepository, favoriteMapper);
        Property property = sampleProperty();
        when(favoriteRepository.existsByUserIdAndPropertyId(5L, 1L)).thenReturn(false);
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));
        when(favoriteRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        FavoriteResponse response = favoriteService.add(1L, 5L);

        assertThat(response.property().title()).isEqualTo("2BHK in Hinjewadi");
    }

    @Test
    void addThrowsDuplicateResourceExceptionWhenAlreadyFavorited() {
        favoriteService = new FavoriteService(favoriteRepository, propertyRepository, favoriteMapper);
        when(favoriteRepository.existsByUserIdAndPropertyId(5L, 1L)).thenReturn(true);

        assertThatThrownBy(() -> favoriteService.add(1L, 5L))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void addThrowsResourceNotFoundExceptionWhenPropertyMissing() {
        favoriteService = new FavoriteService(favoriteRepository, propertyRepository, favoriteMapper);
        when(favoriteRepository.existsByUserIdAndPropertyId(5L, 99L)).thenReturn(false);
        when(propertyRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> favoriteService.add(99L, 5L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void removeDeletesExistingFavorite() {
        favoriteService = new FavoriteService(favoriteRepository, propertyRepository, favoriteMapper);
        Favorite favorite = new Favorite(5L, sampleProperty());
        when(favoriteRepository.findByUserIdAndPropertyId(5L, 1L)).thenReturn(Optional.of(favorite));

        favoriteService.remove(1L, 5L);

        verify(favoriteRepository).delete(favorite);
    }

    @Test
    void removeThrowsResourceNotFoundExceptionWhenNotFavorited() {
        favoriteService = new FavoriteService(favoriteRepository, propertyRepository, favoriteMapper);
        when(favoriteRepository.findByUserIdAndPropertyId(5L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> favoriteService.remove(1L, 5L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void listByUserReturnsMappedFavorites() {
        favoriteService = new FavoriteService(favoriteRepository, propertyRepository, favoriteMapper);
        when(favoriteRepository.findByUserId(5L)).thenReturn(List.of(new Favorite(5L, sampleProperty())));

        List<FavoriteResponse> results = favoriteService.listByUser(5L);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).property().city()).isEqualTo("Pune");
    }

}
