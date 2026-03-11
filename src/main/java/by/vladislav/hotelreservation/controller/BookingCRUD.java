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

import by.vladislav.hotelreservation.entity.dto.BookingDTO;
import by.vladislav.hotelreservation.service.BookingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("rooms/{roomId}/bookings")
@Tag(name = "Booking API", description = "CRUD operations for bookings")
public class BookingCRUD {
  private final BookingService bookingService;

  @PostMapping
  public ResponseEntity<BookingDTO> create(@PathVariable Long roomId, @RequestBody BookingDTO bookingRequest) {
    return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.create(roomId, bookingRequest));
  }

  @GetMapping
  public ResponseEntity<List<BookingDTO>> findById(@PathVariable Long roomId) {
    return ResponseEntity.status(HttpStatus.OK).body(bookingService.findByRoomId(roomId));
  }

  @PutMapping("/{bookingId}")
  public ResponseEntity<BookingDTO> update(@PathVariable Long roomId, @PathVariable Long bookingId,
      @RequestBody BookingDTO bookingRequest) {
    return ResponseEntity.status(HttpStatus.OK).body(bookingService.update(roomId, bookingId, bookingRequest));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<String> removeById(@PathVariable Long roomId, @PathVariable Long id) {
    bookingService.removeById(roomId, id);
    return ResponseEntity.status(HttpStatus.OK).body("Deleted");
  }

}
