package com.jmarqb.ms.project.core.infrastructure.adapters.input.rest.dtos.response;

/**
 * OpenApiResponses
 */
public class OpenApiResponses {

	public static final String ENTITY_NOT_FOUND_EXAMPLE = """
        {
          "timestamp": "2024-12-22T23:00:00Z",
          "status": 404,
          "error": "Not Found",
          "message": "The entity not found.",
          "fieldErrors": []
        }
    """;

	public static final String UNAUTHORIZED_EXAMPLE = """
        {
          "timestamp": "2024-12-22T23:00:00Z",
          "status": 401,
          "error": "Unauthorized",
          "message": "You are not authorized to perform this action.",
          "fieldErrors": []
        }
    """;

	public static final String BAD_REQUEST_EXAMPLE = """
        {
          "timestamp": "2024-12-22T23:00:00Z",
          "status": 400,
          "error": "Bad Request",
          "message": "Invalid input data provided.",
          "fieldErrors": [
            {
              "field": "id",
              "message": "ID must be a positive number."
            }
          ]
        }
    """;
}
