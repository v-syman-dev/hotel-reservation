package by.vladislav.hotelreservation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import by.vladislav.hotelreservation.entity.Convenience;
import by.vladislav.hotelreservation.entity.dto.ConvenienceDTO;
import by.vladislav.hotelreservation.exception.EntityNotFoundException;
import by.vladislav.hotelreservation.mapper.ConvenienceMapper;
import by.vladislav.hotelreservation.repository.ConvenienceRepository;

@ExtendWith(MockitoExtension.class)
public class ConvenienceServiceTest {
  @Mock
  private ConvenienceRepository convenienceRepository;

  @Mock
  private ConvenienceMapper convenienceMapper;

  @InjectMocks
  private ConvenienceService convenienceService;

  @Test
  void findByIdSuccess() {
    long id = 1;
    Convenience entity = new Convenience();
    ConvenienceDTO dto = new ConvenienceDTO(id, "WIFI");

    when(convenienceRepository.findById(id)).thenReturn(Optional.of(entity));
    when(convenienceMapper.toDTO(entity)).thenReturn(dto);

    ConvenienceDTO result = convenienceService.findById(id);

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
}
