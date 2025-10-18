package com.safechain.safechain.repository;

import com.safechain.safechain.entity.File;
import com.safechain.safechain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    
    /**
     * Find all files uploaded by a specific user
     * @param uploadedBy the user who uploaded the files
     * @return List<File> - list of files uploaded by the user
     */
    List<File> findByUploadedBy(User uploadedBy);
    
    /**
     * Find files shared with a specific user
     * @param userId the user ID to find shared files for
     * @return List<File> - list of files shared with the user
     */
    @Query("SELECT fs.file FROM FileShare fs WHERE fs.sharedWithUser.id = :userId")
    List<File> findFilesSharedWithUser(@Param("userId") Long userId);
    
    /**
     * Find files that a user has access to (own files + shared files)
     * @param userId the user ID
     * @return List<File> - list of accessible files
     */
    @Query("SELECT f FROM File f WHERE f.uploadedBy.id = :userId " +
           "UNION " +
           "SELECT fs.file FROM FileShare fs WHERE fs.sharedWithUser.id = :userId")
    List<File> findAccessibleFilesForUser(@Param("userId") Long userId);
}
