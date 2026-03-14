package by.vladislav.hotelreservation.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import by.vladislav.hotelreservation.entity.dto.BookingDtox;
import by.vladislav.hotelreservation.entity.dto.ErrorResponse;
import by.vladislav.hotelreservation.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("rooms/{roomId}/bookings")
@Tag(name = "Booking API", description = "CRUD operations for bookings")
public class BookingController {

  private final BookingService bookingService;

  @PostMapping
  @Operation(summary = "Create new booking", description = "Creates a new booking for a specific room")
  @ApiResponse(responseCode = "201", 
      description = "Booking created successfully", 
      content = @Content(schema = @Schema(implementation = BookingDtox.class)))
  @ApiResponse(responseCode = "400", 
      description = "Invalid input data", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  public ResponseEntity<BookingDtox> create(
      @Parameter(description = "ID of the room", example = "1", required = true) @PathVariable Long roomId,
      @Valid @RequestBody BookingDtox bookingRequest) {
    return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.create(roomId, bookingRequest));
  }

  @GetMapping
  @Operation(summary = "Get bookings by Room ID", 
      description = "Returns a list of all bookings associated with a specific room")
  @ApiResponse(responseCode = "200", 
      description = "List of bookings found", content = @Content(schema = @Schema(implementation = BookingDtox.class)))
  public ResponseEntity<List<BookingDtox>> findByRoomId(
      @Parameter(description = "ID of the room", example = "1", required = true) @PathVariable Long roomId) {
    return ResponseEntity.status(HttpStatus.OK).body(bookingService.findByRoomId(roomId));
  }

  @PutMapping("/{bookingId}")
  @Operation(summary = "Update booking", description = "Updates an existing booking's information")
  @ApiResponse(responseCode = "200", 
      description = "Booking updated successfully", 
      content = @Content(schema = @Schema(implementation = BookingDtox.class)))
  @ApiResponse(responseCode = "400", 
      description = "Invalid input data", 
      content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  @ApiResponse(responseCode = "404", 
      description = "Booking or Room not found", 
      content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  public ResponseEntity<BookingDtox> update(
      @Parameter(description = "ID of the room", example = "1", required = true) @PathVariable Long roomId,
      @Parameter(description = "ID of the booking", example = "10", required = true) @PathVariable Long bookingId,
      @Valid @RequestBody BookingDtox bookingRequest) {
    return ResponseEntity.status(HttpStatus.OK).body(bookingService.update(roomId, bookingId, bookingRequest));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete booking", description = "Removes a booking by its ID")
  @ApiResponse(responseCode = "200", 
      description = "Booking deleted successfully", content = @Content(schema = @Schema(example = "Deleted")))
  @ApiResponse(responseCode = "404", 
      description = "Booking not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  public ResponseEntity<String> removeById(
      @Parameter(description = "ID of the room", example = "1", required = true) @PathVariable Long roomId,
      @Parameter(description = "ID of the booking to delete", example = "10", required = true) @PathVariable Long id) {
    bookingService.removeById(roomId, id);
    return ResponseEntity.status(HttpStatus.OK).body("Deleted");
  }
}
