package by.vladislav.hotelreservation.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import by.vladislav.hotelreservation.entity.Convenience;
import by.vladislav.hotelreservation.entity.Hotel;
import by.vladislav.hotelreservation.entity.Room;
import by.vladislav.hotelreservation.entity.constant.EntityType;
import by.vladislav.hotelreservation.entity.dto.ConvenienceDto;
import by.vladislav.hotelreservation.entity.dto.HotelDto;
import by.vladislav.hotelreservation.entity.dto.HotelShortDto;
import by.vladislav.hotelreservation.exception.EntityAlreadyExistsException;
import by.vladislav.hotelreservation.exception.EntityNotFoundException;
import by.vladislav.hotelreservation.mapper.HotelMapper;
import by.vladislav.hotelreservation.mapper.RoomMapper;
import by.vladislav.hotelreservation.repository.ConvenienceRepository;
import by.vladislav.hotelreservation.repository.HotelRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;

@CacheConfig(cacheNames = "hotels")
@AllArgsConstructor
@Service
public class HotelService {

  private final HotelRepository hotelRepository;
  private final HotelMapper hotelMapper;
  private final RoomMapper roomMapper;
  private final ConvenienceRepository convenienceRepository;
  @PersistenceContext
  private EntityManager entityManager;

  @Transactional
  public HotelDto create(HotelDto dto) {
    if (hotelRepository.existsByName(dto.name())) {
      throw new EntityAlreadyExistsException(EntityType.HOTEL, "name", dto.name());
    }

    Set<Convenience> conveniences = getConveniences(dto.conveniences());

    Hotel hotel = hotelMapper.toEntity(dto);
    hotel.setConveniences(conveniences);

    Hotel savedHotel = hotelRepository.save(hotel);

    if (dto.rooms() != null) {
      List<Room> rooms = dto.rooms().stream()
          .map(roomMapper::toEntity)
          .toList();

      rooms.forEach(room -> room.setHotel(savedHotel));

      savedHotel.setRooms(rooms);
    }

    return hotelMapper.toDTO(savedHotel);
  }

  @Transactional
  public List<HotelDto> saveBulk(List<HotelDto> hotelRequest) {
    validateBulkHotelNames(hotelRequest);

    Set<String> allConvNames = hotelRequest.stream()
        .flatMap(h -> h.conveniences().stream())
        .map(h -> h.name())
        .collect(Collectors.toSet());

    Map<String, Convenience> convMap = convenienceRepository.findAllByNameIn(allConvNames)
        .stream().collect(Collectors.toMap(Convenience::getName, c -> c));

    List<HotelDto> resultDtos = new ArrayList<>(hotelRequest.size());
    int batchSize = 50;

    for (int i = 0; i < hotelRequest.size(); i++) {
      HotelDto dto = hotelRequest.get(i);

      Hotel hotel = hotelMapper.toEntity(dto);

      Set<Convenience> conveniences = dto.conveniences().stream()
          .map(c -> convMap.get(c.name()))
          .filter(Objects::nonNull)
          .collect(Collectors.toSet());
      hotel.setConveniences(conveniences);

      if (dto.rooms() != null) {
        List<Room> rooms = dto.rooms().stream()
            .map(room -> {
              Room roomEntity = roomMapper.toEntity(room);
              roomEntity.setHotel(hotel);
              return roomEntity;
            })
            .toList();
        hotel.setRooms(rooms);
      }

      hotelRepository.save(hotel);

      resultDtos.add(hotelMapper.toDTO(hotel));

      if (i > 0 && i % batchSize == 0) {
        entityManager.flush();
        entityManager.clear();
      }
    }

    return resultDtos;
  }

  @Cacheable(key = "#id")
  @Transactional(readOnly = true)
  public HotelDto findById(long id) {
    Hotel hotel = hotelRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(EntityType.HOTEL, "id", id));
    return hotelMapper.toDTO(hotel);
  }

  @Transactional(readOnly = true)
  public Page<HotelShortDto> findAllWithoutRooms(int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<Hotel> hotels = hotelRepository.findAll(pageable);

    return hotels.map(hotelMapper::toShortDTO);
  }

  @Transactional(readOnly = true)
  public Page<HotelDto> findAll(int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<Hotel> hotels = hotelRepository.findAll(pageable);

    return hotels.map(hotelMapper::toDTO);
  }

  @Transactional(readOnly = true)
  public Page<HotelDto> findByCountryAndGreaterThanMinRating(String country, BigDecimal minRating, int page, int size) {

    Pageable pageable = PageRequest.of(page, size);

    Page<Hotel> hotels = hotelRepository.findBycountryAndMinRating(country, minRating, pageable);
    return hotels.map(hotelMapper::toDTO);
  }

  @CachePut(key = "#id")
  @Transactional
  public HotelDto update(Long id, HotelDto dto) {
    Hotel hotel = hotelRepository.findById(id).orElseThrow(
        () -> new EntityNotFoundException(EntityType.HOTEL, "id", dto.id()));

    if (hotelRepository.existsByName(dto.name())) {
      throw new EntityAlreadyExistsException(EntityType.HOTEL, "name", dto.name());
    }

    hotel.setName(dto.name());
    hotel.setRating(dto.rating());

    hotel.getAddress().setCountry(dto.address().country());
    hotel.getAddress().setCity(dto.address().city());
    hotel.getAddress().setStreet(dto.address().street());

    Set<Convenience> conveniences = getConveniences(dto.conveniences());
    hotel.setConveniences(conveniences);

    hotel.getRooms().clear();

    if (dto.rooms() != null) {
      List<Room> rooms = dto.rooms().stream()
          .map(roomDto -> {
            Room room = roomMapper.toEntity(roomDto);
            room.setHotel(hotel);
            return room;
          })
          .toList();

      hotel.getRooms().addAll(rooms);
    }

    return hotelMapper.toDTO(hotel);
  }

  @CacheEvict(key = "#id")
  @Transactional
  public void deleteById(long id) {
    hotelRepository.deleteById(id);
  }

  private void validateBulkHotelNames(List<HotelDto> hotelRequest) {
    Set<String> requestNames = new HashSet<>();
    for (HotelDto hotelDto : hotelRequest) {
      if (!requestNames.add(hotelDto.name())) {
        throw new EntityAlreadyExistsException("Hotel", "name", hotelDto.name());
      }
    }

    Set<String> names = hotelRequest.stream()
        .map(HotelDto::name)
        .collect(Collectors.toSet());

    List<Hotel> existingHotels = hotelRepository.findAllByNameIn(names);
    if (!existingHotels.isEmpty()) {
      String duplicateName = existingHotels.get(0).getName();
      throw new EntityAlreadyExistsException("Hotel", "name", duplicateName);
    }
  }

  private Set<Convenience> getConveniences(Collection<ConvenienceDto> dtos) {
    Set<String> convenienceStrings = new HashSet<>();
    for (ConvenienceDto dto : dtos) {
      convenienceStrings.add(dto.name());
    }

    return new HashSet<>(convenienceRepository.findByNameIn(convenienceStrings));
  }
}
