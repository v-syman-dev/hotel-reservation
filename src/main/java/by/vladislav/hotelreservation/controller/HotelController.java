package by.vladislav.hotelreservation.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import by.vladislav.hotelreservation.entity.dto.ErrorResponse;
import by.vladislav.hotelreservation.entity.dto.HotelDto;
import by.vladislav.hotelreservation.service.HotelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/hotels")
@Tag(name = "Hotels API", description = "CRUD operations for hotels")
public class HotelController {

  private final HotelService hotelService;

  @PostMapping
  @Operation(summary = "Create new hotel", description = "Adds a hotel to the database and clears cache")
  @ApiResponse(
      responseCode = "201", 
      description = "Hotel created successfully", 
      content = @Content(schema = @Schema(implementation = HotelDto.class)))
  @ApiResponse(
      responseCode = "400", 
      description = "Invalid input data", 
      content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  public ResponseEntity<HotelDto> create(@Valid @RequestBody HotelDto hotelRequest) {
    return ResponseEntity.status(HttpStatus.CREATED).body(hotelService.create(hotelRequest));
  }

  @PostMapping("/bulk")
  @Operation(summary = "Create list of new hotels", description = "Adds multiple hotels and clears cache")
  @ApiResponse(responseCode = "201", 
      description = "Hotels created successfully", 
      content = @Content(schema = @Schema(implementation = HotelDto.class)))
  @ApiResponse(responseCode = "400", 
      description = "Invalid input data", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  public ResponseEntity<List<HotelDto>> createList(@Valid @RequestBody List<HotelDto> hotelRequest) {
    return ResponseEntity.status(HttpStatus.CREATED).body(hotelService.saveBulk(hotelRequest));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get hotel by ID")
  @ApiResponse(responseCode = "200", 
      description = "Hotel found", content = @Content(schema = @Schema(implementation = HotelDto.class)))
  @ApiResponse(responseCode = "404", 
      description = "Hotel not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  public ResponseEntity<HotelDto> findById(
      @Parameter(description = "Hotel ID", example = "1", required = true) @PathVariable Long id) {
    return ResponseEntity.status(HttpStatus.OK).body(hotelService.findById(id));
  }

  @Operation(summary = "Get all hotels", description = "Returns paged hotels")  
  @ApiResponse(responseCode = "200", 
      description = "List of all hotels", content = @Content(schema = @Schema(implementation = HotelDto.class)))
  @GetMapping
  public ResponseEntity<Page<HotelDto>> findAll(
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "10") Integer size) {
    return ResponseEntity.status(HttpStatus.OK).body(hotelService.findAll(page, size));
  }

  @GetMapping("/search")
  @Operation(summary = "Search hotels by country and minimum rating", description = "Returns paginated results")
  @ApiResponse(responseCode = "200", 
      description = "Successful search", content = @Content(schema = @Schema(implementation = Page.class)))
  @ApiResponse(responseCode = "400", 
      description = "Invalid search parameters", 
      content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  public ResponseEntity<Page<HotelDto>> search(
      @Parameter(description = "Country to filter", example = "USA", required = true) @RequestParam String country,

      @Parameter(description = "Minimum rating (inclusive)", 
      example = "4.5", required = true) @RequestParam BigDecimal minRating,

      @Parameter(description = "Page number (0‑based)", example = "0") @RequestParam(defaultValue = "0") int page,

      @Parameter(description = "Page size", example = "10") @RequestParam(defaultValue = "10") int size) {
    Page<HotelDto> result = hotelService.findByCountryAndGreaterThanMinRating(country, minRating, page, size);
    return ResponseEntity.ok(result);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update hotel", description = "Updates hotel data and clears cache")
  @ApiResponse(responseCode = "200", 
      description = "Hotel updated successfully", content = @Content(schema = @Schema(implementation = HotelDto.class)))
  @ApiResponse(responseCode = "400", 
      description = "Invalid input data", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  @ApiResponse(responseCode = "404", 
      description = "Hotel not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  public ResponseEntity<HotelDto> update(@PathVariable Long id, @Valid @RequestBody HotelDto hotelRequest) {
    return ResponseEntity.status(HttpStatus.OK).body(hotelService.update(id, hotelRequest));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete hotel", description = "Deletes hotel and clears cache")
  @ApiResponse(responseCode = "200", 
      description = "Hotel deleted successfully", content = @Content(schema = @Schema(example = "Deleted")))
  @ApiResponse(responseCode = "404", 
      description = "Hotel not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  public ResponseEntity<String> removeById(
      @Parameter(description = "Hotel ID", example = "1", required = true) @PathVariable Long id) {
    hotelService.deleteById(id);
    return ResponseEntity.status(HttpStatus.OK).body("Deleted");
  }

  @PostMapping("/bulk-non-transactional")
  @Operation(summary = "Create list of new hotels with exception",
       description = "Adds half of hotels")
  @ApiResponse(responseCode = "201", 
      description = "Hotels created successfully", 
      content = @Content(schema = @Schema(implementation = HotelDto.class)))
  @ApiResponse(responseCode = "400", 
      description = "Invalid input data", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  public ResponseEntity<List<HotelDto>> createListWithError(@Valid @RequestBody List<HotelDto> hotelRequest) {
    return ResponseEntity.status(HttpStatus.CREATED).body(hotelService.saveBulkNonTransactional(hotelRequest, true));
  }
}
