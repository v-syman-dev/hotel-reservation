package by.vladislav.hotelreservation.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import by.vladislav.hotelreservation.entity.dto.AddressDtox;
import by.vladislav.hotelreservation.entity.dto.ErrorResponse;
import by.vladislav.hotelreservation.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@AllArgsConstructor
@RestController
@RequestMapping("hotels/{hotelId}/address")
@Tag(name = "Address API", description = "CRUD operations for addresses")
public class AddressController {

  private final AddressService addressService;

  @Operation(summary = "Get address", description = "Return addressDto by Hotel id")
  @ApiResponse(responseCode = "200", 
      description = "Address founded", 
      content = @Content(schema = @Schema(implementation = AddressDtox.class)))
  @ApiResponse(responseCode = "400", 
      description = "Invalid input data", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  @GetMapping
  public ResponseEntity<AddressDtox> findAddress(@PathVariable Long hotelId) {
    return ResponseEntity.status(HttpStatus.OK).body(addressService.findAddress(hotelId));
  }

  @Operation(summary = "Update address", description = "Update address by Hotel id")
  @ApiResponse(responseCode = "200", 
      description = "Address updated", 
      content = @Content(schema = @Schema(implementation = AddressDtox.class)))
  @ApiResponse(responseCode = "400", 
      description = "Invalid input data", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))  
  @PutMapping
  public ResponseEntity<AddressDtox> update(@PathVariable Long hotelId, @Valid @RequestBody AddressDtox addressDTO) {
    return ResponseEntity.status(HttpStatus.OK).body(addressService.update(hotelId, addressDTO));
  }

}
