# Overzicht van de oplevering voor 1Login

## Introductie

Voor 1Login wordt bekeken of een ingelogde account werk uitvoert voor een gemeente, die lid is van een samenwerkingsverband.
De informatie is aanwezig in twee MySQL databases:
- useradmin
- suwinetinkijk

De database content wordt exposed via een springboot api applicatie, die binnen het Suwinet Inkijk Upgrade (sas project)
aangesproken wordt. 
De exposure wordt via een OpenAPI contract vastgelegd.

Verder is binnen het sas project voor 1Login een GWT tussenscherm met achterliggende functionaliteit geïmplementeerd.

## Overzicht applicaties.

De volgende applicaties worden opgeleverd
- Gitlab ([services_inkijk](https://gitlab.bkwi.nl/bp/pm/ba/services-inkijk/) ):
  - [Project k8s](https://gitlab.bkwi.nl/bp/pm/ba/services-inkijk/k8s)
    - Dit project is door Gert Jan Kersten maintained. Zou ook door een developer maintained worden kunnen, maar
      dan is omgevingsspecifieke kennnis nodig.
    - Bevat pipeline definities voor test, acceptatie en productie.
  - [Project database-useradmin](https://gitlab.bkwi.nl/bp/pm/ba/services-inkijk/database-useradmin)
    - Verwijst naar een docker container voor een snapshot van database useradmin, gevuld met test-data.
    - Bedoeld voor BDD-tests
  - [Project database-suwinetinkijk](https://gitlab.bkwi.nl/bp/pm/ba/services-inkijk/database-suwinetinkijk)
    - Verwijst naar een docker container voor een snapshot van database suwinetinkijk, gevuld met test-data.
    - Bedoeld voor BDD-tests
  - [Project samenwerkingsverband-contract](https://gitlab.bkwi.nl/bp/pm/ba/services-inkijk/samenwerkingsverband-contract)
    - Bevat het OpenApi v3 contract. Via dit contract spreekt sas het project samenwerkingsverband-api-v001 aan.
  - [Project samenwerkingsverband-api-v001](https://gitlab.bkwi.nl/bp/pm/ba/services-inkijk/samenwerkingsverband-api-v001)
    - Spring Boot project
    - Exposure van databases useradmin en suwinetinkijk via een REST api.
- Gerrit / Jenkins:
  - sas / filter laatste versie.
    - Java-functionaliteit
    - Database content voor development en BTO.
  - regression-test / filter laatste versie.

[back to main](../README.md) |
[previous](./02_Overzicht_functionele_eisen.md) |
[next](./04_Bescrijving_huidige_situatie_SI_Classic.md)