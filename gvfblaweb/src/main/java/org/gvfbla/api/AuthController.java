package org.gvfbla.api;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

import org.gvfbla.AccountManager;
import org.gvfbla.AdminAccount;
import org.gvfbla.EmployerAccount;
import org.gvfbla.StudentAccount;
import org.gvfbla.account;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Handles authentication-related actions such as login, registration, logout, and retrieving authenticated user details.
 */
@WebServlet("/api/auth/*")
public class AuthController extends BaseApiController {
    private static final String JWT_SECRET = "your-secret-key";  // In production, use environment variables
    private AccountManager accountManager;

    @Override
    public void init() throws jakarta.servlet.ServletException {
        super.init();
        this.accountManager = new AccountManager(); // Initialize AccountManager
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String path = request.getPathInfo();
        
        try {
            switch (path) {
                case "/login":
                    handleLogin(request, response);
                    break;
                case "/register/student":
                    handleStudentRegistration(request, response);
                    break;
                case "/register/employer":
                    handleEmployerRegistration(request, response);
                    break;
                case "/logout":
                    handleLogout(request, response);
                    break;
                default:
                    sendError(response, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found");
            }
        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String path = request.getPathInfo();

        if ("/me".equals(path)) {
            handleGetMe(request, response);
        } else {
            sendError(response, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found");
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        LoginRequest loginRequest = gson.fromJson(request.getReader(), LoginRequest.class);
        
        try {
            account account = accountManager.login(
                loginRequest.username,
                loginRequest.password
            );
            
            if (account != null) {
                String token = generateToken(account);
                LoginResponse loginResponse = new LoginResponse(
                    account.getId(),
                    account.getUsername(),
                    account.getRole(),
                    token
                );
                sendJsonResponse(response, loginResponse);
            } else {
                sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid credentials or inactive account");
            }
        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Login failed");
        }
    }

    private void handleStudentRegistration(HttpServletRequest request, 
                                         HttpServletResponse response) 
            throws IOException {
        try {
            RegisterStudentRequest registerRequest = 
                gson.fromJson(request.getReader(), RegisterStudentRequest.class);
            
            StudentAccount student = accountManager.registerStudent(
                registerRequest.username,
                registerRequest.password,
                registerRequest.email,
                registerRequest.firstName,
                registerRequest.lastName
            );
            
            // Auto-login after registration
            String token = generateToken(student);
            LoginResponse loginResponse = new LoginResponse(
                student.getId(),
                student.getUsername(),
                student.getRole(),
                token
            );
            
            sendJsonResponse(response, loginResponse);
        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, 
                     "Registration failed: " + e.getMessage());
        }
    }

    private void handleEmployerRegistration(HttpServletRequest request, 
                                          HttpServletResponse response) 
            throws IOException {
        try {
            RegisterEmployerRequest registerRequest = 
                gson.fromJson(request.getReader(), RegisterEmployerRequest.class);
            
            EmployerAccount employer = accountManager.registerEmployer(
                registerRequest.username,
                registerRequest.password,
                registerRequest.email,
                registerRequest.companyName
            );
            
            // Auto-login after registration
            String token = generateToken(employer);
            LoginResponse loginResponse = new LoginResponse(
                employer.getId(),
                employer.getUsername(),
                employer.getRole(),
                token
            );
            
            sendJsonResponse(response, loginResponse);
        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, 
                     "Registration failed: " + e.getMessage());
        }
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        // Stateless tokens - instruct frontend to delete the token.
        sendJsonResponse(response, Map.of("message", "Logged out successfully"));
    }

    private void handleGetMe(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header");
            return;
        }

        String token = authHeader.substring(7); // Remove "Bearer " prefix
        account user = validateTokenAndGetUser(token);
        if (user == null) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
            return;
        }

        Map<String, String> userData = Map.of(
            "id", user.getId(),
            "username", user.getUsername(),
            "role", user.getRole(),
            "email", user.getEmail(),
            "lastLoginDate", user.getLastLoginDate() != null ? user.getLastLoginDate() : ""
        );

        sendJsonResponse(response, userData);
    }

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

    private String generateToken(account account) {
        // Simple token generation - in production use a proper JWT library
        String tokenData = String.format("%s:%s:%d", 
            account.getId(), 
            account.getRole(), 
            System.currentTimeMillis()
        );
        return Base64.getEncoder().encodeToString(tokenData.getBytes());
    }

    // Request/Response classes
    private static class LoginRequest {
        String username;
        String password;
    }

    private static class RegisterStudentRequest {
        String username;
        String password;
        String email;
        String firstName;
        String lastName;
    }

    private static class RegisterEmployerRequest {
        String username;
        String password;
        String email;
        String companyName;
    }

    private static class LoginResponse {
        String id;
        String username;
        String role;
        String token;

        LoginResponse(String id, String username, String role, String token) {
            this.id = id;
            this.username = username;
            this.role = role;
            this.token = token;
        }
    }
}
