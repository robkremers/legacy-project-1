package nl.bkwi.samenwerkingsverbandapiv001.samenwerkingsverband;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.naming.InvalidNameException;

import lombok.extern.slf4j.Slf4j;
import nl.bkwi.samenwerkingsverbandapiv001.dbsuwinetinkijk.repositories.ProfileRepository;
import nl.bkwi.samenwerkingsverbandapiv001.dbuseradmin.repositories.GemeenteParticipatieRepository;
import nl.bkwi.samenwerkingsverbandapiv001.dto.GemeenteDto;
import nl.bkwi.samenwerkingsverbandapiv001.dto.SamenwerkingsverbandDto;
import nl.bkwi.samenwerkingsverbandapiv001.utilities.UserDnHandling;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
@ActiveProfiles("test")
class SamenwerkingsverbandServiceTest {

    @Mock
    private GemeenteParticipatieRepository gemeenteParticipatieRepository;

    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private SamenwerkingsverbandService samenwerkingsverbandService;

    UserDnHandling userDnHandling;

    private Exception exception = new Exception();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetOrganisationsInSamenwerkingsverbandIfSamenwerkingsVerbandExistsAndOrganisationIsUitvoerend() throws InvalidNameException {
        // Given
        GemeenteDto gemeente1Dto = new GemeenteDto("plaats1", 0);
        GemeenteDto gemeente2Dto = new GemeenteDto("plaats2", 1);
        GemeenteDto gemeente3Dto = new GemeenteDto("plaats3", 0);

        SamenwerkingsverbandDto presentSamenwerkingsverbandDto = new SamenwerkingsverbandDto();
        presentSamenwerkingsverbandDto.setGemeentes(Arrays.asList(gemeente1Dto, gemeente2Dto, gemeente3Dto));

        List<String> presentGemeenteParticipatieplaatsen = Arrays.asList("plaats1", "plaats2", "plaats3");

        String userDN = "cn=babet van plaats1,ou=plaats1,ou=gsd,o=suwi,c=nl";

        given(gemeenteParticipatieRepository
                .findGemeenteParticipaties("plaats1", "GSD"))
                .willReturn(presentGemeenteParticipatieplaatsen);

        given(profileRepository.findVoorkeursGemeente(userDN))
                .willReturn("plaats2");
        // When
        SamenwerkingsverbandDto returnedSamenwerkingsverbandDto = samenwerkingsverbandService.getGemeentesInSamenwerkingsverband(userDN);

        // Then
        assertThat(returnedSamenwerkingsverbandDto).isEqualTo(presentSamenwerkingsverbandDto);
    }

    /**
     * Only users working for GSD should be considered. Users working for other organisations should not be processed and an empty
     * SamenwerkingsverbandDto should be returned.
     */
    @Test
    void testGetNoOrganisationsInSamenwerkingsverbandIfOuNotGemeente() {
        // Given
        String userDN = "cn=babet van plaats1,ou=plaats1,ou=uwv,o=suwi,c=nl";
        SamenwerkingsverbandDto expectedSamenwerkingsverbandDto = new SamenwerkingsverbandDto();
        expectedSamenwerkingsverbandDto.setGemeentes(new ArrayList<>());
        // When
        SamenwerkingsverbandDto returnedSamenwerkingsverbandDto = samenwerkingsverbandService.getGemeentesInSamenwerkingsverband(userDN);
        // Then
        assertThat(returnedSamenwerkingsverbandDto).isEqualTo(expectedSamenwerkingsverbandDto);
    }

    @Test
    void testGetOrganisationsInSamenwerkingsverbandIfNoContentIsFound() {
        // Given
        SamenwerkingsverbandDto presentSamenwerkingsverbandDto = new SamenwerkingsverbandDto();
        presentSamenwerkingsverbandDto.setGemeentes(new ArrayList<>());

        List<String> presentGemeenteParticipatieplaatsen = new ArrayList<>();
        String userDN = "cn=developer,ou=testplaats,ou=gsd,o=suwi,c=nl";

        given(gemeenteParticipatieRepository
                .findGemeenteParticipaties("testplaats", "GSD"))
                .willReturn(presentGemeenteParticipatieplaatsen);

        // When
        SamenwerkingsverbandDto returnedSamenwerkingsverbandDto = samenwerkingsverbandService.getGemeentesInSamenwerkingsverband(userDN);

        // Then
        assertThat(returnedSamenwerkingsverbandDto).isEqualTo(presentSamenwerkingsverbandDto);
    }

    /**
     * userDn = "cn=babet van alkmaar,ou=alkmaar,ou=gsd,o=suwi,c=nl"
     */
    @Test
    void testGetGetOrganizationsInSamenwerkingsverbandIfOnderdeelIsNotPresent() {
        // Given
        SamenwerkingsverbandDto presentSamenwerkingsverbandDto = new SamenwerkingsverbandDto();
        presentSamenwerkingsverbandDto.setGemeentes(new ArrayList<>());

        List<String> presentGemeenteParticipatieplaatsen = new ArrayList<>();
        String userDN = "cn=developer,ou=alkmaar,ou=GSD,o=suwi,c=nl";

        given(gemeenteParticipatieRepository
                .findGemeenteParticipaties("Alkmaar", ""))
                .willReturn(presentGemeenteParticipatieplaatsen);

        // When
        SamenwerkingsverbandDto returnedSamenwerkingsverbandDto = samenwerkingsverbandService.getGemeentesInSamenwerkingsverband(userDN);

        // Then
        assertThat(returnedSamenwerkingsverbandDto).isEqualTo(presentSamenwerkingsverbandDto);
    }

    @Test
    void testGetOrganizationForLdapException() {
        exception = assertThrows(IllegalArgumentException.class, () -> {
            samenwerkingsverbandService.getOrganizationalUnit("bull", 4);

            String expectedMessage = "Failed extraction of the Organisational Unit out of userDN 'bull' with position 4";
            String actualMessage = exception.getMessage();

            assertTrue(actualMessage.contains(expectedMessage));
        });
    }
}
