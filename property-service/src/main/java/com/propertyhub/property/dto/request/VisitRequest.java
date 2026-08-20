package com.propertyhub.property.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record VisitRequest(

        @NotNull(message = "User id is required")
        Long userId,

        @NotNull(message = "Scheduled time is required")
        @Future(message = "Scheduled time must be in the future")
        LocalDateTime scheduledAt,

        String notes

) {
}
