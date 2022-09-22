package nl.bkwi.samenwerkingsverbandapiv001.dbsuwinetinkijk.entities;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * https://www.baeldung.com/jpa-composite-primary-keys
 *
 * With @IdClass, the query is a bit simpler:
 *
 * SELECT account.accountNumber FROM Account account
 * With @EmbeddedId, we have to do one extra traversal:
 *
 * SELECT book.bookId.title FROM Book book
 *
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "profile")
@IdClass(value = ProfileId.class)
@Table(name = "profile")
public class Profile {

    @Id
    @Column(
            nullable = false
            , updatable = false
    )
    private String context;

    @Id
    @Column(
            nullable = false
            , updatable = false
    )
    private String name;

    @Column(updatable = false
    )
    private String value;
    @Column(
            nullable = false
            , updatable = false
    )
    private LocalDate created;

    @Column(updatable = false
    )
    private LocalDate updated;

}
