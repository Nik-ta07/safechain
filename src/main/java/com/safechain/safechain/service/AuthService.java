package com.safechain.safechain.service;

import com.safechain.safechain.dto.AuthResponse;
import com.safechain.safechain.dto.LoginRequest;
import com.safechain.safechain.dto.RegisterRequest;
import com.safechain.safechain.entity.User;
import com.safechain.safechain.repository.UserRepository;
import com.safechain.safechain.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    
    /**
     * Register a new user
     * @param request registration request
     * @return AuthResponse with JWT token
     */
    public AuthResponse register(RegisterRequest request) {
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("User with email " + request.getEmail() + " already exists");
        }
        
        // Create new user
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.Role.USER);
        
        // Save user to database
        User savedUser = userRepository.save(user);
        
        // Generate JWT token
        String token = jwtUtil.generateToken(savedUser.getEmail());
        
        return new AuthResponse(
            token,
            savedUser.getId(),
            savedUser.getFullName(),
            savedUser.getEmail(),
            savedUser.getRole().name()
        );
    }
    
    /**
     * Login user
     * @param request login request
     * @return AuthResponse with JWT token
     */
    public AuthResponse login(LoginRequest request) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );
        
        // Get user details
        User user = (User) authentication.getPrincipal();
        
        // Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail());
        
        return new AuthResponse(
            token,
            user.getId(),
            user.getFullName(),
            user.getEmail(),
            user.getRole().name()
        );
    }
    
    /**
     * Get current authenticated user
     * @return User - current user
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        throw new RuntimeException("No authenticated user found");
    }
}
