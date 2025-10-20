package com.safechain.safechain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLogResponse {
    private Long id;
    private String eventType;
    private Long userId;
    private String userName;
    private Long fileId;
    private String fileName;
    private String details;
    private LocalDateTime createdAt;
}
