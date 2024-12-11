package org.gvfbla;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

/**
 * Represents a generic user account in the system.
 * Includes common attributes and methods for all account types.
 */
public class account {
    private String id;
    private String username;
    private String passwordHash;
    private String salt;
    private String email;
    private String role;
    private boolean isActive;
    private String lastLoginDate;

    public account(String username, String password, String email, String role) {
        this.id = UUID.randomUUID().toString();
        this.username = username;
        this.salt = generateSalt();
        this.passwordHash = hashPassword(password, this.salt);
        this.email = email;
        this.role = role;
        this.isActive = true;
        this.lastLoginDate = "";
    }

    // Password encryption methods
    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);
        return Base64.getEncoder().encodeToString(saltBytes);
    }

    private String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(Base64.getDecoder().decode(salt));
            byte[] hashedPassword = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    /**
     * Verifies if the provided password matches the stored password hash.
     *
     * @param password the password to verify
     * @return true if the password matches, false otherwise
     */
    public boolean verifyPassword(String password) {
        String hashedAttempt = hashPassword(password, this.salt);
        return hashedAttempt.equals(this.passwordHash);
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getSalt() { return salt; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    public String getLastLoginDate() { return lastLoginDate; }
    public void setLastLoginDate(String lastLoginDate) { this.lastLoginDate = lastLoginDate; }

    /**
     * Determines if the account has permission to perform a specific action.
     *
     * @param action the action to check permission for
     * @return true if the account has permission, false otherwise
     */
    public boolean hasPermission(String action) {
        switch (role) {
            case "ADMIN":
                return true; // Admin has all permissions
            case "EMPLOYER":
                switch (action) {
                    case "POST_JOB":
                    case "VIEW_APPLICATIONS":
                    case "UPDATE_JOB":
                    case "DELETE_JOB":
                        return true;
                    default:
                        return false;
                }
            case "STUDENT":
                switch (action) {
                    case "APPLY_JOB":
                    case "VIEW_JOBS":
                    case "UPDATE_PROFILE":
                        return true;
                    default:
                        return false;
                }
            default:
                return false;
        }
    }

    /**
     * Returns a user-friendly account type based on the role.
     *
     * @return the account type as a string
     */
    public String getAccountType() {
        switch (role) {
            case "ADMIN":
                return "Administrator";
            case "EMPLOYER":
                return "Employer";
            case "STUDENT":
                return "Student";
            default:
                return "User";
        }
    }
}
