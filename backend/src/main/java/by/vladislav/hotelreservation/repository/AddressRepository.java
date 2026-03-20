package by.vladislav.hotelreservation.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import by.vladislav.hotelreservation.entity.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
  Optional<Address> findByHotelId(Long hotelId);
}
