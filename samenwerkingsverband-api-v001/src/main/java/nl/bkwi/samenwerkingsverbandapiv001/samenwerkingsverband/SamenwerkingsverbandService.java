package nl.bkwi.samenwerkingsverbandapiv001.samenwerkingsverband;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import nl.bkwi.samenwerkingsverbandapiv001.dbsuwinetinkijk.repositories.ProfileRepository;
import nl.bkwi.samenwerkingsverbandapiv001.dbuseradmin.repositories.GemeenteParticipatieRepository;
import nl.bkwi.samenwerkingsverbandapiv001.dto.GemeenteDto;
import nl.bkwi.samenwerkingsverbandapiv001.dto.SamenwerkingsverbandDto;
import nl.bkwi.samenwerkingsverbandapiv001.utilities.UserDnHandling;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SamenwerkingsverbandService {

    private final GemeenteParticipatieRepository gemeenteParticipatieRepository;

    private final ProfileRepository profileRepository;

    public static final Integer RIGHT_MOST_OU = 3;
    public static final Integer LEFT_MOST_OU = 4;
    private static final String[] INTENDED_OUS = {"GSD", "WGS", "BURGERZAKEN", "GB", "RMC"};

    public static final Integer IS_NOT_VOORKEURSGEMEENTE = 0;
    public static final Integer IS_VOORKEURSGEMEENTE = 1;


    @Autowired
    public SamenwerkingsverbandService(GemeenteParticipatieRepository gemeenteParticipatieRepository,
                                       ProfileRepository profileRepository) {
        this.gemeenteParticipatieRepository = gemeenteParticipatieRepository;
        this.profileRepository = profileRepository;
    }

    /**
     * Functionality:
     * - The name of the organisation (useradmin.gemeente_participatie.naam) will be extracted from the DN.
     * - The rightmost organisational unit (e.g. GSD) will be extracted from the DN.
     * - Check that the user is working for an intended Organisational Unit.
     * - The name of the organisation will be used to
     * determine a List, distinct, ordered of all organisations in a samenwerkingsverband if
     * - a samenwerkingsverband exists.
     * - the organisation is uitvoerend.
     * - The voorkeursgemeente for the userDn will be determined.
     *   - This is set in SI Classic.
     * <p>
     * Example:
     * - userDn = "cn=babet van alkmaar,ou=alkmaar,ou=gsd,o=suwi,c=nl"
     *
     * @param userDn
     * @return
     */
    public SamenwerkingsverbandDto getGemeentesInSamenwerkingsverband(String userDn) {

        SamenwerkingsverbandDto samenwerkingsverbandDto = new SamenwerkingsverbandDto();
        samenwerkingsverbandDto.setGemeentes(new ArrayList<>());

        String organisation;
        List<String> gemeenteParticipaties;
        String foundOu;

        organisation = getOrganizationalUnit(userDn, LEFT_MOST_OU);
        foundOu = getOrganizationalUnit(userDn, RIGHT_MOST_OU).toUpperCase(Locale.ROOT);

        if (Arrays.stream(INTENDED_OUS).noneMatch(x -> x.equals(foundOu))) {
            return samenwerkingsverbandDto;
        }

        log.debug("Distinguished name {} van organisatie {} met onderdeel {} waarvoor een samenwerkingsverband gezocht wordt."
                , userDn, organisation, foundOu);
        // Find all organisations in samenwerkingsverbanden relating to the executing organisation.
      gemeenteParticipaties =
        gemeenteParticipatieRepository.findGemeenteParticipaties(organisation, foundOu);
      if (gemeenteParticipaties.isEmpty()) {
        log.debug("No organisations have been found.");
        return samenwerkingsverbandDto;
      }

      // Determine voorkeursgemeente if any.
      String voorkeursGemeente = profileRepository.findVoorkeursGemeente(userDn);

      gemeenteParticipaties.forEach(gemeenteParticipatie -> {
        if (!Objects.isNull(voorkeursGemeente) && gemeenteParticipatie.equals(voorkeursGemeente)) {
          samenwerkingsverbandDto.addGemeentesItem(new GemeenteDto(gemeenteParticipatie, IS_VOORKEURSGEMEENTE));
        } else {
          samenwerkingsverbandDto.addGemeentesItem(new GemeenteDto(gemeenteParticipatie, IS_NOT_VOORKEURSGEMEENTE));
        }
      });

      String logLine = samenwerkingsverbandDto.getGemeentes().stream().map(x -> x.getGemeente() + "|").collect(Collectors.joining());
      log.info("DN({}). Overzicht van organisaties aanwezig in het samenwerkingsverband - |" + logLine, userDn);

      return samenwerkingsverbandDto;
    }

    public String getOrganizationalUnit(String userDn, int positionOu) {
        String organisation;
        try {
            organisation = UserDnHandling.extractOrganisation(userDn, positionOu);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed extraction of the Organisational Unit out of userDN '" + userDn + "'" + " with position " + positionOu);
        }
        return organisation;
    }
}
