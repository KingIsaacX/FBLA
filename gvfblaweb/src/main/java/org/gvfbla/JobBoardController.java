package org.gvfbla;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobBoardController {
    private static final Logger logger = LoggerFactory.getLogger(JobBoardController.class);

    private final AccountManager accountManager;
    private final PostingManager postingManager;
    private final ApplicationManager applicationManager;

    public JobBoardController() {
        this.accountManager = new AccountManager();
        this.postingManager = new PostingManager();
        this.applicationManager = new ApplicationManager();
    }

    public account login(String username, String password) {
        account user = accountManager.login(username, password);
        if (user != null) {
            logger.info("User logged in successfully: {}", username);
        } else {
            logger.warn("Failed login attempt for username: {}", username);
        }
        return user;
    }

    public StudentAccount registerStudent(String username, String password,
                                          String email, String firstName, String lastName) throws AccountException {
        validateRegistration(username, password, email);
        StudentAccount student = new StudentAccount(username, password, email, firstName, lastName);
        accountManager.addAccount(student);
        logger.info("Student registered successfully: {}", username);
        return student;
    }

    public EmployerAccount registerEmployer(String username, String password,
                                            String email, String companyName) throws AccountException {
        validateRegistration(username, password, email);
        EmployerAccount employer = new EmployerAccount(username, password, email, companyName);
        accountManager.addAccount(employer);
        logger.info("Employer registered successfully: {}", username);
        return employer;
    }

    private void validateRegistration(String username, String password, String email) throws AccountException {
        if (username == null || username.trim().isEmpty()) {
            throw new AccountException("Username is required");
        }
        if (password == null || password.length() < 8) {
            throw new AccountException("Password must be at least 8 characters");
        }
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new AccountException("Valid email is required");
        }
        if (accountManager.isUsernameTaken(username)) {
            throw new AccountException("Username already exists");
        }
    }

    /**
     * Creates a new job posting for an employer.
     */
    public posting createJobPosting(EmployerAccount employer,
                                    String jobTitle,
                                    String jobDescription,
                                    String skills,
                                    String startingSalary,
                                    String location) throws PostingException {
        if (employer == null || !employer.hasPermission("POST_JOB")) {
            throw new PostingException("Insufficient permissions to create job posting");
        }

        String companyName = employer.getCompanyName();
        posting newPosting = postingManager.createPosting(
                companyName, jobTitle, jobDescription, skills, startingSalary, location);
        logger.info("Job posting created successfully with ID: {}", newPosting.getId());
        return newPosting;
    }

    public posting getPostingById(String id) {
        posting post = postingManager.getPostingById(id);
        if (post == null) {
            logger.warn("Posting not found with ID: {}", id);
        } else {
            logger.info("Retrieved posting with ID: {}", id);
        }
        return post;
    }

    public void updatePosting(posting updatedPosting) {
        postingManager.updatePosting(updatedPosting);
        logger.info("Updated posting with ID: {}", updatedPosting.getId());
    }

    public void deletePosting(String postingId) {
        postingManager.deletePosting(postingId);
        logger.info("Deleted posting with ID: {}", postingId);
    }

    public List<posting> searchPostings(String keyword) {
        List<posting> results = postingManager.searchPostings(keyword);
        logger.info("Search with keyword '{}' returned {} results", keyword, results.size());
        return results;
    }

    public List<posting> getAllPostings() {
        List<posting> postings = postingManager.getAllPostings();
        logger.info("Retrieved {} total postings", postings.size());
        return postings;
    }

    public List<posting> filterPostings(Map<String, String> filters) {
        List<posting> results = postingManager.filterPostings(filters);
        logger.info("Filtering postings with {} returned {} results", filters, results.size());
        return results;
    }

    public application submitApplication(StudentAccount student, String postingId,
                                         String firstName, String lastName, String phoneNumber,
                                         String email, String education, String experience,
                                         String references) {
        posting jobPosting = getPostingById(postingId);
        if (jobPosting == null) {
            throw new IllegalArgumentException("Posting not found");
        }
        application newApp = new application(student, firstName, lastName,
                phoneNumber, email, education, experience, references, postingId);

        applicationManager.submitApplication(newApp);
        logger.info("Application submitted successfully for posting ID: {}", postingId);
        return newApp;
    }

    public List<application> getApplicationsForPosting(String postingId) {
        List<application> applications = applicationManager.getApplicationsForPosting(postingId);
        logger.info("Retrieved {} applications for posting ID: {}", applications.size(), postingId);
        return applications;
    }

    public List<application> getApplicationsByUser(String userId) {
        List<application> applications = applicationManager.getApplicationsByUser(userId);
        logger.info("Retrieved {} applications for user ID: {}", applications.size(), userId);
        return applications;
    }

    public application getApplicationById(String applicationId) {
        application app = applicationManager.getAllApplications().stream()
                .filter(a -> a.getId().equals(applicationId))
                .findFirst()
                .orElse(null);
        if (app == null) {
            logger.warn("Application not found with ID: {}", applicationId);
        } else {
            logger.info("Retrieved application with ID: {}", applicationId);
        }
        return app;
    }

    public void acceptApplication(EmployerAccount employer, String applicationId) throws ApplicationException {
        if (!employer.hasPermission("MANAGE_APPLICATIONS")) {
            throw new ApplicationException("Insufficient permissions to accept applications");
        }
        applicationManager.acceptApplication(applicationId);
        logger.info("Application {} accepted by employer {}", applicationId, employer.getUsername());
    }

    public void rejectApplication(EmployerAccount employer, String applicationId, String reason) throws ApplicationException {
        if (!employer.hasPermission("MANAGE_APPLICATIONS")) {
            throw new ApplicationException("Insufficient permissions to reject applications");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new ApplicationException("Rejection reason is required");
        }
        applicationManager.rejectApplication(applicationId, reason);
        logger.info("Application {} rejected by employer {} for reason: {}", applicationId, employer.getUsername(), reason);
    }

    public posting approvePosting(AdminAccount admin, String postingId) throws PostingException {
        if (admin == null) {
            throw new PostingException("Admin account required");
        }
        posting p = getPostingById(postingId);
        if (p == null) {
            throw new PostingException("Posting not found");
        }
        p.setStatus("APPROVED");
        postingManager.updatePosting(p);
        logger.info("Posting {} approved by admin {}", postingId, admin.getUsername());
        return p;
    }

    public void rejectPosting(AdminAccount admin, String postingId, String reason) throws PostingException {
        if (admin == null) {
            throw new PostingException("Admin account required");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new PostingException("Rejection reason is required");
        }
        posting p = getPostingById(postingId);
        if (p == null) {
            throw new PostingException("Posting not found");
        }
        p.setStatus("REJECTED");
        p.setRejectionReason(reason);
        postingManager.updatePosting(p);
        logger.info("Posting {} rejected by admin {} for reason: {}", postingId, admin.getUsername(), reason);
    }

    public int getActiveJobsCount() {
        List<posting> allPostings = postingManager.getAllPostings();
        int count = (int) allPostings.stream()
                .filter(p -> "APPROVED".equalsIgnoreCase(p.getStatus()))
                .count();
        logger.info("Calculated active jobs count: {}", count);
        return count;
    }

    public int getCompaniesCount() {
        List<posting> allPostings = postingManager.getAllPostings();
        int count = (int) allPostings.stream()
                .map(posting::getCompanyName)
                .filter(name -> name != null && !name.trim().isEmpty())
                .distinct()
                .count();
        logger.info("Calculated unique companies count: {}", count);
        return count;
    }

    public int getStudentsPlacedCount() {
        List<application> allApps = applicationManager.getAllApplications();
        int count = (int) allApps.stream()
                .filter(a -> "ACCEPTED".equalsIgnoreCase(a.getStatus()))
                .map(a -> a.getPerson().getId())
                .filter(Objects::nonNull)
                .distinct()
                .count();
        logger.info("Calculated placed students count: {}", count);
        return count;
    }

    public Map<String, Integer> getAllStats() {
        int activeJobs = getActiveJobsCount();
        int companies = getCompaniesCount();
        int studentsPlaced = getStudentsPlacedCount();
        Map<String, Integer> stats = new HashMap<>();
        stats.put("activeJobs", activeJobs);
        stats.put("companies", companies);
        stats.put("studentsPlaced", studentsPlaced);
        logger.info("Retrieved all stats: activeJobs={}, companies={}, studentsPlaced={}",
                activeJobs, companies, studentsPlaced);
        return stats;
    }
}
