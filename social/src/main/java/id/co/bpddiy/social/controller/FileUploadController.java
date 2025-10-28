package id.co.bpddiy.social.controller;

import id.co.bpddiy.social.dto.FileUploadResponse;
import id.co.bpddiy.social.dto.UserDto;
import id.co.bpddiy.social.security.UserPrincipal;
import id.co.bpddiy.social.service.FileStorageService;
import id.co.bpddiy.social.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;


@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    private final FileStorageService fileStorageService;
    @Autowired
    private UserService userService;

    // ✅ Constructor injection tanpa Lombok
    public FileUploadController(FileStorageService fileStorageService, UserService userService) {
        this.fileStorageService = fileStorageService;
        this.userService = userService;
    }

    /**
     * Upload file umum
     * Endpoint: POST /api/files/upload
     */
    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        String fileName = fileStorageService.storeFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/files/download/")
                .path(fileName)
                .toUriString();

        FileUploadResponse response = new FileUploadResponse(
                fileName,
                fileDownloadUri,
                file.getContentType(),
                file.getSize(),
                currentUser.getUsername()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Upload profile image user
     * Endpoint: POST /api/files/profile-image
     */
    // @PostMapping("/profile-image")
    @PostMapping(value = "/profile-image", consumes = {"multipart/form-data"})
    public ResponseEntity<UserDto> uploadProfileImage(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        String fileName = fileStorageService.storeFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/files/download/")
                .path(fileName)
                .toUriString();

        UserDto updatedUser = userService.updateProfileImage(currentUser.getId(), fileDownloadUri);

        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Download file
     * Endpoint: GET /api/files/download/{fileName}
     */
    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable String fileName,
            HttpServletRequest request) {

        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        if (resource == null) {
            return ResponseEntity.notFound().build();
        }

        // Try to determine file's content type
        String contentType;
        try {
            contentType = request.getServletContext()
                    .getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            contentType = null;
        }

        // Fallback to default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    /**
     * Delete file
     * Endpoint: DELETE /api/files/{fileName}
     */
    @DeleteMapping("/{fileName:.+}")
    public ResponseEntity<String> deleteFile(
            @PathVariable String fileName,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        boolean deleted = fileStorageService.deleteFile(fileName);

        if (deleted) {
            return ResponseEntity.ok("File deleted successfully: " + fileName);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}