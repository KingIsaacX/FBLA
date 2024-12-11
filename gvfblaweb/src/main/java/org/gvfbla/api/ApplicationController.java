package org.gvfbla.api;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.gvfbla.StudentAccount;
import org.gvfbla.account;
import org.gvfbla.application;
import org.gvfbla.EmployerAccount;
import org.gvfbla.AccountManager;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Handles HTTP requests related to job applications.
 * Supports submitting new applications, as well as retrieving applications by posting or user.
 */
@WebServlet("/api/applications/*")
public class ApplicationController extends BaseApiController {

    private AccountManager accountManager;

    @Override
    public void init() throws jakarta.servlet.ServletException {
        super.init();
        this.accountManager = new AccountManager(); // Initialize AccountManager
    }

    /**
     * Handles HTTP POST requests to submit a new application or manage applications.
     * Supports paths:
     * - /submit: Submits a new application.
     * - /manage: Employers can accept or reject applicants.
     *
     * @param request  the HttpServletRequest
     * @param response the HttpServletResponse
     * @throws IOException if an I/O error occurs while reading the request or sending the response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String path = request.getPathInfo();

        try {
            switch (path) {
                case "/submit":
                    handleSubmitApplication(request, response);
                    break;
                case "/manage":
                    handleManageApplication(request, response);
                    break;
                default:
                    sendError(response, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found");
            }
        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
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

    private void handleSubmitApplication(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        ApplicationSubmitRequest appRequest = gson.fromJson(request.getReader(), ApplicationSubmitRequest.class);

        // Validate token
        account user = validateTokenAndGetUser(appRequest.token);
        if (user == null || !(user instanceof StudentAccount)) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid or missing student token");
            return;
        }

        try {
            application newApp = jobBoardController.submitApplication(
                (StudentAccount) user,
                appRequest.postingId,
                appRequest.firstName,
                appRequest.lastName,
                appRequest.phoneNumber,
                appRequest.email,
                appRequest.education,
                appRequest.experience,
                appRequest.references
            );
            sendJsonResponse(response, Map.of(
                "message", "Application submitted successfully",
                "application", newApp
            ));
        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Failed to submit application: " + e.getMessage());
        }
    }

    private void handleManageApplication(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        ApplicationManageRequest manageRequest = gson.fromJson(request.getReader(), ApplicationManageRequest.class);

        // Validate employer token
        account user = validateTokenAndGetUser(manageRequest.token);
        if (user == null || !(user instanceof EmployerAccount)) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid or missing employer token");
            return;
        }

        EmployerAccount employer = (EmployerAccount) user;

        try {
            if ("accept".equalsIgnoreCase(manageRequest.action)) {
                jobBoardController.acceptApplication(employer, manageRequest.applicationId);
                sendJsonResponse(response, Map.of("message", "Application accepted successfully"));
            } else if ("reject".equalsIgnoreCase(manageRequest.action)) {
                jobBoardController.rejectApplication(employer, manageRequest.applicationId, manageRequest.reason);
                sendJsonResponse(response, Map.of("message", "Application rejected successfully"));
            } else {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
            }
        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Failed to manage application: " + e.getMessage());
        }
    }

    /**
     * Validates the token and retrieves the associated user account.
     *
     * @param token the authentication token
     * @return the account if valid, null otherwise
     */
    private account validateTokenAndGetUser(String token) {
        try {
            // Decode the token (assuming it's Base64 encoded "id:role:timestamp")
            byte[] decodedBytes = Base64.getDecoder().decode(token);
            String decodedString = new String(decodedBytes);
            String[] parts = decodedString.split(":");
            if (parts.length < 2) {
                return null;
            }
            String userId = parts[0];
            String role = parts[1];
            // Optional: Validate timestamp for token expiration

            // Retrieve the user by ID
            account user = accountManager.findAccountById(userId);
            if (user == null || !user.getRole().equals(role)) {
                return null;
            }
            return user;
        } catch (IllegalArgumentException e) {
            // Invalid Base64 encoding
            return null;
        }
    }

    // Request/Response classes
    private static class AdminRequest {
        String token;       // Bearer token for admin authentication
        String postingId;
        String reason;      // For rejection
    }

    private static class ApplicationSubmitRequest {
        String token;        // Bearer token for student authentication
        String postingId;
        String firstName;
        String lastName;
        String phoneNumber;
        String email;
        String education;
        String experience;
        String references;
    }

    private static class ApplicationManageRequest {
        String token;        // Bearer token for employer authentication
        String applicationId;
        String action;       // "accept" or "reject"
        String reason;       // Required if action is "reject"
    }
}
