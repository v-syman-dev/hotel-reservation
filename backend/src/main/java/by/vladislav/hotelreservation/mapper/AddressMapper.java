package by.vladislav.hotelreservation.mapper;

import org.springframework.stereotype.Component;

import by.vladislav.hotelreservation.entity.Address;
import by.vladislav.hotelreservation.entity.dto.AddressDto;

@Component
public class AddressMapper {
  public Address toEntity(AddressDto dto) {
    return Address.builder()
        .country(dto.country())
        .city(dto.city())
        .street(dto.street())
        .build();
  }

  public AddressDto toDTO(Address entity) {
    return new AddressDto(
        entity.getId(),
        entity.getCountry(),
        entity.getCity(),
        entity.getStreet());
  }
}
