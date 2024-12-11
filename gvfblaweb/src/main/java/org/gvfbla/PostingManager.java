package org.gvfbla;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostingManager {
    private static final Logger logger = LoggerFactory.getLogger(PostingManager.class);
    private List<posting> postings;

    public PostingManager() {
        this.postings = FileStorageManager.loadPostings();
        if (this.postings == null) {
            this.postings = new ArrayList<>();
            logger.info("Initialized new empty postings list");
        } else {
            logger.info("Loaded {} existing postings", this.postings.size());
        }
    }

    // Creates a new posting and automatically saves it
    public posting createPosting(String companyName, String jobTitle, String jobDescription,
                                 String skills, String startingSalary, String location) {
        posting newPosting = new posting(companyName, jobTitle, jobDescription,
                                         skills != null ? skills : "",
                                         startingSalary != null ? startingSalary : "",
                                         location != null ? location : "");
        newPosting.setStatus("PENDING");
        savePosting(newPosting);
        logger.info("Created new posting with ID: {}", newPosting.getId());
        return newPosting;
    }

    public void savePosting(posting newPosting) {
        postings.add(newPosting);
        FileStorageManager.savePostings(postings);
        logger.info("Saved new posting with ID: {}", newPosting.getId());
    }

    public posting getPostingById(String postingId) {
        return postings.stream()
                .filter(p -> p.getId().equals(postingId))
                .findFirst()
                .orElse(null);
    }

    public void updatePosting(posting updatedPosting) {
        boolean found = false;
        for (int i = 0; i < postings.size(); i++) {
            if (postings.get(i).getId().equals(updatedPosting.getId())) {
                postings.set(i, updatedPosting);
                found = true;
                break;
            }
        }
        if (!found) {
            logger.warn("No posting found with ID: {}", updatedPosting.getId());
            throw new IllegalArgumentException("Posting not found");
        }
        FileStorageManager.savePostings(postings);
        logger.info("Updated posting with ID: {}", updatedPosting.getId());
    }

    public void deletePosting(String postingId) {
        int sizeBefore = postings.size();
        postings.removeIf(p -> p.getId().equals(postingId));
        FileStorageManager.savePostings(postings);

        if (sizeBefore == postings.size()) {
            logger.warn("No posting found with ID: {}", postingId);
        } else {
            logger.info("Deleted posting with ID: {}", postingId);
        }
    }

    public List<posting> searchPostings(String keyword) {
        String lowercaseKeyword = keyword == null ? "" : keyword.toLowerCase();
        return postings.stream()
                .filter(p -> matchesKeyword(p, lowercaseKeyword))
                .collect(Collectors.toList());
    }

    public List<posting> getAllPostings() {
        return new ArrayList<>(postings);
    }

    public List<posting> filterPostings(Map<String, String> filters) {
        return postings.stream()
                .filter(p -> matchesFilters(p, filters))
                .collect(Collectors.toList());
    }

    private boolean matchesKeyword(posting p, String keyword) {
        if (keyword.isEmpty()) {
            return true;
        }
        return (p.getCompanyName() != null && p.getCompanyName().toLowerCase().contains(keyword)) ||
               (p.getJobTitle() != null && p.getJobTitle().toLowerCase().contains(keyword)) ||
               (p.getJobDescription() != null && p.getJobDescription().toLowerCase().contains(keyword)) ||
               (p.getSkills() != null && p.getSkills().toLowerCase().contains(keyword)) ||
               (p.getLocation() != null && p.getLocation().toLowerCase().contains(keyword));
    }

    private boolean matchesFilters(posting p, Map<String, String> filters) {
        for (Map.Entry<String, String> entry : filters.entrySet()) {
            if (!matchesSingleFilter(p, entry.getKey(), entry.getValue())) {
                return false;
            }
        }
        return true;
    }

    private boolean matchesSingleFilter(posting p, String key, String value) {
        if (value == null || value.trim().isEmpty()) {
            return true;
        }
        String lowerValue = value.toLowerCase().trim();

        switch (key.toLowerCase()) {
            case "company":
                return p.getCompanyName() != null && p.getCompanyName().toLowerCase().contains(lowerValue);
            case "title":
                return p.getJobTitle() != null && p.getJobTitle().toLowerCase().contains(lowerValue);
            case "location":
                return p.getLocation() != null && p.getLocation().toLowerCase().contains(lowerValue);
            case "skills":
                return p.getSkills() != null && p.getSkills().toLowerCase().contains(lowerValue);
            case "status":
                return p.getStatus() != null && p.getStatus().toLowerCase().equals(lowerValue);
            default:
                // Unknown filter key, ignore
                return true;
        }
    }
}
