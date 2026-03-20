package by.vladislav.hotelreservation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import by.vladislav.hotelreservation.entity.Hotel;
import by.vladislav.hotelreservation.entity.dto.HotelDto;
import by.vladislav.hotelreservation.mapper.HotelMapper;
import by.vladislav.hotelreservation.repository.HotelRepository;

@ExtendWith(MockitoExtension.class)
class HotelServiceTest {
  @Mock
  private HotelRepository hotelRepository;
  @Mock
  private HotelMapper hotelMapper;

  @InjectMocks
  private HotelService hotelService;

  @Test
  void findByCountryAndRatingShouldUseCacheWithPagination() {
    String country = "Belarus";
    BigDecimal rating = BigDecimal.valueOf(5.0);
    int page = 0;
    int size = 10;
    Pageable pageable = PageRequest.of(page, size);

    Hotel hotel = new Hotel();
    HotelDto dto = new HotelDto(1L, "Luxury Hotel", null, rating, null, null);

    Page<Hotel> hotelPage = new PageImpl<>(List.of(hotel), pageable, 1);

    when(hotelRepository.findBycountryAndMinRating(country, rating, pageable))
        .thenReturn(hotelPage);
    when(hotelMapper.toDTO(hotel)).thenReturn(dto);

    Page<HotelDto> result1 = hotelService.findByCountryAndGreaterThanMinRating(country, rating, page, size);

    hotelService.findByCountryAndGreaterThanMinRating(country, rating, page, size);

    assertNotNull(result1);
    assertEquals(1, result1.getTotalElements());
    assertEquals("Luxury Hotel", result1.getContent().get(0).name());

    verify(hotelRepository, times(1)).findBycountryAndMinRating(country, rating, pageable);
  }

  @Test
  void deleteById() {
    long id = 1L;

    hotelService.deleteById(id);

    verify(hotelRepository).deleteById(id);
  }
}
