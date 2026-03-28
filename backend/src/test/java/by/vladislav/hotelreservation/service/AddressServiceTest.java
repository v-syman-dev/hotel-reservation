package by.vladislav.hotelreservation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import by.vladislav.hotelreservation.entity.Address;
import by.vladislav.hotelreservation.entity.Hotel;
import by.vladislav.hotelreservation.entity.dto.AddressDto;
import by.vladislav.hotelreservation.exception.EntityNotFoundException;
import by.vladislav.hotelreservation.mapper.AddressMapper;
import by.vladislav.hotelreservation.repository.AddressRepository;
import by.vladislav.hotelreservation.repository.HotelRepository;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {
  @Mock
  private HotelRepository hotelRepository;
  @Mock
  private AddressRepository addressRepository;
  @Mock
  private AddressMapper addressMapper;

  @InjectMocks
  private AddressService addressService;

  @Test
  void updateSuccess() {
    Long hotelId = 1L;
    AddressDto updateRequest = new AddressDto(null, "USA", "NY", "Wall St");
    Address existingAddress = new Address();
    AddressDto response = new AddressDto(1L, "USA", "NY", "Wall St");

    when(addressRepository.findByHotelId(hotelId)).thenReturn(Optional.of(existingAddress));
    when(addressMapper.toDTO(existingAddress)).thenReturn(response);

    AddressDto result = addressService.update(hotelId, updateRequest);

    assertEquals("USA", existingAddress.getCountry());
    assertEquals("NY", existingAddress.getCity());
    assertEquals("Wall St", existingAddress.getStreet());
    assertEquals(response, result);
    verify(addressMapper).toDTO(existingAddress);
  }

  @Test
  void updateShouldThrowWhenHotelAddressMissing() {
    Long hotelId = 99L;

    when(addressRepository.findByHotelId(hotelId)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> addressService.update(hotelId, new AddressDto(null, "A", "B", "C")));
  }

  @Test
  void findAddressShouldReturnMappedAddress() {
    Long hotelId = 5L;
    Address address = Address.builder().country("Belarus").city("Minsk").street("Lenina").build();
    Hotel hotel = Hotel.builder().address(address).build();
    AddressDto expected = new AddressDto(2L, "Belarus", "Minsk", "Lenina");

    when(hotelRepository.findById(hotelId)).thenReturn(Optional.of(hotel));
    when(addressMapper.toDTO(address)).thenReturn(expected);

    AddressDto result = addressService.findAddress(hotelId);

    assertNotNull(result);
    assertEquals("Belarus", result.country());
    verify(addressMapper).toDTO(address);
  }

  @Test
  void findAddressShouldThrowWhenHotelMissing() {
    Long hotelId = 7L;

    when(hotelRepository.findById(hotelId)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> addressService.findAddress(hotelId));
  }
}
