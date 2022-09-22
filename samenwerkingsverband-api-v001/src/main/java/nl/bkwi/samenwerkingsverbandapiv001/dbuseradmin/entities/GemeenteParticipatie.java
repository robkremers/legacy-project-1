package nl.bkwi.samenwerkingsverbandapiv001.dbuseradmin.entities;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import nl.bkwi.samenwerkingsverbandapiv001.enums.OrganisationType;

/**
 * In this table the organisations (in column 'naam') are present that may be listed in a 'samenwerkingsverband'.
 * If uitvoerend  = 1 the corresponding council will be executing one or more tasks for the other councils.
 * <p>
 * In this application the table should not be updated. However if I add 'insertable = false' the unit tests in which
 * I mock or insert with rollback test data inserts can not be executed anymore. Therefore this parameter has
 * been omitted.
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "gemeente_participatie")
@Table(name = "gemeente_participatie")
public class GemeenteParticipatie {

    @Id
    private Integer id;

    @JoinColumn(
        name = "samenwerkingsverband_id",
        referencedColumnName = "id",
        nullable = false
    )
    @ManyToOne(optional = false)
    private Samenwerkingsverband samenwerkingsverband;

    @Column(
            nullable = false
    )
    private String naam;

    @Column(
            nullable = false
    )
    private Integer uitvoerend;

    @Column(
            nullable = false
    )
    private LocalDate begin;

    @Column(
            name = "eind"
    )
    private LocalDate eind;

    @Column(
            name = "type_organisatie"
            , nullable = false
    )
    @Enumerated(EnumType.STRING)
    private OrganisationType organisationType;
}
