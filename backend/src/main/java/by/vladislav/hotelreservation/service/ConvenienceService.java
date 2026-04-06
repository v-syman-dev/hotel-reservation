package by.vladislav.hotelreservation.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import by.vladislav.hotelreservation.entity.Convenience;
import by.vladislav.hotelreservation.entity.constant.EntityType;
import by.vladislav.hotelreservation.entity.dto.ConvenienceDto;
import by.vladislav.hotelreservation.exception.EntityAlreadyExistsException;
import by.vladislav.hotelreservation.exception.EntityNotFoundException;
import by.vladislav.hotelreservation.mapper.ConvenienceMapper;
import by.vladislav.hotelreservation.repository.ConvenienceRepository;
import lombok.AllArgsConstructor;

@CacheConfig(cacheNames = "conveniences")
@AllArgsConstructor
@Service
public class ConvenienceService {

  private final ConvenienceRepository convenienceRepository;
  private final ConvenienceMapper convenienceMapper;

  @CacheEvict(key = "'all'")
  @Transactional
  public ConvenienceDto create(ConvenienceDto convenienceDTO) {
    Convenience entity = convenienceMapper.toEntity(convenienceDTO);
    if (convenienceRepository.existsByName(convenienceDTO.name())) {
      throw new EntityAlreadyExistsException(convenienceDTO.name());
    }
    entity = convenienceRepository.save(entity);
    return convenienceMapper.toDTO(entity);
  }

  @CacheEvict(key = "'all'")
  @Transactional
  public List<ConvenienceDto> saveBulk(List<ConvenienceDto> convenienceRequest) {
    Set<String> names = new HashSet<>();
    for (ConvenienceDto convenienceDto : convenienceRequest) {
      if (!names.add(convenienceDto.name()) || convenienceRepository.existsByName(convenienceDto.name())) {
        throw new EntityAlreadyExistsException(EntityType.CONVENIENCE, "name", convenienceDto.name());
      }
    }

    List<ConvenienceDto> result = new ArrayList<>(convenienceRequest.size());

    for (ConvenienceDto newConvenienceDto : convenienceRequest) {
      Convenience newConvenience = convenienceMapper.toEntity(newConvenienceDto);
      newConvenience = convenienceRepository.save(newConvenience);

      result.add(convenienceMapper.toDTO(newConvenience));
    }

    return result;
  }

  @Cacheable(key = "#id")
  @Transactional(readOnly = true)
  public ConvenienceDto findById(long id) {
    Convenience entity = convenienceRepository.findById(id)
        .orElseThrow(
            () -> new EntityNotFoundException(EntityType.CONVENIENCE, "id", id));
    return convenienceMapper.toDTO(entity);
  }

  @Cacheable(key = "'all'")
  @Transactional(readOnly = true)
  public List<ConvenienceDto> findAll() {
    List<Convenience> entityList = convenienceRepository.findAll();

    return entityList.stream()
        .map(convenienceMapper::toDTO)
        .toList();
  }

  @Caching(evict = {
      @CacheEvict(key = "#convenienceDTO.id()"),
      @CacheEvict(key = "'all'")
  })
  @Transactional
  public ConvenienceDto update(ConvenienceDto convenienceDTO) {
    Convenience convenienceEntity = convenienceRepository.findById(convenienceDTO.id())
        .orElseThrow(
            () -> new EntityNotFoundException(EntityType.CONVENIENCE, "id", convenienceDTO.id()));

    if (convenienceRepository.existsByName(convenienceDTO.name())) {
      throw new EntityAlreadyExistsException(EntityType.CONVENIENCE, "name", convenienceDTO.name());
    }

    convenienceEntity.setName(convenienceDTO.name());

    Convenience newConvenience = convenienceRepository.save(convenienceEntity);

    return convenienceMapper.toDTO(newConvenience);
  }

  @Caching(evict = {
      @CacheEvict(key = "#id"),
      @CacheEvict(key = "'all'")
  })
  @Transactional
  public void removeById(long id) {
    convenienceRepository.deleteById(id);
  }
}
