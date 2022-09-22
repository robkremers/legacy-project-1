package nl.bkwi.samenwerkingsverbandapiv001.utilities;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

public class UserDnHandling {

  public static final String ORGANIZATIONAL_UNIT = "ou";
  public static final String COMMON_NAME = "cn";

  private UserDnHandling() {
  }

  /**
   * Purpose:
   * Extract the name of an organisation of a DN.
   * <p>
   * Background:
   * - https://www.ibm.com/docs/en/i/7.2?topic=concepts-distinguished-names-dns
     * - https://docs.microsoft.com/en-us/previous-versions/windows/desktop/ldap/distinguished-names
     * <p>
     * Example:
     * - userDn = "cn=babet van alkmaar,ou=alkmaar,ou=gsd,o=suwi,c=nl"
     * - return "alkmaar"
     *
     * @param userDn a Distinguished Name used to define a user.
     * @param ouPlace the place of a organisational unit in a Distinguished Name starting from the right.
     * @return
     */
    public static String extractOrganisation(String userDn, int ouPlace) throws InvalidNameException {

        LdapName user = new LdapName(userDn);
        Rdn organisation;

        // must contain ou=..,ou=gsd,o=suwi,c=nl, if 4th part from the right is really of type ou is checked below
        if (user.size() < 4) {
            return "";
        }

        // must contain "cn"
        if (!isUser(user)) {
            return "";
        }

        //  startsWith, not endsWith and the param list values are also backwords :-)
        if (!user.startsWith(new LdapName("o=suwi,c=nl"))) {
            return "";
        }

        organisation = user.getRdn(ouPlace - 1);
        if (!organisation.getType().equals(ORGANIZATIONAL_UNIT)) {
            return "";
        }

        return (String) organisation.getValue();
    }

    /**
     * Rdn: relative distinguished name
     *
     * @param user
     * @return
     */
    private static boolean isUser(LdapName user) {
        Rdn commonName = user.getRdn(user.size() - 1);
        return commonName.getType().equals(COMMON_NAME);
    }
}
