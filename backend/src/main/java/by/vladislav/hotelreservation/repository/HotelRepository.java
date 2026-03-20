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

  @EntityGraph(attributePaths = {"address"})
  Page<Hotel> findAll(Pageable pageable);

  @Query("SELECT h FROM Hotel h " +
       "JOIN FETCH h.address a " +
       "WHERE a.country = :country AND h.rating >= :minRating")
  Page<Hotel> findBycountryAndMinRating(String country, BigDecimal minRating, Pageable pageable);

  @Query(value = "SELECT h.* FROM hotels h " +
      "JOIN addresses a ON h.address_id = a.id " +
      "WHERE a.country = :country AND h.rating >= :minRating", nativeQuery = true)
  Page<Hotel> findBycountryAndMinRatingNative(String country, BigDecimal minRating, Pageable pageable);
}
