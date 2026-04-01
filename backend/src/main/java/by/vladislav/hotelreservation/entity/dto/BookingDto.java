package by.vladislav.hotelreservation.entity.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Data transfer object for booking")
public record BookingDto(
    @Schema(description = "Unique identifier", example = "100") 
    Long id,

    @NotBlank(message = "Guest name is required") 
    @Schema(description = "Full name of the guest", example = "John Doe") 
    String guestName,

    @NotNull(message = "date should be not null") 
    @Schema(description = "Check-in date", example = "2025-06-01") 
    @FutureOrPresent(message = "check in must be in future, not in the past")
    LocalDate checkInDate,

    @NotNull(message = "date should be not null") 
    @Schema(description = "Check-out date", example = "2025-06-10") 
    @FutureOrPresent(message = "check out must be in future, not in the past")
    LocalDate checkOutDate,

    @Schema(description = "Booked room") 
    RoomDto room) {
}
