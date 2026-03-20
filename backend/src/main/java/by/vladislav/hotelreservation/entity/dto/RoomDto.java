package by.vladislav.hotelreservation.entity.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Data transfer object for room")
public record RoomDto(
    @Schema(description = "Unique identifier", example = "20") 
    Long id,

    @NotNull(message = "Number is required") 
    @Min(value = 0, message = "Number should be greater than 0") 
    @Schema(description = "Room number", example = "101") 
    int number,

    @NotBlank(message = "Type is required") 
    @Schema(description = "Room type", example = "Deluxe") 
    String type,

    @NotNull(message = "Price is required") 
    @DecimalMin(value = "10", message = "Price cannot be smaller than 10") 
    @Schema(description = "Price per night", example = "150.00") 
    BigDecimal pricePerNight) {
}
