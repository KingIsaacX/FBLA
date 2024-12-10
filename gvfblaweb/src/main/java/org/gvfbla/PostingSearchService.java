package org.gvfbla;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PostingSearchService {
    private PostingManager postingManager;

    public PostingSearchService(PostingManager postingManager) {
        this.postingManager = postingManager;
    }

    public List<posting> searchPostings(Map<String, String> filters) {
        List<posting> allPostings = postingManager.getAllPostings();
        
        return allPostings.stream()
            .filter(p -> p.getStatus().equals("APPROVED"))
            .filter(p -> matchesFilters(p, filters))
            .collect(Collectors.toList());
    }

    private boolean matchesFilters(posting p, Map<String, String> filters) {
        for (Map.Entry<String, String> filter : filters.entrySet()) {
            if (!matchesSingleFilter(p, filter.getKey(), filter.getValue())) {
                return false;
            }
        }
        return true;
    }

    private boolean matchesSingleFilter(posting p, String filterKey, String filterValue) {
        String value = filterValue.toLowerCase();
        switch (filterKey) {
            case "keyword":
                return p.getJobTitle().toLowerCase().contains(value) ||
                       p.getJobDescription().toLowerCase().contains(value) ||
                       p.getSkills().toLowerCase().contains(value);
            case "location":
                return p.getLocation().toLowerCase().contains(value);
            case "company":
                return p.getCompanyName().toLowerCase().contains(value);
            case "salary":
                return p.getStartingSalary().contains(value);
            case "skills":
                return Arrays.stream(p.getSkills().toLowerCase().split(","))
                    .anyMatch(skill -> skill.trim().contains(value));
            default:
                return true;
        }
    }
}