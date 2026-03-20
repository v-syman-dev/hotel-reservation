package by.vladislav.hotelreservation.entity;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "conveniences")
public class Convenience {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "convenience_seq")
  @SequenceGenerator(
      name = "convenience_seq", 
      sequenceName = "convenience_sequence", 
      initialValue = 1, 
      allocationSize = 50)
  private long id;

  @Column(unique = true, nullable = false)
  private String name;

  @ManyToMany(mappedBy = "conveniences", fetch = FetchType.LAZY)
  private Set<Hotel> hotels;
}
