# Implementatie van nieuwe functionaliteit in project regression-test t.b.v. 1Login in de Jenkins bouwstraat.

[[_TOC_]]

## Introductie

In project regression-test zijn de diverse tests van SI Upgrade geïmplementeerd.  
Daarnaast wordt in module **$ws/regression-test/k8s-pipeline** de werking van het project in de bouwstraat geconfigureerd.  
Dit hoofdstuk richt zich meer op het laatste.


## Introductie

Voor de lokale uitvoering van de combinatie van de sas-functionaliteit en de samenwerkingsverband-api-v001 wordt de volgende
file aangepast:
- $ws/sas/docker/docker-compose.yml

Deze file zou handmatig gekopieerd worden moeten naar:
- $ws/regression-test/scripts/docker-compose.yml

Echter: dit zorgt er niet voor, dat in de Jenkins bouwstraat de functionaliteit aan elkaar gekoppeld wordt.
Dit vindt plaats in:
- $ws/regression-test/k8s-pipeline/

De onderliggende documentatie is gebaseerd op overleg met Gert Jan Kersten en zal mogelijk nog verder uitgebreid worden.  
Voor de goede orde: de Jenkins bouwstraat is d.m.v.scripts in zowel sas als regression-test geïmplementeerd, voor de 
respectievelijke projecten.
Een compleet overzicht is het volgende NIET.

## Implementatie

### Update van docker-compose.yml in sas en regression-test

**$ws/sas/docker/docker-compose.yml** wordt aangemaakt door uitvoer van **sas/docker/createDockerEnv.sh**.  
Dus een wijziging in **$ws/sas/docker/docker-compose.yml** moet in **sas/docker/createDockerEnv.sh** geïmplementeerd
worden.

In **$ws/sas/docker/docker-compose.yml** is toegevoegd:

#### Ten behoeve van database suwinetinkijk:

```yaml
  xmysql-suwinetinkijk:
    image: "markuman/xmysql:0.4.2"
    expose:
      - "80"
    entrypoint: /bin/sh
    command: [ "-c", "/opt/docker_utils/wait-for-it.sh database-suwinetinkijk:3306 -- echo 'Database up' && cd /usr/src/app/ && node index.js --host database-suwinetinkijk --password bkwi --database suwinetinkijk --user suwinetinkijk --apiPrefix /xmysql-suwinetinkijk/api/ --portNumber 80 --ipAddress 0.0.0.0" ]
    depends_on: [ 'database-suwinetinkijk' ]
    volumes:
      - ./docker_utils:/opt/docker_utils:ro

  database-suwinetinkijk:
    image: "${REGISTRY}/database_suwinetinkijk:${TAG}"
    expose:
      - "3306"
    ports:
      - "3314:3306"
    healthcheck:
      test: ["CMD", "mysqladmin" ,"ping", "-h", "localhost"]
      interval: 30s
      timeout: 5s
      retries: 1

# En waar nodig is de depends-on database-suwinetinkijk toegevoegd:
    depends_on:
      ..
      - database-suwinetinkijk

```

#### Ten behoeve van samenwerkingsverband-api-v001:

```yaml
  samenwerkingsverband-service:
     image: "gitlab.bkwi.nl:4567/bp/pm/ba/services-inkijk/samenwerkingsverband-api-v001:latest"
     expose:
       - "8080"
     ports:
       - "9090:8080"
     depends_on:
       - database-useradmin
       - database-suwinetinkijk
```

Normaliter is hier tevens aanwezig:
```yml
  openam:
    image: "${REGISTRY}/openam:${TAG}"
    expose:
      - "9091"
```

Dit is momenteel in **sas/docker/createDockerEnv.sh** op commentaar gezet, wegens 'bouwwwerkzaamheden' van SST.

Deze file **$ws/sas/docker/docker-compose.yml** is gekopieerd naar:
- $ws/regression-test/scripts/docker-compose.yml

### Implementatie van de databases t.b.v. de bouwstraat

De implementatie voor database useradmin is reeds aanwezig.  
De toepassing van database suwinetinkijk is nieuw voor SI Upgrade en moet dus toegevoegd worden.  
Dit moet toegevoegd worden aan:
- $ws/regression-test/k8s-pipeline/manifest_templates/createKubernetesTemplate.sh
    - De volgende snippet staat op regel 3.

```shell
DATABASES="
  account \
  dossier \
  notification \
  report \
  useradmin \
  suwinetinkijk \
"
```
In de bovenstaande snippet is **suwinetinkijk** toegevoegd.  
Gevolg:
- 2 nieuwe pods / services worden toegevoegd (xmysql en database-suwinetinkijk) na runnen van het script.

Als alles goed is dan is dit alles voor wat betreft de databases t.b.v. docker.  
Als het script createKubernetesTemplate.sh uitgevoerd wordt dan wordt op basis van file **database.template**
voor de databases een pod en een service gedefinieerd in file **database.sbp.yaml.tmpl**.  
Deze file kan dan later via de **$ws/regression-test/k8s-pipeline/Makefile** aangeroepen worden.

### Implementatie van samenwerkingsverband-service

#### Opzet implementatie

Deze service start (zie bovenstaande docker-compose snippet) een pod op, op basis van docker image
**"gitlab.bkwi.nl:4567/bp/pm/ba/services-inkijk/samenwerkingsverband-api-v001:latest"**.

Implementatie-methode:
- Find in Gitlab:
    - https://gitlab.bkwi.nl/bp/pm/ba/services-inkijk/samenwerkingsverband-api-v001/-/pipelines
        - --> Find the pod and service k8s definition
          - Alternatief: definieer op basis van analoge pod / service definities.
        - --> add to a new template in **$ws/regression-test/k8s-pipeline/manifest_templates**
        - Als voorbeeld bekijk de bestaande templates: hierin zitten de k8s definities van de processen
- De nieuwe templates moeten toegevoegd worden aan:
    - $ws/regression-test/k8s-pipeline/Makefile

Geimplementeerd is:
- $ws/regression-test/k8s-pipeline/manifest_templates/samenwerkingsverband.template

Betreffende **$ws/regression-test/k8s-pipeline/Makefile**:
- Hierin zijn relevant de tags:
    - k8s_create_standalone_resources: 
      - voor het opstarten van:
        - serviceaccounts
        - ldap
        - databases
        - proxy
        - splunk
    - k8s_create_remaining_resources:
      - voor het opstarten van:
        - xymsql
        - chromedriver
        - samenwerkingsverband

De nieuwe template voor **samenwerkingsverband-service** mag pas aangeroepen worden als de databases opgestart zijn.
Dit om te voorkomen, dat de pod niet goed opstart (de databases kunnen nog niet up zijn).
Daarom is de nieuwe template voor **samenwerkingsverband-service** toegevoegd aan tag
**k8s_create_remaining_resources:**.

#### Beschrijving

In **$ws/regression-test/k8s-pipeline** is aanwezig:
- /manifest_templates/
  - createKubernetesTemplate.sh
  - *.template
  - *.sbp.yaml.tmpl
- Makefile

- createKubernetesTemplate.sh zal op basis van de ***.template** files de overeenkomstige ***.sbp.yaml.tmpl** files aanmaken.
- In de Jenkins bouwstraat zal de Makefile uitgevoerd worden, die (als ik het goed heb) daar hetzelfde doen zal.
- Dus op basis van **samenwerkingsverband.template** zal aangemaakt worden: **samenwerkingsverband.sbp.yaml.tmpl**

Vervolgens wordt in het k8s cluster de functionaliteit opgezet op basis van de ***.sbp.yaml.tmpl** files.

[back to main](../README.md) |
[previous](./06_Implementatie_samenwerkingsverband_api_v001.md) |
[next](./08_Inkijk_upgrade.md)