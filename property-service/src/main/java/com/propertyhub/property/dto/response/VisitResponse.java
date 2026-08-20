package com.propertyhub.property.dto.response;

import com.propertyhub.property.entity.VisitStatus;

import java.time.LocalDateTime;

public record VisitResponse(

        Long id,
        Long propertyId,
        String propertyTitle,
        Long userId,
        LocalDateTime scheduledAt,
        VisitStatus status,
        String notes,
        LocalDateTime createdAt

) {
}
