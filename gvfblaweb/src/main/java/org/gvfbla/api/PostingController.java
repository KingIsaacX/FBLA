package org.gvfbla.api;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.gvfbla.posting;

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

    /**
     * Handles HTTP GET requests for postings. If no path is specified, returns
     * all postings or those matching a search query. If a posting ID is included
     * in the path, returns the specific posting.
     *
     * @param request  the HttpServletRequest
     * @param response the HttpServletResponse
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String path = request.getPathInfo();
        try {
            if (path == null || path.equals("/")) {
                String searchQuery = request.getParameter("search");
                List<posting> postings;
                
                if (searchQuery != null && !searchQuery.isEmpty()) {
                    postings = jobBoardController.searchPostings(searchQuery);
                } else {
                    postings = jobBoardController.getAllPostings();
                }
                sendJsonResponse(response, postings);
            } else {
                String postingId = path.substring(1);
                posting post = jobBoardController.getPostingById(postingId);
                if (post != null) {
                    sendJsonResponse(response, post);
                } else {
                    sendError(response, HttpServletResponse.SC_NOT_FOUND, "Posting not found");
                }
            }
        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing request");
        }
    }

    /**
     * Handles HTTP POST requests to create a new posting. Expects a JSON body
     * representing the posting details.
     *
     * @param request  the HttpServletRequest
     * @param response the HttpServletResponse
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            PostingRequest postingRequest = gson.fromJson(request.getReader(), PostingRequest.class);
            posting newPosting = jobBoardController.createPosting(
                postingRequest.companyName,
                postingRequest.jobTitle,
                postingRequest.jobDescription,
                postingRequest.skills,
                postingRequest.startingSalary,
                postingRequest.location
            );
            
            sendJsonResponse(response, newPosting);
        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid posting data");
        }
    }

    /**
     * Handles HTTP PUT requests to update an existing posting. Expects a JSON body
     * representing the updated posting details. The posting ID should be included
     * in the request path.
     *
     * @param request  the HttpServletRequest
     * @param response the HttpServletResponse
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String path = request.getPathInfo();
        String postingId = path.substring(1);

        try {
            PostingRequest updateRequest = gson.fromJson(request.getReader(), PostingRequest.class);
            posting existingPosting = jobBoardController.getPostingById(postingId);
            
            if (existingPosting == null) {
                sendError(response, HttpServletResponse.SC_NOT_FOUND, "Posting not found");
                return;
            }

            posting updatedPosting = new posting(
                updateRequest.companyName,
                updateRequest.jobTitle,
                updateRequest.jobDescription,
                updateRequest.skills,
                updateRequest.startingSalary,
                updateRequest.location
            );
            
            jobBoardController.updatePosting(updatedPosting);
            sendJsonResponse(response, updatedPosting);
        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid update data");
        }
    }

    /**
     * Handles HTTP DELETE requests to remove an existing posting by its ID.
     *
     * @param request  the HttpServletRequest
     * @param response the HttpServletResponse
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String path = request.getPathInfo();
        String postingId = path.substring(1);

        try {
            jobBoardController.deletePosting(postingId);
            sendJsonResponse(response, Map.of("message", "Posting deleted successfully"));
        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_NOT_FOUND, "Posting not found");
        }
    }

    /**
     * Represents the request body for creating or updating postings.
     */
    private static class PostingRequest {
        String companyName;
        String jobTitle;
        String jobDescription;
        String skills;
        String startingSalary;
        String location;
    }
}
