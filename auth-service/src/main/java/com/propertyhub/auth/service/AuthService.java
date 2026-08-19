package com.propertyhub.auth.service;

import com.propertyhub.auth.dto.request.RegisterRequest;
import com.propertyhub.auth.dto.response.UserResponse;
import com.propertyhub.auth.entity.User;
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

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse register(RegisterRequest request) {
        log.info("Registration request received");

        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException("A user with email '" + request.email() + "' already exists");
        }

        User user = new User(request.email(), passwordEncoder.encode(request.password()), request.role());
        User saved = userRepository.save(user);

        log.info("User registered successfully");

        return new UserResponse(saved.getId(), saved.getEmail(), saved.getRole(), saved.getCreatedAt());
    }

}
