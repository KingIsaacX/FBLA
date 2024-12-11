package org.gvfbla.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gvfbla.posting;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/postings/filter/*")
public class FilterController extends BaseApiController {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            String category = request.getParameter("category");
            String jobType = request.getParameter("type");
            
            Map<String, String> filters = new HashMap<>();
            if (category != null && !category.equals("all")) {
                filters.put("category", category);
            }
            if (jobType != null) {
                filters.put("type", jobType);
            }
            
            List<posting> filteredPosts = jobBoardController.filterPostings(filters);
            sendJsonResponse(response, filteredPosts);
        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                     "Error filtering postings");
        }
    }
}
