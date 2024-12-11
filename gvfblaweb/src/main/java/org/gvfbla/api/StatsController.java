package org.gvfbla.api;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/stats")
public class StatsController extends BaseApiController {
    private static final Logger logger = LoggerFactory.getLogger(StatsController.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            logger.info("Handling stats request");
            
            // Get all stats in a single call
            Map<String, Integer> stats = jobBoardController.getAllStats();
            
            // Send response
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            sendJsonResponse(response, stats);
            
            logger.info("Successfully sent stats response");
        } catch (Exception e) {
            logger.error("Error fetching statistics", e);
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                     "Error fetching statistics: " + e.getMessage());
        }
    }
}