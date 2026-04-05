package by.vladislav.hotelreservation.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import by.vladislav.hotelreservation.entity.dto.ErrorResponse;
import by.vladislav.hotelreservation.entity.dto.RoomDto;
import by.vladislav.hotelreservation.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@CrossOrigin(origins = "http://localhost:5173")
@AllArgsConstructor
@RestController
@Tag(name = "Rooms API", description = "CRUD operations for rooms")
public class RoomController {

  private final RoomService roomService;

  @PostMapping("/hotels/{hotelId}/rooms")
  @Operation(summary = "Create new room", description = "Adds a room to a specific hotel")
  @ApiResponse(responseCode = "201", 
      description = "Room created successfully", content = @Content(schema = @Schema(implementation = RoomDto.class)))
  @ApiResponse(responseCode = "400", 
      description = "Invalid input data", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  public ResponseEntity<RoomDto> create(
      @Parameter(description = "Hotel ID", example = "1", required = true) @PathVariable Long hotelId,
      @Valid @RequestBody RoomDto roomRequest) {
    return ResponseEntity.status(HttpStatus.CREATED).body(roomService.create(hotelId, roomRequest));
  }

  @PostMapping("/hotels/{hotelId}/rooms/bulk")
  @Operation(summary = "Create new bulk rooms", description = "Adds a list of rooms to a specific hotel")
  @ApiResponse(responseCode = "201", 
      description = "Rooms created successfully", content = @Content(schema = @Schema(implementation = RoomDto.class)))
  @ApiResponse(responseCode = "400", 
      description = "Invalid input data", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  public ResponseEntity<List<RoomDto>> createBulk(
      @Parameter(description = "Hotel ID", example = "1", required = true) @PathVariable Long hotelId,
      @Valid @RequestBody List<RoomDto> roomRequest) {
    return ResponseEntity.status(HttpStatus.CREATED).body(roomService.saveBulk(hotelId, roomRequest));
  }

  @GetMapping("/rooms/{id}")
  @Operation(summary = "Get room by ID")
  @ApiResponse(responseCode = "200", 
      description = "Room found", content = @Content(schema = @Schema(implementation = RoomDto.class)))
  @ApiResponse(responseCode = "404", 
      description = "Room not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  public ResponseEntity<RoomDto> findById(
      @Parameter(description = "Room ID", example = "1", required = true) @PathVariable Long id) {
    return ResponseEntity.status(HttpStatus.OK).body(roomService.findById(id));
  }

  @GetMapping("/hotels/{hotelId}/rooms")
  @Operation(summary = "Get all rooms by hotel ID")
  @ApiResponse(responseCode = "200", 
      description = "List of rooms for the hotel", content = @Content(schema = @Schema(implementation = RoomDto.class)))
  public ResponseEntity<List<RoomDto>> findAllByHotel(
      @Parameter(description = "Hotel ID", example = "1", required = true) @PathVariable Long hotelId) {
    return ResponseEntity.status(HttpStatus.OK).body(roomService.findAllByHotel(hotelId));
  }

  @GetMapping("/rooms")
  @Operation(summary = "Get all rooms", description = "Returns a list of all rooms in the system")
  @ApiResponse(responseCode = "200", 
      description = "List of all rooms", content = @Content(schema = @Schema(implementation = RoomDto.class)))
  public ResponseEntity<List<RoomDto>> findAll() {
    return ResponseEntity.status(HttpStatus.OK).body(roomService.findAll());
  }

  @PutMapping("/rooms")
  @Operation(summary = "Update room", description = "Updates room data")
  @ApiResponse(responseCode = "200", 
      description = "Room updated successfully", content = @Content(schema = @Schema(implementation = RoomDto.class)))
  @ApiResponse(responseCode = "400", 
      description = "Invalid input data", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  @ApiResponse(responseCode = "404", 
      description = "Room not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  public ResponseEntity<RoomDto> update(@Valid @RequestBody RoomDto roomRequest) {
    return ResponseEntity.status(HttpStatus.OK).body(roomService.update(roomRequest));
  }

  @DeleteMapping("/rooms/{id}")
  @Operation(summary = "Delete room", description = "Removes room from the database")
  @ApiResponse(responseCode = "200", 
      description = "Room deleted successfully", content = @Content(schema = @Schema(example = "Deleted")))
  @ApiResponse(responseCode = "404", 
      description = "Room not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  public ResponseEntity<String> deleteById(
      @Parameter(description = "Room ID", example = "1", required = true) @PathVariable Long id) {
    roomService.deleteById(id);
    return ResponseEntity.status(HttpStatus.OK).body("Deleted");
  }

  @PostMapping("/hotels/{hotelId}/rooms/bulk-non-transactional")
  @Operation(summary = "Create new bulk rooms with error",
       description = "Adds a half of list of rooms to a specific hotel")
  @ApiResponse(responseCode = "201", 
      description = "Rooms created successfully", content = @Content(schema = @Schema(implementation = RoomDto.class)))
  @ApiResponse(responseCode = "400", 
      description = "Invalid input data", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  public ResponseEntity<List<RoomDto>> createBulkWithError(
      @Parameter(description = "Hotel ID", example = "1", required = true) @PathVariable Long hotelId,
      @RequestBody List<RoomDto> roomRequest) {
    return ResponseEntity.status(HttpStatus.CREATED).body(
      roomService.saveBulkNonTransactional(hotelId, roomRequest, true));
  }
}
