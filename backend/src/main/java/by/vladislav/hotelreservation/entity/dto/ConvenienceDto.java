package by.vladislav.hotelreservation.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Data transfer object for convenience")
public record ConvenienceDto(
    @Schema(description = "Unique identifier", example = "5", accessMode = Schema.AccessMode.READ_ONLY) 
    Long id,

    @NotBlank(message = "name cannot be empty") 
    @Schema(description = "Name of convenience", example = "WIFI") 
    String name) {
}
