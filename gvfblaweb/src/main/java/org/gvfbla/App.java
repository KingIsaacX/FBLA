package org.gvfbla;

import java.util.List;
import java.util.Map;

import io.javalin.Javalin;
import static io.javalin.apibuilder.ApiBuilder.delete;
import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;
import static io.javalin.apibuilder.ApiBuilder.put;
import io.javalin.http.Context;

public class App {
    private static JobBoardController jobBoardController;

    public static void main(String[] args) {
        // Initialize the file system
        FileStorageManager.initializeStorage();
        
        // Initialize the main controller
        jobBoardController = new JobBoardController();

        Javalin app = Javalin.create(config -> {
            config.plugins.enableCors(cors -> {
                cors.add(it -> {
                    it.anyHost();  // Allows all hosts
                    it.exposeHeader("*");
                    it.allowCredentials = true;
                });
            });
            config.jsonMapper(new GsonJsonMapper(GsonProvider.getGson()));
        });

        // Define routes
        app.routes(() -> {
            // Authentication Routes
            path("api/auth", () -> {
                post("login", App::handleLogin);
                post("register/student", App::handleStudentRegistration);
                post("register/employer", App::handleEmployerRegistration);
                post("logout", App::handleLogout);
            });

            // Job Posting Routes
            path("api/postings", () -> {
                get(ctx -> {
                    String search = ctx.queryParam("search");
                    if (search != null && !search.isEmpty()) {
                        ctx.json(jobBoardController.searchPostings(search));
                    } else {
                        ctx.json(jobBoardController.getAllPostings());
                    }
                });
                
                post(ctx -> {
                    posting newPosting = ctx.bodyAsClass(posting.class);
                    posting created = jobBoardController.createJobPosting(
                        (EmployerAccount)ctx.attribute("user"),
                        newPosting.getJobTitle(),
                        newPosting.getJobDescription(),
                        newPosting.getSkills(),
                        String.valueOf(newPosting.getStartingSalary()),
                        newPosting.getLocation()
                    );
                    ctx.json(created);
                });

                path("{id}", () -> {
                    get(ctx -> {
                        String id = ctx.pathParam("id");
                        posting post = jobBoardController.getPostingById(id);
                        if (post != null) {
                            ctx.json(post);
                        } else {
                            ctx.status(404).json(Map.of("error", "Posting not found"));
                        }
                    });

                    put(ctx -> {
                        String id = ctx.pathParam("id");
                        posting updatedPosting = ctx.bodyAsClass(posting.class);
                        jobBoardController.updatePosting(updatedPosting);
                        ctx.json(updatedPosting);
                    });

                    delete(ctx -> {
                        String id = ctx.pathParam("id");
                        jobBoardController.deletePosting(id);
                        ctx.json(Map.of("message", "Posting deleted successfully"));
                    });
                });
            });

            // Stats Route
            get("api/stats", ctx -> {
                ctx.json(Map.of(
                    "activeJobs", jobBoardController.getActiveJobsCount(),
                    "companies", jobBoardController.getCompaniesCount(),
                    "studentsPlaced", jobBoardController.getStudentsPlacedCount()
                ));
            });

            // Application Routes
            path("api/applications", () -> {
                post(ctx -> {
                    application appl = ctx.bodyAsClass(application.class);
                    jobBoardController.submitApplication(
                        appl.getPerson() instanceof StudentAccount ? (StudentAccount)appl.getPerson() : null,
                        appl.getPostingId(),
                        appl.getFirstName(),
                        appl.getLastName(),
                        appl.getPhoneNumber(),
                        appl.getEmail(),
                        appl.getEducation(),
                        appl.getExperience(),
                        appl.getReferences()
                    );
                    ctx.json(Map.of("message", "Application submitted successfully"));
                });

                get("posting/{postingId}", ctx -> {
                    String postingId = ctx.pathParam("postingId");
                    List<application> applications = jobBoardController.getApplicationsForPosting(postingId);
                    ctx.json(applications);
                });

                get("user/{userId}", ctx -> {
                    String userId = ctx.pathParam("userId");
                    List<application> applications = jobBoardController.getApplicationsByUser(userId);
                    ctx.json(applications);
                });
            });
        });

        // Error Handling
        app.exception(Exception.class, (e, ctx) -> {
            ctx.status(500).json(Map.of(
                "error", "Internal server error",
                "message", e.getMessage()
            ));
            e.printStackTrace();
        });

        // Start the server
        app.start(7000);
    }

    // Auth handlers
    private static void handleLogin(Context ctx) {
        try {
            Map<String, String> credentials = ctx.bodyAsClass(Map.class);
            account user = jobBoardController.login(
                credentials.get("username"),
                credentials.get("password")
            );
            if (user != null) {
                ctx.json(Map.of("success", true, "user", user));
            } else {
                ctx.status(401).json(Map.of("error", "Invalid credentials"));
            }
        } catch (Exception e) {
            ctx.status(400).json(Map.of("error", e.getMessage()));
        }
    }

    private static void handleStudentRegistration(Context ctx) {
        try {
            Map<String, String> registration = ctx.bodyAsClass(Map.class);
            StudentAccount student = jobBoardController.registerStudent(
                registration.get("username"),
                registration.get("password"),
                registration.get("email"),
                registration.get("firstName"),
                registration.get("lastName")
            );
            ctx.json(Map.of("success", true, "user", student));
        } catch (AccountException e) {
            ctx.status(400).json(Map.of("error", e.getMessage()));
        }
    }

    private static void handleEmployerRegistration(Context ctx) {
        try {
            Map<String, String> registration = ctx.bodyAsClass(Map.class);
            EmployerAccount employer = jobBoardController.registerEmployer(
                registration.get("username"),
                registration.get("password"),
                registration.get("email"),
                registration.get("companyName")
            );
            ctx.json(Map.of("success", true, "user", employer));
        } catch (AccountException e) {
            ctx.status(400).json(Map.of("error", e.getMessage()));
        }
    }

    private static void handleLogout(Context ctx) {
        ctx.json(Map.of("message", "Logged out successfully"));
    }
}