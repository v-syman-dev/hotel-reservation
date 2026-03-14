package by.vladislav.hotelreservation.mapper;

import org.springframework.stereotype.Component;

import by.vladislav.hotelreservation.entity.Convenience;
import by.vladislav.hotelreservation.entity.dto.ConvenienceDtox;

@Component
public class ConvenienceMapper {
  public Convenience toEntity(ConvenienceDtox convenienceDTO) {
    return Convenience.builder()
        .name(convenienceDTO.name())
        .build();
  }

  public ConvenienceDtox toDTO(Convenience entity) {
    return new ConvenienceDtox(
        entity.getId(),
        entity.getName());
  }
}
