package by.vladislav.hotelreservation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import by.vladislav.hotelreservation.entity.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
  List<Room> findByHotelId(Long id);

  Optional<Room> findByIdAndHotelId(Long roomId, Long hotelId);
}
