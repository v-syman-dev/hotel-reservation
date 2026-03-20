package by.vladislav.hotelreservation.mapper;

import org.springframework.stereotype.Component;

import by.vladislav.hotelreservation.entity.Room;
import by.vladislav.hotelreservation.entity.dto.RoomDto;

@Component
public class RoomMapper {

  public Room toEntity(RoomDto roomRequest) {
    return Room.builder()
        .number(roomRequest.number())
        .type(roomRequest.type())
        .pricePerNight(roomRequest.pricePerNight())
        .build();
  }

  public RoomDto toDTO(Room roomEntity) {
    return new RoomDto(
        roomEntity.getId(),
        roomEntity.getNumber(),
        roomEntity.getType(),
        roomEntity.getPricePerNight());
  }
}
