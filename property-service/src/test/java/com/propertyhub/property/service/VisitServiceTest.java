package com.propertyhub.property.service;

import com.propertyhub.property.dto.request.VisitRequest;
import com.propertyhub.property.dto.response.VisitResponse;
import com.propertyhub.property.entity.Furnishing;
import com.propertyhub.property.entity.Property;
import com.propertyhub.property.entity.PropertyType;
import com.propertyhub.property.entity.Visit;
import com.propertyhub.property.entity.VisitStatus;
import com.propertyhub.property.exception.ResourceNotFoundException;
import com.propertyhub.property.mapper.VisitMapper;
import com.propertyhub.property.repository.PropertyRepository;
import com.propertyhub.property.repository.VisitRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VisitServiceTest {

    @Mock
    private VisitRepository visitRepository;

    @Mock
    private PropertyRepository propertyRepository;

    private final VisitMapper visitMapper = new VisitMapper();
    private VisitService visitService;

    private Property sampleProperty() {
        return new Property("2BHK in Hinjewadi", "Spacious flat", "Pune", new BigDecimal("7200000"),
                2, new BigDecimal("1150"), PropertyType.APARTMENT, Furnishing.SEMI_FURNISHED, true, 1L);
    }

    @Test
    void scheduleCreatesPendingVisitForExistingProperty() {
        visitService = new VisitService(visitRepository, propertyRepository, visitMapper);
        Property property = sampleProperty();
        LocalDateTime scheduledAt = LocalDateTime.now().plusDays(2);
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));
        when(visitRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        VisitResponse response = visitService.schedule(1L, new VisitRequest(5L, scheduledAt, "Weekend visit"));

        assertThat(response.status()).isEqualTo(VisitStatus.PENDING);
        assertThat(response.propertyTitle()).isEqualTo("2BHK in Hinjewadi");
        assertThat(response.notes()).isEqualTo("Weekend visit");
    }

    @Test
    void scheduleThrowsResourceNotFoundExceptionWhenPropertyMissing() {
        visitService = new VisitService(visitRepository, propertyRepository, visitMapper);
        when(propertyRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> visitService.schedule(99L, new VisitRequest(5L, LocalDateTime.now().plusDays(1), null)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void listByUserReturnsMappedVisits() {
        visitService = new VisitService(visitRepository, propertyRepository, visitMapper);
        Visit visit = new Visit(5L, sampleProperty(), LocalDateTime.now().plusDays(1), null);
        when(visitRepository.findByUserId(5L)).thenReturn(List.of(visit));

        List<VisitResponse> results = visitService.listByUser(5L);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).userId()).isEqualTo(5L);
    }

    @Test
    void listByAgentReturnsMappedVisits() {
        visitService = new VisitService(visitRepository, propertyRepository, visitMapper);
        Visit visit = new Visit(5L, sampleProperty(), LocalDateTime.now().plusDays(1), null);
        when(visitRepository.findByPropertyAgentId(1L)).thenReturn(List.of(visit));

        List<VisitResponse> results = visitService.listByAgent(1L);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).propertyTitle()).isEqualTo("2BHK in Hinjewadi");
    }

}
