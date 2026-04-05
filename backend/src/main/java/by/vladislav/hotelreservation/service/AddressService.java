package by.vladislav.hotelreservation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import by.vladislav.hotelreservation.entity.Address;
import by.vladislav.hotelreservation.entity.Hotel;
import by.vladislav.hotelreservation.entity.constant.EntityType;
import by.vladislav.hotelreservation.entity.dto.AddressDto;
import by.vladislav.hotelreservation.exception.EntityNotFoundException;
import by.vladislav.hotelreservation.mapper.AddressMapper;
import by.vladislav.hotelreservation.repository.AddressRepository;
import by.vladislav.hotelreservation.repository.HotelRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class AddressService {
  private final HotelRepository hotelRepository;
  private final AddressRepository addressRepository;
  private final AddressMapper addressMapper;

  @Transactional
  public AddressDto update(Long hotelId, AddressDto addressDTO) {
    Address address = addressRepository.findByHotelId(hotelId)
        .orElseThrow(() -> new EntityNotFoundException(EntityType.HOTEL, "id", hotelId));

    address.setCountry(addressDTO.country());
    address.setCity(addressDTO.city());
    address.setStreet(addressDTO.street());

    return addressMapper.toDTO(address);
  }

  @Transactional(readOnly = true)
  public AddressDto findAddress(Long hotelId) {
    Hotel hotel = hotelRepository.findById(hotelId)
        .orElseThrow(() -> new EntityNotFoundException(EntityType.HOTEL, "id", hotelId));

    return addressMapper.toDTO(hotel.getAddress());
  }
}
