package by.vladislav.hotelreservation.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import by.vladislav.hotelreservation.entity.dto.BookingDto;
import by.vladislav.hotelreservation.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("hotels/{hotelId}/bookings")
@Tag(name = "Hotel Booking API", description = "Paged booking reads for a hotel")
public class HotelBookingController {

  private final BookingService bookingService;

  @GetMapping
  @Operation(summary = "Get paged bookings by hotel ID")
  @ApiResponse(responseCode = "200",
      description = "Paged list of bookings for the hotel",
      content = @Content(schema = @Schema(implementation = BookingDto.class)))
  public ResponseEntity<Page<BookingDto>> findByHotelId(
      @Parameter(description = "ID of the hotel", example = "1", required = true) @PathVariable Long hotelId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    return ResponseEntity.status(HttpStatus.OK).body(bookingService.findByHotelId(hotelId, page, size));
  }
}

