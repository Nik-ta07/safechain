package com.safechain.safechain.controller;

import com.safechain.safechain.dto.FileResponse;
import com.safechain.safechain.dto.ShareFileRequest;
import com.safechain.safechain.entity.File;
import com.safechain.safechain.service.FileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {
    
    private final FileService fileService;
    
    /**
     * Upload a file
     * POST /api/files/upload
     */
    @PostMapping("/upload")
    public ResponseEntity<FileResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            FileResponse response = fileService.uploadFile(file);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }
    }
    
    /**
     * Get user's uploaded files
     * GET /api/files/my
     */
    @GetMapping("/my")
    public ResponseEntity<List<FileResponse>> getMyFiles() {
        List<FileResponse> files = fileService.getMyFiles();
        return ResponseEntity.ok(files);
    }
    
    /**
     * Get files shared with user
     * GET /api/files/shared-with-me
     */
    @GetMapping("/shared-with-me")
    public ResponseEntity<List<FileResponse>> getSharedFiles() {
        List<FileResponse> files = fileService.getSharedFiles();
        return ResponseEntity.ok(files);
    }
    
    /**
     * Download a file
     * GET /api/files/{id}/download
     */
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        try {
            File file = fileService.downloadFile(id);
            
            Path filePath = Paths.get(file.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());
            
            if (!resource.exists()) {
                throw new RuntimeException("File not found on disk");
            }
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(file.getFileType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"" + file.getFileName() + "\"")
                    .body(resource);
                    
        } catch (Exception e) {
            throw new RuntimeException("File download failed: " + e.getMessage());
        }
    }
    
    /**
     * Share a file with another user
     * POST /api/files/share
     */
    @PostMapping("/share")
    public ResponseEntity<String> shareFile(@Valid @RequestBody ShareFileRequest request) {
        try {
            String message = fileService.shareFile(request);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            throw new RuntimeException("File sharing failed: " + e.getMessage());
        }
    }
}
