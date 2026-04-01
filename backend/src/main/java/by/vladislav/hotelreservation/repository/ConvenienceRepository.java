package by.vladislav.hotelreservation.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import by.vladislav.hotelreservation.entity.Convenience;

@Repository
public interface ConvenienceRepository extends JpaRepository<Convenience, Long> {
  Optional<Convenience> findByName(String name);

  List<Convenience> findByNameIn(Set<String> conveniences);

  boolean existsByName(String name);

  List<Convenience> findAllByNameIn(Set<String> allConvNames);
}
