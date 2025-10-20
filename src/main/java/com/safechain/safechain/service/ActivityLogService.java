package com.safechain.safechain.service;

import com.safechain.safechain.dto.ActivityLogResponse;
import com.safechain.safechain.entity.ActivityLog;
import com.safechain.safechain.entity.User;
import com.safechain.safechain.repository.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final AuthService authService;

    public List<ActivityLogResponse> getAllLogs() {
        User currentUser = authService.getCurrentUser();
        if (currentUser.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Access denied. Admin role required.");
        }
        return activityLogRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<ActivityLogResponse> getMyLogs() {
        User currentUser = authService.getCurrentUser();
        return activityLogRepository.findByUser(currentUser).stream().map(this::toDto).collect(Collectors.toList());
    }

    private ActivityLogResponse toDto(ActivityLog log) {
        return new ActivityLogResponse(
                log.getId(),
                log.getEventType().name(),
                log.getUser() != null ? log.getUser().getId() : null,
                log.getUser() != null ? log.getUser().getFullName() : null,
                log.getFile() != null ? log.getFile().getId() : null,
                log.getFile() != null ? log.getFile().getFileName() : null,
                log.getDetails(),
                log.getCreatedAt()
        );
    }
}


