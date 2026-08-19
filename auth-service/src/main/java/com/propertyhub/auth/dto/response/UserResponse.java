package com.propertyhub.auth.dto.response;

import com.propertyhub.auth.entity.Role;

import java.time.LocalDateTime;

public record UserResponse(

        Long id,
        String email,
        Role role,
        LocalDateTime createdAt

) {
}
