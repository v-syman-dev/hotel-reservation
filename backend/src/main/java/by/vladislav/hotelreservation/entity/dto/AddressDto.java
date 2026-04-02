package by.vladislav.hotelreservation.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Data transfer object for address")
public record AddressDto(
    @Schema(description = "Unique identifier", example = "1", accessMode = Schema.AccessMode.READ_ONLY) 
    Long id,

    @NotBlank(message = "Country is required") 
    @Schema(description = "Country name", example = "France") 
    String country,

    @NotBlank(message = "City is required") 
    @Schema(description = "City name", example = "Paris") 
    String city,

    @NotBlank(message = "Street is required") 
    @Schema(description = "Street address", example = "10 Rue de Rivoli") 
    String street) {
}
