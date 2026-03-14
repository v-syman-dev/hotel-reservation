package by.vladislav.hotelreservation.mapper;

import org.springframework.stereotype.Component;

import by.vladislav.hotelreservation.entity.Booking;
import by.vladislav.hotelreservation.entity.dto.BookingDtox;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class BookingMapper {

  private final RoomMapper roomMapper;

  public Booking toEntity(BookingDtox dto) {

    return Booking.builder()
        .guestName(dto.guestName())
        .checkInDate(dto.checkInDate())
        .checkOutDate(dto.checkOutDate())
        .totalPrice(null)
        .room(null)
        .build();
  }

  public BookingDtox toDTO(Booking entity) {
    return new BookingDtox(
        entity.getId(),
        entity.getGuestName(),
        entity.getCheckInDate(),
        entity.getCheckOutDate(),
        roomMapper.toDTO(entity.getRoom()));
  }
}
