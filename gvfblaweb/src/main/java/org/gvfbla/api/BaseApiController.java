package org.gvfbla.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import org.gvfbla.JobBoardController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;

/**
 * A base controller that provides common functionalities for all API controllers,
 * such as JSON parsing and response handling.
 */
public abstract class BaseApiController extends HttpServlet {
    protected Gson gson = new GsonBuilder().setPrettyPrinting().create();
    protected JobBoardController jobBoardController = new JobBoardController(); // Initialize your main controller

    /**
     * Sends a JSON response with the given data and HTTP status code 200.
     *
     * @param response the HttpServletResponse
     * @param data     the data to send as JSON
     * @throws IOException if an I/O error occurs while writing the response
     */
    protected void sendJsonResponse(HttpServletResponse response, Object data) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();
        out.print(gson.toJson(data));
        out.flush();
    }

    /**
     * Sends an error response with the given status code and message.
     *
     * @param response   the HttpServletResponse
     * @param statusCode the HTTP status code
     * @param message    the error message
     * @throws IOException if an I/O error occurs while writing the response
     */
    protected void sendError(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setContentType("application/json");
        response.setStatus(statusCode);
        PrintWriter out = response.getWriter();
        out.print(gson.toJson(Map.of("error", message)));
        out.flush();
    }
}
