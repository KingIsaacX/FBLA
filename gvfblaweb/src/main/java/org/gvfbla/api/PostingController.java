package org.gvfbla.api;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.gvfbla.PostingManager;
import org.gvfbla.posting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonSyntaxException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Handles HTTP requests related to postings. Supports retrieving all postings,
 * searching for postings, retrieving a specific posting, creating new postings,
 * updating existing postings, and deleting postings.
 */
@WebServlet("/api/postings/*")
public class PostingController extends BaseApiController {
    private static final Logger logger = LoggerFactory.getLogger(PostingController.class);

    /**
     * Handles HTTP GET requests for postings.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String path = request.getPathInfo();
        try {
            if (path == null || path.equals("/")) {
                handleGetAllPostings(request, response);
            } else {
                handleGetSinglePosting(request, response);
            }
        } catch (Exception e) {
            logger.error("Error processing GET request", e);
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                     "Error processing request: " + e.getMessage());
        }
    }

    /**
     * Handles HTTP POST requests to create a new posting.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            // Validate content type
            if (!request.getContentType().contains("application/json")) {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, 
                         "Content type must be application/json");
                return;
            }

            // Read and validate request body
            PostingRequest postingRequest = gson.fromJson(request.getReader(), PostingRequest.class);
            if (!isValidPostingRequest(postingRequest)) {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, 
                         "Invalid posting data: All fields are required");
                return;
            }

            // Create posting
            PostingManager postingManager = new PostingManager();
            posting newPosting = postingManager.createPosting(
                postingRequest.companyName,
                postingRequest.jobTitle,
                postingRequest.jobDescription,
                postingRequest.skills,
                postingRequest.startingSalary,
                postingRequest.location
            );
            
            // Send response
            response.setStatus(HttpServletResponse.SC_CREATED);
            sendJsonResponse(response, newPosting);
            logger.info("Created new posting with ID: {}", newPosting.getId());
            
        } catch (JsonSyntaxException e) {
            logger.error("Invalid JSON format", e);
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, 
                     "Invalid JSON format: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error creating posting", e);
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                     "Error creating posting: " + e.getMessage());
        }
    }

    /**
     * Handles HTTP PUT requests to update an existing posting.
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String path = request.getPathInfo();
        String postingId = path.substring(1);

        try {
            PostingRequest updateRequest = gson.fromJson(request.getReader(), PostingRequest.class);
            if (!isValidPostingRequest(updateRequest)) {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, 
                         "Invalid update data: All fields are required");
                return;
            }

            posting existingPosting = jobBoardController.getPostingById(postingId);
            if (existingPosting == null) {
                sendError(response, HttpServletResponse.SC_NOT_FOUND, "Posting not found");
                return;
            }

            // Update posting fields
            existingPosting.setCompanyName(updateRequest.companyName);
            existingPosting.setJobTitle(updateRequest.jobTitle);
            existingPosting.setJobDescription(updateRequest.jobDescription);
            existingPosting.setSkills(updateRequest.skills);
            existingPosting.setStartingSalary(updateRequest.startingSalary);
            existingPosting.setLocation(updateRequest.location);
            
            jobBoardController.updatePosting(existingPosting);
            sendJsonResponse(response, existingPosting);
            logger.info("Updated posting with ID: {}", postingId);
            
        } catch (Exception e) {
            logger.error("Error updating posting", e);
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                     "Error updating posting: " + e.getMessage());
        }
    }

    /**
     * Handles HTTP DELETE requests to remove an existing posting.
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String path = request.getPathInfo();
        String postingId = path.substring(1);

        try {
            posting existingPosting = jobBoardController.getPostingById(postingId);
            if (existingPosting == null) {
                sendError(response, HttpServletResponse.SC_NOT_FOUND, "Posting not found");
                return;
            }

            jobBoardController.deletePosting(postingId);
            sendJsonResponse(response, Map.of("message", "Posting deleted successfully"));
            logger.info("Deleted posting with ID: {}", postingId);
            
        } catch (Exception e) {
            logger.error("Error deleting posting", e);
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                     "Error deleting posting: " + e.getMessage());
        }
    }

    /**
     * Handles retrieving all postings or searching postings.
     */
    private void handleGetAllPostings(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String searchQuery = request.getParameter("search");
        List<posting> postings;
        
        try {
            if (searchQuery != null && !searchQuery.isEmpty()) {
                postings = jobBoardController.searchPostings(searchQuery);
                logger.info("Searched postings with query: {}, found {} results", 
                          searchQuery, postings.size());
            } else {
                postings = jobBoardController.getAllPostings();
                logger.info("Retrieved all postings, count: {}", postings.size());
            }
            sendJsonResponse(response, postings);
        } catch (Exception e) {
            logger.error("Error retrieving postings", e);
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                     "Error retrieving postings: " + e.getMessage());
        }
    }

    /**
     * Handles retrieving a single posting by ID.
     */
    private void handleGetSinglePosting(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String postingId = request.getPathInfo().substring(1);
        try {
            posting post = jobBoardController.getPostingById(postingId);
            if (post != null) {
                sendJsonResponse(response, post);
                logger.info("Retrieved posting with ID: {}", postingId);
            } else {
                sendError(response, HttpServletResponse.SC_NOT_FOUND, "Posting not found");
                logger.warn("Posting not found with ID: {}", postingId);
            }
        } catch (Exception e) {
            logger.error("Error retrieving posting", e);
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                     "Error retrieving posting: " + e.getMessage());
        }
    }

    /**
     * Validates that all required fields are present in a posting request.
     */
    private boolean isValidPostingRequest(PostingRequest request) {
        if (request == null) {
            return false;
        }

        try {
            validateField("Company Name", request.companyName);
            validateField("Job Title", request.jobTitle);
            validateField("Job Description", request.jobDescription);
            validateField("Skills", request.skills);
            validateField("Starting Salary", request.startingSalary);
            validateField("Location", request.location);
            return true;
        } catch (IllegalArgumentException e) {
            logger.warn("Posting validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Validates a single field, ensuring it is not null or empty.
     */
    private void validateField(String fieldName, String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
    }

    /**
     * Represents the expected JSON request body for creating or updating a posting.
     * All fields are required.
     */
    private static class PostingRequest {
        String companyName;
        String jobTitle;
        String jobDescription;
        String skills;
        String startingSalary;
        String location;
        
        @Override
        public String toString() {
            return String.format(
                "PostingRequest{company='%s', title='%s', location='%s'}",
                companyName, jobTitle, location
            );
        }
    }

    /**
     * Utility method to check if a string is not blank.
     */
    private boolean isNotBlank(String str) {
        return str != null && !str.trim().isEmpty();
    }
}