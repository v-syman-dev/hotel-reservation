package by.vladislav.hotelreservation.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import by.vladislav.hotelreservation.entity.Booking;
import by.vladislav.hotelreservation.entity.Room;
import by.vladislav.hotelreservation.entity.constant.EntityType;
import by.vladislav.hotelreservation.entity.dto.BookingDto;
import by.vladislav.hotelreservation.exception.EntityNotFoundException;
import by.vladislav.hotelreservation.mapper.BookingMapper;
import by.vladislav.hotelreservation.repository.BookingRepository;
import by.vladislav.hotelreservation.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class BookingService {

  private final BookingRepository bookingRepository;
  private final BookingMapper bookingMapper;
  private final RoomRepository roomRepository;

  @Transactional
  public BookingDto create(Long roomId, BookingDto dto) {

    if (dto.checkOutDate().isBefore(dto.checkInDate())) {
      throw new IllegalArgumentException("Check-out date must be after check-in date");
    }

    boolean exists = bookingRepository
        .existsByRoomIdAndCheckInDateLessThanAndCheckOutDateGreaterThan(
            roomId,
            dto.checkOutDate(),
            dto.checkInDate());

    if (exists) {
      throw new IllegalStateException("Room is already booked for these dates");
    }

    Room room = roomRepository.findById(roomId)
        .orElseThrow(() -> new EntityNotFoundException(EntityType.ROOM, "id", roomId));

    Booking entity = bookingMapper.toEntity(dto);

    BigDecimal totalPrice = calculatePrice(
        dto.checkInDate(),
        dto.checkOutDate(),
        room.getPricePerNight());

    entity.setTotalPrice(totalPrice);
    entity.setRoom(room);

    entity = bookingRepository.save(entity);
    return bookingMapper.toDTO(entity);
  }

  public List<BookingDto> findByRoomId(long roomId) {
    List<Booking> list = bookingRepository.findByRoomId(roomId);
    List<BookingDto> result = new ArrayList<>(list.size());
    for (Booking booking : list) {
      BookingDto dto = bookingMapper.toDTO(booking);
      result.add(dto);
    }
    return result;
  }

  @Transactional
  public BookingDto update(Long roomId, Long bookingId, BookingDto dto) {

    if (dto.checkOutDate().isBefore(dto.checkInDate())) {
      throw new IllegalArgumentException("Check-out date must be after check-in date");
    }

    boolean exists = bookingRepository
        .existsByRoomIdAndCheckInDateLessThanAndCheckOutDateGreaterThan(
            roomId,
            dto.checkOutDate(),
            dto.checkInDate());

    if (exists) {
      throw new IllegalStateException("Room is already booked for these dates");
    }

    Booking entity = findBooking(roomId, bookingId);

    entity.setGuestName(dto.guestName());
    entity.setCheckInDate(dto.checkInDate());
    entity.setCheckOutDate(dto.checkOutDate());

    BigDecimal newPrice = calculatePrice(
        dto.checkInDate(),
        dto.checkOutDate(),
        dto.room().pricePerNight());

    entity.setTotalPrice(newPrice);

    Booking updatedEntity = bookingRepository.save(entity);

    return bookingMapper.toDTO(updatedEntity);
  }

  @Transactional
  public void removeById(Long roomId, Long bookingId) {
    Booking booking = findBooking(roomId, bookingId);
    bookingRepository.delete(booking);
  }

  private BigDecimal calculatePrice(LocalDate start, LocalDate end, BigDecimal pricePerNight) {
    if (start == null || end == null || !end.isAfter(start)) {
      throw new IllegalArgumentException("Invalid dates: check-out must be after check-in");
    }
    long nights = ChronoUnit.DAYS.between(start, end);
    return pricePerNight.multiply(BigDecimal.valueOf(nights));
  }

  public Booking findBooking(Long roomId, Long bookingId) {
    return bookingRepository.findByIdAndRoomId(bookingId, roomId)
        .orElseThrow(() -> new EntityNotFoundException(EntityType.BOOKING, "id", bookingId));
  }
}
