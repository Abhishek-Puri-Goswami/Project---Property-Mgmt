package com.propertyhub.auth.dto.response;

public record LoginResponse(

        String token,
        String tokenType,
        long expiresInSeconds,
        UserResponse user

) {
}
