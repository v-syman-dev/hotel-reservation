package by.vladislav.hotelreservation.mapper;

import org.springframework.stereotype.Component;

import by.vladislav.hotelreservation.entity.Booking;
import by.vladislav.hotelreservation.entity.dto.BookingDto;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class BookingMapper {

  private final RoomMapper roomMapper;

  public Booking toEntity(BookingDto dto) {

    return Booking.builder()
        .guestName(dto.guestName())
        .checkInDate(dto.checkInDate())
        .checkOutDate(dto.checkOutDate())
        .totalPrice(null)
        .room(null)
        .build();
  }

  public BookingDto toDTO(Booking entity) {
    return new BookingDto(
        entity.getId(),
        entity.getGuestName(),
        entity.getCheckInDate(),
        entity.getCheckOutDate(),
        roomMapper.toDTO(entity.getRoom()));
  }
}
