package by.vladislav.hotelreservation.service;

import org.springframework.stereotype.Service;

import by.vladislav.hotelreservation.entity.Address;
import by.vladislav.hotelreservation.entity.Hotel;
import by.vladislav.hotelreservation.entity.constant.EntityType;
import by.vladislav.hotelreservation.entity.dto.AddressDtox;
import by.vladislav.hotelreservation.exception.EntityNotFoundException;
import by.vladislav.hotelreservation.mapper.AddressMapper;
import by.vladislav.hotelreservation.repository.AddressRepository;
import by.vladislav.hotelreservation.repository.HotelRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class AddressService {
  private final HotelRepository hotelRepository;
  private final AddressRepository addressRepository;
  private final AddressMapper addressMapper;

  @Transactional
  public AddressDtox update(Long hotelId, AddressDtox addressDTO) {
    Address address = addressRepository.findByHotelId(hotelId)
        .orElseThrow(() -> new EntityNotFoundException(EntityType.HOTEL, "id", hotelId));

    address.setCountry(addressDTO.country());
    address.setCity(addressDTO.city());
    address.setStreet(addressDTO.street());

    return addressMapper.toDTO(address);
  }

  public AddressDtox findAddress(Long hotelId) {
    Hotel hotel = hotelRepository.findById(hotelId)
        .orElseThrow(() -> new EntityNotFoundException(EntityType.HOTEL, "id", hotelId));

    return addressMapper.toDTO(hotel.getAddress());
  }
}
