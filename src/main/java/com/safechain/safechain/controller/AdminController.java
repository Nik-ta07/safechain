package com.safechain.safechain.controller;

import com.safechain.safechain.dto.UserResponse;
import com.safechain.safechain.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    
    private final AdminService adminService;
    
    /**
     * Get all users (admin only)
     * GET /api/admin/users
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        try {
            List<UserResponse> users = adminService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get users: " + e.getMessage());
        }
    }
    
    /**
     * Delete a user (admin only)
     * DELETE /api/admin/users/{id}
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        try {
            String message = adminService.deleteUser(id);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete user: " + e.getMessage());
        }
    }
}
