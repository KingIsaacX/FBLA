package org.gvfbla.api;

import java.io.IOException;
import java.util.Map;

import org.gvfbla.AdminAccount;
import org.gvfbla.PostingException;
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

            if ("/approve".equals(path)) {
                posting approvedPosting = jobBoardController.approvePosting(
                    adminRequest.admin,
                    adminRequest.postingId
                );
                sendJsonResponse(response, Map.of(
                    "message", "Posting approved successfully",
                    "posting", approvedPosting
                ));
            } else if ("/reject".equals(path)) {
                jobBoardController.rejectPosting(
                    adminRequest.admin,
                    adminRequest.postingId,
                    adminRequest.reason
                );
                sendJsonResponse(response, Map.of("message", "Posting rejected successfully"));
            } else {
                sendError(response, HttpServletResponse.SC_NOT_FOUND, "Invalid endpoint");
            }
        } catch (PostingException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing request");
        }
    }

    /**
     * Represents the expected JSON request body for admin actions,
     * including approving or rejecting a posting.
     */
    private static class AdminRequest {
        AdminAccount admin;
        String postingId;
        String reason;  // For rejection
    }
}
