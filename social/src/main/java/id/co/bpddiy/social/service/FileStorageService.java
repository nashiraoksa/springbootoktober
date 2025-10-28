package id.co.bpddiy.social.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import id.co.bpddiy.social.exception.FileStorageException;
import id.co.bpddiy.social.exception.InvalidFileException;
import id.co.bpddiy.social.exception.ResourceNotFoundException;
import jakarta.annotation.PostConstruct;

/**
 * Service untuk mengelola file storage
 */
@Service
public class FileStorageService {
    
    private final Path fileStorageLocation;
    
    @Value("${file.allowed-extensions}")
    private String allowedExtensions;
    
    @Value("${file.max-size}")
    private long maxFileSize;
    
    public FileStorageService(@Value("${file.upload-dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
    }
    
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException ex) {
            throw new FileStorageException("Could not create upload directory", ex);
        }
    }
    
    /**
     * Menyimpan file dengan validasi tipe dan ukuran
     */
    public String storeFile(MultipartFile file) {
        // Validasi file tidak kosong
        if (file.isEmpty()) {
            throw new InvalidFileException("File tidak boleh kosong");
        }
        
        // Validasi ukuran file
        if (file.getSize() > maxFileSize) {
            throw new InvalidFileException(
                String.format("Ukuran file melebihi batas maksimum %d bytes", maxFileSize)
            );
        }
        
        // Normalize filename
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        
        // Validasi extension
        String extension = FilenameUtils.getExtension(originalFileName);
        if (!isValidExtension(extension)) {
            throw new InvalidFileException(
                String.format("Tipe file .%s tidak diizinkan. Tipe yang diizinkan: %s", 
                    extension, allowedExtensions)
            );
        }
        
        // Generate unique filename
        String fileName = generateUniqueFileName(originalFileName);
        
        try {
            // Check for invalid characters
            if (fileName.contains("..")) {
                throw new FileStorageException("Filename contains invalid path sequence " + fileName);
            }
            
            // Copy file to target location
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName, ex);
        }
    }
    
    /**
     * Load file as Resource
     */
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("File not found: " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new ResourceNotFoundException("File not found: " + fileName, ex);
        }
    }
    
    /**
     * Delete file
     */
    public boolean deleteFile(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            return Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new FileStorageException("Could not delete file " + fileName, ex);
        }
    }
    
    /**
     * Validasi extension file
     */
    private boolean isValidExtension(String extension) {
        if (extension == null || extension.isEmpty()) {
            return false;
        }
        String[] allowedExts = allowedExtensions.split(",");
        return Arrays.stream(allowedExts)
                .anyMatch(ext -> ext.trim().equalsIgnoreCase(extension));
    }
    
    /**
     * Generate unique filename dengan UUID
     */
    private String generateUniqueFileName(String originalFileName) {
        String extension = FilenameUtils.getExtension(originalFileName);
        String baseName = FilenameUtils.getBaseName(originalFileName);
        String uniqueId = UUID.randomUUID().toString();
        return String.format("%s_%s.%s", baseName, uniqueId, extension);
    }
}