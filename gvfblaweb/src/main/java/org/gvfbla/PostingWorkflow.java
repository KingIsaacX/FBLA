package org.gvfbla;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PostingWorkflow {
    private PostingManager postingManager;
    private static final Set<String> RESTRICTED_KEYWORDS = new HashSet<>(Arrays.asList(
        "confidential", "secret", "classified"
    ));

    public PostingWorkflow(PostingManager postingManager) {
        this.postingManager = postingManager;
    }

    public posting submitPosting(EmployerAccount employer, String jobTitle, 
                               String jobDescription, String skills, 
                               String startingSalary, String location) throws PostingException {
        // Basic content validation
        if (containsRestrictedKeywords(jobDescription)) {
            throw new PostingException("Posting contains restricted content");
        }

        // Create posting with pending status
        posting newPosting = postingManager.createPosting(
            employer.getCompanyName(), 
            jobTitle,
            jobDescription,
            skills,
            startingSalary,
            location
        );
        
        newPosting.setStatus("PENDING_APPROVAL");
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
