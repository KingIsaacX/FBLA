package org.gvfbla.api;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

import org.gvfbla.AccountManager;
import org.gvfbla.AdminAccount;
import org.gvfbla.PostingException;
import org.gvfbla.account;
import org.gvfbla.posting;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Handles administrative actions such as approving or rejecting postings.
 * Requires an AdminAccount to perform these actions.
 */
@WebServlet("/api/admin/*")
public class AdminController extends BaseApiController {

    private AccountManager accountManager;

    @Override
    public void init() throws jakarta.servlet.ServletException {
        super.init();
        this.accountManager = new AccountManager(); // Initialize AccountManager
    }

    /**
     * Handles HTTP POST requests for admin actions. Supports:
     * - "/approve": Approves a posting with the given posting ID.
     * - "/reject": Rejects a posting with the given posting ID and a reason.
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
            AdminRequest adminRequest = gson.fromJson(request.getReader(), AdminRequest.class);

            // Validate admin token
            AdminAccount admin = validateAdminToken(adminRequest.token);
            if (admin == null) {
                sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid or missing admin token");
                return;
            }

            if ("/approve".equals(path)) {
                posting approvedPosting = jobBoardController.approvePosting(
                    admin,
                    adminRequest.postingId
                );
                sendJsonResponse(response, Map.of(
                    "message", "Posting approved successfully",
                    "posting", approvedPosting
                ));
            } else if ("/reject".equals(path)) {
                jobBoardController.rejectPosting(
                    admin,
                    adminRequest.postingId,
                    adminRequest.reason
                );
                sendJsonResponse(response, Map.of("message", "Posting rejected successfully"));
            } else {
                sendError(response, HttpServletResponse.SC_NOT_FOUND, "Invalid endpoint");
            }
        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing request");
        }
    }

    /**
     * Validates the admin token and retrieves the AdminAccount.
     *
     * @param token the admin token from the request
     * @return the AdminAccount if valid, null otherwise
     */
    private AdminAccount validateAdminToken(String token) {
        account user = validateTokenAndGetUser(token);
        if (user instanceof AdminAccount) {
            return (AdminAccount) user;
        }
        return null;
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

    /**
     * Represents the expected JSON request body for admin actions,
     * including approving or rejecting a posting.
     */
    private static class AdminRequest {
        String token;       // Bearer token for admin authentication
        String postingId;
        String reason;      // For rejection
    }
}
