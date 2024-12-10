package org.gvfbla;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages operations related to job postings, including creation, retrieval, search,
 * deletion, and updating of postings.
 */
public class PostingManager {
    private List<posting> postings;

    /**
     * Creates a new PostingManager and loads the existing postings from storage.
     */
    public PostingManager() {
        this.postings = FileStorageManager.loadPostings();
    }

    /**
     * Creates a new job posting, saves it, and returns the created posting.
     *
     * @param companyName     the name of the company
     * @param jobTitle        the title of the job
     * @param jobDescription  a description of the job
     * @param skills          the required skills for the job
     * @param startingSalary  the starting salary offered
     * @param location        the location of the job
     * @return the newly created posting
     */
    public posting createPosting(String companyName, String jobTitle, String jobDescription,
                                 String skills, String startingSalary, String location) {
        posting newPosting = new posting(companyName, jobTitle, jobDescription,
                                         skills, startingSalary, location);
        postings.add(newPosting);
        FileStorageManager.savePostings(postings);
        return newPosting;
    }

    /**
     * Retrieves all postings currently available.
     *
     * @return a list of all postings
     */
    public List<posting> getAllPostings() {
        return new ArrayList<>(postings);
    }

    /**
     * Searches for postings that match a given keyword in their company name, job title,
     * job description, or required skills.
     *
     * @param keyword the search keyword
     * @return a list of postings that match the keyword
     */
    public List<posting> searchPostings(String keyword) {
        return postings.stream()
            .filter(p -> matchesKeyword(p, keyword.toLowerCase()))
            .collect(Collectors.toList());
    }

    /**
     * Deletes a posting by its ID.
     *
     * @param postingId the ID of the posting to delete
     */
    public void deletePosting(String postingId) {
        postings.removeIf(p -> p.getId().equals(postingId));
        FileStorageManager.savePostings(postings);
    }

    /**
     * Updates an existing posting with new information and saves the changes.
     *
     * @param updatedPosting the posting containing updated details
     */
    public void updatePosting(posting updatedPosting) {
        for (int i = 0; i < postings.size(); i++) {
            if (postings.get(i).getId().equals(updatedPosting.getId())) {
                postings.set(i, updatedPosting);
                break;
            }
        }
        FileStorageManager.savePostings(postings);
    }

    /**
     * Checks if a posting matches a given keyword by examining its company name, job title,
     * job description, and required skills.
     *
     * @param p       the posting to check
     * @param keyword the keyword to match
     * @return true if the posting matches the keyword, false otherwise
     */
    private boolean matchesKeyword(posting p, String keyword) {
        return p.getCompanyName().toLowerCase().contains(keyword) ||
               p.getJobTitle().toLowerCase().contains(keyword) ||
               p.getJobDescription().toLowerCase().contains(keyword) ||
               p.getSkills().toLowerCase().contains(keyword);
    }
}
