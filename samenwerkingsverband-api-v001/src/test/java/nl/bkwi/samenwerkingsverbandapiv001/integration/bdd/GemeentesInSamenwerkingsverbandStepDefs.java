package nl.bkwi.samenwerkingsverbandapiv001.integration.bdd;

import io.cucumber.java.nl.Gegeven;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("bdd")
public class GemeentesInSamenwerkingsverbandStepDefs {

  private static final String GSD_USERDNVALUEVEENENDAAL = ("cn=Henk VD Handhaven,ou=Dummy Gemeente Veenendaal,ou=gsd,o=suwi,c=nl");
  private static final String WGS_USERDNVALUEZUTPHEN = ("cn=WGS FR4150028,ou=Zutphen,ou=wgs,o=suwi,c=nl");
  private static final String GSD_USERDNVALUERVNIJMEGEN_HANDHAVEN = ("cn=Henk RvN Handhaven,ou=Dummy mrg Rijk van Nijmegen,ou=gsd,o=suwi,c=nl");
  private String baseurl;

  public GemeentesInSamenwerkingsverbandStepDefs(
      @Value("${acceptatietest.targetUrl}") String targetUrl) {
    baseurl = targetUrl + "/gemeentes-in-samenwerkingsverband";
  }

  @Gegeven("gemeentes in samenwerkingsverband {string} opvragen en valideer de responseBody")
  public void gemeentes_in_samenwerkingsverband_opvragen_en_valideer_de_response_body(
      String samenwerkingsverband)
      throws IOException, JSONException, Exception {
    String userDn = "";
    String expectedResponsePath = "";

    switch (samenwerkingsverband) {
      case "gsdsamenwerkingsVerbandVeenendaal":
        userDn = GSD_USERDNVALUEVEENENDAAL;
        expectedResponsePath = "gsdsamenwerkingsVerbandVeenendaal.json";
        break;
      case "wgssamenwerkingsVerbandZutphen":
        userDn = WGS_USERDNVALUEZUTPHEN;
        expectedResponsePath = "wgssamenwerkingsVerbandZutphen.json";
        break;
      case "gsdsamenwerkingsVerbandrvnijmegen":
        userDn = GSD_USERDNVALUERVNIJMEGEN_HANDHAVEN;
        expectedResponsePath = "gsdsamenwerkingsVerbandrvnijmegen.json";
        break;
      default:
        throw new Exception("niet bekend samenwerkingsverband");
    }

    restAssuredRequest(userDn)
        .then()
        .assertThat()
        .statusCode(200);
    assertVerwachteGebruikersAlsInJSONFile(
        "/expected-responses/" + expectedResponsePath,
        userDn);
  }

  private Response restAssuredRequest(String usderDanValue) {
    RestAssured.baseURI = baseurl;
    RequestSpecification request = RestAssured.given();
    Response response = request
      .queryParam("userDn", usderDanValue)
      .header("X-Correlation-ID", "911")
      .get("/");
    return response;
  }

  private void assertVerwachteGebruikersAlsInJSONFile(String expectedGebruikersJSONFile,
      String usderDanValue)
      throws IOException, JSONException {
    String body = restAssuredRequest(usderDanValue).asString();
    String expectedJSON = readExpectedJSONFromFile(expectedGebruikersJSONFile);
    JSONAssert.assertEquals(expectedJSON, body, false);
  }

  private String readExpectedJSONFromFile(String pathRelativeToSrcTestResources)
      throws IOException {
    InputStream inputStream = GemeentesInSamenwerkingsverbandStepDefs.class.getResourceAsStream(
        pathRelativeToSrcTestResources);
    String expectedJSON = readFromInputStream(inputStream);
    return expectedJSON;
  }

  private String readFromInputStream(InputStream inputStream)
      throws IOException {
    StringBuilder resultStringBuilder = new StringBuilder();
    try (BufferedReader br
        = new BufferedReader(new InputStreamReader(inputStream))) {
      String line;
      while ((line = br.readLine()) != null) {
        resultStringBuilder.append(line).append("\n");
      }
    }
    return resultStringBuilder.toString();
  }
}
