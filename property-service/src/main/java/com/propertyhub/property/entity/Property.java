package com.propertyhub.property.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.CheckConstraint;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "properties", check = {
        @CheckConstraint(name = "properties_price_positive", constraint = "price > 0"),
        @CheckConstraint(name = "properties_bhk_positive", constraint = "bhk > 0"),
        @CheckConstraint(name = "properties_area_positive", constraint = "area > 0")
})
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer bhk;

    @Column(nullable = false)
    private BigDecimal area;

    @Enumerated(EnumType.STRING)
    @Column(name = "property_type", nullable = false)
    private PropertyType propertyType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Furnishing furnishing;

    @Column(nullable = false)
    private boolean parking;

    @Column(name = "agent_id", nullable = false)
    private Long agentId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Property(String title, String description, String city, BigDecimal price, Integer bhk, BigDecimal area,
                     PropertyType propertyType, Furnishing furnishing, boolean parking, Long agentId) {
        this.title = title;
        this.description = description;
        this.city = city;
        this.price = price;
        this.bhk = bhk;
        this.area = area;
        this.propertyType = propertyType;
        this.furnishing = furnishing;
        this.parking = parking;
        this.agentId = agentId;
    }

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void update(String title, String description, String city, BigDecimal price, Integer bhk, BigDecimal area,
                        PropertyType propertyType, Furnishing furnishing, boolean parking, Long agentId) {
        this.title = title;
        this.description = description;
        this.city = city;
        this.price = price;
        this.bhk = bhk;
        this.area = area;
        this.propertyType = propertyType;
        this.furnishing = furnishing;
        this.parking = parking;
        this.agentId = agentId;
    }

}
