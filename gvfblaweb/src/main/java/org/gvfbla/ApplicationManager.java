package org.gvfbla;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ApplicationManager {
    private List<application> applications;

    public ApplicationManager() {
        this.applications = FileStorageManager.loadApplications();
        if (applications == null) {
            applications = new ArrayList<>();
        }
    }

    public void submitApplication(application app) {
        applications.add(app);
        FileStorageManager.saveApplications(applications);
    }

    public List<application> getApplicationsForPosting(String postingId) {
        return applications.stream()
                .filter(a -> a.getPostingId().equals(postingId))
                .collect(Collectors.toList());
    }

    public List<application> getApplicationsByUser(String userId) {
        return applications.stream()
                .filter(a -> a.getPerson().getId().equals(userId))
                .collect(Collectors.toList());
    }

    public List<application> getAllApplications() {
        return new ArrayList<>(applications);
    }

    public void acceptApplication(String applicationId) throws ApplicationException {
        application app = findApplicationById(applicationId);
        if (!"PENDING".equalsIgnoreCase(app.getStatus())) {
            throw new ApplicationException("Application already processed");
        }
        app.setStatus("ACCEPTED");
        FileStorageManager.saveApplications(applications);
    }

    public void rejectApplication(String applicationId, String reason) throws ApplicationException {
        application app = findApplicationById(applicationId);
        if (!"PENDING".equalsIgnoreCase(app.getStatus())) {
            throw new ApplicationException("Application already processed");
        }
        app.setStatus("REJECTED");
        // If you want, you can store reason in application class by adding a field for it
        // Not currently implemented here, but can be added easily
        FileStorageManager.saveApplications(applications);
    }

    private application findApplicationById(String applicationId) throws ApplicationException {
        Optional<application> appOpt = applications.stream()
                .filter(a -> a.getId().equals(applicationId))
                .findFirst();
        if (appOpt.isEmpty()) {
            throw new ApplicationException("Application not found");
        }
        return appOpt.get();
    }
}
