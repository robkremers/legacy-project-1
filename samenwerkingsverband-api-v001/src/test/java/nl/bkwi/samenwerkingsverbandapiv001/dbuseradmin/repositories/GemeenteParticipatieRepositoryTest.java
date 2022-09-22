package nl.bkwi.samenwerkingsverbandapiv001.dbuseradmin.repositories;

import static nl.bkwi.samenwerkingsverbandapiv001.samenwerkingsverband.Constants.IS_EXECUTING_ORGANISATION;
import static nl.bkwi.samenwerkingsverbandapiv001.samenwerkingsverband.Constants.IS_NOT_EXECUTING_ORGANISATION;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import nl.bkwi.samenwerkingsverbandapiv001.dbuseradmin.entities.GemeenteParticipatie;
import nl.bkwi.samenwerkingsverbandapiv001.dbuseradmin.entities.Samenwerkingsverband;
import nl.bkwi.samenwerkingsverbandapiv001.enums.OrganisationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@ActiveProfiles("test")
@DataJpaTest
class GemeenteParticipatieRepositoryTest {

    @Autowired
    GemeenteParticipatieRepository gemeenteParticipatieRepository;

    @Autowired
    SamenwerkingsverbandRepository samenwerkingsverbandRepository;

    LocalDate begin;
    LocalDate eind;
    List<String> savedOrganisations;

    @BeforeEach
    void setUp() {
        savedOrganisations = new ArrayList<>();
        begin = LocalDate.now();
        eind = LocalDate.now();
    }

    @Test
    void testFindGemeenteParticipatiesIfOrganisationIsUitvoerend() {
        // Given
        Samenwerkingsverband samenwerkingsverband1 = new Samenwerkingsverband(1000, "SW1", begin, eind, "GSD");
        samenwerkingsverbandRepository.save(samenwerkingsverband1);

        GemeenteParticipatie plaats1 = new GemeenteParticipatie(100, samenwerkingsverband1, "Plaats1", IS_EXECUTING_ORGANISATION, begin, eind, OrganisationType.GEMEENTE);
        gemeenteParticipatieRepository.save(plaats1);
        savedOrganisations.add(plaats1.getNaam());

        GemeenteParticipatie plaats2 = new GemeenteParticipatie(101, samenwerkingsverband1, "Plaats2", IS_NOT_EXECUTING_ORGANISATION, begin, eind, OrganisationType.GEMEENTE);
        gemeenteParticipatieRepository.save(plaats2);
        savedOrganisations.add(plaats2.getNaam());
        log.debug("Saved organisations:");
        savedOrganisations.forEach(organisation -> log.debug("Plaats = {}", organisation));

        // When
        List<String> foundOrganisations = gemeenteParticipatieRepository.findGemeenteParticipaties("Plaats1", "GSD");
        log.debug("Found organisations");
        foundOrganisations.forEach(organisation -> log.debug("Plaats = {}", organisation));
        // Then
        assertThat(foundOrganisations).isEqualTo(savedOrganisations);
    }

    // Situation that the organisation is not uitvoerend. An empty response is expected.
    @Test
    void testNoGemeenteParticipatieIfOrganisationIsNotUitvoerend() {
        // Given
      Samenwerkingsverband samenwerkingsverband1 = new Samenwerkingsverband(1000, "SW1", begin, eind, "GSD");
        samenwerkingsverbandRepository.save(samenwerkingsverband1);
        GemeenteParticipatie plaats1 = new GemeenteParticipatie(100, samenwerkingsverband1, "Plaats1", IS_EXECUTING_ORGANISATION, begin, eind, OrganisationType.GEMEENTE);
        gemeenteParticipatieRepository.save(plaats1);
        savedOrganisations.add(plaats1.getNaam());
        GemeenteParticipatie plaats2 = new GemeenteParticipatie(101, samenwerkingsverband1, "Plaats2", IS_NOT_EXECUTING_ORGANISATION, begin, eind, OrganisationType.GEMEENTE);
        gemeenteParticipatieRepository.save(plaats2);
        savedOrganisations.add(plaats2.getNaam());

        // When
      List<String> returnedOrganisations = gemeenteParticipatieRepository.findGemeenteParticipaties("Plaats2", "GSD");
        // Then
        assertThat(returnedOrganisations).isEmpty();
    }

    // Situation that the organisation is uitvoerend in multiple samenwerkingsverbanden.
    @Test
    void testFindMultipleSamenwerkingsverbandenIfOrganisationIsUitvoerendInMultipleSamenwerkingsverbanden() {
        // Given
      Samenwerkingsverband samenwerkingsverband1 = new Samenwerkingsverband(1000, "SW1", begin, eind, "GSD");
        samenwerkingsverbandRepository.save(samenwerkingsverband1);
        GemeenteParticipatie plaats11 = new GemeenteParticipatie(100, samenwerkingsverband1, "Plaats1", IS_EXECUTING_ORGANISATION, begin, eind, OrganisationType.GEMEENTE);
        gemeenteParticipatieRepository.save(plaats11);
        savedOrganisations.add(plaats11.getNaam());
        GemeenteParticipatie plaats2 = new GemeenteParticipatie(101, samenwerkingsverband1, "Plaats2", IS_NOT_EXECUTING_ORGANISATION, begin, eind, OrganisationType.GEMEENTE);
        gemeenteParticipatieRepository.save(plaats2);
        savedOrganisations.add(plaats2.getNaam());

      Samenwerkingsverband samenwerkingsverband2 = new Samenwerkingsverband(1001, "SW2", begin, eind, "GSD");
        samenwerkingsverbandRepository.save(samenwerkingsverband2);
        GemeenteParticipatie plaats12 = new GemeenteParticipatie(102, samenwerkingsverband2, "Plaats1", IS_EXECUTING_ORGANISATION, begin, eind, OrganisationType.GEMEENTE);
        gemeenteParticipatieRepository.save(plaats12);
        savedOrganisations.add(plaats12.getNaam());
        GemeenteParticipatie plaats3 = new GemeenteParticipatie(103, samenwerkingsverband2, "Plaats3", IS_NOT_EXECUTING_ORGANISATION, begin, eind, OrganisationType.GEMEENTE);
        gemeenteParticipatieRepository.save(plaats3);
        savedOrganisations.add(plaats3.getNaam());

        List<String> distinctSavedOrganisations = new ArrayList<>(new HashSet<>(savedOrganisations));

        // When
      List<String> returnedOrganisations = gemeenteParticipatieRepository.findGemeenteParticipaties("Plaats1", "GSD");

        // Then
        assertThat(returnedOrganisations).isEqualTo(distinctSavedOrganisations);
    }
}
