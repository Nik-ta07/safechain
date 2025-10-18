package com.safechain.safechain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileResponse {
    
    private Long id;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String uploadedBy;
    private LocalDateTime uploadDate;
    private String downloadUrl;
    
    public FileResponse(Long id, String fileName, String fileType, Long fileSize, 
                       String uploadedBy, LocalDateTime uploadDate) {
        this.id = id;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.uploadedBy = uploadedBy;
        this.uploadDate = uploadDate;
        this.downloadUrl = "/api/files/" + id + "/download";
    }
}
