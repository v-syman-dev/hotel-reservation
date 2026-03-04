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
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
public class RoomCRUD {

  private final RoomService roomService;

  @PostMapping
  public ResponseEntity<RoomDTO> create(@PathVariable Long hotelId, @RequestBody RoomDTO roomRequest) {
    return ResponseEntity.status(HttpStatus.CREATED).body(roomService.create(hotelId, roomRequest));
  }

  @GetMapping("/{id}")
  public ResponseEntity<RoomDTO> findById(@PathVariable Long hotelId, @PathVariable Long id) {
    return ResponseEntity.status(HttpStatus.OK).body(roomService.findById(hotelId, id));
  }

  @GetMapping
  public ResponseEntity<List<RoomDTO>> findAll(@PathVariable Long hotelId) {
    return ResponseEntity.status(HttpStatus.OK).body(roomService.findAll(hotelId));
  }

  @PutMapping
  public ResponseEntity<RoomDTO> update(@PathVariable Long hotelId, @RequestBody RoomDTO roomRequest) {
    return ResponseEntity.status(HttpStatus.OK).body(roomService.update(hotelId, roomRequest));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteById(@PathVariable Long hotelId, @PathVariable Long id) {
    roomService.deleteById(hotelId, id);
    return ResponseEntity.status(HttpStatus.OK).body("Deleted");
  }
}
