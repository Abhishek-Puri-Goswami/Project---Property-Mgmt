package com.propertyhub.ai.client;

import com.propertyhub.ai.dto.PropertyDto;
import com.propertyhub.ai.dto.PropertySummaryDto;
import com.propertyhub.ai.exception.PropertyNotFoundException;
import com.propertyhub.ai.service.PropertyClientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Not run as part of {@code mvn verify} (class name intentionally omits the
 * Surefire "Test"/"Tests"/"TestCase" suffix so it is excluded from the default
 * discovery glob). Run explicitly once eureka-server and property-service are
 * both up, with at least one property registered:
 *
 * <pre>
 * mvn test -Dtest=PropertyFeignClientLiveCheck
 * </pre>
 *
 * Set PROPERTY_ID to an id that actually exists in the running property-service
 * before invoking (see STEP-08 result notes).
 */
@SpringBootTest
class PropertyFeignClientLiveCheck {

    private static final Long PROPERTY_ID = 1L;

    @Autowired
    private PropertyClientService propertyClientService;

    @Test
    void getPropertySucceedsAgainstLivePropertyService() {
        PropertyDto property = propertyClientService.getProperty(PROPERTY_ID);

        assertThat(property.id()).isEqualTo(PROPERTY_ID);
        assertThat(property.city()).isNotBlank();
    }

    @Test
    void searchPropertiesSucceedsAgainstLivePropertyService() {
        List<PropertySummaryDto> results = propertyClientService.searchProperties(null, null, null, null);

        assertThat(results).isNotEmpty();
    }

    @Test
    void getPropertiesByIdsSucceedsAgainstLivePropertyService() {
        List<PropertySummaryDto> results = propertyClientService.getPropertiesByIds(List.of(PROPERTY_ID));

        assertThat(results).anySatisfy(p -> assertThat(p.id()).isEqualTo(PROPERTY_ID));
    }

    @Test
    void getPropertyOnNonexistentIdMapsTo404() {
        assertThatThrownBy(() -> propertyClientService.getProperty(999999L))
                .isInstanceOf(PropertyNotFoundException.class);
    }

}
