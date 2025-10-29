package com.safechain.safechain.service;

import com.safechain.safechain.dto.FileResponse;
import com.safechain.safechain.dto.ShareInfoResponse;
import com.safechain.safechain.dto.ShareFileRequest;
import com.safechain.safechain.entity.ActivityLog;
import com.safechain.safechain.entity.File;
import com.safechain.safechain.entity.FileShare;
import com.safechain.safechain.entity.User;
import com.safechain.safechain.repository.FileRepository;
import com.safechain.safechain.repository.FileShareRepository;
import com.safechain.safechain.repository.ActivityLogRepository;
import com.safechain.safechain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;
    private final FileShareRepository fileShareRepository;
    private final UserRepository userRepository;
    private final ActivityLogRepository activityLogRepository;
    private final AuthService authService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
     * Upload a file
     * 
     * @param file the multipart file to upload
     * @return FileResponse with file details
     */
    public FileResponse uploadFile(MultipartFile file) throws IOException {
        User currentUser = authService.getCurrentUser();

        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

        // Save file to disk
        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Create file entity
        File fileEntity = new File();
        fileEntity.setFileName(originalFilename);
        fileEntity.setFilePath(filePath.toString());
        fileEntity.setFileType(file.getContentType());
        fileEntity.setFileSize(file.getSize());
        fileEntity.setUploadedBy(currentUser);
        fileEntity.setUploadDate(LocalDateTime.now());

        // Save to database
        File savedFile = fileRepository.save(fileEntity);

        // Log activity
        ActivityLog log = new ActivityLog();
        log.setEventType(ActivityLog.EventType.UPLOAD);
        log.setUser(currentUser);
        log.setFile(savedFile);
        log.setDetails("Uploaded file: " + savedFile.getFileName());
        activityLogRepository.save(log);

        return new FileResponse(
                savedFile.getId(),
                savedFile.getFileName(),
                savedFile.getFileType(),
                savedFile.getFileSize(),
                savedFile.getUploadedBy().getFullName(),
                savedFile.getUploadDate());
    }

    /**
     * Get files uploaded by current user
     * 
     * @return List<FileResponse> - user's files
     */
    public List<FileResponse> getMyFiles() {
        User currentUser = authService.getCurrentUser();
        List<File> files = fileRepository.findByUploadedBy(currentUser);

        return files.stream()
                .map(file -> new FileResponse(
                        file.getId(),
                        file.getFileName(),
                        file.getFileType(),
                        file.getFileSize(),
                        file.getUploadedBy().getFullName(),
                        file.getUploadDate()))
                .collect(Collectors.toList());
    }

    /**
     * Get files shared with current user
     * 
     * @return List<FileResponse> - shared files
     */
    public List<FileResponse> getSharedFiles() {
        User currentUser = authService.getCurrentUser();
        List<File> files = fileRepository.findFilesSharedWithUser(currentUser.getId());

        return files.stream()
                .map(file -> new FileResponse(
                        file.getId(),
                        file.getFileName(),
                        file.getFileType(),
                        file.getFileSize(),
                        file.getUploadedBy().getFullName(),
                        file.getUploadDate()))
                .collect(Collectors.toList());
    }

    /**
     * Share a file with another user
     * 
     * @param request share request
     * @return String - success message
     */
    public String shareFile(ShareFileRequest request) {
        User currentUser = authService.getCurrentUser();

        // Find the file
        File file = fileRepository.findById(request.getFileId())
                .orElseThrow(() -> new RuntimeException("File not found"));

        // Check if user owns the file
        if (!file.getUploadedBy().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only share files you own");
        }

        // Find the user to share with
        User userToShareWith = userRepository.findByEmail(request.getUserEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if already shared
        if (fileShareRepository.findByFileAndSharedWithUser(file, userToShareWith).isPresent()) {
            throw new RuntimeException("File already shared with this user");
        }

        // Create share record
        FileShare fileShare = new FileShare();
        fileShare.setFile(file);
        fileShare.setSharedWithUser(userToShareWith);
        fileShare.setSharedByUser(currentUser);
        fileShare.setSharedDate(LocalDateTime.now());

        fileShareRepository.save(fileShare);

        // Log activity
        ActivityLog log = new ActivityLog();
        log.setEventType(ActivityLog.EventType.SHARE);
        log.setUser(currentUser);
        log.setFile(file);
        log.setDetails("Shared file with " + userToShareWith.getEmail());
        activityLogRepository.save(log);

        return "File shared successfully with " + userToShareWith.getFullName();
    }

    /**
     * Download a file
     * 
     * @param fileId the file ID
     * @return File - the file entity
     */
    public File downloadFile(Long fileId) {
        User currentUser = authService.getCurrentUser();

        // Find the file
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        // Check if user has access (owner or shared with)
        boolean isOwner = file.getUploadedBy().getId().equals(currentUser.getId());
        boolean isShared = fileShareRepository.hasUserAccessToFile(fileId, currentUser.getId());

        if (!isOwner && !isShared) {
            throw new RuntimeException("You don't have access to this file");
        }

        // Log activity
        ActivityLog log = new ActivityLog();
        log.setEventType(ActivityLog.EventType.DOWNLOAD);
        log.setUser(currentUser);
        log.setFile(file);
        log.setDetails("Downloaded file: " + file.getFileName());
        activityLogRepository.save(log);

        return file;
    }

    /**
     * Delete a file (owner or admin)
     */
    public String deleteFile(Long fileId) throws IOException {
        User currentUser = authService.getCurrentUser();
        File file = fileRepository.findById(fileId).orElseThrow(() -> new RuntimeException("File not found"));

        boolean isOwner = file.getUploadedBy().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == User.Role.ADMIN;
        if (!isOwner && !isAdmin) {
            throw new RuntimeException("You don't have permission to delete this file");
        }

        // Delete shares
        fileShareRepository.deleteByFile(file);

        // Delete file on disk
        Path filePath = Paths.get(file.getFilePath());
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // Continue, but report failure
        }

        // Delete metadata
        fileRepository.delete(file);

        // Log activity
        ActivityLog log = new ActivityLog();
        log.setEventType(ActivityLog.EventType.DELETE);
        log.setUser(currentUser);
        log.setFile(null);
        log.setDetails("Deleted file id=" + fileId + ", name=" + file.getFileName());
        activityLogRepository.save(log);

        return "File deleted";
    }

    /**
     * List users who have access to a file (owner-only)
     *
     * @param fileId the file ID
     * @return list of share info
     */
    public List<ShareInfoResponse> listSharedUsers(Long fileId) {
        User currentUser = authService.getCurrentUser();

        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        if (!file.getUploadedBy().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only view shares for files you own");
        }

        List<FileShare> shares = fileShareRepository.findByFile(file);
        return shares.stream()
                .map(share -> new ShareInfoResponse(
                        share.getSharedWithUser().getId(),
                        share.getSharedWithUser().getEmail(),
                        share.getSharedWithUser().getFullName(),
                        share.getSharedByUser().getEmail(),
                        share.getSharedDate()))
                .collect(Collectors.toList());
    }

    /**
     * Revoke a specific user's access to a file (owner-only)
     *
     * @param fileId    the file ID
     * @param userEmail the user's email to unshare
     * @return success message
     */
    public String unshareFile(Long fileId, String userEmail) {
        User currentUser = authService.getCurrentUser();

        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        if (!file.getUploadedBy().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only modify shares for files you own");
        }

        User targetUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<FileShare> existing = fileShareRepository.findByFileAndSharedWithUser(file, targetUser);
        if (existing.isEmpty()) {
            throw new RuntimeException("This file is not shared with the specified user");
        }

        fileShareRepository.delete(existing.get());

        ActivityLog log = new ActivityLog();
        log.setEventType(ActivityLog.EventType.SHARE);
        log.setUser(currentUser);
        log.setFile(file);
        log.setDetails("Revoked access for " + targetUser.getEmail());
        activityLogRepository.save(log);

        return "Access revoked for " + targetUser.getFullName();
    }
}
