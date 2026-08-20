package com.propertyhub.property.mapper;

import com.propertyhub.property.dto.response.VisitResponse;
import com.propertyhub.property.entity.Visit;
import org.springframework.stereotype.Component;

@Component
public class VisitMapper {

    public VisitResponse toResponse(Visit visit) {
        return new VisitResponse(
                visit.getId(),
                visit.getProperty().getId(),
                visit.getProperty().getTitle(),
                visit.getUserId(),
                visit.getScheduledAt(),
                visit.getStatus(),
                visit.getNotes(),
                visit.getCreatedAt()
        );
    }

}
