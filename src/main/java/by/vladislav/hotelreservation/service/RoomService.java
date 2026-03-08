package by.vladislav.hotelreservation.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import by.vladislav.hotelreservation.entity.Hotel;
import by.vladislav.hotelreservation.entity.Room;
import by.vladislav.hotelreservation.entity.constant.EntityType;
import by.vladislav.hotelreservation.entity.dto.RoomDTO;
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

  public RoomDTO create(Long hotelId, RoomDTO hotelRequest) {
    Room newRoom = roomMapper.toEntity(hotelRequest);

    Hotel hotel = hotelRepository.findById(hotelId)
        .orElseThrow(() -> new EntityNotFoundException(EntityType.HOTEL, "id", hotelId));

    newRoom.setHotel(hotel);

    Room createdRoom = roomRepository.save(newRoom);

    return roomMapper.toDTO(createdRoom);
  }

  public List<RoomDTO> findAll(Long hotelId) {
    List<Room> rooms = roomRepository.findByHotelId(hotelId);
    List<RoomDTO> roomDTOs = new ArrayList<>(rooms.size());
    for (Room room : rooms) {
      RoomDTO roomDTO = roomMapper.toDTO(room);
      roomDTOs.add(roomDTO);
    }

    return roomDTOs;
  }

  public RoomDTO findById(Long hotelId, Long id) {
    Room room = findByIdAndHotelId(id, hotelId);

    return roomMapper.toDTO(room);
  }

  @Transactional
  public RoomDTO update(Long hotelId, RoomDTO roomDTO) {
    Room room = findByIdAndHotelId(roomDTO.id(), hotelId);

    room.setNumber(roomDTO.number());
    room.setType(roomDTO.type());
    room.setPricePerNight(roomDTO.pricePerNight());

    return roomMapper.toDTO(room);
  }

  public void deleteById(Long hotelId, Long id) {
    Room room = findByIdAndHotelId(id, hotelId);
    roomRepository.delete(room);
  }
}
