package by.vladislav.hotelreservation.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import by.vladislav.hotelreservation.entity.Booking;
import by.vladislav.hotelreservation.entity.Room;
import by.vladislav.hotelreservation.entity.dto.BookingDTO;
import by.vladislav.hotelreservation.entity.dto.RoomDTO;
import by.vladislav.hotelreservation.mapper.BookingMapper;
import by.vladislav.hotelreservation.repository.BookingRepository;
import by.vladislav.hotelreservation.repository.RoomRepository;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
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
    BookingDTO requestDto = new BookingDTO(null, "Guest N1", in, out, new RoomDTO(roomId, 101, "Suite", pricePerNight));

    when(bookingRepository.existsByRoomIdAndCheckInDateLessThanAndCheckOutDateGreaterThan(any(), any(), any()))
        .thenReturn(false);

    when(bookingMapper.toEntity(any())).thenReturn(new Booking());
    when(roomRepository.findById(anyLong())).thenReturn(Optional.of(room));

    when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

    bookingService.create(roomId, requestDto);

    verify(bookingRepository).save(argThat(booking -> booking.getTotalPrice().compareTo(BigDecimal.valueOf(300)) == 0));
  }

  @Test
  void createShouldThrowIfDatesAreWrong() {
    BookingDTO dto = new BookingDTO(null, "guest N1", LocalDate.now().plusDays(5), LocalDate.now().plusDays(2), null);

    assertThrows(IllegalArgumentException.class, () -> bookingService.create(1L, dto));
  }
}
