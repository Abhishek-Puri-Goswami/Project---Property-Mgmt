package com.propertyhub.property.repository;

import com.propertyhub.property.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    boolean existsByUserIdAndPropertyId(Long userId, Long propertyId);

    Optional<Favorite> findByUserIdAndPropertyId(Long userId, Long propertyId);

    List<Favorite> findByUserId(Long userId);

}
