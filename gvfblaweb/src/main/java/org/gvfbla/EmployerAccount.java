package org.gvfbla;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an employer account with the ability to post, view, update, and delete job postings.
 * Each employer account is associated with a company and can track the IDs of posted jobs.
 */
public class EmployerAccount extends account {
    private String companyName;
    private String industry;
    private String companySize;
    private String location;
    private List<String> postedJobs;

    /**
     * Constructs a new EmployerAccount with the provided account credentials and company name.
     *
     * @param username    the username for the employer account
     * @param password    the password for the employer account
     * @param email       the email address associated with the employer account
     * @param companyName the name of the company associated with this employer account
     */
    public EmployerAccount(String username, String password, String email, String companyName) {
        super(username, password, email, "EMPLOYER");
        this.companyName = companyName;
        this.postedJobs = new ArrayList<>();
    }

    /**
     * Checks whether this employer has permission to perform a given action.
     *
     * @param action the action to check
     * @return true if the employer can perform the action, false otherwise
     */
    @Override
    public boolean hasPermission(String action) {
        switch (action) {
            case "POST_JOB":
            case "VIEW_APPLICATIONS":
            case "UPDATE_JOB":
            case "DELETE_JOB":
                return true;
            default:
                return false;
        }
    }

    /**
     * Returns the account type.
     *
     * @return the string "Employer"
     */
    @Override
    public String getAccountType() {
        return "Employer";
    }

    /**
     * Returns the name of the company associated with this employer.
     *
     * @return the company name
     */
    public String getCompanyName() {
        return companyName;
    }

    /**
     * Sets the name of the company associated with this employer.
     *
     * @param companyName the company name
     */
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    /**
     * Returns the industry of the company.
     *
     * @return the company industry
     */
    public String getIndustry() {
        return industry;
    }

    /**
     * Sets the industry of the company.
     *
     * @param industry the company industry
     */
    public void setIndustry(String industry) {
        this.industry = industry;
    }

    /**
     * Returns the size of the company.
     *
     * @return the company size
     */
    public String getCompanySize() {
        return companySize;
    }

    /**
     * Sets the size of the company.
     *
     * @param companySize the company size
     */
    public void setCompanySize(String companySize) {
        this.companySize = companySize;
    }

    /**
     * Returns the location of the company.
     *
     * @return the company location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the location of the company.
     *
     * @param location the company location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Returns a list of job IDs that this employer has posted.
     *
     * @return a list of posted job IDs
     */
    public List<String> getPostedJobs() {
        return new ArrayList<>(postedJobs);
    }

    /**
     * Adds a new job ID to the list of jobs posted by this employer.
     *
     * @param jobId the ID of the new job posting
     */
    public void addPostedJob(String jobId) {
        this.postedJobs.add(jobId);
    }
}
