package by.vladislav.hotelreservation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import by.vladislav.hotelreservation.entity.Convenience;
import by.vladislav.hotelreservation.entity.dto.ConvenienceDto;
import by.vladislav.hotelreservation.exception.EntityAlreadyExistsException;
import by.vladislav.hotelreservation.exception.EntityNotFoundException;
import by.vladislav.hotelreservation.mapper.ConvenienceMapper;
import by.vladislav.hotelreservation.repository.ConvenienceRepository;

@ExtendWith(MockitoExtension.class)
class ConvenienceServiceTest {
  @Mock
  private ConvenienceRepository convenienceRepository;

  @Mock
  private ConvenienceMapper convenienceMapper;

  @InjectMocks
  private ConvenienceService convenienceService;

  @Test
  void createShouldSaveAndMapEntity() {
    ConvenienceDto request = new ConvenienceDto(null, "WIFI");
    Convenience entity = Convenience.builder().name("WIFI").build();
    Convenience saved = Convenience.builder().id(1L).name("WIFI").build();
    ConvenienceDto expected = new ConvenienceDto(1L, "WIFI");

    when(convenienceRepository.existsByName("WIFI")).thenReturn(false);
    when(convenienceMapper.toEntity(request)).thenReturn(entity);
    when(convenienceRepository.save(entity)).thenReturn(saved);
    when(convenienceMapper.toDTO(saved)).thenReturn(expected);

    ConvenienceDto result = convenienceService.create(request);

    assertEquals(expected, result);
  }

  @Test
  void saveBulkShouldPersistEachConvenience() {
    ConvenienceDto first = new ConvenienceDto(null, "WIFI");
    ConvenienceDto second = new ConvenienceDto(null, "SPA");
    Convenience firstEntity = Convenience.builder().name("WIFI").build();
    Convenience secondEntity = Convenience.builder().name("SPA").build();
    Convenience savedFirst = Convenience.builder().id(1L).name("WIFI").build();
    Convenience savedSecond = Convenience.builder().id(2L).name("SPA").build();
    ConvenienceDto firstDto = new ConvenienceDto(1L, "WIFI");
    ConvenienceDto secondDto = new ConvenienceDto(2L, "SPA");

    when(convenienceRepository.existsByName("WIFI")).thenReturn(false);
    when(convenienceRepository.existsByName("SPA")).thenReturn(false);
    when(convenienceMapper.toEntity(first)).thenReturn(firstEntity);
    when(convenienceMapper.toEntity(second)).thenReturn(secondEntity);
    when(convenienceRepository.save(firstEntity)).thenReturn(savedFirst);
    when(convenienceRepository.save(secondEntity)).thenReturn(savedSecond);
    when(convenienceMapper.toDTO(savedFirst)).thenReturn(firstDto);
    when(convenienceMapper.toDTO(savedSecond)).thenReturn(secondDto);

    List<ConvenienceDto> result = convenienceService.saveBulk(List.of(first, second));

    assertIterableEquals(List.of(firstDto, secondDto), result);
  }

  @Test
  void findByIdSuccess() {
    long id = 1;
    Convenience entity = new Convenience();
    ConvenienceDto dto = new ConvenienceDto(id, "WIFI");

    when(convenienceRepository.findById(id)).thenReturn(Optional.of(entity));
    when(convenienceMapper.toDTO(entity)).thenReturn(dto);

    ConvenienceDto result = convenienceService.findById(id);

    assertNotNull(result);
    assertEquals("WIFI", result.name());
    verify(convenienceRepository).findById(id);
  }

  @Test
  void findByIdNotFoundThrowsException() {
    long id = 99;
    when(convenienceRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> convenienceService.findById(id));
  }

  @Test
  void findAllShouldMapAllEntities() {
    Convenience first = new Convenience();
    Convenience second = new Convenience();
    ConvenienceDto firstDto = new ConvenienceDto(1L, "WIFI");
    ConvenienceDto secondDto = new ConvenienceDto(2L, "SPA");

    when(convenienceRepository.findAll()).thenReturn(List.of(first, second));
    when(convenienceMapper.toDTO(first)).thenReturn(firstDto);
    when(convenienceMapper.toDTO(second)).thenReturn(secondDto);

    List<ConvenienceDto> result = convenienceService.findAll();

    assertIterableEquals(List.of(firstDto, secondDto), result);
  }

  @Test
  void updateShouldModifyEntityAndReturnMappedDto() {
    ConvenienceDto request = new ConvenienceDto(5L, "PARKING");
    Convenience entity = Convenience.builder().id(5L).name("OLD").build();
    ConvenienceDto expected = new ConvenienceDto(5L, "PARKING");

    when(convenienceRepository.findById(5L)).thenReturn(Optional.of(entity));
    when(convenienceRepository.findByName("PARKING")).thenReturn(Optional.empty());
    when(convenienceRepository.save(entity)).thenReturn(entity);
    when(convenienceMapper.toDTO(entity)).thenReturn(expected);

    ConvenienceDto result = convenienceService.update(request);

    assertEquals(expected, result);
    assertEquals("PARKING", entity.getName());
  }

  @Test
  void updateShouldThrowWhenEntityMissing() {
    ConvenienceDto request = new ConvenienceDto(5L, "PARKING");

    when(convenienceRepository.findById(5L)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> convenienceService.update(request));
  }

  @Test
  void createShouldThrowWhenNameAlreadyExists() {
    ConvenienceDto request = new ConvenienceDto(null, "WIFI");

    when(convenienceRepository.existsByName("WIFI")).thenReturn(true);

    assertThrows(EntityAlreadyExistsException.class, () -> convenienceService.create(request));
  }

  @Test
  void removeByIdShouldDeleteById() {
    convenienceService.removeById(3L);

    verify(convenienceRepository).deleteById(3L);
  }
}
