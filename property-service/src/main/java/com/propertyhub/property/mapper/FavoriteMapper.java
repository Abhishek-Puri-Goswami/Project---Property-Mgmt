package com.propertyhub.property.mapper;

import com.propertyhub.property.dto.response.FavoriteResponse;
import com.propertyhub.property.entity.Favorite;
import org.springframework.stereotype.Component;

@Component
public class FavoriteMapper {

    private final PropertyMapper propertyMapper;

    public FavoriteMapper(PropertyMapper propertyMapper) {
        this.propertyMapper = propertyMapper;
    }

    public FavoriteResponse toResponse(Favorite favorite) {
        return new FavoriteResponse(
                favorite.getId(),
                propertyMapper.toSummary(favorite.getProperty()),
                favorite.getCreatedAt()
        );
    }

}
