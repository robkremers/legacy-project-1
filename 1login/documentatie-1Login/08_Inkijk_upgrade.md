# Beschrijving werking in inkijk upgrade

## Werking

Vanuit de GWT-frontend wordt vanuit InkijkActivity @Override public void openNewDossier(String
forSubject) { ...

via de InkijkRestClient een call gedaan naar de inkijk server. Deze call komt terecht op het
endpoint in InkijkService:

```
@Operation(description = "Create a new dossier (representing a person identified by bsn) for a specific task")
@POST
@Path("{task}/dossier")
```

POST {"bsn":"768510338", "subjectType":"PERSON"} naar:
http://localhost:8080/server/inkijk/api/0406-dummy-handhaven/VERSION-INKIJK-SERVER/dossier

Response:

```
"reference":null,"workloadReference":"ae8af22a-257e-49e2-8f68-e8ca03915543","samenwerkingsverband":"Dummy Gemeente Veenendaal"}
```

De InkijkService class is uitgebreid met een backchannel call naar een samenwerkingsverband-service.
Deze service draait lokaal binnen het docker composed netwerk. De betreffende url is:

    http://samenwerkingsverband-service:8080/gemeentes-in-samenwerkingsverband

gedefinieerd in inkijk.properties

Code SamenwerkingsverbandService:

```
 public SamenwerkingsverbandResult retrieveSamenwerkingsverband() {
        RestTemplate restTemplate = new RestTemplate();
        String userDn = userProvider.get().getDn().toString();
        String samenwerkingsverbandenUrl = url + "?userDn={dn}";
        ResponseEntity<SamenwerkingsverbandDto> response = restTemplate.getForEntity(samenwerkingsverbandenUrl, SamenwerkingsverbandDto.class, userDn);
        Builder builder = SamenwerkingsverbandResult.builder();
        response.getBody().getGemeentes().stream()
            .sorted(Comparator.comparing(GemeenteDto::getGemeente))
            .forEach(x -> {
                Gemeente gemeente = new Gemeente();
                gemeente.setGemeente(x.getGemeente());
                gemeente.setVoorkeursgemeente(x.getVoorkeursgemeente());
                builder.addGemeenteList(gemeente);
            });
        return builder.build();
    }
}
```

Hiervoor moest wel RestTemplate als dependency meegenomen worden. Dus in de pom.xml van het
inkijk/webapp project:

```
<dependency>
<groupId>org.springframework</groupId>
<artifactId>spring-web</artifactId>
<version>4.0.2.RELEASE</version>
</dependency>
```

De versie is bewust gekozen om in lijn te zijn met de overige Spring libraries die in inkijk upgrade
gebruikt wordt.

De call vindt zowel plaats bij het openen van een nieuw dossier alswel bij het opvragen van
gemeentes binnen een samenwerkingsverband.

Bij het openen van een nieuw dossier wordt er een DataReference geretourneerd. Deze is uitgebreid
met Optional<String> getSamenwerkingsverband(); Als er sprake is van een samenwerkingsverband (dan
is de lijst met gemeentes dus NIET leeg: gemeentes-in-samenwerkingsverband) en wordt het veld
samenwerkingsverband in DataReference.class gevuld met de organisatie van de gebruiker.

Hierop wordt getriggered in InkijkActivity:

service.startNewDossier(presenterState.taskId(), forSubject, new ReferenceHandler() { @Override
public void onResult(final SearchReference reference) {

        if (reference.getReferenceId().isPresent() && reference.getSamenwerkingsverband().isPresent()) {
          loadGemeentenInSamenwerkingsverband(reference.getReferenceId().get());
        }

loadGemeentenInSamenwerkingsverband(...) in InkijkActivity zorgt voor het ophalen van de gemeentes
in het samenwerkingsverband
( {"
gemeenteList":[{"gemeente":"Dummy Gemeente Veenendaal","voorkeursgemeente":0},{"gemeente":"Renswoude","voorkeursgemeente":1},{"gemeente":"Rhenen","voorkeursgemeente":0}]
} )
en het tonen van de SamenwerkingsverbandWidget.

Het contract van achterliggende springboot
service: http://localhost:9090/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config#/

We werken contract first dat betekent dat het DTO model gegenereerd wordt obv. een open-api
contract.

[back to main](../README.md) |
[previous](./07_Implementatie_functionaliteit_regression_test.md) |
[next](./13_Unit_test_dekking.md)