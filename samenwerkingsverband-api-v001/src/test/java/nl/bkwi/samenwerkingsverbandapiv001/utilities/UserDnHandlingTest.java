package nl.bkwi.samenwerkingsverbandapiv001.utilities;

import lombok.extern.slf4j.Slf4j;
import nl.bkwi.samenwerkingsverbandapiv001.samenwerkingsverband.SamenwerkingsverbandService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import javax.naming.InvalidNameException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * An example of a correct userDN is: cn=babet van alkmaar,ou=alkmaar,ou=gsd,o=suwi,c=nl"
 */
@Slf4j
class UserDnHandlingTest {

    /**
     * In the following test the parameter ouPlace is hardcoded.
     * For normal use constants are defined in class Constants.
     *
     * @param userDn A Distinguished name identifying an account.
     * @param organisationalUnit An identifier for the unit for which the user is working.
     * @param ouPlace The position of an Organisational Unit counting from right.
     * @throws InvalidNameException
     */
    @ParameterizedTest
    @CsvSource(value = {
            "cn=babet van alkmaar,ou=alkmaar,ou=gsd,o=suwi,c=nl:alkmaar:4",
            "cn=babet van alkmaar,ou=alkmaar,ou=gsd,o=suwi,c=nl:gsd:3"
    }, delimiter = ':')
    void testUserDnCorrect(String userDn, String organisationalUnit, Integer ouPlace) throws InvalidNameException {
        String extractOrganisation = UserDnHandling.extractOrganisation(userDn, ouPlace);
        assertThat(extractOrganisation).isEqualTo(organisationalUnit);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "cn=developer,ou=BKWI,o=suwi,c=nl",
            "o=suwi,c=nl",
            "ou=alkmaar,ou=gsd,o=suwi,c=nl",
            "cn=babet van alkmaar,ou=alkmaar,ou=gsd,o=suwi",
            "cn=babet van alkmaar,ou=gsd,o=suwi,c=nl"
    })
    void testUser(String userDn) throws InvalidNameException {
      String extractOrganisation = UserDnHandling.extractOrganisation(userDn, SamenwerkingsverbandService.LEFT_MOST_OU);
        assertThat(extractOrganisation).isEmpty();
    }
}
