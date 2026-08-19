package com.propertyhub.auth.service;

import com.propertyhub.auth.dto.request.LoginRequest;
import com.propertyhub.auth.dto.request.RegisterRequest;
import com.propertyhub.auth.dto.response.LoginResponse;
import com.propertyhub.auth.dto.response.UserResponse;
import com.propertyhub.auth.entity.Role;
import com.propertyhub.auth.entity.User;
import com.propertyhub.auth.exception.InvalidCredentialsException;
import com.propertyhub.auth.exception.UserAlreadyExistsException;
import com.propertyhub.auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    private AuthService authService;

    @Test
    void registersUserWithEncodedPassword() {
        authService = new AuthService(userRepository, passwordEncoder, jwtService);
        RegisterRequest request = new RegisterRequest("buyer@example.com", "secret123", Role.BUYER);

        when(userRepository.existsByEmail("buyer@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("encoded-hash");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            return u;
        });

        UserResponse response = authService.register(request);

        assertThat(response.email()).isEqualTo("buyer@example.com");
        assertThat(response.role()).isEqualTo(Role.BUYER);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getPassword()).isEqualTo("encoded-hash");
    }

    @Test
    void throwsWhenEmailAlreadyRegistered() {
        authService = new AuthService(userRepository, passwordEncoder, jwtService);
        RegisterRequest request = new RegisterRequest("buyer@example.com", "secret123", Role.BUYER);

        when(userRepository.existsByEmail("buyer@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(UserAlreadyExistsException.class);

        verify(userRepository, org.mockito.Mockito.never()).save(any(User.class));
    }

    @Test
    void loginReturnsTokenOnValidCredentials() {
        authService = new AuthService(userRepository, passwordEncoder, jwtService);
        User user = new User("buyer@example.com", "encoded-hash", Role.BUYER);
        LoginRequest request = new LoginRequest("buyer@example.com", "secret123");

        when(userRepository.findByEmail("buyer@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret123", "encoded-hash")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("signed-jwt");
        when(jwtService.getExpirationSeconds()).thenReturn(3600L);

        LoginResponse response = authService.login(request);

        assertThat(response.token()).isEqualTo("signed-jwt");
        assertThat(response.user().email()).isEqualTo("buyer@example.com");
    }

    @Test
    void throwsOnWrongPassword() {
        authService = new AuthService(userRepository, passwordEncoder, jwtService);
        User user = new User("buyer@example.com", "encoded-hash", Role.BUYER);
        LoginRequest request = new LoginRequest("buyer@example.com", "wrong-password");

        when(userRepository.findByEmail("buyer@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "encoded-hash")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void throwsOnUnknownEmail() {
        authService = new AuthService(userRepository, passwordEncoder, jwtService);
        LoginRequest request = new LoginRequest("nobody@example.com", "secret123");

        when(userRepository.findByEmail("nobody@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void getCurrentUserReturnsUserResponse() {
        authService = new AuthService(userRepository, passwordEncoder, jwtService);
        User user = new User("buyer@example.com", "encoded-hash", Role.BUYER);

        when(userRepository.findByEmail("buyer@example.com")).thenReturn(Optional.of(user));

        UserResponse response = authService.getCurrentUser("buyer@example.com");

        assertThat(response.email()).isEqualTo("buyer@example.com");
        assertThat(response.role()).isEqualTo(Role.BUYER);
    }

    @Test
    void getCurrentUserThrowsWhenUserNotFound() {
        authService = new AuthService(userRepository, passwordEncoder, jwtService);

        when(userRepository.findByEmail("nobody@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.getCurrentUser("nobody@example.com"))
                .isInstanceOf(com.propertyhub.auth.exception.ResourceNotFoundException.class);
    }

}
