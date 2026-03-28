package by.vladislav.hotelreservation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import by.vladislav.hotelreservation.entity.Booking;
import by.vladislav.hotelreservation.entity.Room;
import by.vladislav.hotelreservation.entity.dto.BookingDto;
import by.vladislav.hotelreservation.entity.dto.RoomDto;
import by.vladislav.hotelreservation.exception.EntityNotFoundException;
import by.vladislav.hotelreservation.mapper.BookingMapper;
import by.vladislav.hotelreservation.repository.BookingRepository;
import by.vladislav.hotelreservation.repository.RoomRepository;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
  @Mock
  private BookingRepository bookingRepository;
  @Mock
  private RoomRepository roomRepository;
  @Mock
  private BookingMapper bookingMapper;
  @InjectMocks
  private BookingService bookingService;

  @Test
  void createShouldCalculateCorrectPrice() {

    Long roomId = 1L;
    LocalDate in = LocalDate.now();
    LocalDate out = in.plusDays(3);
    BigDecimal pricePerNight = BigDecimal.valueOf(100);
    Room room = Room.builder().pricePerNight(pricePerNight).build();
    BookingDto requestDto = new BookingDto(null, "Guest N1", in, out, new RoomDto(roomId, 101, "Suite", pricePerNight));
    Booking entity = new Booking();
    BookingDto expected = new BookingDto(1L, "Guest N1", in, out, new RoomDto(roomId, 101, "Suite", pricePerNight));

    when(bookingRepository.existsByRoomIdAndCheckInDateLessThanAndCheckOutDateGreaterThan(any(), any(), any()))
        .thenReturn(false);
    when(bookingMapper.toEntity(any())).thenReturn(entity);
    when(roomRepository.findById(anyLong())).thenReturn(Optional.of(room));
    when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(bookingMapper.toDTO(entity)).thenReturn(expected);

    BookingDto result = bookingService.create(roomId, requestDto);

    assertEquals(expected, result);
    verify(bookingRepository).save(argThat(booking -> booking.getTotalPrice().compareTo(BigDecimal.valueOf(300)) == 0));
  }

  @Test
  void createShouldThrowIfDatesAreWrong() {
    BookingDto dto = new BookingDto(null, "guest N1", LocalDate.now().plusDays(5), LocalDate.now().plusDays(2), null);

    assertThrows(IllegalArgumentException.class, () -> bookingService.create(1L, dto));
  }

  @Test
  void createShouldThrowWhenCheckInAndCheckOutAreEqual() {
    LocalDate date = LocalDate.now();
    BookingDto dto = new BookingDto(null, "guest", date, date, null);
    Room room = Room.builder().pricePerNight(BigDecimal.valueOf(100)).build();

    when(bookingRepository.existsByRoomIdAndCheckInDateLessThanAndCheckOutDateGreaterThan(any(), any(), any()))
        .thenReturn(false);
    when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
    when(bookingMapper.toEntity(dto)).thenReturn(new Booking());

    assertThrows(IllegalArgumentException.class, () -> bookingService.create(1L, dto));
  }

  @Test
  void createShouldThrowWhenCheckInDateIsNull() {
    LocalDate checkOut = LocalDate.now().plusDays(1);
    BookingDto dto = new BookingDto(null, "guest", null, checkOut, null);

    assertThrows(IllegalArgumentException.class, () -> bookingService.create(1L, dto));
  }

  @Test
  void createShouldThrowWhenCheckOutDateIsNull() {
    LocalDate checkIn = LocalDate.now();
    BookingDto dto = new BookingDto(null, "guest", checkIn, null, null);

    assertThrows(IllegalArgumentException.class, () -> bookingService.create(1L, dto));
  }

  @Test
  void createShouldThrowWhenRoomAlreadyBooked() {
    BookingDto dto = new BookingDto(null, "guest", LocalDate.now(), LocalDate.now().plusDays(2), null);

    when(bookingRepository.existsByRoomIdAndCheckInDateLessThanAndCheckOutDateGreaterThan(any(), any(), any()))
        .thenReturn(true);

    assertThrows(IllegalStateException.class, () -> bookingService.create(1L, dto));
  }

  @Test
  void createShouldThrowWhenRoomMissing() {
    BookingDto dto = new BookingDto(null, "guest", LocalDate.now(), LocalDate.now().plusDays(2), null);

    when(bookingRepository.existsByRoomIdAndCheckInDateLessThanAndCheckOutDateGreaterThan(any(), any(), any()))
        .thenReturn(false);
    when(roomRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> bookingService.create(1L, dto));
  }

  @Test
  void findByRoomIdShouldMapAllBookings() {
    Booking first = new Booking();
    Booking second = new Booking();
    BookingDto firstDto = new BookingDto(1L, "A", LocalDate.now(), LocalDate.now().plusDays(1), null);
    BookingDto secondDto = new BookingDto(2L, "B", LocalDate.now(), LocalDate.now().plusDays(2), null);

    when(bookingRepository.findByRoomId(3L)).thenReturn(List.of(first, second));
    when(bookingMapper.toDTO(first)).thenReturn(firstDto);
    when(bookingMapper.toDTO(second)).thenReturn(secondDto);

    List<BookingDto> result = bookingService.findByRoomId(3L);

    assertIterableEquals(List.of(firstDto, secondDto), result);
  }

  @Test
  void updateShouldRecalculatePriceAndSave() {
    Long roomId = 1L;
    Long bookingId = 2L;
    LocalDate in = LocalDate.now();
    LocalDate out = in.plusDays(4);
    RoomDto roomDto = new RoomDto(roomId, 11, "Suite", BigDecimal.valueOf(120));
    BookingDto request = new BookingDto(bookingId, "Updated Guest", in, out, roomDto);
    Booking booking = new Booking();
    BookingDto expected = new BookingDto(bookingId, "Updated Guest", in, out, roomDto);

    when(bookingRepository.existsByRoomIdAndCheckInDateLessThanAndCheckOutDateGreaterThan(any(), any(), any()))
        .thenReturn(false);
    when(bookingRepository.findByIdAndRoomId(bookingId, roomId)).thenReturn(Optional.of(booking));
    when(bookingRepository.save(booking)).thenReturn(booking);
    when(bookingMapper.toDTO(booking)).thenReturn(expected);

    BookingDto result = bookingService.update(roomId, bookingId, request);

    assertEquals(expected, result);
    assertEquals("Updated Guest", booking.getGuestName());
    assertEquals(BigDecimal.valueOf(480), booking.getTotalPrice());
  }

  @Test
  void updateShouldThrowIfDatesAreWrong() {
    BookingDto dto = new BookingDto(1L, "guest", LocalDate.now().plusDays(3), LocalDate.now().plusDays(1),
        new RoomDto(1L, 1, "Type", BigDecimal.TEN));

    assertThrows(IllegalArgumentException.class, () -> bookingService.update(1L, 1L, dto));
  }

  @Test
  void updateShouldThrowWhenCheckInDateIsNull() {
    BookingDto dto = new BookingDto(1L, "guest", null, LocalDate.now().plusDays(1),
        new RoomDto(1L, 1, "Type", BigDecimal.TEN));

    assertThrows(IllegalArgumentException.class, () -> bookingService.update(1L, 1L, dto));
  }

  @Test
  void updateShouldThrowWhenCheckOutDateIsNull() {
    BookingDto dto = new BookingDto(1L, "guest", LocalDate.now(), null,
        new RoomDto(1L, 1, "Type", BigDecimal.TEN));

    assertThrows(IllegalArgumentException.class, () -> bookingService.update(1L, 1L, dto));
  }

  @Test
  void updateShouldThrowWhenRoomAlreadyBooked() {
    BookingDto dto = new BookingDto(1L, "guest", LocalDate.now(), LocalDate.now().plusDays(1),
        new RoomDto(1L, 1, "Type", BigDecimal.TEN));

    when(bookingRepository.existsByRoomIdAndCheckInDateLessThanAndCheckOutDateGreaterThan(any(), any(), any()))
        .thenReturn(true);

    assertThrows(IllegalStateException.class, () -> bookingService.update(1L, 1L, dto));
  }

  @Test
  void removeByIdShouldDeleteFoundBooking() {
    Booking booking = new Booking();

    when(bookingRepository.findByIdAndRoomId(2L, 1L)).thenReturn(Optional.of(booking));

    bookingService.removeById(1L, 2L);

    verify(bookingRepository).delete(booking);
  }

  @Test
  void findBookingShouldThrowWhenMissing() {
    when(bookingRepository.findByIdAndRoomId(2L, 1L)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> bookingService.findBooking(1L, 2L));
  }

  @Test
  void findBookingShouldReturnEntityWhenPresent() {
    Booking booking = new Booking();

    when(bookingRepository.findByIdAndRoomId(3L, 2L)).thenReturn(Optional.of(booking));

    Booking result = bookingService.findBooking(2L, 3L);

    assertNotNull(result);
    assertEquals(booking, result);
  }

  @Test
  void calculatePriceShouldThrowWhenStartIsNull() {
    LocalDate end = LocalDate.now().plusDays(1);
    BigDecimal price = BigDecimal.TEN;
    assertThrows(IllegalArgumentException.class,
        () -> ReflectionTestUtils.invokeMethod(
            bookingService,
            "calculatePrice",
            null,
            end,
            price));
  }

  @Test
  void calculatePriceShouldThrowWhenEndIsNull() {
    LocalDate start = LocalDate.now();
    BigDecimal price = BigDecimal.TEN;
    assertThrows(IllegalArgumentException.class,
        () -> ReflectionTestUtils.invokeMethod(
            bookingService,
            "calculatePrice",
            start,
            null,
            price));
  }
}
