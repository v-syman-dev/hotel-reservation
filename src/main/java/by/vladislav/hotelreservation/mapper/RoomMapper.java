package by.vladislav.hotelreservation.mapper;

import org.springframework.stereotype.Component;

import by.vladislav.hotelreservation.entity.Room;
import by.vladislav.hotelreservation.entity.dto.RoomDtox;

@Component
public class RoomMapper {

  public Room toEntity(RoomDtox roomRequest) {
    return Room.builder()
        .number(roomRequest.number())
        .type(roomRequest.type())
        .pricePerNight(roomRequest.pricePerNight())
        .build();
  }

  public RoomDtox toDTO(Room roomEntity) {
    return new RoomDtox(
        roomEntity.getId(),
        roomEntity.getNumber(),
        roomEntity.getType(),
        roomEntity.getPricePerNight());
  }
}
