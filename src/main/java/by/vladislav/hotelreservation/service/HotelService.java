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
import by.vladislav.hotelreservation.entity.dto.ConvenienceDTO;
import by.vladislav.hotelreservation.entity.dto.HotelDTO;
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

  private final Map<HotelSearchKey, Page<HotelDTO>> searchCache = new ConcurrentHashMap<>();

  @Transactional
  public HotelDTO create(HotelDTO dto) {
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
  public List<HotelDTO> createList(List<HotelDTO> hotelRequest) {
    List<HotelDTO> hotelDTOs = new ArrayList<>(hotelRequest.size());
    for (HotelDTO hotelDTO : hotelRequest) {
      hotelDTOs.add(create(hotelDTO));
    }

    return hotelDTOs;
  }

  @Transactional
  public HotelDTO findById(long id) {
    Hotel hotel = hotelRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(EntityType.HOTEL, "id", id));
    return hotelMapper.toDTO(hotel);
  }

  public Page<HotelDTO> findAll(int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<Hotel> hotels = hotelRepository.findAll(pageable);

    return hotels.map(hotelMapper::toDTO);
  }

  public Page<HotelDTO> findByCountryAndGreaterThanMinRating(String country, BigDecimal minRating, int page, int size) {
    HotelSearchKey key = new HotelSearchKey(country, minRating, page, size);

    return searchCache.computeIfAbsent(key, k -> {
      Pageable pageable = PageRequest.of(page, size);

      Page<Hotel> hotels = hotelRepository.findBycountryAndMinRating(country, minRating, pageable);
      return hotels.map(hotelMapper::toDTO);
    });
  }

  @Transactional
  public HotelDTO update(Long id, HotelDTO dto) {
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

  private Set<Convenience> getConveniences(Collection<ConvenienceDTO> dtos) {
    Set<String> convenienceStrings = new HashSet<>();
    for (ConvenienceDTO dto : dtos) {
      convenienceStrings.add(dto.name());
    }

    return new HashSet<>(convenienceRepository.findByNameIn(convenienceStrings));
  }
}
