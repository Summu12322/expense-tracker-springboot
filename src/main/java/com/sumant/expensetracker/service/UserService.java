package com.sumant.expensetracker.service;

import com.sumant.expensetracker.dto.LoginRequest;
import com.sumant.expensetracker.dto.RegisterRequest;
import com.sumant.expensetracker.dto.UserResponse;
import com.sumant.expensetracker.entity.User;
import com.sumant.expensetracker.exception.DuplicateUsernameException;
import com.sumant.expensetracker.exception.InvalidCredentialsException;
import com.sumant.expensetracker.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse register(RegisterRequest req) {
        if (userRepository.existsByUsername(req.username()))
            throw new DuplicateUsernameException("Username already exists");
        User u = new User();
        u.setUsername(req.username());
        u.setPassword(passwordEncoder.encode(req.password()));
        u = userRepository.save(u);
        return new UserResponse(u.getId(), u.getUsername());
    }

    public UserResponse login(LoginRequest req) {
        User u = userRepository.findByUsername(req.username())
                .filter(user -> passwordEncoder.matches(req.password(), user.getPassword()))
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));
        return new UserResponse(u.getId(), u.getUsername());
    }
}
