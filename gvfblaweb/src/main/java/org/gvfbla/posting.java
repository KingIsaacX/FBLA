package org.gvfbla;

import java.util.Queue;
import java.util.UUID;

/**
 * Represents a job posting containing details about the company, job, required skills,
 * and other attributes. Each posting maintains a queue of applicants.
 */
public class posting {
    private String companyName;
    private String jobTitle;
    private String jobDescription;
    private String skills;
    private String startingSalary;
    private String location;
    private final String id;
    private Queue<application> applicants;

    /**
     * Creates a new posting with the given details and generates a unique ID for it.
     *
     * @param companyName    the name of the company offering the job
     * @param jobTitle       the title of the job
     * @param jobDescription a description of the job duties and responsibilities
     * @param skills         the required skills for the job
     * @param startingSalary the starting salary offered
     * @param location       the location of the job
     */
    public posting(String companyName, String jobTitle, String jobDescription, String skills,
                   String startingSalary, String location) {
        this.companyName = companyName;
        this.jobTitle = jobTitle;
        this.jobDescription = jobDescription;
        this.skills = skills;
        this.startingSalary = startingSalary;
        this.location = location;
        this.id = UUID.randomUUID().toString();
    }

    /**
     * Returns the unique identifier for this posting.
     *
     * @return the posting's unique ID
     */
    public String getId() {
        return id;
    }

    /**
     * Adds a new application to the queue of applicants for this posting.
     *
     * @param data the application to be added
     */
    public void addApplication(application data) {
        applicants.add(data);
    }

    /**
     * Retrieves and removes the next applicant in the queue.
     *
     * @return the next applicant, or null if the queue is empty
     */
    public application getApplicant() {
        return applicants.poll();
    }

    /**
     * Returns the name of the company offering the job.
     *
     * @return the company name
     */
    public String getCompanyName() {
        return companyName;
    }

    /**
     * Sets the name of the company offering the job.
     *
     * @param companyName the company name
     */
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    /**
     * Returns the job title.
     *
     * @return the job title
     */
    public String getJobTitle() {
        return jobTitle;
    }

    /**
     * Sets the job title.
     *
     * @param jobTitle the job title
     */
    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    /**
     * Returns the job description.
     *
     * @return the job description
     */
    public String getJobDescription() {
        return jobDescription;
    }

    /**
     * Sets the job description.
     *
     * @param jobDescription the description of the job
     */
    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    /**
     * Returns the required skills for the job.
     *
     * @return the required skills
     */
    public String getSkills() {
        return skills;
    }

    /**
     * Sets the required skills for the job.
     *
     * @param skills the required skills
     */
    public void setSkills(String skills) {
        this.skills = skills;
    }

    /**
     * Returns the starting salary for the job.
     *
     * @return the starting salary
     */
    public String getStartingSalary() {
        return startingSalary;
    }

    /**
     * Sets the starting salary for the job.
     *
     * @param startingSalary the starting salary
     */
    public void setStartingSalary(String startingSalary) {
        this.startingSalary = startingSalary;
    }

    /**
     * Returns the location of the job.
     *
     * @return the job location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the location of the job.
     *
     * @param location the job location
     */
    public void setLocation(String location) {
        this.location = location;
    }
}
