package by.vladislav.hotelreservation.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import by.vladislav.hotelreservation.entity.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
  Optional<Booking> findByIdAndRoomId(Long id, Long roomId);

  List<Booking> findByRoomId(Long roomId);

  boolean existsByRoomIdAndCheckInDateLessThanAndCheckOutDateGreaterThan(
      Long roomId,
      LocalDate checkOutDate,
      LocalDate checkInDate);
}
