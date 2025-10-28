package id.co.bpddiy.social.dto;

import jakarta.validation.constraints.Size;

/**
 * DTO untuk update user profile
 */
public class UserProfileUpdateDto {

    @Size(max = 100, message = "Nama lengkap maksimal 100 karakter")
    private String fullName;

    @Size(max = 500, message = "Bio maksimal 500 karakter")
    private String bio;

    private String profileImageUrl;

    // Konstruktor tanpa argumen
    public UserProfileUpdateDto() {
    }

    // Konstruktor dengan semua argumen
    public UserProfileUpdateDto(String fullName, String bio, String profileImageUrl) {
        this.fullName = fullName;
        this.bio = bio;
        this.profileImageUrl = profileImageUrl;
    }

    // Getter dan Setter
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    // toString()
    @Override
    public String toString() {
        return "UserProfileUpdateDto{" +
                "fullName='" + fullName + '\'' +
                ", bio='" + bio + '\'' +
                ", profileImageUrl='" + profileImageUrl + '\'' +
                '}';
    }
}