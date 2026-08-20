package com.propertyhub.property.dto.response;

import java.time.LocalDateTime;

public record FavoriteResponse(

        Long id,
        PropertySummaryResponse property,
        LocalDateTime createdAt

) {
}
