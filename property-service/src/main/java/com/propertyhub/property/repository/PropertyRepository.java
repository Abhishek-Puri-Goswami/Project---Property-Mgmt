package com.propertyhub.property.repository;

import com.propertyhub.property.entity.Furnishing;
import com.propertyhub.property.entity.Property;
import com.propertyhub.property.entity.PropertyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface PropertyRepository extends JpaRepository<Property, Long> {

    @Query("""
            SELECT p FROM Property p
            WHERE (:city IS NULL OR LOWER(p.city) = LOWER(CAST(:city AS string)))
              AND (:bhk IS NULL OR p.bhk = :bhk)
              AND (:minPrice IS NULL OR p.price >= :minPrice)
              AND (:maxPrice IS NULL OR p.price <= :maxPrice)
              AND (:minArea IS NULL OR p.area >= :minArea)
              AND (:maxArea IS NULL OR p.area <= :maxArea)
              AND (:propertyType IS NULL OR p.propertyType = :propertyType)
              AND (:furnishing IS NULL OR p.furnishing = :furnishing)
              AND (:parking IS NULL OR p.parking = :parking)
            """)
    List<Property> search(
            @Param("city") String city,
            @Param("bhk") Integer bhk,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("minArea") BigDecimal minArea,
            @Param("maxArea") BigDecimal maxArea,
            @Param("propertyType") PropertyType propertyType,
            @Param("furnishing") Furnishing furnishing,
            @Param("parking") Boolean parking
    );

}
