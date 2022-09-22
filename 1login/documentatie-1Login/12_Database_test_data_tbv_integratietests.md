# Overzicht test data ten behoeve van integratietests

[[_TOC_]]

## Introductie

De hier beschreven test data is bedoeld voor integratie-tests (BDD) in development en in BTO.  
Zoals eerder beschreven worden in development de database stubs gebruikt en in BTO de echte databases.  
De database content blijft hierbij hetzelfde.

## Opzet

## Test omgeving

Voor een actuele stand van zaken betreffende url en test accounts zie:  
- https://office.bkwi.nl/bkwiki/afdelingen/renp/ontwerp/bto/start

## Test data in database useradmin

```mysql
use useradmin;

select * from samenwerkingsverband;
select * from gemeente_participatie;

```

Ten behoeve van WGS zijn meerdere samenwerkingsverbanden gedefinieerd.  
Onder andere **Samenwerkingsverband Zoeterwoude Zutphen**. Test accounts worden hieronder aangegeven.

## Test data in database suwinetinkijk

```mysql
select * from suwinetinkijk.profile;
+----------------------------------------------------------------------+----------------------------------------+-----------+---------------------+---------+
| context                                                              | name                                   | value     | created             | updated |
+----------------------------------------------------------------------+----------------------------------------+-----------+---------------------+---------+
| cn=Henk VD Handhaven,ou=Dummy Gemeente Veenendaal,ou=gsd,o=suwi,c=nl | Samenwerkingsverband.voorkeursgemeente | Renswoude | 2022-07-19 00:00:00 | NULL    |
| cn=WGS FR4150028,ou=Zutphen,ou=wgs,o=suwi,c=nl                       | Samenwerkingsverband.voorkeursgemeente | Zutphen   | 2022-07-19 00:00:00 | NULL    |
+----------------------------------------------------------------------+----------------------------------------+-----------+---------------------+---------+
2 rows in set (0.01 sec)

```

## Test accounts voor WGS (lokaal)

De test accounts moeten gelinkt zijn aan gemeentes, die reeds taken / rollen hebben t.b.v. WGS.
B.v. in het geval van Zoeterwoude zie:  
- ${ws}/sas/inkijk/config/src/main/resources/definitions/organization/00000001001797839000.json
  - Hierin zijn taken / rollen gedefinieerd voor onder GSD, BZ, WGS.



```shell
# Account voor gemeente Zoeterwoude, die onderdeel is van Samenwerkingsverband Zoeterwoude Zutphen, maar
# die geen uitvoerende gemeente is.
# Na inloggen en invoeren van een BSN wordt meteen doorgegaan naar de klant gegevens.
dn: cn=WGS FR4150047,ou=Zoeterwoude,ou=wgs,o=suwi,c=nl
objectclass: top
objectclass: person
objectclass: inetOrgPerson
objectclass: organizationalPerson
uid: wgs_fr4150047
givenName: WGS
sn: FR4150047
businessCategory: BASIS_WGS
businessCategory: FR4150047
userPassword: test
createTimestamp: 20010501221501Z
ds-pwp-last-login-time: 20401110090807
pwdChangedTime: 20401110090807.654Z
mail: wgs_fr4150047@bkwi.nl

# Account voor gemeente Zutphen, die onderdeel is van Samenwerkingsverband Zoeterwoude Zutphen, maar
# die een uitvoerende gemeente is.
# Zijn dn staat geconfigureerd in suwinetinkijk.profile met als voorkeursgemeente Zutphen.
# Na inloggen wordt een tussenscherm 'Samenwerkingsverband' getoond met zichtbaar 'Zutphen'.
dn: cn=WGS FR4150028,ou=Zutphen,ou=wgs,o=suwi,c=nl
objectclass: top
objectclass: person
objectclass: inetOrgPerson
objectclass: organizationalPerson
uid: wgs_fr4150028
givenName: WGS
sn: FR4150028
businessCategory: BASIS_WGS
businessCategory: FR4150028
userPassword: test
createTimestamp: 20010501221501Z
ds-pwp-last-login-time: 20401110090807
pwdChangedTime: 20401110090807.654Z
mail: wgs_fr4150028@bkwi.nl

# Account voor gemeente Zutphen, die onderdeel is van Samenwerkingsverband Zoeterwoude Zutphen, maar
# die een uitvoerende gemeente is.
# Zijn dn staat niet geconfigureerd in suwinetinkijk.profile en heeft dus geen voorkeursgemeente.
# Na inloggen wordt een tussenscherm 'Samenwerkingsverband' getoond zonder voorkeursgemeente. 
# Dit moet in de dropdownlist geselecteerd worden.
dn: cn=WGS FR2027,ou=Zutphen,ou=wgs,o=suwi,c=nl
objectclass: top
objectclass: person
objectclass: inetOrgPerson
objectclass: organizationalPerson
uid: wgs_fr2027
givenName: WGS
sn: FR2027
businessCategory: BASIS_WGS
businessCategory: FR2027
userPassword: test
createTimestamp: 20010501221501Z
ds-pwp-last-login-time: 20401110090807
pwdChangedTime: 20401110090807.654Z
mail: wgs_fr2027@bkwi.nl

# Account voor gemeente Zwolle, die geen onderdeel is van een samenwerkingsverband.
# Na inloggen en invoeren van een BSN wordt meteen doorgegaan naar de klant gegevens.
dn: cn=WGS FR4110036,ou=Zwolle,ou=wgs,o=suwi,c=nl
objectclass: top
objectclass: person
objectclass: inetOrgPerson
objectclass: organizationalPerson
uid: wgs_fr4110036
givenName: WGS
sn: FR4110036
businessCategory: BASIS_WGS
businessCategory: FR4110036
userPassword: test
createTimestamp: 20010501221501Z
ds-pwp-last-login-time: 20401110090807
pwdChangedTime: 20401110090807.654Z
mail: wgs_fr4110036@bkwi.nl
```

[back to main](../README.md) |
[previous](./11_Overzicht_gitlab_k8s_omgeving.md)