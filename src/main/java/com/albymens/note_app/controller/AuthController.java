package com.albymens.note_app.controller;

import com.albymens.note_app.dto.AuthRequest;
import com.albymens.note_app.dto.AuthResponse;
import com.albymens.note_app.model.User;
import com.albymens.note_app.service.JwtService;
import com.albymens.note_app.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    UserService userService;
    @Autowired
    JwtService jwtService;
    @Autowired
    AuthenticationManager authenticationManager;


    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid  @RequestBody AuthRequest userRequest){
        try {
            logger.info("Signup attempt for username: {}", userRequest.getUsername());
            User user = userService.registerUser(userRequest);
            String token = jwtService.generateToken(user.getUsername(), user.getId());

            AuthResponse response = new AuthResponse(
                    token,
                    user.getId(),
                    user.getUsername(),
                    user.getEmail()
            );

            logger.info("User registered successfully {}", user.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e){
            logger.warn("Signup failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new AuthResponse(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid  @RequestBody AuthRequest request) {
        try {
            logger.info("Login attempt for: {}", request.getUsernameOrEmail());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsernameOrEmail(), request.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            User user = userService.findByUsernameOrEmail(request.getUsernameOrEmail());

            if (user == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AuthResponse("User not found"));
            }

            String token = jwtService.generateToken(user.getUsername(), user.getId());
            AuthResponse response = new AuthResponse(
                    token,
                    user.getId(),
                    user.getUsername(),
                    user.getEmail()
            );

            logger.info("User logged in successfully: {}", user.getUsername());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Login failed for {}: {}", request.getUsernameOrEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse("Invalid credentials"));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = authentication.getName();
        User user = userService.findByUsernameOrEmail(username);

        if (user != null) {
            AuthResponse response = new AuthResponse(null, user.getId(), user.getUsername(), user.getEmail());
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}
