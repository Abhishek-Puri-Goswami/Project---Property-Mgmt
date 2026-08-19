package com.propertyhub.auth.service;

import com.propertyhub.auth.entity.Role;
import com.propertyhub.auth.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private static final String SECRET = "unit-test-secret-key-that-is-long-enough-for-hmac256";

    private final JwtService jwtService = new JwtService(SECRET, 3600000L);

    private User testUser() throws Exception {
        User user = new User("buyer@example.com", "encoded-hash", Role.BUYER);
        var idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(user, 42L);
        return user;
    }

    @Test
    void generatedTokenContainsExpectedClaims() throws Exception {
        String token = jwtService.generateToken(testUser());

        Claims claims = jwtService.parseClaims(token);

        assertThat(claims.getSubject()).isEqualTo("buyer@example.com");
        assertThat(claims.get("role", String.class)).isEqualTo("BUYER");
        assertThat(claims.get("userId", Integer.class)).isEqualTo(42);
    }

    @Test
    void validTokenPassesValidation() throws Exception {
        String token = jwtService.generateToken(testUser());

        assertThat(jwtService.isValid(token)).isTrue();
    }

    @Test
    void expiredTokenFailsValidation() {
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        String expiredToken = Jwts.builder()
                .subject("buyer@example.com")
                .issuedAt(Date.from(LocalDateTime.now().minusHours(2).atZone(java.time.ZoneId.systemDefault()).toInstant()))
                .expiration(Date.from(LocalDateTime.now().minusHours(1).atZone(java.time.ZoneId.systemDefault()).toInstant()))
                .signWith(key)
                .compact();

        assertThat(jwtService.isValid(expiredToken)).isFalse();
    }

    @Test
    void tamperedSignatureFailsValidation() throws Exception {
        String token = jwtService.generateToken(testUser());
        String tampered = token.substring(0, token.length() - 2) + "xx";

        assertThat(jwtService.isValid(tampered)).isFalse();
    }

}
