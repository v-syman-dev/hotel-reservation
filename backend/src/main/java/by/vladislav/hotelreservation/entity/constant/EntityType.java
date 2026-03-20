package by.vladislav.hotelreservation.entity.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EntityType {
  HOTEL("Hotel"),
  ROOM("Room"),
  BOOKING("Booking"),
  CONVENIENCE("Convenience"),
  ADDRESS("Address");

  private final String name;
}
