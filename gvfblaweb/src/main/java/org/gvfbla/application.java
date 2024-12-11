package org.gvfbla;

import java.time.LocalDateTime;
import java.util.UUID;

public class application {
    private account person;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String education;
    private String experience;
    private final String id;
    private final String postingId;
    private String status;
    private LocalDateTime applicationDate;
    private LocalDateTime lastUpdated;
    private String references;

    public application(account person, String firstName, String lastName,
                       String phoneNumber, String email, String education,
                       String experience, String references, String postingId) {
        this.person = person;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.education = education;
        this.experience = experience;
        this.references = references;
        this.status = "PENDING";
        this.id = UUID.randomUUID().toString();
        this.postingId = postingId;
        this.applicationDate = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
    }

    public String getPostingId() {
        return postingId;
    }

    public String getId() {
        return id;
    }

    public account getPerson() {
        return person;
    }

    public void setPerson(account person) {
        this.person = person;
    }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) {
        this.firstName = firstName; this.lastUpdated = LocalDateTime.now();
    }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) {
        this.lastName = lastName; this.lastUpdated = LocalDateTime.now();
    }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber; this.lastUpdated = LocalDateTime.now();
    }

    public String getEmail() { return email; }
    public void setEmail(String email) {
        this.email = email; this.lastUpdated = LocalDateTime.now();
    }

    public String getEducation() { return education; }
    public void setEducation(String education) {
        this.education = education; this.lastUpdated = LocalDateTime.now();
    }

    public String getExperience() { return experience; }
    public void setExperience(String experience) {
        this.experience = experience; this.lastUpdated = LocalDateTime.now();
    }

    public String getReferences() { return references; }
    public void setReferences(String references) {
        this.references = references; this.lastUpdated = LocalDateTime.now();
    }

    public String getStatus() { return status; }
    public void setStatus(String status) {
        this.status = status; this.lastUpdated = LocalDateTime.now();
    }

    public LocalDateTime getApplicationDate() { return applicationDate; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }
}
