package org.gvfbla;

import java.util.UUID;

/**
 * Represents a job application submitted by a user for a specific posting.
 * Contains the applicant's personal information, education, experience, and references.
 */
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
    private String references;

    /**
     * Creates a new application associated with a given job posting.
     *
     * @param person       the account of the person applying
     * @param firstName    the applicant's first name
     * @param lastName     the applicant's last name
     * @param phoneNumber  the applicant's phone number
     * @param email        the applicant's email address
     * @param education    the applicant's educational background
     * @param experience   the applicant's work experience
     * @param references   the applicant's references
     * @param postingId    the ID of the posting this application is for
     */
    public application(account person, String firstName, String lastName, String phoneNumber, String email,
                       String education, String experience, String references, String postingId) {
        this.person = person;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.education = education;
        this.experience = experience;
        this.references = references;
        this.id = UUID.randomUUID().toString();
        this.postingId = postingId;
    }

    /**
     * Returns the ID of the posting this application is for.
     *
     * @return the posting ID
     */
    public String getPostingId() {
        return postingId;
    }

    /**
     * Returns the unique ID of this application.
     *
     * @return the application ID
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the account of the person who submitted the application.
     *
     * @return the applicant's account
     */
    public account getPerson() {
        return person;
    }

    /**
     * Sets the account of the person who submitted the application.
     *
     * @param person the applicant's account
     */
    public void setPerson(account person) {
        this.person = person;
    }

    /**
     * Returns the first name of the applicant.
     *
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name of the applicant.
     *
     * @param firstName the applicant's first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Returns the last name of the applicant.
     *
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name of the applicant.
     *
     * @param lastName the applicant's last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Returns the applicant's phone number.
     *
     * @return the phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the applicant's phone number.
     *
     * @param phoneNumber the applicant's phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Returns the applicant's email address.
     *
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the applicant's email address.
     *
     * @param email the applicant's email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the applicant's educational background.
     *
     * @return the education details
     */
    public String getEducation() {
        return education;
    }

    /**
     * Sets the applicant's educational background.
     *
     * @param education the applicant's education details
     */
    public void setEducation(String education) {
        this.education = education;
    }

    /**
     * Returns the applicant's work experience.
     *
     * @return the work experience details
     */
    public String getExperience() {
        return experience;
    }

    /**
     * Sets the applicant's work experience.
     *
     * @param experience the applicant's experience details
     */
    public void setExperience(String experience) {
        this.experience = experience;
    }

    /**
     * Returns the applicant's references.
     *
     * @return the references
     */
    public String getReferences() {
        return references;
    }

    /**
     * Sets the applicant's references.
     *
     * @param references the applicant's references
     */
    public void setReferences(String references) {
        this.references = references;
    }
}
