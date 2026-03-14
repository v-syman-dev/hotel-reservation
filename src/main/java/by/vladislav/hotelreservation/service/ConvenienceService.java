package by.vladislav.hotelreservation.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import by.vladislav.hotelreservation.entity.Convenience;
import by.vladislav.hotelreservation.entity.constant.EntityType;
import by.vladislav.hotelreservation.entity.dto.ConvenienceDTO;
import by.vladislav.hotelreservation.exception.EntityNotFoundException;
import by.vladislav.hotelreservation.mapper.ConvenienceMapper;
import by.vladislav.hotelreservation.repository.ConvenienceRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ConvenienceService {

  private final ConvenienceRepository convenienceRepository;
  private final ConvenienceMapper convenienceMapper;

  @Transactional
  public ConvenienceDTO create(ConvenienceDTO convenienceDTO) {
    Convenience entity = convenienceMapper.toEntity(convenienceDTO);
    entity = convenienceRepository.save(entity);
    return convenienceMapper.toDTO(entity);
  }

  @Transactional
  public List<ConvenienceDTO> saveBulk(List<ConvenienceDTO> convenienceRequest) {
    List<ConvenienceDTO> result = new ArrayList<>(convenienceRequest.size());

    for (ConvenienceDTO newConvenienceDto : convenienceRequest) {
      Convenience newConvenience = convenienceMapper.toEntity(newConvenienceDto);
      newConvenience = convenienceRepository.save(newConvenience);

      result.add(convenienceMapper.toDTO(newConvenience));
    }

    return result;
  }

  public ConvenienceDTO findById(long id) {
    Convenience entity = convenienceRepository.findById(id)
        .orElseThrow(
            () -> new EntityNotFoundException(EntityType.CONVENIENCE, "id", id));
    return convenienceMapper.toDTO(entity);
  }

  public List<ConvenienceDTO> findAll() {
    List<Convenience> entityList = convenienceRepository.findAll();

    return entityList.stream()
        .map(entity -> convenienceMapper.toDTO(entity))
        .toList();
  }

  @Transactional
  public ConvenienceDTO update(ConvenienceDTO convenienceDTO) {
    Convenience convenienceEntity = convenienceRepository.findById(convenienceDTO.id())
        .orElseThrow(
            () -> new EntityNotFoundException(EntityType.CONVENIENCE, "id", convenienceDTO.id()));

    convenienceEntity.setName(convenienceDTO.name());

    Convenience newConvenience = convenienceRepository.save(convenienceEntity);

    return convenienceMapper.toDTO(newConvenience);
  }

  @Transactional
  public void removeById(long id) {
    convenienceRepository.deleteById(id);
  }
}
