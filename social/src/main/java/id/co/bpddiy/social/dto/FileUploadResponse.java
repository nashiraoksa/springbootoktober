package id.co.bpddiy.social.dto;

/**
 * DTO untuk response file upload
 */
public class FileUploadResponse {
    private String fileName;
    private String fileDownloadUri;
    private String fileType;
    private long size;
    private String uploadedBy;

    // Konstruktor tanpa argumen
    public FileUploadResponse() {
    }

    // Konstruktor dengan semua argumen
    public FileUploadResponse(String fileName, String fileDownloadUri, String fileType, long size, String uploadedBy) {
        this.fileName = fileName;
        this.fileDownloadUri = fileDownloadUri;
        this.fileType = fileType;
        this.size = size;
        this.uploadedBy = uploadedBy;
    }

    // Getter dan Setter
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileDownloadUri() {
        return fileDownloadUri;
    }

    public void setFileDownloadUri(String fileDownloadUri) {
        this.fileDownloadUri = fileDownloadUri;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    // toString()
    @Override
    public String toString() {
        return "FileUploadResponse{" +
                "fileName='" + fileName + '\'' +
                ", fileDownloadUri='" + fileDownloadUri + '\'' +
                ", fileType='" + fileType + '\'' +
                ", size=" + size +
                ", uploadedBy='" + uploadedBy + '\'' +
                '}';
    }
}