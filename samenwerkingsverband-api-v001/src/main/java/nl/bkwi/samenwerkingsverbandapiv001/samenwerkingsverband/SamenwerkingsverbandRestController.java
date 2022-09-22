package nl.bkwi.samenwerkingsverbandapiv001.samenwerkingsverband;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.extern.slf4j.Slf4j;
import nl.bkwi.samenwerkingsverbandapiv001.dto.SamenwerkingsverbandDto;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.naming.InvalidNameException;

@RestController
@Slf4j
@OpenAPIDefinition(servers = {
  @Server(description = "Local", url = "http://localhost:${server.port}")
}
)
public class SamenwerkingsverbandRestController {

  public static final String X_CORRELATION_ID = "X-Correlation-ID";
  private final SamenwerkingsverbandService samenwerkingsverbandService;

  @Autowired
  public SamenwerkingsverbandRestController(SamenwerkingsverbandService samenwerkingsverbandService) {
    this.samenwerkingsverbandService = samenwerkingsverbandService;
  }

  /**
   * Functionality - Return a List of all gemeentes in a samenwerkingsverband if a samenwerkingsverband exists and the gemeente is
   * uitvoerend. - If the organisation has type 'GEMEENTE' it will be part of the list. - If the organisation has type
   * 'SAMENWERKINGSVERBAND' it will be excluded. - The list is in alphabetical order and no duplicates should exist.
   * <p>
   * Example: - http://localhost:8080/gemeentes-in-samenwerkingsverband?userDn=cn=babet%20van%20alkmaar,ou=alkmaar,ou=gsd,o=suwi,c=nl -
   * Response (in SI Classic Enschede was set to be the voorkeursgemeente: [ { gemeente: "Alkmaar", voorkeursgemeente: 0 }, { gemeente:
   * "Castricum", voorkeursgemeente: 0 }, { gemeente: "Enschede", voorkeursgemeente: 1 } ]
   *
   * @param userDn
   * @return
   */
  @GetMapping(value = "${server.openapi.uri}", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public SamenwerkingsverbandDto getGemeentesInSamenwerkingsverband(
    @RequestParam(name = "userDn") String userDn,
    @RequestHeader(X_CORRELATION_ID) String correlationId) {
    MDC.put(X_CORRELATION_ID, correlationId);
    try {
      return samenwerkingsverbandService.getGemeentesInSamenwerkingsverband(userDn);
    } catch (IllegalArgumentException iae) {
      throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST, iae.getMessage()
      );
    }
  }
}
