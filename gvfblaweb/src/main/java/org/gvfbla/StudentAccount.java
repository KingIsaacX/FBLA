package org.gvfbla;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a student account with specific attributes such as first name, last name,
 * education details, skills, and a list of applied jobs. This account type allows certain
 * permissions like applying for jobs, viewing jobs, and updating the profile.
 */
public class StudentAccount extends account {
    private String firstName;
    private String lastName;
    private String education;
    private String graduationYear;
    private List<String> skills;
    private List<String> appliedJobs;

    /**
     * Constructs a new StudentAccount with the provided account credentials and personal details.
     *
     * @param username   the username for the student account
     * @param password   the password for the student account
     * @param email      the email address associated with the student account
     * @param firstName  the first name of the student
     * @param lastName   the last name of the student
     */
    public StudentAccount(String username, String password, String email, 
                          String firstName, String lastName) {
        super(username, password, email, "STUDENT");
        this.firstName = firstName;
        this.lastName = lastName;
        this.skills = new ArrayList<>();
        this.appliedJobs = new ArrayList<>();
    }

    /**
     * Determines if the student has permission to perform a given action.
     *
     * @param action the action to check
     * @return true if the student can perform the action, false otherwise
     */
    @Override
    public boolean hasPermission(String action) {
        switch (action) {
            case "APPLY_JOB":
            case "VIEW_JOBS":
            case "UPDATE_PROFILE":
                return true;
            default:
                return false;
        }
    }

    /**
     * Returns the account type for this student account.
     *
     * @return the account type "Student"
     */
    @Override
    public String getAccountType() {
        return "Student";
    }

    /**
     * Returns the first name of the student.
     *
     * @return the student's first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name of the student.
     *
     * @param firstName the new first name of the student
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Returns the last name of the student.
     *
     * @return the student's last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name of the student.
     *
     * @param lastName the new last name of the student
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Returns the education details of the student.
     *
     * @return the student's education details
     */
    public String getEducation() {
        return education;
    }

    /**
     * Sets the education details of the student.
     *
     * @param education the new education details
     */
    public void setEducation(String education) {
        this.education = education;
    }

    /**
     * Returns the graduation year of the student.
     *
     * @return the student's graduation year
     */
    public String getGraduationYear() {
        return graduationYear;
    }

    /**
     * Sets the graduation year of the student.
     *
     * @param graduationYear the new graduation year
     */
    public void setGraduationYear(String graduationYear) {
        this.graduationYear = graduationYear;
    }

    /**
     * Returns a copy of the list of skills possessed by the student.
     *
     * @return a list of the student's skills
     */
    public List<String> getSkills() {
        return new ArrayList<>(skills);
    }

    /**
     * Adds a new skill to the student's skill set.
     *
     * @param skill the skill to be added
     */
    public void addSkill(String skill) {
        this.skills.add(skill);
    }

    /**
     * Returns a copy of the list of job IDs that the student has applied to.
     *
     * @return a list of applied job IDs
     */
    public List<String> getAppliedJobs() {
        return new ArrayList<>(appliedJobs);
    }

    /**
     * Adds a new job ID to the list of jobs the student has applied for.
     *
     * @param jobId the job ID to add
     */
    public void addAppliedJob(String jobId) {
        this.appliedJobs.add(jobId);
    }
}
