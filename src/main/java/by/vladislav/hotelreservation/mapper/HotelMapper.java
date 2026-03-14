package by.vladislav.hotelreservation.mapper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import by.vladislav.hotelreservation.entity.Address;
import by.vladislav.hotelreservation.entity.Hotel;
import by.vladislav.hotelreservation.entity.dto.AddressDtox;
import by.vladislav.hotelreservation.entity.dto.ConvenienceDtox;
import by.vladislav.hotelreservation.entity.dto.HotelDtox;
import by.vladislav.hotelreservation.entity.dto.RoomDtox;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class HotelMapper {

  private final RoomMapper roomMapper;
  private final ConvenienceMapper convenienceMapper;

  public Hotel toEntity(HotelDtox dto) {
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

  public HotelDtox toDTO(Hotel hotel) {

    AddressDtox addressDTO = new AddressDtox(
        hotel.getAddress().getId(),
        hotel.getAddress().getCountry(),
        hotel.getAddress().getCity(),
        hotel.getAddress().getStreet());

    Set<ConvenienceDtox> conveniencesDTOs = hotel.getConveniences().stream()
        .map(convenienceMapper::toDTO)
        .collect(Collectors.toSet());

    List<RoomDtox> roomsDTO = hotel.getRooms().stream()
        .map(room -> roomMapper.toDTO(room))
        .toList();

    return new HotelDtox(
        hotel.getId(),
        hotel.getName(),
        addressDTO,
        hotel.getRating(),
        roomsDTO,
        conveniencesDTOs);
  }
}
