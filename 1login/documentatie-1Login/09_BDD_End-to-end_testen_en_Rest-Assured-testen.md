# BDD End-to-end en Rest-Assured-testen

## Introductie
Voor 1login hebben we ervoor gekozen om acceptatiecriteria te formuleren bij de user stories aan de hand van de 'BDD' aanpak. 
Per feature of functionaliteit zijn er dan scenario's geformuleerd volgens het 'Given - when - then' principe. In het Nederlands ook wel:
Gegeven - Wanneer - Dan. Deze formulering van acceptatiecriteria en daarmee van de verwachte werking van de applicatie wordt door de ontwerper gemaakt. 
Deze gestructureerde formuleringen zijn door de ontwikkelaar en tester makkelijk te interpreteren en te vertalen naar (test)code. 
Dit doen we dankzij de Gherkin syntax. Eerst een overzicht van de technieken die we gebruiken om onze BDD
testen te realiseren.

## Documentatie

- [test-port-forwarding t.b.v. testen van een applicatie in een pod](https://gitlab.bkwi.nl/bvv/documentatie/-/blob/master/Technisch/test-port-forwarding.md#bdd-testen)

## Technieken
Bij onze BDD testen gebruiken we verschillende frameworks:
 - [Cucumber](http://angiejones.tech/rest-assured-with-cucumber-using-bdd-for-web-services-automation?refreshed=y) ->voor de
  verbinding tussen de BDD userstories en de RestAssured test code met behulp van de Gherkin syntax. Zie [voorbeeld project](https://github.com/EnlightenedSoftware/restassured-with-cucumber-demo) 
 - [Rest-Assured](http://rest-assured.io/) voor het lezen en controleren van de responses. Dankzij Rest-Assured hebben we hiervoor geen user interface nodig.
 
## Stap 1: Van acceptatiecriteria naar feature files
Wanneer we de beschikking hebben over een feature (de user story ofwel een stukje functionaliteit) dan kunnen we deze in het applicatie project toevoegen als
een feature file. Onder src-test-resources kunnen we een map 'features' maken. Daaronder kun je deze eventueel nog submappen hangen. 
Dan wordt daarin een file geplaatst met een naam die eindigt op '.feature'. 
Om deze feature file te kunnen interpreteren heb je een aantal dependencies nodig in je pom.xml: 
 - cucumber-java
 - cucumber-junit
 - rest-assured

Zie voor de volledige dependencies de pom.xml van het de regression-test en samenwerkingsverband-api-v001 projecten als voorbeeld. 
- pom samenwerkingsverband-api-v001 project:
    - Path: samenwerkingsverband-api-v001/pom.xml
- pom  regression-test project
    - Path: workspace/regression-test/pom.xml
 
Een feature file kan uit meerdere scenario's bestaan en er bijvoorbeeld als volgt uitzien: 
```
#language: nl

Functionaliteit: Samenwerkingsverband Rijk van Nijmegen heeft alle taken overgenomen van een aantal gemeentes waaronder Berg en Dal

  Achtergrond:
    Gegeven er is ingelogd met een rvnijmegen_handhaven account en er is een dossier gestart

  Scenario: Samenwerkingsverband Rijk van Nijmegen voert overgenomen taak xxxx-dummy-handhaven uit
    Als de gebruiker naar "Samenwerkingsverband" navigeert
    En als de gebruiker "Berg en Dal" in het dropdownveld selecteert
    En hij op de button verder drukt
    Dan wordt de gemeentenaam Berg en Dal getoond
    Als de gebruiker op de "Persoonsgegevens historie" tab klikt
    Dan zijn de volgende blokken zichtbaar:
      | Opschorting persoonslijst   |
      | Persoonsgegevens - historie |

  Scenario: Samenwerkingsverband Rijk van Nijmegen wisselt van Berg en dal naar Nijmegen
    Als de gebruiker naar "Samenwerkingsverband" navigeert
    En als de gebruiker "Nijmegen" in het dropdownveld selecteert
    En hij op de button verder drukt
    Dan wordt de gemeentenaam Nijmegen getoond
    Als de gebruiker op de "Persoonsgegevens historie" tab klikt
    Dan zijn de volgende blokken zichtbaar:
      | Opschorting persoonslijst   |
      | Persoonsgegevens - historie |

  Scenario: Samenwerkingsverband Rijk van Nijmegen annuleert wisseling van Berg en dal naar Nijmegen
    Als de gebruiker naar "Samenwerkingsverband" navigeert
    En als de gebruiker "Nijmegen" in het dropdownveld selecteert
    En hij op de button annuleren drukt
```
Ieder scenario is als het ware een uit te voeren test. 
Dankzij de Cucumber/Gherkin plugin worden de feature files goed leesbaar. De woorden die in Gherkin een speciale betekenis hebben worden 
dan blauw gekleurd. Bijvoorbeeld 'Functionaliteit', 'Scenario', 'Gegeven' en 'Wanneer'. Je kunt Cucumber ook instellen in het Engels, 
maar wij hebben voor Nederlands gekozen dankzij de '#language: nl' aan het begin. 
Vanaf ieder blauw 'code'woord begint een nieuwe stap in het scenario.

## Stap 2: het maken van een algemene cucumber klasse
Om de stappen in de scenario's te kunnen uitvoeren, moeten deze gekoppeld worden aan testcode, die daadwerkelijk iets 'doen' of 'controleren' in de applicatie. 
Daarvoor moet Cucumber 'weten' welke feature files bij welke 'Step definitions' horen. Step definitions zijn klassen waarin de verschillende stappen
met behulp van java code worden uitgevoerd. Dankzij annotaties worden de stappen gekoppeld aan de woorden uit de Gherkin syntax (Gegeven - Wanneer - Dan). 
Maar ook moet Cucumber centraal weten in welke mappen de feature files en step definition klassen gevonden kunnen worden. 
Dit doe je door middel van een algemene Cucumber test klasse met annotaties die de configuratie verzorgen.
Deze klasse heet 'CucumberIT.java' en kan er zo uitzien (in de samenwerkingsverbandapiv001 project):
- Path:samenwerkingsverband-api-v001
    - /src/test/java/nl/bkwi/samenwerkingsverbandapiv001/bdd/CucumberIT.java
```
package nl.bkwi.samenwerkingsverbandapiv001.bdd;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.spring.CucumberContextConfiguration;
import nl.bkwi.samenwerkingsverbandapiv001.SamenwerkingsverbandApiV001Application;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@CucumberContextConfiguration
@SpringBootTest(classes = SamenwerkingsverbandApiV001Application.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@RunWith(Cucumber.class)
@CucumberOptions(
    plugin = {"pretty"},
    glue = {"nl.bkwi.samenwerkingsverbandapiv001"},
    features = {"src/test/resources"})
public class CucumberIT {

}
```
Dankzij de annotatie @CucumberOptions en de directories onder glue en features weet het framework waar de betrokken informatie te vinden is. 

## Stap 3: van feature files naar step definitions
Zoals gezegd, bij alle stappen in de scenario's moet code geschreven worden. Wanneer je een nieuwe stap toevoegd in de feature file, die nog geen implementatie heeft, 
dan zie je dat doordat deze stap licht gemarkeerd wordt. Je kunt dan op het gele lampje klikken en kiezen voor 'Create step definition'. 
Vervolgens kun je kiezen uit een bestaande Step Definition klasse of voor het maken van een nieuwe klasse. De klassse naam eindigt op '.StepDefinitions.java'

In de Step Definition klasse wordt dan automatisch het Gherkin woord uit de stap toegevoegd als annotatie met daarbij de stap in tekst en een lege methode.
Deze kun je vervolgens zelf verder invullen met code. 
Wij schrijven bij regression-test en samenwerkingsverband-api-v001 deze code bij de teststappen met behulp van jUnit en Rest-Assured. 

Bijvoorbeeld, uit het project "regression-test": 
- Path: 
    - regression-test/inkijk/src/test/java/nl/bkwi/regression/EenLoginSteps.java
```
  Gegeven("^er is ingelogd met een veenendaal_handhaven account$", () -> {
        task.login(SAMENWERKINGSVERBAND_VEENENDAAL);
        task.selectTask(GSD_HANDHAVEN);
      });
   Als("^hij dan BSN \"([^\"]*)\" bevraagt$", (String bsn) -> {
          task.openDossier(bsn);
        });
   Dan("^wordt de gemeentenaam Renswoude getoond", () -> {
          $(By.id("weergaveGemeente")).shouldBe(Condition.visible);
          assertEquals("Renswoude", searchpanel.getSelectedGemeentenaam());
        });
```
Bijvoorbeeld, uit het project "samenwerkingsverband-api-v001":
- Path:samenwerkingsverband-api-v001
    - /src/test/java/nl/bkwi/samenwerkingsverbandapiv001/bdd/GemeentesInSamenwerkingsverbandStepdefs.java
```
 @Gegeven("^gemeentes in samenwerkingsverband Veenendaal opvragen en valideer de responseBody")
  public void gemeentes_in_samenwerkingsverband_Alkmaar_opvragen_en_valideer_de_responseBody()
      throws IOException, JSONException {
    restAssurdRequist(USERDNVALUEVEENENDAAL)
        .then()
        .assertThat()
        .statusCode(200);
    assertVerwachteGebruikersAlsInJSONFile("/expected-responses/samenwerkingVerbandVeendaal.json",
        USERDNVALUEVEENENDAAL);
  }
```
Je ziet dat de tekst achter @Gegeven, @Wanneer, @Dan en @En overeenkomt met de tekst in de feature file. 
Het verschil is dat er in de StepDefinitions gebruik gemaakt is van variabelen, zoals in dit geval BSN. 
Dit omdat de Step dan voor alle mogelijke BSN's die je wilt testen bruikbaar is.


#### Gebruik van Rest-Assured 

In de step definitions maken we gebruik van Rest-Assured dependencie om de testen te kunnen uitvoeren tegen de applicatie.

Rest-Assured:

Met behulp van Rest-assured kunnen we Http requesten sturen en de response opvangen en verifiëren. Zo kan je
dankzij Rest-assured bijvoorbeeld aangeven om welke soort request het gaat (POST of GET bijvoorbeeld) en om welke headers
en welke body er aan de request worden meegegeven.<br>
Deze test omvat het RestAssured tests met behulp van  Query-parameters<br>


### Gebruiken van expected-responses file
- Path:
    - samenwerkingsverband-api-v001/src/test/resources/expected-responses

om de expected-response file te gebruiken hebben we vier java methoden gebruikt
``` java
@Gegeven("^gemeentes in samenwerkingsverband rvnijmegen_handhaven opvragen en valideer de responseBody")
  public void gemeentes_in_samenwerkingsverband_rvnijmegen_handhaven_opvragen_met_verkeerde_UserDn()
      throws IOException, JSONException {
    restAssurdRequist(USERDNVALUERVNIJMEGEN_HANDHAVEN)
        .then()
        .assertThat()
        .statusCode(200);
    assertVerwachteGebruikersAlsInJSONFile("/expected-responses/samenwerkingVerbandrvnijmegen.json",
        USERDNVALUERVNIJMEGEN_HANDHAVEN);
  }
```

De vier java methoden zijn te vinden in:
samenwerkingsverbandapiv001/bdd/GemeentesInSamenwerkingsverbandStepdefs.java

``` java
 private Response restAssurdRequist(String usderDanValue) {
    RestAssured.baseURI = BASEURL;
    RequestSpecification request = RestAssured.given();
    Response response = request
        .queryParam("userDn", usderDanValue)
        .get("/");
    return response;
  }
```

``` java
  private void assertVerwachteGebruikersAlsInJSONFile(String expectedGebruikersJSONFile,
      String usderDanValue)
      throws IOException, JSONException {
    String body = restAssurdRequist(usderDanValue).asString();
    String expectedJSON = readExpectedJSONFromFile(expectedGebruikersJSONFile);
    JSONAssert.assertEquals(expectedJSON, body, false);
  }
```

``` java
  private String readExpectedJSONFromFile(String pathRelativeToSrcTestResources)
      throws IOException {
    InputStream inputStream = GemeentesInSamenwerkingsverbandStepdefs.class.getResourceAsStream(
        pathRelativeToSrcTestResources);
    String expectedJSON = readFromInputStream(inputStream);
    return expectedJSON;
  }
```

``` java
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
```

# Testen

De testen worden per onderdeel in een eigen feature-file geschreven.<br>
Op dit moment zijn de volgende testen aangemaakt:

#### 1- BDD End-to-end testen

- project:regression-test
  - Url: https://office.bkwi.nl/gerrit/#/admin/projects/regression-test
  - stepdefinitions
    - path: regression/inkijk/cucumber/EenLoginSteps.java
  - Feature-file
    - path: regression-test/inkijk/src/test/resources/cucumber/inkijk

| BDD-test                                                       | Feature-file                                                   | Omschrijving                                                                                                       |
|----------------------------------------------------------------|----------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------|
| Samenwerkingsverband GSD Rijk van Nijmegen                     | Samenwerkingsverband-GSD-Rijk-van-Nijmegen                     | Samenwerkingsverband Rijk van Nijmegen heeft alle taken overgenomen van een aantal gemeentes waaronder Berg en Dal |
| Samenwerkingsverband WGS Zutphen                               | Samenwerkingsverband-WGS-Zutphen                               | Er wordt ingelogd in WGS samenwerkingsverband Zutphen met een gebruiker met 'ou=wgs' (WGS FR2046)                  |
| Samenwerkingsverband-GSD-Veenendaal                            | Samenwerkingsverband-GSD-Veenendaal-met-rede-van-opvragings    | Er wordt ingelogd in GSD samenwerkingsverband Veenendaal zonder whitelist check BSN nummer                         |
| Samenwerkingsverband-WGS-Zutphen                               | Samenwerkingsverband-WGS-Zutphen-met-rede-van-opvragings       | Er wordt ingelogd met wgs_fr2027 in WGS samenwerkingsverband Zutphen zonder whitelist check BSN nummer             |
| samenwerkingsverband-niet-werkzaam-zijn-onder-niet-gsd.feature | samenwerkingsverband-niet-werkzaam-zijn-onder-niet-gsd.feature | voor een gebruiker die niet onder 'ou=gsd' of 'ou=wgs' valt, moet het samenwerkingsverband niet werkzaam zijn      |
| samenwerkingsverband-GSD-Zonder-Whitelist.feature              | samenwerkingsverband-GSD-Zonder-Whitelist.feature              | Er wordt ingelogd in GSD samenwerkingsverband zonder whitelist check BSN nummer                                    |
| samenwerkingsverband-WGS-Zonder-Whitelist.feature              | samenwerkingsverband-WGS-Zonder-Whitelist.feature              | Er wordt ingelogd met wgs_fr2027 in een WGS samenwerkingsverband zonder whitelist met een BSN-nummer check         |

#### 2- De Rest-Assured-testen

- Project: samenwerkingsverband-api-v001:
  - Url: https://gitlab.bkwi.nl/bp/pm/ba/services-inkijk/samenwerkingsverband-api-v001
  - stepdefinitions
    - path: samenwerkingsverbandapiv001/integration/bdd/GemeentesInSamenwerkingsverbandStepDefs.java
  - Feature-file
    - path: samenwerkingsverband-api-v001/src/test/resources/seviceGemeentesInSamenWerking.feature

| BDD-test                                        |Feature-file | Omschrijving                                  |
|-------------------------------------------------|---|-----------------------------------------------|
| de wgs en gsd gemeentes-in-samenwerkingsverband |seviceGemeentesInSamenWerking.feature| gsd samenwerkingsverband Veenendaal           |
| de wgs en gsd gemeentes-in-samenwerkingsverband |seviceGemeentesInSamenWerking.feature| gsd samenwerkingsverband rvnijmegen_handhaven |
| de wgs en gsd gemeentes-in-samenwerkingsverband |seviceGemeentesInSamenWerking.feature| wgs samenwerkingsverband Zutphen              |

[back to main](../README.md) |
[previous](./13_Unit_test_dekking.md) |
[next](./10_Overzicht_configuratie_gitlab_k8s_omgeving.md)



