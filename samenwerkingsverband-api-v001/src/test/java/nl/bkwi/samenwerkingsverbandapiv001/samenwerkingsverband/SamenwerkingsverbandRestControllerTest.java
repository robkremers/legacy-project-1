package nl.bkwi.samenwerkingsverbandapiv001.samenwerkingsverband;

import static nl.bkwi.samenwerkingsverbandapiv001.utilities.StringUtilities.objectToJson;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import nl.bkwi.samenwerkingsverbandapiv001.dto.GemeenteDto;
import nl.bkwi.samenwerkingsverbandapiv001.dto.SamenwerkingsverbandDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration tests.
 */
@ExtendWith({org.springframework.test.context.junit.jupiter.SpringExtension.class})
@WebMvcTest(SamenwerkingsverbandRestController.class)
@AutoConfigureMockMvc
@Slf4j
@ActiveProfiles("test")
class SamenwerkingsverbandRestControllerTest {

    @MockBean
    private SamenwerkingsverbandService samenwerkingsverbandService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testOrganisationIsNotUitvoerend() throws Exception {
      // Given
      String userDn = "cn=babet van plaats1,ou=plaats1,ou=gsd,o=suwi,c=nl";
      // When
      when(samenwerkingsverbandService.getGemeentesInSamenwerkingsverband(userDn))
        .thenReturn(new SamenwerkingsverbandDto());
      // Then
      mockMvc.perform(get("/gemeentes-in-samenwerkingsverband/?userDn=" + userDn)
          .header("X-Correlation-ID", "911"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      ;
    }

    @Test
    void testFindOrganisationsInSamenwerkingsverbandIfOrganisationIsUitvoerend() throws Exception {
        // Given
        GemeenteDto gemeente1Dto = new GemeenteDto("Plaats1", 1);
      GemeenteDto gemeente2Dto = new GemeenteDto("Plaats2", 0);
      GemeenteDto gemeente3Dto = new GemeenteDto("Plaats3", 0);

      SamenwerkingsverbandDto samenwerkingsverbandDto = new SamenwerkingsverbandDto();
      samenwerkingsverbandDto.setGemeentes(Arrays.asList(gemeente1Dto, gemeente2Dto, gemeente3Dto));

      String jsonObject = objectToJson(samenwerkingsverbandDto);

      String userDn = "cn=babet van plaats1,ou=plaats1,ou=gsd,o=suwi,c=nl";

      given(samenwerkingsverbandService.getGemeentesInSamenwerkingsverband(userDn))
        .willReturn(
          samenwerkingsverbandDto
        );

      // When
      // Then
      mockMvc.perform(get("/gemeentes-in-samenwerkingsverband/?userDn=" + userDn)
          .header("X-Correlation-ID", "911"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(jsonObject))
        .andReturn();
    }

    /**
     * For the situation that the userDn is not a correct Distinguished Name the controller should return
     * a HTTP status code 400 (Bad Request).
     * See:
     * - https://docs.microsoft.com/en-us/previous-versions/windows/desktop/ldap/distinguished-names
     * Examples of userDn:
     * - Correct  : cn=babet van plaats1,ou=plaats1,ou=gsd,o=suwi,c=nl
     * - Incorrect: Incorrect Distinguished Name.
     */
    @Test
    void testIncorrectUserDnshouldReturnHttpBadRequest() throws Exception {
        // Given
        String userDn = "Incorrect Distinguished Name";
        // When
        when(samenwerkingsverbandService.getGemeentesInSamenwerkingsverband(userDn))
                .thenThrow(new IllegalArgumentException("Failed extraction of the organisation out of userDN '" + userDn + "'"));

        // Then
        mockMvc.perform(get("/gemeentes-in-samenwerkingsverband/?userDn=" + userDn))
                .andExpect((status().isBadRequest()))
                .andReturn();
    }
}
