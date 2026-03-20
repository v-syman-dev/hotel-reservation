package by.vladislav.hotelreservation.entity;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.annotations.BatchSize;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "rooms", indexes = {
    @Index(name = "idx_room_hotel_id", columnList = "hotel_id")
})
public class Room {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "room_seq")
  @SequenceGenerator(name = "room_seq", sequenceName = "room_sequence", initialValue = 1, allocationSize = 50)
  private long id;

  @Column(nullable = false)
  private int number;

  @Column(nullable = false)
  private String type;

  @Column(nullable = false)
  private BigDecimal pricePerNight;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "hotel_id")
  private Hotel hotel;

  @BatchSize(size = 10)
  @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Booking> bookings;
}
