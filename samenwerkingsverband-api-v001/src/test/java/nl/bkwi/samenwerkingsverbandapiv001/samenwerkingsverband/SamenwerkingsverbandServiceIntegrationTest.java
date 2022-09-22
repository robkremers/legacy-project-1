package nl.bkwi.samenwerkingsverbandapiv001.samenwerkingsverband;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import javax.naming.InvalidNameException;
import nl.bkwi.samenwerkingsverbandapiv001.dbsuwinetinkijk.entities.Profile;
import nl.bkwi.samenwerkingsverbandapiv001.dbsuwinetinkijk.repositories.ProfileRepository;
import nl.bkwi.samenwerkingsverbandapiv001.dbuseradmin.entities.GemeenteParticipatie;
import nl.bkwi.samenwerkingsverbandapiv001.dbuseradmin.entities.Samenwerkingsverband;
import nl.bkwi.samenwerkingsverbandapiv001.dbuseradmin.repositories.GemeenteParticipatieRepository;
import nl.bkwi.samenwerkingsverbandapiv001.dbuseradmin.repositories.SamenwerkingsverbandRepository;
import nl.bkwi.samenwerkingsverbandapiv001.dto.GemeenteDto;
import nl.bkwi.samenwerkingsverbandapiv001.dto.SamenwerkingsverbandDto;
import nl.bkwi.samenwerkingsverbandapiv001.enums.OrganisationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/*
 * Using the @Import annotation we make more beans available to the default beans activated by @DataJpaTest
 */
@ActiveProfiles("test")
@DataJpaTest
@Import(nl.bkwi.samenwerkingsverbandapiv001.samenwerkingsverband.SamenwerkingsverbandService.class)
class SamenwerkingsverbandServiceIntegrationTest {

    @Autowired
    private SamenwerkingsverbandRepository samenwerkingsverbandRepository;

    @Autowired
    private GemeenteParticipatieRepository gemeenteParticipatieRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private SamenwerkingsverbandService samenwerkingsverbandService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testGetOrganisationsInSamenwerkingsverbandIfSamenwerkingsVerbandExistsAndOrganisationIsUitvoerend()
            throws InvalidNameException {
        // Given

        // Maak een nieuw samenwerkingsverband aan
        Samenwerkingsverband samenwerkingsverband = new Samenwerkingsverband();
        samenwerkingsverband.setId(100);
        samenwerkingsverband.setNaam("Samenwerking tussen plaats1, plaats2 en plaats3");
        LocalDate now = LocalDate.now();
        LocalDate tomorrow = now.plus(1, DAYS);
        samenwerkingsverband.setBegin(now);
        samenwerkingsverband.setEind(tomorrow);
      samenwerkingsverband.setOnderdeel("GSD");
      samenwerkingsverbandRepository.save(samenwerkingsverband);

        // Maak nu 3 nieuwe gemeente participaties aan en hang deze aan het samenwerkingsverband
        Integer pk = 100;
        GemeenteParticipatie gemeenteParticipatie1 = createGemeenteParticipatie("plaats1",
                samenwerkingsverband);
        GemeenteParticipatie gemeenteParticipatie2 = createGemeenteParticipatie("plaats2",
                samenwerkingsverband);
        GemeenteParticipatie gemeenteParticipatie3 = createGemeenteParticipatie("plaats3",
                samenwerkingsverband);
        gemeenteParticipatie1.setId(pk++);
        gemeenteParticipatie2.setId(pk++);
        gemeenteParticipatie3.setId(pk++);

        // Maak de eerste gemeente participatie de uitvoerende
        GemeenteParticipatie uitvoerendGemeenteParticipatie = setUitvoerendeParticipatie(
                gemeenteParticipatie1);

        // Sla nu de 3 gemeente participaties op
        gemeenteParticipatieRepository.save(gemeenteParticipatie1);
        gemeenteParticipatieRepository.save(gemeenteParticipatie2);
        gemeenteParticipatieRepository.save(gemeenteParticipatie3);

        // Maak de 2e gemeente de voorkeur gemeente
        setVoorkeurGemeente(gemeenteParticipatie2);

        // Dit is de hardcoded context van de gebruiker voor deze test
        String userDN = "cn=babet van plaats1,ou=plaats1,ou=gsd,o=suwi,c=nl";

        // Given
        List<String> presentGemeenteParticipaties = gemeenteParticipatieRepository.findGemeenteParticipaties(
          uitvoerendGemeenteParticipatie.getNaam(), "GSD");

        // When
        SamenwerkingsverbandDto returnedSamenwerkingsverbandDto = samenwerkingsverbandService
                .getGemeentesInSamenwerkingsverband(userDN);

        // Then
        GemeenteDto plaats1Dto = new GemeenteDto(gemeenteParticipatie1.getNaam(), 0);
        GemeenteDto plaats2Dto = new GemeenteDto(gemeenteParticipatie2.getNaam(), 1);
        GemeenteDto plaats3Dto = new GemeenteDto(gemeenteParticipatie3.getNaam(), 0);

        SamenwerkingsverbandDto presentSamenwerkingsverbandDto = new SamenwerkingsverbandDto();
        presentSamenwerkingsverbandDto.setGemeentes(Arrays.asList(plaats1Dto, plaats2Dto, plaats3Dto));

        assertThat(returnedSamenwerkingsverbandDto).isEqualTo(presentSamenwerkingsverbandDto);

        List<String> expectedParticipaties = Arrays.asList(gemeenteParticipatie1.getNaam(),
                gemeenteParticipatie2.getNaam(), gemeenteParticipatie3.getNaam());
        assertThat(presentGemeenteParticipaties).isEqualTo(expectedParticipaties);
    }

    private GemeenteParticipatie createGemeenteParticipatie(String plaatsNaam,
                                                            Samenwerkingsverband samenwerkingsverband) {
        LocalDate now = LocalDate.now();
        GemeenteParticipatie gemeenteParticipatie = new GemeenteParticipatie();
        gemeenteParticipatie.setNaam(plaatsNaam);

        // Start today
        gemeenteParticipatie.setBegin(now);

        // End tomorrow
        gemeenteParticipatie.setEind(now.plus(1, DAYS));

        gemeenteParticipatie.setOrganisationType(OrganisationType.GEMEENTE);
        gemeenteParticipatie.setUitvoerend(0);

        gemeenteParticipatie.setSamenwerkingsverband(samenwerkingsverband);

        return gemeenteParticipatie;
    }

    // Zet gemeente met de naam plaats.naam als voorkeur gemeente
    private void setVoorkeurGemeente(GemeenteParticipatie gemeenteParticipatie) {
        Profile profile = new Profile();
        String userDN = "cn=babet van plaats1,ou=plaats1,ou=gsd,o=suwi,c=nl";

        profile.setContext(userDN);
        profile.setName("Samenwerkingsverband.voorkeursgemeente");
        profile.setValue(gemeenteParticipatie.getNaam());
        profile.setCreated(LocalDate.now());

        profileRepository.save(profile);
    }

    private GemeenteParticipatie setUitvoerendeParticipatie(
            GemeenteParticipatie gemeenteParticipatie) {
        gemeenteParticipatie.setUitvoerend(1);

        return gemeenteParticipatie;
    }

}
