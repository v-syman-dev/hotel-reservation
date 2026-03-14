package by.vladislav.hotelreservation.mapper;

import org.springframework.stereotype.Component;

import by.vladislav.hotelreservation.entity.Address;
import by.vladislav.hotelreservation.entity.dto.AddressDtox;

@Component
public class AddressMapper {
  public Address toEntity(AddressDtox dto) {
    return Address.builder()
        .country(dto.country())
        .city(dto.city())
        .street(dto.street())
        .build();
  }

  public AddressDtox toDTO(Address entity) {
    return new AddressDtox(
        entity.getId(),
        entity.getCountry(),
        entity.getCity(),
        entity.getStreet());
  }
}
