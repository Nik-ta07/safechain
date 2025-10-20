package com.safechain.safechain.controller;

import com.safechain.safechain.dto.ActivityLogResponse;
import com.safechain.safechain.service.ActivityLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/activity")
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    /**
     * Get current user's activity logs
     * GET /api/activity/my
     */
    @GetMapping("/my")
    public ResponseEntity<List<ActivityLogResponse>> getMyActivityLogs() {
        List<ActivityLogResponse> logs = activityLogService.getMyLogs();
        return ResponseEntity.ok(logs);
    }
}
