package by.vladislav.hotelreservation.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import by.vladislav.hotelreservation.entity.Hotel;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
  Optional<Hotel> findByName(String name);

  @EntityGraph(attributePaths = { "address" })
  Page<Hotel> findAll(Pageable pageable);

  @Query("SELECT h FROM Hotel h " +
      "JOIN FETCH h.address a " +
      "WHERE a.country = :country AND h.rating >= :minRating")
  Page<Hotel> findBycountryAndMinRating(String country, BigDecimal minRating, Pageable pageable);
}
