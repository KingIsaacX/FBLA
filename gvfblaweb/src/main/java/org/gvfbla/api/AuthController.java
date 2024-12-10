package org.gvfbla.api;

import java.io.IOException;
import java.util.Map;

import org.gvfbla.account;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Handles authentication-related endpoints such as user login.
 */
@WebServlet("/api/auth/*")
public class AuthController extends BaseApiController {

    /**
     * Handles HTTP POST requests for authentication endpoints.
     * Currently supports the "/login" endpoint, which attempts to authenticate
     * a user with provided credentials.
     *
     * @param request  the HttpServletRequest
     * @param response the HttpServletResponse
     * @throws IOException if an I/O error occurs during request processing
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String path = request.getPathInfo();

        if ("/login".equals(path)) {
            try {
                LoginRequest loginRequest = gson.fromJson(request.getReader(), LoginRequest.class);
                account acc = jobBoardController.login(
                    loginRequest.username, 
                    loginRequest.password
                );

                if (acc != null) {
                    Map<String, String> loginResponse = Map.of(
                        "id", acc.getId(),
                        "username", acc.getUsername(),
                        "role", acc.getRole()
                    );
                    sendJsonResponse(response, loginResponse);
                } else {
                    sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid credentials");
                }
            } catch (Exception e) {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid request format");
            }
        } else {
            sendError(response, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found");
        }
    }

    /**
     * Represents the expected JSON request body for user login.
     */
    private static class LoginRequest {
        String username;
        String password;
    }
}
