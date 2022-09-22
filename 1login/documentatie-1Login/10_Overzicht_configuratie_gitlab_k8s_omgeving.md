# Overzicht van de configuratie van samenwerkingsverband projecten in de gitlab / kubernetes omgeving.

[[_TOC_]]

## Introductie

In dit hoofdstuk wordt besproken welke configurationele informatie toegevoegd is om de applicatie in de Gitlab / 
Kubernetes (k8s) omgeving te laten functioneren.

## Resources

Zie voor verdere achtergrond:
1. [gitlab-ci documentatie](https://gitlab.bkwi.nl/bkwi/beheer/infraops/ontwikkeltooling/gitlab-ci)
2. [Beheer InfraOps Docs](https://gitlab.bkwi.nl/bkwi/beheer/infraops/docs)
3. [Baeldung: Dockerizing Java Apps using Jib](https://www.baeldung.com/jib-dockerizing#:~:text=Jib%20is%20an%20open%2Dsource,need%20to%20write%20a%20dockerfile.)
4. [Spring Cloud – Zipkin and Sleuth](https://howtodoinjava.com/spring-cloud/spring-cloud-zipkin-sleuth-tutorial/)

De configuratie zoals in dit hoofdstuk beschreven is deels op bovenstaande resources gebaseerd.  
Met name de [gitlab-ci documentatie](https://gitlab.bkwi.nl/bkwi/beheer/infraops/ontwikkeltooling/gitlab-ci) wordt 
zoveel mogelijk door InfraOps (Gert Jan Kersten) bijgehouden.

## Opzet applicatie

### File .gitignore 

De huidige configuratie in .gitignore is als volgt en wordt voor iedere java spring applicatie gekopieerd.

### De application*.yml files

De application*.yml files zijn, indien noodzakelijk, aanwezig in:
- samenwerkingsverband-api-v001/src/main/resources/config/
  - application.yml
  - application-dev.yml
- samenwerkingsverband-api-v001/src/test/resources/
  - /config
    - application-bdd.yml
    - application-test.yml

Het gebruik hiervan is beschreven in [Implementatie samenwerkingsverband-api-v001#Uitvoering](5_Implementatie_samenwerkingsverband_api_v001.md#uitvoering)

In application-dev.yml wordt vaak een server port gedefinieerd, die afwijkt van 8080 en 8081 om niet in conflict te 
komen met andere applicaties, die deze port nummers gebruiken (Suwinet Inkijk Upgrade).

### File .gitlab-cy.yml

Deze file zal normaliter standaard settings hebben:

```yaml
include:
  - project: "bkwi/beheer/infraops/ontwikkeltooling/gitlab-ci"
    file: "gitlab-ci-yml/microservice-deploy.yml"

variables:
  SUPPORT_LATEST_TAG: "true"
  SKIP_SERENITY: "true"
```

### directory .gitlab

Deze directory bevat het volgende de file CODEOWNERS:
```shell
.gitlab-ci.yml  @codeowners
.gitlab/CODEOWNERS @codeowners
```

### Toevoeging van jib-maven-plugin in pom.xml t.b.v. het builden van Docker images

- pom.xml:
  - plugin **jib-maven-plugin** toegevoegd

Doel:
- Jib is an open-source Java tool maintained by Google for building Docker images of Java applications. 
It simplifies containerization since with it, we don't need to write a dockerfile.
- In de bouwstraat is vormt jib de kern van de stap **Create-image**.

```xml
        <plugin>
          <groupId>com.google.cloud.tools</groupId>
          <artifactId>jib-maven-plugin</artifactId>
          <version>3.1.4</version>
          <configuration>
                    <to>
                        <image>gitlab.bkwi.nl:4567/inkijk/samenwerkingsverband/${project.artifactId}</image>
                    </to>
                </configuration>
            </plugin>
          <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.8.1</version>
            <configuration>
              <source>11</source>
              <target>11</target>
            </configuration>
          </plugin>
          <plugin>
```

### Toevoeging van spring-boot-starter-actuator in pom.xml t.b.v. Health Check

In application.yml is zoals genoemd in de **Opzet applicatie** een basis configuratie aanwezig.  
Voor de werking van deze configuratie moet in **pom.xml** het volgende aanwezig zijn:

```xml
    <!-- On behalf of health checks -->
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
```

Als de applicatie draait (met server.port = 8082; zie application-dev.yml) dan kan een health check uitgevoerd worden.
Dit zal ook plaats vinden in de bouwstraat. De health check kan ook lokaal uitgevoerd worden.
In het onderstaande voorbeeld is aangenomen, dat het dev-profile gebruikt wordt (zie boven):

```shell
$ curl localhost:8082/health | jq .
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100    15    0    15    0     0     82      0 --:--:-- --:--:-- --:--:--    82
{
  "status": "UP"
}
```

### Toevoeging van Zipkin and Sleuth ten behoeve van trace logging

Voor meer informatie betreffende Zipkin en Sleuth wordt verwezen naar ref. 4.  
Zipkin / Sleuth wordt toegepast om trace logging te introduceren. De functionaliteit is reeds aanwezig in de Gitlab / 
Kubernetes bouwstraat. Toevoeging van de configuratie is bedoeld om hierop in te pluggen.

#### Configuratie van Zipkin / Sleuth

##### pom.xml

```xml
      <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-sleuth</artifactId>
        <version>3.1.2</version>
      </dependency>

      <dependency>
        <groupId>ch.qos.logback.contrib</groupId>
        <artifactId>logback-jackson</artifactId>
        <version>0.1.5</version>
      </dependency>
      <dependency>
        <groupId>ch.qos.logback.contrib</groupId>
        <artifactId>logback-json-classic</artifactId>
        <version>0.1.5</version>
      </dependency>
```

##### logback.xml

Ten behoeve van correct printen van logging is **samenwerkingsverband-api-v001/src/main/resources/logback.xml**
toegevoegd. Deze configuratie file is bedoeld voor de Jackson dependencies, aangegeven in het bovenstaande.

```xml
<configuration>
  <!-- LOG INFO as JSON String -->
  <appender name="STDOUT_JSON" class="ch.qos.logback.core.ConsoleAppender">
    <layout class="ch.qos.logback.contrib.json.classic.JsonLayout">
      <jsonFormatter
        class="ch.qos.logback.contrib.jackson.JacksonJsonFormatter">
        <prettyPrint>false</prettyPrint>
      </jsonFormatter>
      <timestampFormat>yyyy-MM-dd' 'HH:mm:ss.SSS</timestampFormat>
      <appendLineSeparator>true</appendLineSeparator>
    </layout>
  </appender>

  <root level="info">
    <appender-ref ref="STDOUT_JSON"/>
  </root>

  <logger name="reactor.netty.http.client" level="warn"/>
</configuration>
```

##### Configuratie van class SamenwerkingsverbandRestController

In het onderstaande wordt de parameter **X_CORRELATION_ID** doorgegeven aan de tracing.  
Deze parameter wordt in sas bij elk REST GET request naar samenwerkingsverband-api gegenereerd.
```java
  public SamenwerkingsverbandDto getGemeentesInSamenwerkingsverband(
    @RequestParam(name = "userDn") String userDn,
    @RequestHeader(X_CORRELATION_ID) String correlationId){
        MDC.put(X_CORRELATION_ID,correlationId);
            // code
        }
```

Ter illustratie de overeenkomstige code in sas; class SamenwerkingsverbandService.  
Dus zowel in sas als in samenwerkingsverband-api wordt de parameter **X-CORRELATION-ID** gelogd, in verbinding
met de **DN**, waarvoor het samenwerkingsverband gevraagd wordt.

```java
    final RestTemplate restTemplate = new RestTemplate();
    final HttpHeaders httpHeaders = new HttpHeaders();

    String correlationId = UUID.randomUUID().toString();

    httpHeaders.set("X-Correlation-ID", correlationId);
    final HttpEntity<String> entity = new HttpEntity<>(httpHeaders);

    String userDn = userProvider.get().getDn().toString();
    log.info(String.format("Samenwerkingsverband request voor DN %s met X-CORRELATION-ID %s", userDn, correlationId));
    String samenwerkingsverbandenUrl = url + "?userDn={dn}";

    ResponseEntity<SamenwerkingsverbandDto> response = restTemplate.exchange(
            samenwerkingsverbandenUrl,
            HttpMethod.GET,
            entity,
            SamenwerkingsverbandDto.class,
            userDn);
```


### Directory kubernetes

Deze bevat configuratie om de algemene kubernetes opzet in de bouwstraat te customizen.

Opzet:
kubernetes
- base
  - bkwi.properties
  - kustomization.yml
- overlays
  - **Bevat content, die properties in gewone properties files, zoals application.yml, overschrijven**.
  - bvv-test
    - deployment.yml
    - kustomization.yml
  - bvv-acceptatie
    - deployment.yml
    - kustomization.yml
  - bvv-productie
    - deployment.yml
    - kustomization.yml

Het bovenstaande is momenteel nog in ontwikkeling, omdat de bouwstraat nog niet vastgelegd is.

Content van kubernetes/base/kustomization.yml:

```yaml
bases:
  - git@gitlab.bkwi.nl:bkwi/beheer/infraops/ontwikkeltooling/gitlab-ci.git/kustomize-vs
configMapGenerator:
  - behavior: merge
    env: bkwi.properties
    name: 0bkwi-vars
```

Content van kubernetes/base/bkwi.properties
```shell
tier=edge-inbound
```

Voorbeeld van de invulling van deployment.yml, die de overeenkomstige content van application.yml overschrijft:
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: $(PROJECT_IN_NAME)
spec:
  template:
    spec:
      containers:
        - name: ${PROJECT}
          env:
            - name: application.datasource.suwinetinkijk.username
              value: useradmin
            - name: application.datasource.suwinetinkijk.password
              value: CopJedLidyogMet8
```

### PortForwarding ten behoeve van het exposen van databases voor BDD tests.

De BDD tests worden uitgevoerd tegen de database stubs database-useradmin en database-suwinetinkijk.
De database stubs zijn aanwezig in namespace **services-inkijk**, maar worden gerund in **Rancher**!!!
(oky: ik snap, dat dit ongelofelijk is, waarom niet in **services-inkijk**, maar Gert Jan heeft dit zo uitgelegd).  
Een overzicht van de opzet wordt hier gegeven:
- https://gitlab.bkwi.nl/bvv/k8s/-/tree/master/sbp#overzicht

De configuratie van de databases is als volgt:

- samenwerkingsverband-api-v001/src/test/resources/config/application-bdd.yml
```yaml
..
application:
  datasource:
    # Connection for database useradmin: tables gemeente_participatie, samenwerkingsverband.
    useradmin:
      jdbc-url: jdbc:mysql://localhost:3313/useradmin
      username: useradmindml
      password: bkwi
      driver-class-name: com.mysql.cj.jdbc.Driver
    # Connection for database suwinetinkijk: table profile.
    suwinetinkijk:
      jdbc-url: jdbc:mysql://localhost:3314/suwinetinkijk
      username: suwinetinkijk
      password: bkwi
      driver-class-name: com.mysql.cj.jdbc.Driver
```

Het port-forwarden is als volgt geconfigureerd:
- samenwerkingsverband-api-v001/prepare4integrationtest.shsrc
```shell
before() {
  echo Starting port-forward ...

  startPortForwarding useradmin database-useradmin 3313 3306
  startPortForwarding suwinetinkijk database-suwinetinkijk 3314 3306
}

after() {
  echo Stopping port-forward ...

  stopPortForwarding
}
```

### Peer authenticatie toevoegen voor databases useradmin / suwinetinkijk

De volgende commando's werken:
```shell
kubectl --context=eqap_am3_bto -n services-inkijk exec -it deploy/database-useradmin -- mysql -h database-suwinetinkijk -usuwinetinkijk -p
kubectl --context=eqap_am3_bto -n services-inkijk exec -it deploy/database-suwinetinkijk -- mysql -h database-useradmin -uuseradmin -p
kubectl --context=eqap_am3_bto -n services-inkijk exec -it deploy/database-suwinetinkijk -- mysql -h mysql.bto.bkwi.local -uuseradmin -p
```

Toevoegen peerauthenticatie aan de kubernetes overlays voor development en test:
```shell
apiVersion: security.istio.io/v1beta1
kind: PeerAuthentication
metadata:
  name: "database-suwinetinkijk"
  namespace: services-inkijk
spec:
  selector:
    matchLabels:
      app: database-suwinetinkijk
  portLevelMtls:
    3306:
      mode: DISABLE
---
apiVersion: security.istio.io/v1beta1
kind: PeerAuthentication
metadata:
  name: "database-useradmin"
  namespace: services-inkijk
spec:
  selector:
    matchLabels:
      app: database-useradmin
  portLevelMtls:
    3306:
      mode: DISABLE
```

### Het gebruik van Secret configuratiefiles om databases te kunnen benaderen.

Het volgende is analoog aan de opzet bij project [audit-service](https://gitlab.bkwi.nl/bvv/audit-service).
Momenteel is voor ons relevant de feature branch **feat-v2**.

Een Secret is een Kubernetes configuratie file, waarbij de inhoud encrypted is om te voorkomen,
dat b.v. database credentials uitgelezen worden kunnen door onbevoegden.  
Dit wordt voor Test, Acceptatie en Productie toegepast.


Een Secret moet allereerst gemount worden, waarna het Secret uitgelezen worden kan in de applicatie.

Opzet:
- Secret sa-db-1login is the same in Test, Acceptatie en Productie toegepast regarding setup and name.
- the data (DB_SERVER, DB_PASSWORD AND DB_USER) may have a different content.
- DB_SERVER, DB_PASSWORD AND DB_USER are base64 encoded.

```shell
# Go to the test environment
$ kubens services-inkijk
$ get secret
sa-db-1login                  generic                               3      4d10h
$ k edit secret sa-db-1login

# Please edit the object below. Lines beginning with a '#' will be ignored,
# and an empty file will abort the edit. If an error occurs while saving this file will be
# reopened with the relevant failures.
#
apiVersion: v1
data:
  DB_PASSWORD: <base64 encoded>
  DB_SERVER: <base64 encoded>
  DB_USER: <base64 encoded>
kind: Secret
metadata:
  creationTimestamp: "2022-06-17T10:37:25Z"
  labels:
    eqap.io/istio: "true"
  name: sa-db-1login
  namespace: services-inkijk
  ownerReferences:
  - apiVersion: bitnami.com/v1alpha1
    controller: true
    kind: SealedSecret
    name: sa-db-1login
    uid: eb3eebac-e107-4e3b-8801-c4029aee12cc
  resourceVersion: "170054422"
  uid: d9a751a6-ac86-40d5-a378-28b2ccc64880
type: generic

# decode as follows:
$ echo "<base64 encoded>" | base64 -D
```


- samenwerkingsverband-api-v001/kubernetes/base/deployment.yml
  - Kubernetes zal de base64 encoded property values decoden en op de aangegeven plaatsen zetten.

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: $(PROJECT_IN_NAME)
spec:
  template:
    spec:
      containers:
        - name: $(PROJECT)
          env:
            - name: DB_SERVER
              valueFrom:
                secretKeyRef:
                  name: sa-db-1login
                  key: DB_SERVER

            - name: application.datasource.useradmin.username
              valueFrom:
                secretKeyRef:
                  name: sa-db-1login
                  key: DB_USER
            - name: application.datasource.useradmin.password
              valueFrom:
                secretKeyRef:
                  name: sa-db-1login
                  key: DB_PASSWORD

            - name: application.datasource.suwinetinkijk.username
              valueFrom:
                secretKeyRef:
                  name: sa-db-1login
                  key: DB_USER
            - name: application.datasource.suwinetinkijk.password
              valueFrom:
                secretKeyRef:
                  name: sa-db-1login
                  key: DB_PASSWORD
```

- samenwerkingsverband-api-v001/src/main/resources/application.yml

Opzet:
- In application.yml worden de waarden voor de ontwikkelomgeving **services-inkijk-development** gezet.
- Deze worden door kubernetes overschreven indien waarden in secret sa-db-1login aanwezig zijn.
- In jbdc-url worden default waarden gegeven:
  - ${DB_SERVER:database-useradmin}
    - Indien 'DB_SERVER' leeg is dan wordt de default waarde 'database-useradmin' gebruikt.

```yaml
application:
  datasource:
    # Connection for database useradmin: tables gemeente_participatie, samenwerkingsverband.
    useradmin:
      # Indien
      jdbc-url: jdbc:mysql://${DB_SERVER:database-useradmin}:3306/useradmin
      username: useradmin
      password: bkwi
      driver-class-name: com.mysql.cj.jdbc.Driver
    # Connection for database suwinetinkijk: table profile.
    suwinetinkijk:
      jdbc-url: jdbc:mysql://${DB_SERVER:database-suwinetinkijk}:3306/suwinetinkijk
      username: suwinetinkijk
      password: bkwi
      driver-class-name: com.mysql.cj.jdbc.Driver
```

## Project k8s

Project [k8s](https://gitlab.bkwi.nl/bp/pm/ba/services-inkijk/k8s) bevat instructies voor de opbouw van:  
- accceptatie
- test
- productie

Momenteel is geen separate namespaces definitie voor productie aanwezig. 
Volgens Gert Jan Kersten is dat niet nodig indien de application.yml in samenwerkingsverband-api-v001 gelijk is
voor acceptatie en productie.

[back to main](../README.md) |
[previous](./09_BDD_End-to-end_testen_en_Rest-Assured-testen.md) |
[next](./11_Overzicht_gitlab_k8s_omgeving.md)