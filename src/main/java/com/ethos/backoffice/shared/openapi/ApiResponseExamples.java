package com.ethos.backoffice.shared.openapi;

public final class ApiResponseExamples {

    private ApiResponseExamples() {}

    public static final String UNAUTHORIZED = """
            {
              "status": 401,
              "error": "Unauthorized",
              "message": "Invalid email or password",
              "timestamp": "2026-03-04T10:00:00",
              "fieldErrors": null
            }
            """;

    public static final String TOO_MANY_REQUESTS = """
            {
              "success": false,
              "message": "Rate limit exceeded. Try again later.",
              "data": null
            }
            """;

    public static final String INTERNAL_SERVER_ERROR = """
            {
              "status": 500,
              "error": "Internal Server Error",
              "message": "An unexpected error occurred",
              "timestamp": "2026-03-04T10:00:00",
              "fieldErrors": null
            }
            """;

    public static final String USER_NOT_FOUND = """
            {
              "status": 404,
              "error": "Not Found",
              "message": "User not found with id: 550e8400-e29b-41d4-a716-446655440000",
              "timestamp": "2026-03-04T10:00:00",
              "fieldErrors": null
            }
            """;

    public static final String USER_CONFLICT = """
            {
              "status": 409,
              "error": "Conflict",
              "message": "User already exists with email: john@example.com",
              "timestamp": "2026-03-04T10:00:00",
              "fieldErrors": null
            }
            """;

    public static final String PROJECT_NOT_FOUND = """
            {
              "status": 404,
              "error": "Not Found",
              "message": "Project not found with id: 550e8400-e29b-41d4-a716-446655440000",
              "timestamp": "2026-03-04T10:00:00",
              "fieldErrors": null
            }
            """;

    public static final String PROJECT_VALIDATION = """
            {
              "status": 400,
              "error": "Bad Request",
              "message": "Validation failed",
              "timestamp": "2026-03-04T10:00:00",
              "fieldErrors": {
                "name": "must not be blank",
                "ownerId": "must not be null"
              }
            }
            """;
}
