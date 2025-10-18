package com.safechain.safechain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ShareFileRequest {
    
    @NotNull(message = "File ID is required")
    private Long fileId;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String userEmail;
}
