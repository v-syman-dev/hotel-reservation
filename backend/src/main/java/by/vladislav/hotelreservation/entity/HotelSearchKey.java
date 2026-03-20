package by.vladislav.hotelreservation.entity;

import java.math.BigDecimal;

public record HotelSearchKey(
    String country,
    BigDecimal minRating,
    int page,
    int size) {
}
