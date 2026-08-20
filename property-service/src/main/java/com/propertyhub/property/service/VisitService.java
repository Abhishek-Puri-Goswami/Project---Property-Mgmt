package com.propertyhub.property.service;

import com.propertyhub.property.dto.request.VisitRequest;
import com.propertyhub.property.dto.response.VisitResponse;
import com.propertyhub.property.entity.Property;
import com.propertyhub.property.entity.Visit;
import com.propertyhub.property.exception.ResourceNotFoundException;
import com.propertyhub.property.mapper.VisitMapper;
import com.propertyhub.property.repository.PropertyRepository;
import com.propertyhub.property.repository.VisitRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class VisitService {

    private final VisitRepository visitRepository;
    private final PropertyRepository propertyRepository;
    private final VisitMapper visitMapper;

    public VisitService(VisitRepository visitRepository, PropertyRepository propertyRepository, VisitMapper visitMapper) {
        this.visitRepository = visitRepository;
        this.propertyRepository = propertyRepository;
        this.visitMapper = visitMapper;
    }

    public VisitResponse schedule(Long propertyId, VisitRequest request) {
        log.info("Visit scheduling requested");

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property with id " + propertyId + " was not found"));

        Visit saved = visitRepository.save(new Visit(request.userId(), property, request.scheduledAt(), request.notes()));

        log.info("Visit scheduled successfully");

        return visitMapper.toResponse(saved);
    }

    public List<VisitResponse> listByUser(Long userId) {
        return visitRepository.findByUserId(userId).stream()
                .map(visitMapper::toResponse)
                .toList();
    }

    public List<VisitResponse> listByAgent(Long agentId) {
        return visitRepository.findByPropertyAgentId(agentId).stream()
                .map(visitMapper::toResponse)
                .toList();
    }

}
