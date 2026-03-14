package by.vladislav.hotelreservation.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import by.vladislav.hotelreservation.entity.Convenience;
import by.vladislav.hotelreservation.entity.Hotel;
import by.vladislav.hotelreservation.entity.HotelSearchKey;
import by.vladislav.hotelreservation.entity.Room;
import by.vladislav.hotelreservation.entity.constant.EntityType;
import by.vladislav.hotelreservation.entity.dto.ConvenienceDto;
import by.vladislav.hotelreservation.entity.dto.HotelDto;
import by.vladislav.hotelreservation.exception.EntityNotFoundException;
import by.vladislav.hotelreservation.mapper.HotelMapper;
import by.vladislav.hotelreservation.mapper.RoomMapper;
import by.vladislav.hotelreservation.repository.ConvenienceRepository;
import by.vladislav.hotelreservation.repository.HotelRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class HotelService {

  private final HotelRepository hotelRepository;
  private final HotelMapper hotelMapper;
  private final RoomMapper roomMapper;
  private final ConvenienceRepository convenienceRepository;

  private final Map<HotelSearchKey, Page<HotelDto>> searchCache = new ConcurrentHashMap<>();

  @Transactional
  public HotelDto create(HotelDto dto) {
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
    List<HotelDto> hotelDTOs = new ArrayList<>(hotelRequest.size());

    for (HotelDto dto : hotelRequest) {
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
      hotelDTOs.add(dto);
    }

    searchCache.clear();

    return hotelDTOs;
  }

  @Transactional
  public HotelDto findById(long id) {
    Hotel hotel = hotelRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(EntityType.HOTEL, "id", id));
    return hotelMapper.toDTO(hotel);
  }

  public Page<HotelDto> findAll(int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<Hotel> hotels = hotelRepository.findAll(pageable);

    return hotels.map(hotelMapper::toDTO);
  }

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
    Hotel hotel;
    if (id == null) {
      hotel = hotelRepository.findById(dto.id()).orElseThrow(
          () -> new EntityNotFoundException(EntityType.HOTEL, "id", dto.id()));
    } else {
      hotel = hotelRepository.findById(id).orElseThrow(
          () -> new EntityNotFoundException(EntityType.HOTEL, "id", dto.id()));
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

    searchCache.clear();

    return hotelMapper.toDTO(hotel);
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
