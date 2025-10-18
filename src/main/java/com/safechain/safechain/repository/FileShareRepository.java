package com.safechain.safechain.repository;

import com.safechain.safechain.entity.File;
import com.safechain.safechain.entity.FileShare;
import com.safechain.safechain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileShareRepository extends JpaRepository<FileShare, Long> {
    
    /**
     * Find all files shared with a specific user
     * @param sharedWithUser the user who received the shared files
     * @return List<FileShare> - list of file shares
     */
    List<FileShare> findBySharedWithUser(User sharedWithUser);
    
    /**
     * Find all files shared by a specific user
     * @param sharedByUser the user who shared the files
     * @return List<FileShare> - list of file shares
     */
    List<FileShare> findBySharedByUser(User sharedByUser);
    
    /**
     * Check if a file is already shared with a user
     * @param file the file to check
     * @param sharedWithUser the user to check
     * @return Optional<FileShare> - the share if exists, empty otherwise
     */
    Optional<FileShare> findByFileAndSharedWithUser(File file, User sharedWithUser);
    
    /**
     * Find all users who have access to a specific file
     * @param file the file to check
     * @return List<FileShare> - list of shares for the file
     */
    List<FileShare> findByFile(File file);
    
    /**
     * Check if a user has access to a specific file
     * @param fileId the file ID
     * @param userId the user ID
     * @return boolean - true if user has access, false otherwise
     */
    @Query("SELECT CASE WHEN COUNT(fs) > 0 THEN true ELSE false END " +
           "FROM FileShare fs WHERE fs.file.id = :fileId AND fs.sharedWithUser.id = :userId")
    boolean hasUserAccessToFile(@Param("fileId") Long fileId, @Param("userId") Long userId);
}
