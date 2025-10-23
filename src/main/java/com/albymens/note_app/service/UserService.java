package com.albymens.note_app.service;

import com.albymens.note_app.dto.SignupRequest;
import com.albymens.note_app.exception.DuplicateResourceException;
import com.albymens.note_app.exception.ResourceNotFoundException;
import com.albymens.note_app.model.User;
import com.albymens.note_app.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail).orElseThrow(()-> {
            logger.error("User with username: {} not found", usernameOrEmail);
            return new UsernameNotFoundException(String.format("User with username: %s not found", usernameOrEmail));
        });

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities("USER")
                .build();
    }

    public User registerUser(SignupRequest userRequest) {
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new DuplicateResourceException("Email already registered");
        }

        if (userRepository.existsByUsername(userRequest.getUsername())) {
            throw new DuplicateResourceException("Username already taken");
        }

        User user = new User();
        user.setEmail(userRequest.getEmail());
        user.setUsername(userRequest.getUsername());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        return userRepository.save(user);
    }

    public User findByUsernameOrEmail(String usernameOrEmail){
        return userRepository.findByUsernameOrEmail(usernameOrEmail).orElseThrow(()-> {
            logger.error("User not found with username or email: {}", usernameOrEmail);
            return new  ResourceNotFoundException( "User not found with username or email: " + usernameOrEmail);
        });
    }

    Optional<User> findByUsername(String usernameOrEmail){
        return userRepository.findByUsernameOrEmail(usernameOrEmail);
    }

    public boolean validatePassword(String plainTextPassword, String encodedPassword){
        return passwordEncoder.matches(plainTextPassword, encodedPassword);
    }
}

