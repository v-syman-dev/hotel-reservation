package by.vladislav.hotelreservation.entity.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Data transfer object for hotel")
public record HotelDtox(
    @Schema(description = "Unique identifier", example = "10") 
    Long id,

    @NotBlank(message = "Name cannot be empty") 
    @Schema(description = "Hotel name", example = "Grand Hotel") 
    String name,

    @NotNull(message = "Address is required") 
    @Valid 
    @Schema(description = "Hotel address") 
    AddressDtox address,

    @Min(value = 1, message = "Rating must be positive") 
    @Schema(description = "Hotel rating", example = "4.5") 
    BigDecimal rating,

    @Schema(description = "List of rooms in the hotel") 
    List<@Valid RoomDtox> rooms,

    @Schema(description = "Set of conveniences offered") 
    Set<@Valid ConvenienceDtox> conveniences) {
}
