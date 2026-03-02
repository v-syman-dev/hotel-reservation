package by.vladislav.hotelreservation.entity.dto;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Implement single error format for all endpoints")
public record ErrorResponse(
    @Schema(description = "Timestamp of error", example = "2025-05-20T14:30:00") 
    LocalDateTime timestamp,

    @Schema(description = "HTTP status code", example = "400") 
    int status,

    @Schema(description = "HTTP error reason", example = "Bad Request") 
    String error,

    @Schema(description = "Error message", example = "Validation failed") 
    String message,

    @Schema(description = "List of detailed error messages") 
    List<String> details) {
}
