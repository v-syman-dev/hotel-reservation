package by.vladislav.hotelreservation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import by.vladislav.hotelreservation.entity.Address;
import by.vladislav.hotelreservation.entity.dto.AddressDto;
import by.vladislav.hotelreservation.mapper.AddressMapper;
import by.vladislav.hotelreservation.repository.AddressRepository;

@ExtendWith(MockitoExtension.class)
public class AddressServiceTest {
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

    when(addressRepository.findByHotelId(hotelId)).thenReturn(Optional.of(existingAddress));

    addressService.update(hotelId, updateRequest);

    assertEquals("USA", existingAddress.getCountry());
    assertEquals("NY", existingAddress.getCity());

    verify(addressMapper).toDTO(existingAddress);
  }
}
