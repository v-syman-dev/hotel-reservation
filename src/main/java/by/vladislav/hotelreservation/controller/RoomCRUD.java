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
import org.springframework.web.bind.annotation.RestController;

import by.vladislav.hotelreservation.entity.dto.RoomDTO;
import by.vladislav.hotelreservation.service.RoomService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@Tag(name = "Rooms API", description = "CRUD operations for rooms")
public class RoomCRUD {

  private final RoomService roomService;

  @PostMapping("/hotels/{hotelId}/rooms")
  public ResponseEntity<RoomDTO> create(@PathVariable Long hotelId, @RequestBody RoomDTO roomRequest) {
    return ResponseEntity.status(HttpStatus.CREATED).body(roomService.create(hotelId, roomRequest));
  }

  @GetMapping("/rooms/{id}")
  public ResponseEntity<RoomDTO> findById(@PathVariable Long id) {
    return ResponseEntity.status(HttpStatus.OK).body(roomService.findById(id));
  }

  @GetMapping("/hotels/{hotelId}/rooms")
  public ResponseEntity<List<RoomDTO>> findAllByHotel(@PathVariable Long hotelId) {
    return ResponseEntity.status(HttpStatus.OK).body(roomService.findAllByHotel(hotelId));
  }

  @GetMapping("/rooms")
  public ResponseEntity<List<RoomDTO>> findAll() {
    return ResponseEntity.status(HttpStatus.OK).body(roomService.findAll());
  }

  @PutMapping("/rooms")
  public ResponseEntity<RoomDTO> update(@RequestBody RoomDTO roomRequest) {
    return ResponseEntity.status(HttpStatus.OK).body(roomService.update(roomRequest));
  }

  @DeleteMapping("/rooms/{id}")
  public ResponseEntity<String> deleteById(@PathVariable Long id) {
    roomService.deleteById(id);
    return ResponseEntity.status(HttpStatus.OK).body("Deleted");
  }
}
