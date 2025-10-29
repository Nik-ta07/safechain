package com.safechain.safechain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShareInfoResponse {

    private Long userId;
    private String userEmail;
    private String userFullName;
    private String sharedByEmail;
    private LocalDateTime sharedDate;
}


