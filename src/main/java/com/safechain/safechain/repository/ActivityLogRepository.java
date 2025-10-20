package com.safechain.safechain.repository;

import com.safechain.safechain.entity.ActivityLog;
import com.safechain.safechain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    List<ActivityLog> findByUser(User user);
}


