package by.vladislav.hotelreservation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "addresses", indexes = {
    @Index(name = "idx_address_country_city", columnList = "country, city")
})
public class Address {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "address_seq")
  @SequenceGenerator(name = "address_seq", sequenceName = "address_sequence", initialValue = 1, allocationSize = 50)
  private long id;

  @Column(nullable = false)
  private String country;

  @Column(nullable = false)
  private String city;

  @Column(nullable = false)
  private String street;

  @OneToOne(mappedBy = "address", fetch = FetchType.LAZY)
  private Hotel hotel;
}
