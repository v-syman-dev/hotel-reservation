package by.vladislav.hotelreservation.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "bookings", indexes = {
    @Index(name = "idx_booking_room_id", columnList = "room_id")
})
public class Booking {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "booking_seq")
  @SequenceGenerator(name = "booking_seq", sequenceName = "booking_sequence", initialValue = 1, allocationSize = 50)
  private long id;

  @Column(nullable = false)
  private String guestName;

  @Column(nullable = false)
  private LocalDate checkInDate;

  @Column(nullable = false)
  private LocalDate checkOutDate;

  @Column(nullable = false)
  private BigDecimal totalPrice;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "room_id", nullable = false)
  private Room room;
}
