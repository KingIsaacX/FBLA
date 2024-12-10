package org.gvfbla.api;

import java.io.IOException;
import java.util.Map;

import org.gvfbla.JobBoardController;

import com.google.gson.Gson;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Provides common functionalities for API controllers, including sending JSON responses
 * and error messages. Serves as a base class for other API endpoints to extend.
 */
public abstract class BaseApiController extends HttpServlet {
    protected final Gson gson = new Gson();
    protected final JobBoardController jobBoardController;

    /**
     * Constructs a BaseApiController and initializes a JobBoardController instance
     * for handling job board operations.
     */
    public BaseApiController() {
        this.jobBoardController = new JobBoardController();
    }

    /**
     * Sends a JSON response to the client with the specified data object.
     *
     * @param response the HttpServletResponse to which the JSON will be written
     * @param data     the data object to serialize as JSON
     * @throws IOException if an I/O error occurs when writing to the response
     */
    protected void sendJsonResponse(HttpServletResponse response, Object data) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(gson.toJson(data));
    }

    /**
     * Sends an error response to the client with a specified HTTP status code and message.
     *
     * @param response the HttpServletResponse to which the error message will be written
     * @param status   the HTTP status code to return
     * @param message  the error message to include in the response
     * @throws IOException if an I/O error occurs when writing to the response
     */
    protected void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        sendJsonResponse(response, Map.of("error", message));
    }
}
