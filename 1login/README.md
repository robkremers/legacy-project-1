# Project: porting the 1Login functionality from SI Classic to SI Upgrade

# Doel

BRP-bevragingen door gemeentelijke samenwerkingsverbanden onder mandaat 
moeten correct gelogd worden, zodat we hierop controles uit kunnen voeren.

# Introductie

VNG Realisatie heeft aangegeven dat de huidige werking van 1Login in Suwinet-Inkijk Classic ook in Suwinet-Inkijk Upgrade gerealiseerd moet worden. Vanuit VNG Realisatie is dit een randvoorwaarde om de GSD’s over te laten gaan op Upgrade.

Een (groot) aantal gemeenten maakt nu al gebruik van Upgrade voor WGS taken. Een aantal gemeentelijke samenwerkingsverbanden wil dit ook. Om de bevragingen correct te kunnen loggen (ten behoeve van RvIG), moet de 1Login-functionaliteit voor hen nu al in Upgrade gerealiseerd worden.

Het gaat om de volgende vormen van samenwerking:
- Gemeentelijke samenwerkingsverbanden waarbij de beslissingsbevoegdheid en verantwoordelijkheid voor de uitvoering van taken bij de aangesloten gemeente blijft. Ofwel gemeenten die op basis van de Wet gemeenschappelijke regeling (WGR) een samenwerkingsvorm zijn aangegaan (art. 8 lid 2 ev WGR), maar waarvoor geen openbaar lichaam (art. 8 lid 1 Wgr) [1]is opgericht.
- Samenwerkingsverbanden die gemeenten op basis van een privaatrechtelijke dienstverleningsovereenkomst (art 7:400 BW) zijn aangegaan, die de uitvoering van taken aan een samenwerkingsvorm of andere gemeente hebben opgedragen of uitbesteed.


# Content

- [Legenda](./documentatie-1Login/01_Legenda.md)
- [Overzicht functionele eisen](./documentatie-1Login/02_Overzicht_functionele_eisen.md)
- [Overzicht oplevering](./documentatie-1Login/03_Overzicht_oplevering.md)
- [Analyse huidige situatie SI Classic](./documentatie-1Login/04_Bescrijving_huidige_situatie_SI_Classic.md)
- [Beschrijving Whitelist Handling in SI Upgrade](./documentatie-1Login/05_Beschrijving_Whitelist_Handling_SI_Upgrade.md)
- [Implementatie samenwerkingsverband-api-v001](./documentatie-1Login/06_Implementatie_samenwerkingsverband_api_v001.md)
- [Implementatie functionaliteit regression-test / Jenkins pipeline](./documentatie-1Login/07_Implementatie_functionaliteit_regression_test.md)
- [Inkijk_upgrade](./documentatie-1Login/08_Inkijk_upgrade.md)
- [Unit_test_dekking.md](./documentatie-1Login/13_Unit_test_dekking.md)
- [BDD End-to-end en Rest-Assured-testen](./documentatie-1Login/09_BDD_End-to-end_testen_en_Rest-Assured-testen.md)
- [Overzicht configuratie samenwerkingsverband project in Gitlab / kubernetes](./documentatie-1Login/10_Overzicht_configuratie_gitlab_k8s_omgeving.md)
- [Overzicht van Gitlab / kubernetes met betrekking tot project 1Login](./documentatie-1Login/11_Overzicht_gitlab_k8s_omgeving.md)
- [Database test data en accounts t.b.v. integratietests](./documentatie-1Login/12_Database_test_data_tbv_integratietests.md)