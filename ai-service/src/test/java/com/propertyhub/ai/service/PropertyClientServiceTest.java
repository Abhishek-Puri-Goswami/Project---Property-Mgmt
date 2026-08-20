package com.propertyhub.ai.service;

import com.propertyhub.ai.client.PropertyFeignClient;
import com.propertyhub.ai.dto.PropertyDto;
import com.propertyhub.ai.dto.PropertySummaryDto;
import com.propertyhub.ai.exception.AiServiceException;
import com.propertyhub.ai.exception.PropertyNotFoundException;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PropertyClientServiceTest {

    @Mock
    private PropertyFeignClient propertyFeignClient;

    private PropertyClientService propertyClientService;

    private Request dummyRequest() {
        return Request.create(Request.HttpMethod.GET, "/api/properties/1",
                java.util.Map.of(), null, StandardCharsets.UTF_8, new RequestTemplate());
    }

    @Test
    void getPropertyReturnsDtoOnSuccess() {
        propertyClientService = new PropertyClientService(propertyFeignClient);
        PropertyDto dto = new PropertyDto(1L, "Title", "Desc", "Pune", new BigDecimal("100"), 2,
                new BigDecimal("100"), "APARTMENT", "FURNISHED", true, 1L);
        when(propertyFeignClient.getProperty(1L)).thenReturn(dto);

        PropertyDto result = propertyClientService.getProperty(1L);

        assertThat(result.city()).isEqualTo("Pune");
    }

    @Test
    void getPropertyThrowsPropertyNotFoundOn404() {
        propertyClientService = new PropertyClientService(propertyFeignClient);
        FeignException.NotFound notFound = new FeignException.NotFound("not found", dummyRequest(), null, null);
        when(propertyFeignClient.getProperty(99L)).thenThrow(notFound);

        assertThatThrownBy(() -> propertyClientService.getProperty(99L))
                .isInstanceOf(PropertyNotFoundException.class);
    }

    @Test
    void getPropertyThrowsAiServiceExceptionOnOtherFeignError() {
        propertyClientService = new PropertyClientService(propertyFeignClient);
        FeignException.InternalServerError serverError =
                new FeignException.InternalServerError("boom", dummyRequest(), null, null);
        when(propertyFeignClient.getProperty(1L)).thenThrow(serverError);

        assertThatThrownBy(() -> propertyClientService.getProperty(1L))
                .isInstanceOf(AiServiceException.class);
    }

    @Test
    void searchPropertiesReturnsResultsOnSuccess() {
        propertyClientService = new PropertyClientService(propertyFeignClient);
        PropertySummaryDto summary = new PropertySummaryDto(1L, "Title", "Pune", new BigDecimal("100"), 2,
                new BigDecimal("100"), "APARTMENT", "FURNISHED", true);
        when(propertyFeignClient.searchProperties("Pune", 2, null, null)).thenReturn(List.of(summary));

        List<PropertySummaryDto> results = propertyClientService.searchProperties("Pune", 2, null, null);

        assertThat(results).hasSize(1);
    }

    @Test
    void getPropertiesByIdsReturnsResultsOnSuccess() {
        propertyClientService = new PropertyClientService(propertyFeignClient);
        PropertySummaryDto summary = new PropertySummaryDto(1L, "Title", "Pune", new BigDecimal("100"), 2,
                new BigDecimal("100"), "APARTMENT", "FURNISHED", true);
        when(propertyFeignClient.getPropertiesByIds(List.of(1L, 2L))).thenReturn(List.of(summary));

        List<PropertySummaryDto> results = propertyClientService.getPropertiesByIds(List.of(1L, 2L));

        assertThat(results).hasSize(1);
    }

}
