package by.vladislav.hotelreservation.mapper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import by.vladislav.hotelreservation.entity.Address;
import by.vladislav.hotelreservation.entity.Hotel;
import by.vladislav.hotelreservation.entity.dto.AddressDto;
import by.vladislav.hotelreservation.entity.dto.ConvenienceDto;
import by.vladislav.hotelreservation.entity.dto.HotelDto;
import by.vladislav.hotelreservation.entity.dto.RoomDto;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class HotelMapper {

  private final RoomMapper roomMapper;
  private final ConvenienceMapper convenienceMapper;

  public Hotel toEntity(HotelDto dto) {
    Address address = Address.builder()
        .country(dto.address().country())
        .city(dto.address().city())
        .street(dto.address().street())
        .build();

    return Hotel.builder()
        .name(dto.name())
        .address(address)
        .rating(dto.rating())
        .build();
  }

  public HotelDto toDTO(Hotel hotel) {

    AddressDto addressDTO = new AddressDto(
        hotel.getAddress().getId(),
        hotel.getAddress().getCountry(),
        hotel.getAddress().getCity(),
        hotel.getAddress().getStreet());

    Set<ConvenienceDto> conveniencesDTOs = hotel.getConveniences().stream()
        .map(convenienceMapper::toDTO)
        .collect(Collectors.toSet());

    List<RoomDto> roomsDTO = hotel.getRooms().stream()
        .map(room -> roomMapper.toDTO(room))
        .toList();

    return new HotelDto(
        hotel.getId(),
        hotel.getName(),
        addressDTO,
        hotel.getRating(),
        roomsDTO,
        conveniencesDTOs);
  }
}
