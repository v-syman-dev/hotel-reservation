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

import by.vladislav.hotelreservation.entity.dto.ConvenienceDto;
import by.vladislav.hotelreservation.entity.dto.ErrorResponse;
import by.vladislav.hotelreservation.service.ConvenienceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/conveniences")
@Tag(name = "Conveniences API", description = "CRUD operations for conveniences")
public class ConvenienceController {
  private final ConvenienceService convenienceService;

  @Operation(summary = "Create new convenience", description = "Create one new convenience")
  @ApiResponse(responseCode = "201", 
      description = "Conveniences created successfully", 
      content = @Content(schema = @Schema(implementation = ConvenienceDto.class)))
  @ApiResponse(responseCode = "400", 
      description = "Invalid input data", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  @PostMapping
  public ResponseEntity<ConvenienceDto> create(@RequestBody ConvenienceDto convenienceRequest) {
    return ResponseEntity.status(HttpStatus.CREATED).body(convenienceService.create(convenienceRequest));
  }

  @Operation(summary = "Create new conveniences", description = "Create a list of new conveniences")
  @ApiResponse(responseCode = "201", 
      description = "Conveniences created successfully", 
      content = @Content(schema = @Schema(implementation = ConvenienceDto.class)))
  @ApiResponse(responseCode = "400", 
      description = "Invalid input data", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  @PostMapping("/bulk")
  public ResponseEntity<List<ConvenienceDto>> createBulk(@RequestBody List<ConvenienceDto> convenienceRequest) {
    return ResponseEntity.status(HttpStatus.CREATED).body(convenienceService.saveBulk(convenienceRequest));
  }


  @Operation(summary = "Get convenience by id", description = "Return convenienceDto by id")
  @ApiResponse(responseCode = "200", 
      description = "Convenience founded", 
      content = @Content(schema = @Schema(implementation = ConvenienceDto.class)))
  @ApiResponse(responseCode = "400", 
      description = "Invalid input data", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  @GetMapping("/{id}")
  public ResponseEntity<ConvenienceDto> findById(@PathVariable Long id) {
    return ResponseEntity.status(HttpStatus.OK).body(convenienceService.findById(id));
  }

  @Operation(summary = "Get all conveniences", description = "Return all existed conveniences")
  @ApiResponse(responseCode = "200", 
      description = "Conveniences founded", 
      content = @Content(schema = @Schema(implementation = ConvenienceDto.class)))
  @ApiResponse(responseCode = "400", 
      description = "Invalid input data", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  @GetMapping
  public ResponseEntity<List<ConvenienceDto>> findAll() {
    return ResponseEntity.status(HttpStatus.OK).body(convenienceService.findAll());
  }

  @Operation(summary = "Update convenience by id", description = "Update convenience by id")
  @ApiResponse(responseCode = "200", 
      description = "Convenience updated", 
      content = @Content(schema = @Schema(implementation = ConvenienceDto.class)))
  @ApiResponse(responseCode = "400", 
      description = "Invalid input data", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  @PutMapping
  public ResponseEntity<ConvenienceDto> update(@RequestBody ConvenienceDto convenienceRequest) {
    return ResponseEntity.status(HttpStatus.OK).body(convenienceService.update(convenienceRequest));
  }

  @Operation(summary = "Delete convenience", description = "Delete convenience by id")
  @ApiResponse(responseCode = "200", 
      description = "Convenience deleted", 
      content = @Content(schema = @Schema(implementation = ConvenienceDto.class)))
  @ApiResponse(responseCode = "400", 
      description = "Invalid input data", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  @DeleteMapping("/{id}")
  public ResponseEntity<String> removeById(@PathVariable Long id) {
    convenienceService.removeById(id);
    return ResponseEntity.status(HttpStatus.OK).body("Deleted");
  }

}
