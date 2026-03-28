package by.vladislav.hotelreservation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
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
class RoomServiceTest {
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
    RoomDto dto = new RoomDto(null, 1, "Type", BigDecimal.ZERO);

    when(hotelRepository.findById(hotelId)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class,
        () -> roomService.create(hotelId, dto));
  }

  @Test
  void saveBulkShouldPersistAllRooms() {
    Long hotelId = 1L;
    Hotel hotel = new Hotel();
    RoomDto first = new RoomDto(null, 1, "Single", BigDecimal.TEN);
    RoomDto second = new RoomDto(null, 2, "Double", BigDecimal.ONE);
    Room firstEntity = new Room();
    Room secondEntity = new Room();
    Room firstSaved = new Room();
    Room secondSaved = new Room();
    RoomDto firstDto = new RoomDto(1L, 1, "Single", BigDecimal.TEN);
    RoomDto secondDto = new RoomDto(2L, 2, "Double", BigDecimal.ONE);

    when(hotelRepository.findById(hotelId)).thenReturn(Optional.of(hotel));
    when(roomMapper.toEntity(first)).thenReturn(firstEntity);
    when(roomMapper.toEntity(second)).thenReturn(secondEntity);
    when(roomRepository.save(firstEntity)).thenReturn(firstSaved);
    when(roomRepository.save(secondEntity)).thenReturn(secondSaved);
    when(roomMapper.toDTO(firstSaved)).thenReturn(firstDto);
    when(roomMapper.toDTO(secondSaved)).thenReturn(secondDto);

    List<RoomDto> result = roomService.saveBulk(hotelId, List.of(first, second));

    assertIterableEquals(List.of(firstDto, secondDto), result);
  }

  @Test
  void saveBulkShouldThrowWhenHotelMissing() {
    when(hotelRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> roomService.saveBulk(1L, List.of()));
  }

  @Test
  void findAllByHotelShouldMapRooms() {
    Room first = new Room();
    Room second = new Room();
    RoomDto firstDto = new RoomDto(1L, 1, "Single", BigDecimal.TEN);
    RoomDto secondDto = new RoomDto(2L, 2, "Double", BigDecimal.ONE);

    when(roomRepository.findByHotelId(1L)).thenReturn(List.of(first, second));
    when(roomMapper.toDTO(first)).thenReturn(firstDto);
    when(roomMapper.toDTO(second)).thenReturn(secondDto);

    List<RoomDto> result = roomService.findAllByHotel(1L);

    assertIterableEquals(List.of(firstDto, secondDto), result);
  }

  @Test
  void findAllShouldMapRooms() {
    Room first = new Room();
    Room second = new Room();
    RoomDto firstDto = new RoomDto(1L, 1, "Single", BigDecimal.TEN);
    RoomDto secondDto = new RoomDto(2L, 2, "Double", BigDecimal.ONE);

    when(roomRepository.findAll()).thenReturn(List.of(first, second));
    when(roomMapper.toDTO(first)).thenReturn(firstDto);
    when(roomMapper.toDTO(second)).thenReturn(secondDto);

    List<RoomDto> result = roomService.findAll();

    assertIterableEquals(List.of(firstDto, secondDto), result);
  }

  @Test
  void findByIdShouldReturnMappedRoom() {
    Room room = new Room();
    RoomDto expected = new RoomDto(1L, 101, "Deluxe", BigDecimal.TEN);

    when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
    when(roomMapper.toDTO(room)).thenReturn(expected);

    RoomDto result = roomService.findById(1L);

    assertEquals(expected, result);
  }

  @Test
  void findByIdShouldThrowWhenMissing() {
    when(roomRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(java.util.NoSuchElementException.class, () -> roomService.findById(1L));
  }

  @Test
  void updateShouldModifyExistingRoom() {
    Room room = new Room();
    RoomDto request = new RoomDto(1L, 202, "Suite", BigDecimal.valueOf(500));
    RoomDto expected = new RoomDto(1L, 202, "Suite", BigDecimal.valueOf(500));

    when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
    when(roomMapper.toDTO(room)).thenReturn(expected);

    RoomDto result = roomService.update(request);

    assertEquals(expected, result);
    assertEquals(202, room.getNumber());
    assertEquals("Suite", room.getType());
  }

  @Test
  void deleteByIdShouldRemoveFoundRoom() {
    Room room = new Room();

    when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

    roomService.deleteById(1L);

    verify(roomRepository).delete(room);
  }

  @Test
  void saveBulkNonTransactionalShouldReturnDtos() {
    Long hotelId = 1L;
    Hotel hotel = new Hotel();
    RoomDto first = new RoomDto(null, 1, "Single", BigDecimal.TEN);
    RoomDto second = new RoomDto(null, 2, "Double", BigDecimal.ONE);
    Room firstEntity = new Room();
    Room secondEntity = new Room();
    Room firstSaved = new Room();
    Room secondSaved = new Room();
    RoomDto firstDto = new RoomDto(1L, 1, "Single", BigDecimal.TEN);
    RoomDto secondDto = new RoomDto(2L, 2, "Double", BigDecimal.ONE);

    when(hotelRepository.findById(hotelId)).thenReturn(Optional.of(hotel));
    when(roomMapper.toEntity(first)).thenReturn(firstEntity);
    when(roomMapper.toEntity(second)).thenReturn(secondEntity);
    when(roomRepository.save(firstEntity)).thenReturn(firstSaved);
    when(roomRepository.save(secondEntity)).thenReturn(secondSaved);
    when(roomMapper.toDTO(firstSaved)).thenReturn(firstDto);
    when(roomMapper.toDTO(secondSaved)).thenReturn(secondDto);

    List<RoomDto> result = roomService.saveBulkNonTransactional(hotelId, List.of(first, second), false);

    assertIterableEquals(List.of(firstDto, secondDto), result);
  }

  @Test
  void saveBulkNonTransactionalShouldThrowWhenExceptionRequested() {
    Long hotelId = 1L;
    Hotel hotel = new Hotel();
    RoomDto first = new RoomDto(null, 1, "Single", BigDecimal.TEN);
    RoomDto second = new RoomDto(null, 2, "Double", BigDecimal.ONE);
    Room firstEntity = new Room();

    when(hotelRepository.findById(hotelId)).thenReturn(Optional.of(hotel));
    when(roomMapper.toEntity(first)).thenReturn(firstEntity);
    when(roomRepository.save(firstEntity)).thenReturn(new Room());

    assertThrows(IllegalArgumentException.class,
        () -> roomService.saveBulkNonTransactional(hotelId, List.of(first, second), true));
  }
}
