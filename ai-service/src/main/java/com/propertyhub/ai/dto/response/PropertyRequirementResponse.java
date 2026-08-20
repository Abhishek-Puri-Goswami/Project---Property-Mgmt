package com.propertyhub.ai.dto.response;

import java.math.BigDecimal;

public record PropertyRequirementResponse(

        String city,
        Integer bhk,
        BigDecimal maxBudget,
        Boolean parkingRequired

) {
}
