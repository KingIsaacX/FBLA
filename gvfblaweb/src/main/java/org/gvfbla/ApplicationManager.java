package org.gvfbla;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages application operations, including submitting applications
 * and retrieving applications by posting or user.
 */
public class ApplicationManager {
    private List<application> applications;

    /**
     * Creates a new ApplicationManager and loads existing applications from storage.
     */
    public ApplicationManager() {
        this.applications = FileStorageManager.loadApplications();
    }

    /**
     * Submits a new application and saves it to the storage.
     *
     * @param app the application to be submitted
     */
    public void submitApplication(application app) {
        applications.add(app);
        FileStorageManager.saveApplications(applications);
    }

    /**
     * Retrieves all applications for a given posting.
     *
     * @param postingId the ID of the posting
     * @return a list of applications associated with the specified posting
     */
    public List<application> getApplicationsForPosting(String postingId) {
        return applications.stream()
            .filter(a -> a.getPostingId().equals(postingId))
            .collect(Collectors.toList());
    }

    /**
     * Retrieves all applications submitted by a specific user.
     *
     * @param userId the ID of the user
     * @return a list of applications associated with the specified user
     */
    public List<application> getApplicationsByUser(String userId) {
        return applications.stream()
            .filter(a -> a.getPerson().getId().equals(userId))
            .collect(Collectors.toList());
    }
}
