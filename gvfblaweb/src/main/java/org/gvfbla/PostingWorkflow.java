package org.gvfbla;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PostingWorkflow {
    private PostingManager postingManager;
    private static final Set<String> RESTRICTED_KEYWORDS = new HashSet<>(Arrays.asList(
        "confidential", "secret", "classified"
    ));

    public PostingWorkflow(PostingManager postingManager) {
        this.postingManager = postingManager;
    }

    public posting submitPosting(EmployerAccount employer, String jobTitle, String jobDescription,
                                List<String> skills, double startingSalary, String location) throws PostingException {
        // Validate input parameters
        if (jobTitle == null || jobTitle.isEmpty()) {
            throw new PostingException("Job title is required.");
        }
        if (jobDescription == null || jobDescription.isEmpty()) {
            throw new PostingException("Job description is required.");
        }
        if (skills == null || skills.isEmpty()) {
            throw new PostingException("At least one skill is required.");
        }
        if (startingSalary <= 0) {
            throw new PostingException("Starting salary must be a positive number.");
        }
        if (location == null || location.isEmpty()) {
            throw new PostingException("Job location is required.");
        }

        // Create a new posting
        posting newPosting = new posting(
            employer.getCompanyName(),
            jobTitle,
            jobDescription,
            skills.toString(),
            String.valueOf(startingSalary),
            location
        );
        newPosting.setStatus("PENDING_APPROVAL"); // Initial status

        // Persist the posting using PostingManager
        postingManager.savePosting(newPosting);

        return newPosting;
    }


    public void approvePosting(AdminAccount admin, String postingId) throws PostingException {
        posting post = findPosting(postingId);
        if (post == null) throw new PostingException("Posting not found");

        post.setStatus("APPROVED");
        post.setApprovalDate(LocalDateTime.now());
        post.setApprovedBy(admin.getId());
        
        postingManager.updatePosting(post);
    }

    public void rejectPosting(AdminAccount admin, String postingId, String reason) 
            throws PostingException {
        posting post = findPosting(postingId);
        if (post == null) throw new PostingException("Posting not found");

        post.setStatus("REJECTED");
        post.setRejectionReason(reason);
        
        postingManager.updatePosting(post);
    }

    private boolean containsRestrictedKeywords(String text) {
        String lowerText = text.toLowerCase();
        return RESTRICTED_KEYWORDS.stream()
            .anyMatch(lowerText::contains);
    }

    private posting findPosting(String postingId) {
        return postingManager.getAllPostings().stream()
            .filter(p -> p.getId().equals(postingId))
            .findFirst()
            .orElse(null);
    }
}
