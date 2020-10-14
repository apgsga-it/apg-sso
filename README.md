# APG|SGA SSO

## Setup

### Software

Um dieses Projekt ausführen zu können, werden folgende Tools benötigt:
* [JDK 8](https://github.com/AdoptOpenJDK/openjdk8-binaries/releases/tag/jdk8u242-b08)
* [Docker](https://www.docker.com/)
* [Docker Compose](https://docs.docker.com/compose/)

### Hosts-Einträge

Zusätzlich braucht es lokale Hosts-Einträge. Die folgenden Namen müssen für die IP `127.0.0.1` registriert werden.
* `keycloak.local`
* `angular-app.local`
* `vaadin8-app.local`

### Zugriff auf APG Entwicklungsdatenbank

Um den Keykloak starten und verwenden zu können, braucht es Zugriff auf die APG Entwicklungsdatenbank.

## Build

Das Projekt kann mit dem Script `build.sh` gebaut werden.

Dazu muss lokal Docker am Laufen sein, da beim Build Docker Images erstellt und beim lokalen Docker-Prozess registiert werden.

Die erzeugten Docker Images können im lokalen Docker-Prozess mit dem Befehl 'docker images | grep apg' angezeigt werden.

## Ausführen

Zum Ausführen wird Docker Compose verwendet.

Alle Services können mit dem Befehl `docker-compose -p apg-sso up` gestartet werden.

Alle Services können mit dem Befehl `docker-compose -p apg-sso down` gestoppt und abgeräumt werden (notwendig, da das Setup vom Keycloak nur einmal ausgeführt werden kann).

Die Befehle können kombiniert werden `docker-compose -p apg-sso down && docker-compose -p apg-sso up`.

Docker-Compose startet den Keycloak-Service und warted 45 Sekunden, bevor die Applikationen gestartet werden. Dies ist notwendig, da Keycloak am laufen sein muss, damit die Applikationen die
OpenID Connect Informationen abrufen können.

## Zugriff im Browser

Alle Services sind im Browser erreichbar:
* [Keycloak](http://keycloak.local:8080/)
* [Vaadin App](http://vaadin-app.local:8081/)
* [Angular App](http://angular-app.local:8082/)

### Zugriff auf Keycloak

Zur Anmeldung bei Keycloak muss der Benutzer `admin` mit dem Passwort `admin` verwendet werden.

### Zugriff auf Apps

Für den Zugriff auf die Apps können die Test-Benutzer TU01, TU02, TU03, TU16, TU17, TU18, TU19, TU20, TU21 verwendet werden.

Test-Benutzer mit der Rolle 'VERKAUF' haben weitere Funktionen.

## Zugriff über REST API

Die in der Anuglar App implementiert REST API kann auch Stateless verwendet werden. Damit muss ein gültiges Access Token im Header übergeben werden.

### Access Token bei Keycloak holen

```
curl -X POST 'http://keycloak.local:8080/auth/realms/apg/protocol/openid-connect/token' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'client_id=apg-sso-angular-app' \
--data-urlencode 'grant_type=password' \
--data-urlencode 'scope=openid profile email' \
--data-urlencode 'username=<USERNAME>' \
--data-urlencode 'password=<PASSWORD>'
```

### Aufruf auf REST API

```
curl 'http://angular-app.local:8082/api/configuration/internal' \
--header 'Connection: keep-alive' \
--header 'Pragma: no-cache' \
--header 'Cache-Control: no-cache' \
--header 'Accept: application/json, text/plain, */*' \
--header 'Authorization: Bearer <ACCESS_TOKEN>'
```

## Client Configuration

### Vaadin 8 App

Die Vaadin 8 App verwendet das OpenID Connect Login von Spring Security. Nach dem Login wird das Access Token in der HTTP Session (stateful) gehalten.

### Angular App

Die Angular App verwendet den [Keycloak JavaScript Adapter](https://www.keycloak.org/docs/latest/securing_apps/index.html#_javascript_adapter) mit einem [Angular Wrapper](https://github.com/mauriciovigolo/keycloak-angular).
Der Keycloak JavaScript Adapter setzt das Access Token in den Request Header und daher kann das Backend (stateless) sein.
Zusätzlich kann der Keycloak JavaScript Adapter mittels eines iFrames herausfinden, ob eine aktive User Session existiert oder nicht und kann den Benutzer automatisch an- beziehungsweise abmelden.

## Entwicklung

### Keycloak

Um den Keycloak zu entwickeln, können Tests Unit Tests verwendet werden.

Ansonsten muss das Projekt im Keycloak deployt und darin getestet werden. Dabei ist ein Remote-Debug-Zugriff auf Port 5005 im Docker Image für Keycloak eingerichtet.
Um nur das Keycloak Docker Image zu starten, kann der Befehl `docker-compose -p apg-sso down && docker-compose -p apg-sso up keycloak` verwendet werden.

#### User Storage SPI

Der Keycloak User Storage SPI integriert Benutzer aus der APG Oracle Datenbank basierend auf [Server Developer Guide](https://www.keycloak.org/docs/latest/server_development/#_user-storage-spi).
Die Verwendung des APG Oracle User Storage Providers kann unter _User Federation_ verifiziert werden.

#### Theme

Um die Login-Seite von Keycloak im Design der APG|SGA erscheinen zu lassen, wurde ein Theme basierend auf dem [Server Developer Guide](https://www.keycloak.org/docs/latest/server_development/#_themes) entwickelt.
Die Verwendung des APG Themes kann im Keycloak unter _Realm Settings -> Themes -> Login Theme_ verifiziert werden.

#### Username Password Form

Um die Links auf die Passwort-vergessen- und Registrierungs-Seite konfigurierbar zu machen, wurde die _UsernamePasswordForm_ erweitert.
Die Verwendung des APG Username Password Form kann im Keycloak unter _Authentication -> Flows -> Apg-browser_ verifiziert werden.
Zusätzlich kann dort auch die Konfiguration der URLs vorgenommen werden.

### Vaadin 8 App

Um die Vaadin 8 App zu entwickeln, muss die Spring Boot App `ApgSsoVaadin8App` gestartet werden.

### Angular App

Um die Angular App zu entwickeln, muss die Spring Boot App `ApgSsoAngularApp` gestartet werden.
Zusätzlich muss das Angular Frontend mit `npm start` im Verzeichnis `angular-app` gestartet werden. Die Angular App ist dann auf Port 4200 und nicht auf 8082 verfügbar.

## Ablauf APG-Registrierungsprozess

Im APG-Registrierungsprozess sind drei Applikationen involviert.
1. Fachapplikation (beispielsweise PosterDirect) - Benutzer klickt Anmelden
2. Keycloak - Benutzer klickt auf dem Login-Formular den Link 'Registrieren'
3. APG-Registeirungs-Applikation - Die Applikation, welche den APG-Registierungsprozess umsetzt (Formular und E-Mail-Verifikation)

Damit der Benutzer am Ende des Registierungsprozesses sich in einem authentifizierten Zustand auf der Fachapplikation befindet, ist folgender Ablauf notwendig:
1. Fachapplikation: Leitet den Benutzer auf die Login-Seite von Keycloak für den konfigurierten Client weiter
2. Keycloak: Leitet den Benutzer auf die Registierungsseite der APG-Registeirungs-Applikation mit der Client-ID als URL-Parameter weiter
3. APG-Registeirungs-Applikation: Fügt die Client-ID der URL in der Registierungs-E-Mail zu
4. APG-Registeirungs-Applikation: Beim Click auf die URL in der Registrierungs-E-Mail wird der Benutzer aktiviert und es wird auf eine URL der Fachapplikation weitergeleitet (Mapping pro Client-ID), welche den Anmeldevorgang startet
    1. URL zeigt auf einen geschützten Bereich und der Anmeldevorgang wird deshalb gestartet
    2. URL zeiggt auf einen öffentlichen Bereich, hat aber einen Parameter, welcher von der Fachapplikation ausgelesen wird und diese startet deshalb den Anmeldevorgang
5. Fachapplikation leitet den Benutzer auf Keycloak für die Anmeldung weiter
6. Keycloak leitet den Benutzer nach erfolgreicher Anmeldung auf die Fachapplikation weiter

## Ablauf Posterdirect Login

Posterdirect benötigt für die Umsetzung der [Authentifizierung](https://wiki.apgsga.ch/display/EC/Autorisierung+und+Authentifizierung) die Möglichkeit, sich direkt mit einem Access Token zu authentifizieren.
Dazu wurde der `ApgAccessTokenRequestParamAuthenticator` umgesetzt.

Der Ablauf der Authentifizierung mit dem APG SSO in Posterdirect sieht wie folgt aus:
1. Benutzername und Passwort validieren und aus der Antwort das Access Token holen
   ```
   curl -X POST 'http://keycloak.local:8080/auth/realms/apg/protocol/openid-connect/token' \
   --header 'Content-Type: application/x-www-form-urlencoded' \
   --data-urlencode 'client_id=apg-sso-angular-app' \
   --data-urlencode 'grant_type=password' \
   --data-urlencode 'scope=openid profile email' \
   --data-urlencode 'username=<USERNAME>' \
   --data-urlencode 'password=<PASSWORD>'

   {
     "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJpejVDUTVmVjBvWFBmeDJoWlVCd2UzTUVYZjZEN0NBYlBPUjBnbGVUbmlNIn0.eyJleHAiOjE1OTcyMzc4NzcsImlhdCI6MTU5NzIzNzU3NywianRpIjoiOWI4ZDM3NTgtYjJjOC00ZTMxLWJlM2ItNmFiM2FhOTliMjQyIiwiaXNzIjoiaHR0cDovL2tleWNsb2FrLmxvY2FsOjgwODAvYXV0aC9yZWFsbXMvYXBnIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6ImY6ZTYyY2E4YmEtN2ZmNS00Yzk4LTliNDctNWEzOTNhYWViMGY0OlRVMDEiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJhcGctc3NvLWFuZ3VsYXItYXBwIiwic2Vzc2lvbl9zdGF0ZSI6IjIyMWUwZjdhLTNkZTItNGZkYy04YmE0LWFkZWNlYzYzM2RjMiIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoib3BlbmlkIGFwZyBlbWFpbCBwcm9maWxlIiwiYXBnLXJvbGVzIjpbIkJBU0lTUk9MTEUiLCJQRF9TQ0hSRUlCRU4iXSwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJUVTAxIn0.MbzRjr6gpbMX-8vIjVne_uMA4zwHLNTDOHPGRUryf2IVm8YE8rXkz5_5yNmN3RHDn7XIDVItxUV5BY2xnnHBVbwEstJm6ud2HK-o4JXQ2T8XeCUFzJZJsG3Dsb97kU0CHPQfib1cQqySbowIrB8p91SzGP0Yt6FpqWXkABUi8yJ5ukT4GSVEaaVqlv6liVcH58ETMbZrz17GWY7bBFAddC2I4qEOq0ru_Ev9XZeszdkoubE-AFDLs_33wUBL63Fi2H6hmTweVyDHOkldL8DaMC1qZpq9SzhmUTptkb3T1Zq4LVSJE6P9WqiKY3bBFKiKvx7dPYGyRe-B-Lad8v3aqA",
     "expires_in": 300,
     "refresh_expires_in": 1800,
     "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI3NjVlZjJkNS01NTJlLTQ0NzMtOWY3My0xMTNhZmE3MjU4NmUifQ.eyJleHAiOjE1OTcyMzkzNzcsImlhdCI6MTU5NzIzNzU3NywianRpIjoiMTQxZGEzZjktNzQzOC00ODMyLWFjOTQtMjE4YjdiMWJmNzMxIiwiaXNzIjoiaHR0cDovL2tleWNsb2FrLmxvY2FsOjgwODAvYXV0aC9yZWFsbXMvYXBnIiwiYXVkIjoiaHR0cDovL2tleWNsb2FrLmxvY2FsOjgwODAvYXV0aC9yZWFsbXMvYXBnIiwic3ViIjoiZjplNjJjYThiYS03ZmY1LTRjOTgtOWI0Ny01YTM5M2FhZWIwZjQ6VFUwMSIsInR5cCI6IlJlZnJlc2giLCJhenAiOiJhcGctc3NvLWFuZ3VsYXItYXBwIiwic2Vzc2lvbl9zdGF0ZSI6IjIyMWUwZjdhLTNkZTItNGZkYy04YmE0LWFkZWNlYzYzM2RjMiIsInNjb3BlIjoib3BlbmlkIGFwZyBlbWFpbCBwcm9maWxlIn0.QRgsH8FdUWf-4It3TbCV8XF9yptL7EDDkdSpJ-EOCrw",
     "token_type": "bearer",
     "id_token": "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJpejVDUTVmVjBvWFBmeDJoWlVCd2UzTUVYZjZEN0NBYlBPUjBnbGVUbmlNIn0.eyJleHAiOjE1OTcyMzc4NzcsImlhdCI6MTU5NzIzNzU3NywiYXV0aF90aW1lIjowLCJqdGkiOiIxMDNjYWFmNC1hMjZkLTQ0MWEtOGExNi02YmJkNzEwODYxMjIiLCJpc3MiOiJodHRwOi8va2V5Y2xvYWsubG9jYWw6ODA4MC9hdXRoL3JlYWxtcy9hcGciLCJhdWQiOiJhcGctc3NvLWFuZ3VsYXItYXBwIiwic3ViIjoiZjplNjJjYThiYS03ZmY1LTRjOTgtOWI0Ny01YTM5M2FhZWIwZjQ6VFUwMSIsInR5cCI6IklEIiwiYXpwIjoiYXBnLXNzby1hbmd1bGFyLWFwcCIsInNlc3Npb25fc3RhdGUiOiIyMjFlMGY3YS0zZGUyLTRmZGMtOGJhNC1hZGVjZWM2MzNkYzIiLCJhdF9oYXNoIjoiblQ4TFBBMTVSRXEtSlJwRlR4eE1pZyIsImFjciI6IjEiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsInByZWZlcnJlZF91c2VybmFtZSI6IlRVMDEifQ.GHeWVEOPv-ISnwXF7sEfjqWs_WaIPamqHqnIr3CGKQBDQ-db6Ih4h_e7yBNbEuSjYQ1yyiSjYV-QKxa8S75_ueVdphBvVzZFjQsrbZStpM4IUBIykI00yT0WGb-gZkm1Y_WLmGum4kx6dV58V-sLbTIAR2gVvVIh0LyBSf3ujY2At0XU5Fickz8dDywLGuBfi91GM484AOsE8PtUAFQWdRHP0s19bHQ7TyDbs5Wp1hqK2MtJPvUs8ZJ_wDjKIdW_gnF7DT-cnpjAjIKzKqFE6faBk--zh3tlSh6TyDiPz10Rg4f3WtUXicm2nBAeMsc-FpjbuIEdt-0dA87s5tSLUA",
     "not-before-policy": 0,
     "session_state": "221e0f7a-3de2-4fdc-8ba4-adecec633dc2",
     "scope": "openid apg email profile"
   }
   ```
2. OIDC Flow mit Access Token ausführen
   Dazu auf die Login URL gehen und zusätzlich den Request Parameter `access_token` mit dem im Schritt 1 erhaltenen Wert befüllen.
   In diesem Fall wird der Benutzer direkt angemeldet und der Benutzername und das Passwort müssen nicht mehr angegeben werden.
