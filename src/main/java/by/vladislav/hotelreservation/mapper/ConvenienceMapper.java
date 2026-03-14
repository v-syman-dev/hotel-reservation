package by.vladislav.hotelreservation.mapper;

import org.springframework.stereotype.Component;

import by.vladislav.hotelreservation.entity.Convenience;
import by.vladislav.hotelreservation.entity.dto.ConvenienceDto;

@Component
public class ConvenienceMapper {
  public Convenience toEntity(ConvenienceDto convenienceDTO) {
    return Convenience.builder()
        .name(convenienceDTO.name())
        .build();
  }

  public ConvenienceDto toDTO(Convenience entity) {
    return new ConvenienceDto(
        entity.getId(),
        entity.getName());
  }
}
