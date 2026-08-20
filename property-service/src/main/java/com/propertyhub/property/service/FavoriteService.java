package com.propertyhub.property.service;

import com.propertyhub.property.dto.response.FavoriteResponse;
import com.propertyhub.property.entity.Favorite;
import com.propertyhub.property.entity.Property;
import com.propertyhub.property.exception.DuplicateResourceException;
import com.propertyhub.property.exception.ResourceNotFoundException;
import com.propertyhub.property.mapper.FavoriteMapper;
import com.propertyhub.property.repository.FavoriteRepository;
import com.propertyhub.property.repository.PropertyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final PropertyRepository propertyRepository;
    private final FavoriteMapper favoriteMapper;

    public FavoriteService(FavoriteRepository favoriteRepository, PropertyRepository propertyRepository,
                            FavoriteMapper favoriteMapper) {
        this.favoriteRepository = favoriteRepository;
        this.propertyRepository = propertyRepository;
        this.favoriteMapper = favoriteMapper;
    }

    public FavoriteResponse add(Long propertyId, Long userId) {
        log.info("Favorite creation requested");

        if (favoriteRepository.existsByUserIdAndPropertyId(userId, propertyId)) {
            throw new DuplicateResourceException("Property with id " + propertyId + " is already favorited by user " + userId);
        }

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property with id " + propertyId + " was not found"));

        Favorite saved = favoriteRepository.save(new Favorite(userId, property));

        log.info("Favorite created successfully");

        return favoriteMapper.toResponse(saved);
    }

    public void remove(Long propertyId, Long userId) {
        Favorite favorite = favoriteRepository.findByUserIdAndPropertyId(userId, propertyId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Property with id " + propertyId + " is not favorited by user " + userId));

        favoriteRepository.delete(favorite);
    }

    public List<FavoriteResponse> listByUser(Long userId) {
        return favoriteRepository.findByUserId(userId).stream()
                .map(favoriteMapper::toResponse)
                .toList();
    }

}
