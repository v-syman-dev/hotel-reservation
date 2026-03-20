package by.vladislav.hotelreservation.entity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
@Table(name = "hotels", indexes = {
    @Index(name = "idx_hotel_rating", columnList = "rating"),
})
public class Hotel {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hotel_seq")
  @SequenceGenerator(name = "hotel_seq", sequenceName = "hotel_sequence", initialValue = 1, allocationSize = 50)
  private long id;

  @Column(nullable = false, unique = true)
  private String name;

  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JoinColumn(name = "address_id", nullable = false)
  private Address address;

  private BigDecimal rating;

  @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL)
  private List<Room> rooms;

  @ManyToMany
  @JoinTable(name = "hotel_conveniences", 
      joinColumns = @JoinColumn(name = "hotel_id"), 
      inverseJoinColumns = @JoinColumn(name = "convenience_id"), 
      indexes = {
        @Index(name = "idx_hotel_convenience_id", columnList = "convenience_id"),
        @Index(name = "idx_convenience_hotel_id", columnList = "hotel_id")})
  private Set<Convenience> conveniences;
}
