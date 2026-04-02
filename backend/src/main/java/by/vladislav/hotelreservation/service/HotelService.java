package by.vladislav.hotelreservation.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import by.vladislav.hotelreservation.entity.Convenience;
import by.vladislav.hotelreservation.entity.Hotel;
import by.vladislav.hotelreservation.entity.HotelSearchKey;
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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Service
public class HotelService {

  private final HotelRepository hotelRepository;
  private final HotelMapper hotelMapper;
  private final RoomMapper roomMapper;
  private final ConvenienceRepository convenienceRepository;
  @PersistenceContext
  private EntityManager entityManager;

  private final Map<HotelSearchKey, Page<HotelDto>> searchCache = new ConcurrentHashMap<>();

  @Transactional
  public HotelDto create(HotelDto dto) {
    ensureHotelNameIsUnique(dto.name(), null);

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

    searchCache.clear();

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

    searchCache.clear();
    return resultDtos;
  }

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
    HotelSearchKey key = new HotelSearchKey(country, minRating, page, size);

    return searchCache.computeIfAbsent(key, k -> {
      Pageable pageable = PageRequest.of(page, size);

      Page<Hotel> hotels = hotelRepository.findBycountryAndMinRating(country, minRating, pageable);
      return hotels.map(hotelMapper::toDTO);
    });
  }

  @Transactional
  public HotelDto update(Long id, HotelDto dto) {
    Long hotelId = id == null ? dto.id() : id;
    Hotel hotel;
    if (id == null) {
      hotel = hotelRepository.findById(dto.id()).orElseThrow(
          () -> new EntityNotFoundException(EntityType.HOTEL, "id", dto.id()));
    } else {
      hotel = hotelRepository.findById(id).orElseThrow(
          () -> new EntityNotFoundException(EntityType.HOTEL, "id", dto.id()));
    }
    ensureHotelNameIsUnique(dto.name(), hotelId);

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

    searchCache.clear();

    return hotelMapper.toDTO(hotel);
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

  private void ensureHotelNameIsUnique(String name, Long currentHotelId) {
    hotelRepository.findByName(name).ifPresent(existingHotel -> {
      if (currentHotelId == null || !Objects.equals(existingHotel.getId(), currentHotelId)) {
        throw new EntityAlreadyExistsException("Hotel", "name", name);
      }
    });
  }

  @Transactional
  public void deleteById(long id) {
    hotelRepository.deleteById(id);
    searchCache.clear();
  }

  private Set<Convenience> getConveniences(Collection<ConvenienceDto> dtos) {
    Set<String> convenienceStrings = new HashSet<>();
    for (ConvenienceDto dto : dtos) {
      convenienceStrings.add(dto.name());
    }

    return new HashSet<>(convenienceRepository.findByNameIn(convenienceStrings));
  }

  public List<HotelDto> saveBulkNonTransactional(List<HotelDto> hotelRequest, boolean isException) {
    List<HotelDto> hotelDTOs = new ArrayList<>(hotelRequest.size());

    int i = 0;

    for (HotelDto dto : hotelRequest) {
      i++;
      Set<Convenience> conveniences = getConveniences(dto.conveniences());

      Hotel hotel = hotelMapper.toEntity(dto);
      hotel.setConveniences(conveniences);

      Hotel savedHotel = hotelRepository.save(hotel);

      if (i >= hotelRequest.size() / 2 && isException) {
        throw new IllegalArgumentException("Error");
      }

      if (dto.rooms() != null) {
        List<Room> rooms = dto.rooms().stream()
            .map(roomMapper::toEntity)
            .toList();

        rooms.forEach(room -> room.setHotel(savedHotel));

        savedHotel.setRooms(rooms);
      }
      hotelDTOs.add(dto);
    }

    searchCache.clear();

    return hotelDTOs;
  }
}
