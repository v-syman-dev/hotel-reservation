package by.vladislav.hotelreservation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import by.vladislav.hotelreservation.entity.Hotel;
import by.vladislav.hotelreservation.entity.Room;
import by.vladislav.hotelreservation.entity.dto.RoomDto;
import by.vladislav.hotelreservation.exception.EntityNotFoundException;
import by.vladislav.hotelreservation.mapper.RoomMapper;
import by.vladislav.hotelreservation.repository.HotelRepository;
import by.vladislav.hotelreservation.repository.RoomRepository;

@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {
  @Mock
  private RoomRepository roomRepository;
  @Mock
  private RoomMapper roomMapper;
  @Mock
  private HotelRepository hotelRepository;

  @InjectMocks
  private RoomService roomService;

  @Test
  void createSuccess() {
    Long hotelId = 1L;
    RoomDto roomRequest = new RoomDto(null, 101, "Deluxe", BigDecimal.valueOf(100));
    Hotel hotel = new Hotel();
    Room roomEntity = new Room();
    Room savedRoom = new Room();
    RoomDto expectedDto = new RoomDto(1L, 101, "Deluxe", BigDecimal.valueOf(100));

    when(roomMapper.toEntity(roomRequest)).thenReturn(roomEntity);
    when(hotelRepository.findById(hotelId)).thenReturn(Optional.of(hotel));
    when(roomRepository.save(roomEntity)).thenReturn(savedRoom);
    when(roomMapper.toDTO(savedRoom)).thenReturn(expectedDto);

    RoomDto result = roomService.create(hotelId, roomRequest);

    assertNotNull(result);
    assertEquals(101, result.number());
    verify(hotelRepository).findById(hotelId);
    verify(roomRepository).save(roomEntity);
  }

  @Test
  void createHotelNotFoundThrowsException() {

    Long hotelId = 99L;
    when(hotelRepository.findById(hotelId)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class,
        () -> roomService.create(hotelId, new RoomDto(null, 1, "Type", BigDecimal.ZERO)));
  }
}
