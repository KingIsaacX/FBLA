package org.gvfbla.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.gvfbla.application;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/applications/status/*")
public class ApplicationStatusController extends BaseApiController {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            String applicationId = request.getPathInfo().substring(1);
            application app = jobBoardController.getApplicationById(applicationId);
            
            if (app != null) {
                Map<String, Object> status = new HashMap<>();
                status.put("status", app.getStatus());
                status.put("applicationDate", app.getApplicationDate());
                status.put("lastUpdated", app.getLastUpdated());
                
                sendJsonResponse(response, status);
            } else {
                sendError(response, HttpServletResponse.SC_NOT_FOUND, 
                         "Application not found");
            }
        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                     "Error fetching application status");
        }
    }
}
    

