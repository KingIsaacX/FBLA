package org.gvfbla.api;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.gvfbla.StudentAccount;
import org.gvfbla.application;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Handles HTTP requests related to job applications.
 * Supports submitting new applications, as well as retrieving applications by posting or user.
 */
@WebServlet("/api/applications/*")
public class ApplicationController extends BaseApiController {

    /**
     * Handles HTTP POST requests to submit a new application. Expects a JSON request body containing
     * application details, including associated student account and posting information.
     *
     * @param request  the HttpServletRequest
     * @param response the HttpServletResponse
     * @throws IOException if an I/O error occurs while reading the request or sending the response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            ApplicationRequest appRequest = gson.fromJson(request.getReader(), ApplicationRequest.class);

            if (!isValidApplication(appRequest)) {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Missing required fields");
                return;
            }

            jobBoardController.submitApplication(
                appRequest.student,
                appRequest.postingId,
                appRequest.firstName,
                appRequest.lastName,
                appRequest.phoneNumber,
                appRequest.email,
                appRequest.education,
                appRequest.experience,
                appRequest.references
            );

            sendJsonResponse(response, Map.of("message", "Application submitted successfully"));
        } catch (IllegalArgumentException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing application");
        }
    }

    /**
     * Handles HTTP GET requests to retrieve applications.
     * Supports paths of the form:
     * - /posting/{postingId} to get applications for a specific posting
     * - /user/{userId} to get applications for a specific user
     *
     * @param request  the HttpServletRequest
     * @param response the HttpServletResponse
     * @throws IOException if an I/O error occurs while reading the request or sending the response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String path = request.getPathInfo();

        try {
            if (path != null && path.startsWith("/posting/")) {
                String postingId = path.substring("/posting/".length());
                List<application> applications = jobBoardController.getApplicationsForPosting(postingId);
                sendJsonResponse(response, applications);
            } else if (path != null && path.startsWith("/user/")) {
                String userId = path.substring("/user/".length());
                List<application> applications = jobBoardController.getApplicationsByUser(userId);
                sendJsonResponse(response, applications);
            } else {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid request path");
            }
        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retrieving applications");
        }
    }

    /**
     * Validates that the required fields for an application request are present and non-empty.
     *
     * @param request the ApplicationRequest object to validate
     * @return true if the request is valid, false otherwise
     */
    private boolean isValidApplication(ApplicationRequest request) {
        return request.student != null &&
               request.postingId != null &&
               request.firstName != null && !request.firstName.trim().isEmpty() &&
               request.lastName != null && !request.lastName.trim().isEmpty() &&
               request.email != null && !request.email.trim().isEmpty();
    }

    /**
     * Represents the expected JSON request body for submitting a new application.
     */
    private static class ApplicationRequest {
        StudentAccount student;
        String postingId;
        String firstName;
        String lastName;
        String phoneNumber;
        String email;
        String education;
        String experience;
        String references;
    }
}
