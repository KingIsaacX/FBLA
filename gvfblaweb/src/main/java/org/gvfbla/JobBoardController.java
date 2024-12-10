package org.gvfbla;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Acts as a central controller for job board operations, coordinating actions
 * between account, posting, and application managers. Provides functionality
 * for user login, posting creation and approval, application submission, and
 * job searching.
 */
public class JobBoardController {
    private final AccountManager accountManager;
    private final PostingManager postingManager;
    private final ApplicationManager applicationManager;
    private final PostingWorkflow postingWorkflow;
    private final PostingSearchService searchService;

    /**
     * Constructs a new JobBoardController, initializing managers and services.
     */
    public JobBoardController() {
        this.accountManager = new AccountManager();
        this.postingManager = new PostingManager();
        this.applicationManager = new ApplicationManager();
        this.postingWorkflow = new PostingWorkflow(postingManager);
        this.searchService = new PostingSearchService(postingManager);
    }

    /**
     * Authenticates a user by username and password.
     *
     * @param username the username of the account
     * @param password the password of the account
     * @return the authenticated account or null if authentication fails
     */
    public account login(String username, String password) {
        return accountManager.login(username, password);
    }

    /**
     * Submits a job application on behalf of a student for the specified posting.
     *
     * @param student      the student account submitting the application
     * @param postingId    the ID of the job posting being applied to
     * @param firstName    the applicant's first name
     * @param lastName     the applicant's last name
     * @param phoneNumber  the applicant's phone number
     * @param email        the applicant's email address
     * @param education    the applicant's educational background
     * @param experience   the applicant's work experience
     * @param references   the applicant's references
     */
    public void submitApplication(StudentAccount student, String postingId,
                                  String firstName, String lastName, String phoneNumber,
                                  String email, String education, String experience,
                                  String references) {
        posting jobPosting = postingManager.getAllPostings().stream()
            .filter(p -> p.getId().equals(postingId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Posting not found"));

        application newApplication = new application(student, firstName, lastName,
            phoneNumber, email, education, experience, references, jobPosting.getId());
        
        applicationManager.submitApplication(newApplication);
    }

    /**
     * Creates a new job posting for an employer.
     *
     * @param employer       the employer account creating the posting
     * @param jobTitle       the job title
     * @param jobDescription the job description
     * @param skills         the required skills
     * @param startingSalary the starting salary offered
     * @param location       the location of the job
     * @return the newly created posting
     * @throws PostingException if there is an error creating the posting
     */
    public posting createJobPosting(EmployerAccount employer, String jobTitle,
                                    String jobDescription, String skills,
                                    String startingSalary, String location) throws PostingException {
        return postingWorkflow.submitPosting(employer, jobTitle, jobDescription,
            skills, startingSalary, location);
    }

    /**
     * Retrieves all applications for a given employer's job posting.
     *
     * @param employer   the employer account
     * @param postingId  the ID of the posting
     * @return a list of applications associated with the given posting
     */
    public List<application> getApplicationsForEmployer(EmployerAccount employer, String postingId) {
        posting jobPosting = postingManager.getAllPostings().stream()
            .filter(p -> p.getId().equals(postingId))
            .filter(p -> p.getCompanyName().equals(employer.getCompanyName()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Posting not found or unauthorized"));

        return applicationManager.getApplicationsForPosting(postingId);
    }

    /**
     * Searches postings based on given filters.
     *
     * @param filters a map of filter criteria (e.g., by location, skills)
     * @return a list of postings matching the filters
     */
    public List<posting> searchPostings(Map<String, String> filters) {
        return searchService.searchPostings(filters);
    }

    /**
     * Retrieves a list of postings that are pending approval for an admin to review.
     *
     * @param admin the admin account requesting the list
     * @return a list of postings pending approval
     */
    public List<posting> getPendingPostings(AdminAccount admin) {
        return postingManager.getAllPostings().stream()
            .filter(p -> p.getStatus().equals("PENDING_APPROVAL"))
            .collect(Collectors.toList());
    }

    /**
     * Retrieves all applications submitted by a given student.
     *
     * @param student the student account
     * @return a list of applications submitted by the student
     */
    public List<application> getStudentApplications(StudentAccount student) {
        return applicationManager.getApplicationsByUser(student.getId());
    }

    /**
     * Retrieves all job postings belonging to a given employer.
     *
     * @param employer the employer account
     * @return a list of the employer's postings
     */
    public List<posting> getEmployerPostings(EmployerAccount employer) {
        return postingManager.getAllPostings().stream()
                .filter(p -> p.getCompanyName().equals(employer.getCompanyName()))
                .collect(Collectors.toList());
    }
    
    /**
     * Retrieves all job postings.
     *
     * @return a list of all postings
     */
    public List<posting> getAllPostings() {
        return postingManager.getAllPostings();
    }

    /**
     * Retrieves a specific posting by its ID.
     *
     * @param id the ID of the posting
     * @return the posting with the given ID or null if not found
     */
    public posting getPostingById(String id) {
        return postingManager.getAllPostings().stream()
            .filter(p -> p.getId().equals(id))
            .findFirst()
            .orElse(null);
    }

    /**
     * Creates a new posting with the specified details.
     *
     * @param companyName    the name of the company
     * @param jobTitle       the job title
     * @param jobDescription the job description
     * @param skills         the required skills
     * @param startingSalary the starting salary offered
     * @param location       the job location
     * @return the newly created posting
     */
    public posting createPosting(String companyName, String jobTitle, 
                                 String jobDescription, String skills, 
                                 String startingSalary, String location) {
        return postingManager.createPosting(companyName, jobTitle, jobDescription,
                                            skills, startingSalary, location);
    }

    /**
     * Updates an existing posting with new details.
     *
     * @param updatedPosting the posting object containing updated information
     */
    public void updatePosting(posting updatedPosting) {
        postingManager.updatePosting(updatedPosting);
    }

    /**
     * Deletes a posting by its ID.
     *
     * @param postingId the ID of the posting to delete
     */
    public void deletePosting(String postingId) {
        postingManager.deletePosting(postingId);
    }

    /**
     * Searches postings using a keyword. Matches may be found in company name, job title,
     * job description, or required skills.
     *
     * @param keyword the keyword to search for
     * @return a list of postings matching the keyword
     */
    public List<posting> searchPostings(String keyword) {
        return postingManager.searchPostings(keyword);
    }

    /**
     * Retrieves all applications associated with a specific posting.
     *
     * @param postingId the ID of the posting
     * @return a list of applications for the specified posting
     */
    public List<application> getApplicationsForPosting(String postingId) {
        return applicationManager.getApplicationsForPosting(postingId);
    }

    /**
     * Retrieves all applications submitted by a specific user.
     *
     * @param userId the ID of the user
     * @return a list of applications submitted by the user
     */
    public List<application> getApplicationsByUser(String userId) {
        return applicationManager.getApplicationsByUser(userId);
    }

    /**
     * Approves a posting as an admin.
     *
     * @param admin     the admin account approving the posting
     * @param postingId the ID of the posting to approve
     * @return the approved posting
     * @throws PostingException if the admin is null or the posting is not found
     */
    public posting approvePosting(AdminAccount admin, String postingId) throws PostingException {
        if (admin == null) {
            throw new PostingException("Admin account required");
        }

        posting post = getPostingById(postingId);
        if (post == null) {
            throw new PostingException("Posting not found");
        }

        postingWorkflow.approvePosting(admin, postingId);
        return post;
    }

    /**
     * Rejects a posting with a specified reason as an admin.
     *
     * @param admin     the admin account rejecting the posting
     * @param postingId the ID of the posting to reject
     * @param reason    the reason for the rejection
     * @throws PostingException if the admin is null, the reason is invalid, or the posting is not found
     */
    public void rejectPosting(AdminAccount admin, String postingId, String reason) 
            throws PostingException {
        if (admin == null) {
            throw new PostingException("Admin account required");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new PostingException("Rejection reason is required");
        }

        posting post = getPostingById(postingId);
        if (post == null) {
            throw new PostingException("Posting not found");
        }

        postingWorkflow.rejectPosting(admin, postingId, reason);
    }
}
