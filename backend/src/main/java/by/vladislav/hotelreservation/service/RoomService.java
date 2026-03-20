package by.vladislav.hotelreservation.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import by.vladislav.hotelreservation.entity.Hotel;
import by.vladislav.hotelreservation.entity.Room;
import by.vladislav.hotelreservation.entity.constant.EntityType;
import by.vladislav.hotelreservation.entity.dto.RoomDto;
import by.vladislav.hotelreservation.exception.EntityNotFoundException;
import by.vladislav.hotelreservation.mapper.RoomMapper;
import by.vladislav.hotelreservation.repository.HotelRepository;
import by.vladislav.hotelreservation.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class RoomService {
  private final RoomRepository roomRepository;
  private final RoomMapper roomMapper;
  private final HotelRepository hotelRepository;

  @Transactional
  public RoomDto create(Long hotelId, RoomDto roomRequest) {
    Room newRoom = roomMapper.toEntity(roomRequest);

    Hotel hotel = hotelRepository.findById(hotelId)
        .orElseThrow(() -> new EntityNotFoundException(EntityType.HOTEL, "id", hotelId));

    newRoom.setHotel(hotel);

    Room createdRoom = roomRepository.save(newRoom);

    return roomMapper.toDTO(createdRoom);
  }

  @Transactional
  public List<RoomDto> saveBulk(Long hotelId, List<RoomDto> roomRequest) {
    Hotel hotel = hotelRepository.findById(hotelId)
        .orElseThrow(() -> new EntityNotFoundException(EntityType.HOTEL, "id", hotelId));

    List<RoomDto> result = new ArrayList<>(roomRequest.size());

    for (RoomDto newRoomDto : roomRequest) {
      Room newRoom = roomMapper.toEntity(newRoomDto);

      newRoom.setHotel(hotel);

      Room createdRoom = roomRepository.save(newRoom);

      result.add(roomMapper.toDTO(createdRoom));
    }

    return result;
  }

  public List<RoomDto> findAllByHotel(Long hotelId) {
    List<Room> rooms = roomRepository.findByHotelId(hotelId);
    List<RoomDto> roomDTOs = new ArrayList<>(rooms.size());
    for (Room room : rooms) {
      RoomDto roomDTO = roomMapper.toDTO(room);
      roomDTOs.add(roomDTO);
    }

    return roomDTOs;
  }

  public List<RoomDto> findAll() {
    List<Room> rooms = roomRepository.findAll();
    List<RoomDto> roomDTOs = new ArrayList<>(rooms.size());
    for (Room room : rooms) {
      RoomDto roomDTO = roomMapper.toDTO(room);
      roomDTOs.add(roomDTO);
    }

    return roomDTOs;
  }

  public RoomDto findById(Long id) {
    Room room = roomRepository.findById(id).orElseThrow(

    );

    return roomMapper.toDTO(room);
  }

  @Transactional
  public RoomDto update(RoomDto roomDTO) {
    Room room = roomRepository.findById(roomDTO.id()).orElseThrow();

    room.setNumber(roomDTO.number());
    room.setType(roomDTO.type());
    room.setPricePerNight(roomDTO.pricePerNight());

    return roomMapper.toDTO(room);
  }

  @Transactional
  public void deleteById(Long id) {
    Room room = roomRepository.findById(id).orElseThrow();
    roomRepository.delete(room);
  }

  public List<RoomDto> saveBulkNonTransactional(Long hotelId, List<RoomDto> roomRequest, boolean isException) {
    Hotel hotel = hotelRepository.findById(hotelId)
        .orElseThrow(() -> new EntityNotFoundException(EntityType.HOTEL, "id", hotelId));

    List<RoomDto> result = new ArrayList<>(roomRequest.size());

    int i = 0;
    for (RoomDto newRoomDto : roomRequest) {
      Room newRoom = roomMapper.toEntity(newRoomDto);

      newRoom.setHotel(hotel);

      Room createdRoom = roomRepository.save(newRoom);

      if (i >= roomRequest.size() / 2 && isException) {
        throw new IllegalArgumentException("Error");
      }

      result.add(roomMapper.toDTO(createdRoom));
    }

    return result;
  }
}
