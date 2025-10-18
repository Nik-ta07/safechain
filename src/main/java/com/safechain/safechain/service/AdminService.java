package com.safechain.safechain.service;

import com.safechain.safechain.dto.UserResponse;
import com.safechain.safechain.entity.User;
import com.safechain.safechain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {
    
    private final UserRepository userRepository;
    private final AuthService authService;
    
    /**
     * Get all users (admin only)
     * @return List<UserResponse> - all users
     */
    public List<UserResponse> getAllUsers() {
        User currentUser = authService.getCurrentUser();
        
        // Check if user is admin
        if (currentUser.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Access denied. Admin role required.");
        }
        
        List<User> users = userRepository.findAll();
        
        return users.stream()
                .map(user -> new UserResponse(
                    user.getId(),
                    user.getFullName(),
                    user.getEmail(),
                    user.getRole().name(),
                    user.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }
    
    /**
     * Delete a user (admin only)
     * @param userId the user ID to delete
     * @return String - success message
     */
    public String deleteUser(Long userId) {
        User currentUser = authService.getCurrentUser();
        
        // Check if user is admin
        if (currentUser.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Access denied. Admin role required.");
        }
        
        // Check if trying to delete self
        if (currentUser.getId().equals(userId)) {
            throw new RuntimeException("Cannot delete your own account");
        }
        
        // Find and delete user
        User userToDelete = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        userRepository.delete(userToDelete);
        
        return "User deleted successfully";
    }
}
