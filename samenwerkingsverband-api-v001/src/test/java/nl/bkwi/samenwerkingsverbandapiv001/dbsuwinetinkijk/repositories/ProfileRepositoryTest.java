package nl.bkwi.samenwerkingsverbandapiv001.dbsuwinetinkijk.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import nl.bkwi.samenwerkingsverbandapiv001.dbsuwinetinkijk.entities.Profile;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@ActiveProfiles("test")
@DataJpaTest
class ProfileRepositoryTest {

    @Autowired
    ProfileRepository profileRepository;

    @Test
    void testFindVoorkeursGemeenteForAUserDnIfVoorkeursgemeenteIsSet() {
        // Given
        String context = "cn=babet van plaats1,ou=plaats1,ou=gsd,o=suwi,c=nl";
        String name = "Samenwerkingsverband.voorkeursgemeente";
        String value = "plaats2";
        LocalDate created = LocalDate.now();
        LocalDate updated = null;

        Profile profileBabette = new Profile(context, name, value, created, updated);

        // When
        profileRepository.save(profileBabette);

        // Then
        String foundValue = profileRepository.findVoorkeursGemeente(context);
        assertThat(foundValue).isEqualTo(value);
    }
}