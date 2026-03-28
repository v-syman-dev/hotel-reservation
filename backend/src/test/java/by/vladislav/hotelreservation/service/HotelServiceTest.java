package by.vladislav.hotelreservation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import by.vladislav.hotelreservation.entity.Address;
import by.vladislav.hotelreservation.entity.Convenience;
import by.vladislav.hotelreservation.entity.Hotel;
import by.vladislav.hotelreservation.entity.Room;
import by.vladislav.hotelreservation.entity.dto.AddressDto;
import by.vladislav.hotelreservation.entity.dto.ConvenienceDto;
import by.vladislav.hotelreservation.entity.dto.HotelDto;
import by.vladislav.hotelreservation.entity.dto.HotelShortDto;
import by.vladislav.hotelreservation.entity.dto.RoomDto;
import by.vladislav.hotelreservation.exception.EntityNotFoundException;
import by.vladislav.hotelreservation.mapper.HotelMapper;
import by.vladislav.hotelreservation.mapper.RoomMapper;
import by.vladislav.hotelreservation.repository.ConvenienceRepository;
import by.vladislav.hotelreservation.repository.HotelRepository;
import jakarta.persistence.EntityManager;

@ExtendWith(MockitoExtension.class)
class HotelServiceTest {
  @Mock
  private HotelRepository hotelRepository;
  @Mock
  private HotelMapper hotelMapper;
  @Mock
  private RoomMapper roomMapper;
  @Mock
  private ConvenienceRepository convenienceRepository;
  @Mock
  private EntityManager entityManager;

  @Spy
  @InjectMocks
  private HotelService hotelService;

  @Test
  void createShouldSaveHotelWithRoomsAndConveniences() {
    ConvenienceDto convenienceDto = new ConvenienceDto(1L, "WIFI");
    RoomDto roomDto = new RoomDto(null, 101, "Suite", BigDecimal.valueOf(150));
    AddressDto addressDto = new AddressDto(null, "Belarus", "Minsk", "Lenina");
    HotelDto request = new HotelDto(null, "Grand", addressDto,
        BigDecimal.valueOf(5), List.of(roomDto), Set.of(convenienceDto));
    Hotel hotel = Hotel.builder().address(new Address()).build();
    Hotel saved = Hotel.builder().id(1L).address(new Address()).build();
    Room room = new Room();
    Convenience convenience = Convenience.builder().id(1L).name("WIFI").build();
    HotelDto expected = new HotelDto(1L, "Grand", addressDto,
        BigDecimal.valueOf(5), List.of(roomDto), Set.of(convenienceDto));

    when(convenienceRepository.findByNameIn(Set.of("WIFI"))).thenReturn(List.of(convenience));
    when(hotelMapper.toEntity(request)).thenReturn(hotel);
    when(hotelRepository.save(hotel)).thenReturn(saved);
    when(roomMapper.toEntity(roomDto)).thenReturn(room);
    when(hotelMapper.toDTO(saved)).thenReturn(expected);

    HotelDto result = hotelService.create(request);

    assertEquals(expected, result);
    assertEquals(1, saved.getRooms().size());
    assertEquals(saved, saved.getRooms().get(0).getHotel());
    assertEquals(Set.of(convenience), hotel.getConveniences());
  }

  @Test
  void createShouldSaveHotelWithoutRoomsWhenRoomsAreNull() {
    AddressDto addressDto = new AddressDto(null, "Belarus", "Minsk", "Lenina");
    ConvenienceDto convenienceDto = new ConvenienceDto(1L, "WIFI");
    HotelDto request = new HotelDto(null, "No Rooms Hotel", addressDto, BigDecimal.valueOf(4), null,
        Set.of(convenienceDto));
    Hotel hotel = Hotel.builder().address(new Address()).build();
    Hotel saved = Hotel.builder().id(10L).address(new Address()).build();
    Convenience convenience = Convenience.builder().id(1L).name("WIFI").build();
    HotelDto expected = new HotelDto(10L, "No Rooms Hotel", addressDto, BigDecimal.valueOf(4), null,
        Set.of(convenienceDto));

    when(convenienceRepository.findByNameIn(Set.of("WIFI"))).thenReturn(List.of(convenience));
    when(hotelMapper.toEntity(request)).thenReturn(hotel);
    when(hotelRepository.save(hotel)).thenReturn(saved);
    when(hotelMapper.toDTO(saved)).thenReturn(expected);

    HotelDto result = hotelService.create(request);

    assertEquals(expected, result);
  }

  @Test
  void saveBulkShouldSaveHotelsAndFlushBatch() {
    List<HotelDto> request = new ArrayList<>();
    Set<ConvenienceDto> conveniences = Set.of(new ConvenienceDto(1L, "WIFI"));

    for (int i = 0; i < 51; i++) {
      HotelDto dto = new HotelDto((long) i, "Hotel " + i, new AddressDto(null, "C", "City", "Street"),
          BigDecimal.valueOf(4), List.of(new RoomDto(null, 100 + i, "Type", BigDecimal.TEN)), conveniences);
      Hotel hotel = Hotel.builder().address(new Address()).rooms(new ArrayList<>()).build();
      Room room = new Room();
      request.add(dto);

      when(hotelMapper.toEntity(dto)).thenReturn(hotel);
      when(roomMapper.toEntity(dto.rooms().get(0))).thenReturn(room);
      when(hotelMapper.toDTO(hotel)).thenReturn(dto);
    }

    when(convenienceRepository.findAllByNameIn(Set.of("WIFI")))
        .thenReturn(List.of(Convenience.builder().id(1L).name("WIFI").build()));
    ReflectionTestUtils.setField(hotelService, "entityManager", entityManager);

    List<HotelDto> result = hotelService.saveBulk(request);

    assertEquals(51, result.size());
    verify(entityManager).flush();
    verify(entityManager).clear();
  }

  @Test
  void saveBulkShouldHandleHotelsWithoutRooms() {
    HotelDto dto = new HotelDto(1L, "Hotel", new AddressDto(null, "C", "City", "Street"),
        BigDecimal.valueOf(4), null, Set.of(new ConvenienceDto(1L, "WIFI")));
    Hotel hotel = Hotel.builder().address(new Address()).build();
    Convenience convenience = Convenience.builder().id(1L).name("WIFI").build();

    when(convenienceRepository.findAllByNameIn(Set.of("WIFI"))).thenReturn(List.of(convenience));
    when(hotelMapper.toEntity(dto)).thenReturn(hotel);
    when(hotelMapper.toDTO(hotel)).thenReturn(dto);

    List<HotelDto> result = hotelService.saveBulk(List.of(dto));

    assertIterableEquals(List.of(dto), result);
  }

  @Test
  void findByIdShouldReturnMappedHotel() {
    Hotel hotel = Hotel.builder().build();
    HotelDto expected = new HotelDto(1L, "Grand", new AddressDto(null, "C", "City", "Street"), BigDecimal.ONE,
        List.of(), Set.of());

    when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
    when(hotelMapper.toDTO(hotel)).thenReturn(expected);

    HotelDto result = hotelService.findById(1L);

    assertEquals(expected, result);
  }

  @Test
  void findByIdShouldThrowWhenHotelMissing() {
    when(hotelRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> hotelService.findById(1L));
  }

  @Test
  void findAllWithoutRoomsShouldMapPage() {
    Pageable pageable = PageRequest.of(0, 2);
    Hotel hotel = Hotel.builder().build();
    HotelShortDto dto = new HotelShortDto(1L, "Short", new AddressDto(null, "C", "City", "Street"), BigDecimal.TEN,
        Set.of());
    Page<Hotel> page = new PageImpl<>(List.of(hotel), pageable, 1);

    when(hotelRepository.findAll(pageable)).thenReturn(page);
    when(hotelMapper.toShortDTO(hotel)).thenReturn(dto);

    Page<HotelShortDto> result = hotelService.findAllWithoutRooms(0, 2);

    assertEquals(dto, result.getContent().get(0));
  }

  @Test
  void findAllShouldMapPage() {
    Pageable pageable = PageRequest.of(0, 2);
    Hotel hotel = Hotel.builder().build();
    HotelDto dto = new HotelDto(1L, "Full", new AddressDto(null, "C", "City", "Street"), BigDecimal.TEN, List.of(),
        Set.of());
    Page<Hotel> page = new PageImpl<>(List.of(hotel), pageable, 1);

    when(hotelRepository.findAll(pageable)).thenReturn(page);
    when(hotelMapper.toDTO(hotel)).thenReturn(dto);

    Page<HotelDto> result = hotelService.findAll(0, 2);

    assertEquals(dto, result.getContent().get(0));
  }

  @Test
  void findByCountryAndRatingShouldUseCacheWithPagination() {
    String country = "Belarus";
    BigDecimal rating = BigDecimal.valueOf(5.0);
    int page = 0;
    int size = 10;
    Pageable pageable = PageRequest.of(page, size);

    Hotel hotel = new Hotel();
    HotelDto dto = new HotelDto(1L, "Luxury Hotel", null, rating, null, null);
    Page<Hotel> hotelPage = new PageImpl<>(List.of(hotel), pageable, 1);

    when(hotelRepository.findBycountryAndMinRating(country, rating, pageable))
        .thenReturn(hotelPage);
    when(hotelMapper.toDTO(hotel)).thenReturn(dto);

    Page<HotelDto> result1 = hotelService.findByCountryAndGreaterThanMinRating(country, rating, page, size);
    Page<HotelDto> result2 = hotelService.findByCountryAndGreaterThanMinRating(country, rating, page, size);

    assertNotNull(result1);
    assertEquals(result1.getContent(), result2.getContent());
    verify(hotelRepository, times(1)).findBycountryAndMinRating(country, rating, pageable);
  }

  @Test
  void updateShouldUseProvidedIdWhenPresent() {
    Hotel hotel = Hotel.builder()
        .name("Old")
        .rating(BigDecimal.ONE)
        .address(Address.builder().country("A").city("B").street("C").build())
        .rooms(new ArrayList<>(List.of(new Room())))
        .conveniences(new HashSet<>())
        .build();
    HotelDto request = new HotelDto(99L, "New", new AddressDto(null, "Belarus", "Minsk", "Lenina"),
        BigDecimal.valueOf(5), List.of(new RoomDto(1L, 10, "Suite", BigDecimal.valueOf(100))),
        Set.of(new ConvenienceDto(1L, "WIFI")));
    Room room = new Room();
    Convenience convenience = Convenience.builder().id(1L).name("WIFI").build();
    HotelDto expected = new HotelDto(1L, "New", request.address(), request.rating(), request.rooms(),
        request.conveniences());

    when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
    when(convenienceRepository.findByNameIn(Set.of("WIFI"))).thenReturn(List.of(convenience));
    when(roomMapper.toEntity(request.rooms().get(0))).thenReturn(room);
    when(hotelMapper.toDTO(hotel)).thenReturn(expected);

    HotelDto result = hotelService.update(1L, request);

    assertEquals(expected, result);
    assertEquals("New", hotel.getName());
    assertEquals("Belarus", hotel.getAddress().getCountry());
    assertEquals(1, hotel.getRooms().size());
  }

  @Test
  void updateShouldUseDtoIdWhenMethodIdIsNull() {
    Hotel hotel = Hotel.builder()
        .address(Address.builder().country("A").city("B").street("C").build())
        .rooms(new ArrayList<>())
        .conveniences(new HashSet<>())
        .build();
    HotelDto request = new HotelDto(5L, "Name", new AddressDto(null, "Belarus", "Minsk", "Lenina"),
        BigDecimal.valueOf(4), null, Set.of());
    HotelDto expected = new HotelDto(5L, "Name", request.address(), request.rating(), null, Set.of());

    when(hotelRepository.findById(5L)).thenReturn(Optional.of(hotel));
    when(convenienceRepository.findByNameIn(Set.of())).thenReturn(List.of());
    when(hotelMapper.toDTO(hotel)).thenReturn(expected);

    HotelDto result = hotelService.update(null, request);

    assertEquals(expected, result);
  }

  @Test
  void updateShouldThrowWhenHotelMissing() {
    HotelDto request = new HotelDto(5L, "Name", new AddressDto(null, "Belarus", "Minsk", "Lenina"),
        BigDecimal.valueOf(4), null, Set.of());

    when(hotelRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> hotelService.update(1L, request));
  }

  @Test
  void updateShouldThrowWhenMethodIdIsNullAndDtoHotelMissing() {
    HotelDto request = new HotelDto(5L, "Name", new AddressDto(null, "Belarus", "Minsk", "Lenina"),
        BigDecimal.valueOf(4), null, Set.of());

    when(hotelRepository.findById(5L)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> hotelService.update(null, request));
  }

  @Test
  void deleteByIdShouldClearCacheAndDelete() {
    String country = "Belarus";
    BigDecimal rating = BigDecimal.valueOf(4);
    Pageable pageable = PageRequest.of(0, 10);
    Hotel hotel = new Hotel();
    HotelDto dto = new HotelDto(1L, "Cached Hotel", null, rating, null, null);
    Page<Hotel> hotelPage = new PageImpl<>(List.of(hotel), pageable, 1);

    when(hotelRepository.findBycountryAndMinRating(country, rating, pageable)).thenReturn(hotelPage);
    when(hotelMapper.toDTO(hotel)).thenReturn(dto);

    hotelService.findByCountryAndGreaterThanMinRating(country, rating, 0, 10);
    hotelService.deleteById(1L);
    hotelService.findByCountryAndGreaterThanMinRating(country, rating, 0, 10);

    verify(hotelRepository).deleteById(1L);
    verify(hotelRepository, times(2)).findBycountryAndMinRating(country, rating, pageable);
  }

  @Test
  void saveBulkNonTransactionalShouldReturnInputDtos() {
    Convenience convenience = Convenience.builder().id(1L).name("WIFI").build();
    HotelDto dto = new HotelDto(1L, "Hotel", new AddressDto(null, "C", "City", "Street"), BigDecimal.ONE,
        List.of(new RoomDto(null, 101, "Suite", BigDecimal.TEN)), Set.of(new ConvenienceDto(1L, "WIFI")));
    Hotel hotel = Hotel.builder().build();
    Hotel savedHotel = Hotel.builder().build();
    Room room = new Room();

    when(convenienceRepository.findByNameIn(Set.of("WIFI"))).thenReturn(List.of(convenience));
    when(hotelMapper.toEntity(dto)).thenReturn(hotel);
    when(hotelRepository.save(hotel)).thenReturn(savedHotel);
    when(roomMapper.toEntity(dto.rooms().get(0))).thenReturn(room);

    List<HotelDto> result = hotelService.saveBulkNonTransactional(List.of(dto), false);

    assertIterableEquals(List.of(dto), result);
  }

  @Test
  void saveBulkNonTransactionalShouldHandleHotelWithoutRooms() {
    HotelDto dto = new HotelDto(1L, "Hotel", new AddressDto(null, "C", "City", "Street"), BigDecimal.ONE, null,
        Set.of(new ConvenienceDto(1L, "WIFI")));
    Convenience convenience = Convenience.builder().id(1L).name("WIFI").build();
    Hotel hotel = Hotel.builder().build();
    Hotel savedHotel = Hotel.builder().build();

    when(convenienceRepository.findByNameIn(Set.of("WIFI"))).thenReturn(List.of(convenience));
    when(hotelMapper.toEntity(dto)).thenReturn(hotel);
    when(hotelRepository.save(hotel)).thenReturn(savedHotel);

    List<HotelDto> result = hotelService.saveBulkNonTransactional(List.of(dto), false);

    assertIterableEquals(List.of(dto), result);
  }

  @Test
  void saveBulkNonTransactionalShouldThrowWhenExceptionRequested() {
    HotelDto dto1 = new HotelDto(1L, "Hotel1", new AddressDto(null, "C", "City", "Street"), BigDecimal.ONE, null,
        Set.of());
    HotelDto dto2 = new HotelDto(2L, "Hotel2", new AddressDto(null, "C", "City", "Street"), BigDecimal.ONE, null,
        Set.of());
    HotelDto dto3 = new HotelDto(3L, "Hotel3", new AddressDto(null, "C", "City", "Street"), BigDecimal.ONE, null,
        Set.of());
    HotelDto dto4 = new HotelDto(4L, "Hotel4", new AddressDto(null, "C", "City", "Street"), BigDecimal.ONE, null,
        Set.of());
    Hotel hotel1 = Hotel.builder().build();
    Hotel hotel2 = Hotel.builder().build();
    List<HotelDto> request = List.of(dto1, dto2, dto3, dto4);

    when(convenienceRepository.findByNameIn(Set.of())).thenReturn(List.of());
    when(hotelMapper.toEntity(dto1)).thenReturn(hotel1);
    when(hotelMapper.toEntity(dto2)).thenReturn(hotel2);
    when(hotelRepository.save(hotel1)).thenReturn(hotel1);
    when(hotelRepository.save(hotel2)).thenReturn(hotel2);

    assertThrows(IllegalArgumentException.class, () -> hotelService.saveBulkNonTransactional(request, true));
  }
}
