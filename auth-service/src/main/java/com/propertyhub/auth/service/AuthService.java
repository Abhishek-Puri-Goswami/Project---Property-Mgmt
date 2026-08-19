package com.propertyhub.auth.service;

import com.propertyhub.auth.dto.request.LoginRequest;
import com.propertyhub.auth.dto.request.RegisterRequest;
import com.propertyhub.auth.dto.response.LoginResponse;
import com.propertyhub.auth.dto.response.UserResponse;
import com.propertyhub.auth.entity.User;
import com.propertyhub.auth.exception.InvalidCredentialsException;
import com.propertyhub.auth.exception.ResourceNotFoundException;
import com.propertyhub.auth.exception.UserAlreadyExistsException;
import com.propertyhub.auth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public UserResponse register(RegisterRequest request) {
        log.info("Registration request received");

        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException("A user with email '" + request.email() + "' already exists");
        }

        User user = new User(request.email(), passwordEncoder.encode(request.password()), request.role());
        User saved = userRepository.save(user);

        log.info("User registered successfully");

        return toUserResponse(saved);
    }

    public LoginResponse login(LoginRequest request) {
        log.info("Login attempt received");

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> {
                    log.warn("Authentication failed");
                    return new InvalidCredentialsException("Invalid email or password");
                });

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            log.warn("Authentication failed");
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = jwtService.generateToken(user);
        log.info("Login successful");

        return new LoginResponse(token, "Bearer", jwtService.getExpirationSeconds(), toUserResponse(user));
    }

    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return toUserResponse(user);
    }

    private UserResponse toUserResponse(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getRole(), user.getCreatedAt());
    }

}
