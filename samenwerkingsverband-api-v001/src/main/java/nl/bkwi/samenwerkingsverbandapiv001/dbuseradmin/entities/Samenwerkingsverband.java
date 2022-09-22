package nl.bkwi.samenwerkingsverbandapiv001.dbuseradmin.entities;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "samenwerkingsverband")
@Table(name = "samenwerkingsverband")
public class Samenwerkingsverband {

    @Id
    private Integer id;

    @Column(
            nullable = false
    )
    private String naam;

    @Column(
      nullable = false
    )
    private LocalDate begin;

  @Column(
    name = "eind"
  )
  private LocalDate eind;

  @Column(
    name = "onderdeel"
  )
  private String onderdeel;
}
