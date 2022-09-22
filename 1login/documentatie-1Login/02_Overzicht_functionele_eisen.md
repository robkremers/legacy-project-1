# Overzicht van functionele eisen / implementatie voor de implementatie van 1Login voor SI Upgrade

## Introductie

Momenteel worden diverse documenten geproduceerd om aan te geven wat de achtergrond van het project
is en welke functionele eisen gesteld worden.  
In dit project wordt gepoogd om alles op een rij te zetten.

Momenteel is 1Login geïmplementeerd in SI Classic.  
De bedoeling is om dit (zo veel mogelijk) één op één te implementeren in in SI Upgrade. Hierbij moet
uiteraard rekening gehouden met de verschillen in implementatie tussen SI Classic en SI Upgrade.

## Overzicht van eisen

### De verschillende mogelijkheden om samenwerkingsverbanden op te zetten en hun consequenties

Gemeentes kunnen in een samenwerkingsverband geplaatst worden. Zie voor de details 
[Bescrijving huidige situatie SI Classic](./04_Bescrijving_huidige_situatie_SI_Classic.md).

Het is mogelijk:
- dat een samenwerkingsverband opgezet wordt waaronder gemeentes vallen
  en waarbij een gemeente uitvoerend wordt.  
- dat een aantal gemeentes in een samenwerkingsverband zitten, maar er is geen separaat
  samenwerkingsverband geconfigureerd. Hierbij is weer een gemeente uitvoerend.

Voor een overzicht van de huidige situatie zie [samenwerkingsverbanden 071021 PRD](./documents/samenwerkingsverbanden_071021_PRD.xlsx)

Technisch gezien zijn (waarschijnlijk) nog meer situaties mogelijk.
Waar het om gaat is, dat alleen een account van een uitvoerende gemeente de functionaliteit
van 1Login te zien krijgen mag.  
Hierin worden dan alleen de samenwerkende gemeentes getoond, niet een eventueel samenwerkingsverband.

### Het activeren van 1Login in SI Upgrade door middel van toekenning van een account aan een uitvoerende gemeente / samenwerkingsverband.

In SI Classic zal een account, dat toegekend is aan een uitvoerende gemeente of een samenwerkingsverband, 
automatisch voor alle taken een gemeente kiezen.  
Dus ook indien het een taak betreft, die voor hun gemeente uitgevoerd worden moet en niet voor het samenwerkingsverband.  
Het is de bedoeling om de functionaliteit 1-op-1 te porten, afgezien van evt. technische problemen.

### De werking van 1Login in SI Upgrade

1Login werkt in SI Classic via een dropdown list (zie [samenwerkingsverbanden 071021 PRD](./documents/samenwerkingsverbanden_071021_PRD.xlsx))

Het idee is om in SI Upgrade het whitelist tussenscherm te kopiëren en hierin het dropdown veld te vullen met
de gemeentes in het samenwerkingsverband.

### De implementatie van een samenwerkingsverband voor meerdere gemeentetaken.

Onder gemeentetaken valt onder andere:
- De Gemeentelijke Sociale Dienst (GSD)
- De uitvoering van de Wet Gemeentelijke Schuldenregeling (WGS)
- Burgerzaken
- GB (?)

Momenteel is in SI Classic een samenwerkingsverband alleen beschikbaar voor de GSD.  
Onderdeel van de functionele eisen is om dit in het kader van project 1Login ook beschikbaar te maken voor WGS.
Mogelijk worden in de toekomst ook Burgerzaken en GB toe te voegen.

### De workflow van Whitelist en 1Login in SI Upgrade

Dit wordt in het volgende document uitgelegd: [1Login Flow diagram v0.2.pdf](./documents/1_Login_Flow_diagram_v0.2.pdf)

Hierbij moet worden opgemerkt, dat de gemeente voor een account een Distinguished Name (DN) gebruikt, b.v. voor gebruiker:
- Naam: Babet van Alkmaar 
- Username: 0001_gsd_alkmaar_we
- DN: cn=babet van alkmaar,ou=alkmaar,ou=gsd,o=suwi,c=nl

De naam van de plaats of het samenwerkingsverband zal in de eerste Organisational Unit (ou) gebruikt worden 
(feitelijk de huidige methode).

[back to main](../README.md) |
[previous](01_Legenda.md) |
[next](./03_Overzicht_oplevering.md)
