package by.vladislav.hotelreservation.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import by.vladislav.hotelreservation.entity.dto.AddressDTO;
import by.vladislav.hotelreservation.service.AddressService;
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
public class AddressCRUD {

  private final AddressService addressService;

  @GetMapping
  public ResponseEntity<AddressDTO> findAddress(@PathVariable Long hotelId) {
    return ResponseEntity.status(HttpStatus.OK).body(addressService.findAddress(hotelId));
  }

  @PutMapping
  public ResponseEntity<AddressDTO> update(@PathVariable Long hotelId, @Valid @RequestBody AddressDTO addressDTO) {
    return ResponseEntity.status(HttpStatus.OK).body(addressService.update(hotelId, addressDTO));
  }

}
