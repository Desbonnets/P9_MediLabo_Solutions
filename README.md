# MediLabo Solutions

Application de détection du risque diabétique basée sur une architecture microservices entièrement dockerisée.

---

## Sommaire

- [Architecture](#architecture)
- [Prérequis](#prérequis)
- [Démarrage rapide](#démarrage-rapide)
- [Microservices](#microservices)
  - [back — Gestion des patients](#back--gestion-des-patients)
  - [notes — Gestion des notes médicales](#notes--gestion-des-notes-médicales)
  - [risk — Évaluation du risque diabétique](#risk--évaluation-du-risque-diabétique)
  - [gateway — Spring Cloud Gateway](#gateway--spring-cloud-gateway)
  - [front — Interface utilisateur](#front--interface-utilisateur)
- [Données de test](#données-de-test)
- [Algorithme de risque](#algorithme-de-risque)
- [Tests](#tests)
- [Structure du projet](#structure-du-projet)

---

## Architecture

```
┌─────────────────────────────────────────────────────┐
│                    Navigateur                        │
│                  localhost:5173                      │
└──────────────────────┬──────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────┐
│              Spring Cloud Gateway                    │
│                  port 8080                           │
│  /api/users/**  → back:8081                         │
│  /api/notes/**  → notes:8082                        │
│  /api/assess/** → risk:8083                         │
└────────┬──────────────┬──────────────┬──────────────┘
         │              │              │
┌────────▼────┐  ┌──────▼──────┐  ┌───▼────────┐
│    back     │  │    notes    │  │    risk    │
│  port 8081  │  │  port 8082  │  │  port 8083 │
│ Spring Boot │  │ Spring Boot │  │Spring Boot │
│ PostgreSQL  │  │  MongoDB    │  │  (no DB)   │
└────────┬────┘  └──────┬──────┘  └────────────┘
         │              │           ↑          ↑
┌────────▼────┐  ┌──────▼──────┐   │          │
│  postgres   │  │   mongodb   │───┘          │
│  port 5432  │  │  port 27017 │──────────────┘
└─────────────┘  └─────────────┘
```

| Service  | Port | Technologie               | Base de données  |
|----------|------|---------------------------|-----------------|
| front    | 5173 | React 19 + Vite 7         | —               |
| gateway  | 8080 | Spring Cloud Gateway      | —               |
| back     | 8081 | Spring Boot 4 + JPA       | PostgreSQL 16   |
| notes    | 8082 | Spring Boot 4 + MongoDB   | MongoDB 7       |
| risk     | 8083 | Spring Boot 4             | —               |

---

## Prérequis

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) ≥ 24
- Docker Compose v2 (inclus dans Docker Desktop)

Aucune installation Java, Node.js ou MongoDB n'est nécessaire : tout s'exécute dans des conteneurs.

---

## Démarrage rapide

```bash
# Cloner le dépôt
git clone https://github.com/Desbonnets/P9_MediLabo_Solutions.git
cd P9_MediLabo_Solutions

# Construire et démarrer tous les services
docker compose up --build
```

L'application est accessible sur **http://localhost:5173** une fois tous les conteneurs démarrés.

> La première construction prend plusieurs minutes (téléchargement des images Gradle/JDK).
> Les lancements suivants sont beaucoup plus rapides grâce au cache Docker.

### Arrêt

```bash
# Arrêter les conteneurs
docker compose down

# Arrêter et supprimer les volumes (repart de zéro)
docker compose down -v
```

---

## Microservices

### back — Gestion des patients

**Port :** 8081 | **Base :** PostgreSQL

Expose les endpoints REST pour la gestion des dossiers patients. Les données sont automatiquement initialisées avec 4 patients de test au premier démarrage.

#### Endpoints

| Méthode | URL                  | Description                        | Code retour |
|---------|----------------------|------------------------------------|-------------|
| GET     | `/api/users`         | Liste tous les patients            | 200         |
| GET     | `/api/users/{id}`    | Détail d'un patient                | 200 / 404   |
| POST    | `/api/users`         | Créer un patient                   | 201 / 400   |
| PUT     | `/api/users/{id}`    | Modifier un patient                | 200 / 404   |
| DELETE  | `/api/users/{id}`    | Supprimer un patient               | 204 / 404   |

#### Modèle Patient

```json
{
  "id": 1,
  "firstName": "Test",
  "lastName": "TestNone",
  "birthDate": "1966-12-31",
  "gender": "F",
  "address": "1 Brookside St",
  "phone": "100-222-3333"
}
```

Champs obligatoires : `firstName`, `lastName`, `birthDate`, `gender`.  
Champs optionnels : `address`, `phone`.

---

### notes — Gestion des notes médicales

**Port :** 8082 | **Base :** MongoDB

Stocke les notes médicales libres associées à chaque patient. Les formats originaux (sauts de ligne, etc.) sont préservés. Initialisé avec 9 notes de test au premier démarrage.

#### Endpoints

| Méthode | URL                          | Description                        | Code retour |
|---------|------------------------------|------------------------------------|-------------|
| GET     | `/api/notes/patient/{patId}` | Toutes les notes d'un patient      | 200         |
| GET     | `/api/notes/{id}`            | Une note par son ID                | 200 / 404   |
| POST    | `/api/notes`                 | Créer une note                     | 201         |
| PUT     | `/api/notes/{id}`            | Modifier une note                  | 200 / 404   |
| DELETE  | `/api/notes/{id}`            | Supprimer une note                 | 204 / 404   |

#### Modèle Note

```json
{
  "id": "64a1f2e3b4c5d6e7f8a9b0c1",
  "patId": 1,
  "content": "Le patient déclare qu'il 'se sent très bien'\nPoids égal ou inférieur au poids recommandé"
}
```

---

### risk — Évaluation du risque diabétique

**Port :** 8083 | **Base :** aucune

Calcule le niveau de risque diabétique d'un patient en interrogeant les deux autres microservices. Ne possède pas de base de données propre.

#### Endpoint

| Méthode | URL                  | Description                        | Code retour |
|---------|----------------------|------------------------------------|-------------|
| GET     | `/api/assess/{patId}`| Évaluation du risque d'un patient  | 200 / 404   |

#### Réponse

```json
{
  "patientId": 3,
  "risk": "InDanger"
}
```

Valeurs possibles : `None`, `Borderline`, `InDanger`, `EarlyOnset`.

---

### gateway — Spring Cloud Gateway

**Port :** 8080

Point d'entrée unique qui route les requêtes vers les microservices appropriés. Le frontend et tout client externe passent par la gateway.

| Route                | Destination      |
|----------------------|-----------------|
| `/api/users/**`      | `back:8081`     |
| `/api/notes/**`      | `notes:8082`    |
| `/api/assess/**`     | `risk:8083`     |

---

### front — Interface utilisateur

**Port :** 5173 (dev) | **Technologie :** React 19 + Vite 7

Interface web permettant de :
- Visualiser la liste des patients avec leur niveau de risque diabétique (badge coloré)
- Créer, modifier et supprimer des patients
- Consulter l'historique des notes médicales par patient
- Ajouter de nouvelles notes

---

## Données de test

Quatre patients sont chargés automatiquement à l'initialisation, correspondant aux 4 niveaux de risque :

| ID | Nom            | Prénom | Naissance  | Genre | Risque attendu |
|----|----------------|--------|------------|-------|----------------|
| 1  | TestNone       | Test   | 1966-12-31 | F     | **None**       |
| 2  | TestBorderline | Test   | 1945-06-24 | M     | **Borderline** |
| 3  | TestInDanger   | Test   | 2004-06-18 | M     | **InDanger**   |
| 4  | TestEarlyOnset | Test   | 2002-06-28 | F     | **EarlyOnset** |

Neuf notes médicales sont également pré-chargées (voir `notes/src/main/java/medilabo/notes/DataLoader.java`).

---

## Algorithme de risque

Le microservice `risk` analyse les notes d'un patient pour y détecter des **termes déclencheurs**, puis détermine le niveau de risque selon l'âge et le genre du patient.

### Termes déclencheurs (11 groupes)

La recherche est insensible à la casse. Chaque groupe compte pour **1 déclencheur** même si plusieurs termes du groupe apparaissent.

| # | Terme(s)               | Remarque                                      |
|---|------------------------|-----------------------------------------------|
| 1 | Hémoglobine A1C        |                                               |
| 2 | Microalbumine          |                                               |
| 3 | Taille                 |                                               |
| 4 | Poids                  |                                               |
| 5 | Fumeur / Fumeuse       | Un seul point quel que soit le terme trouvé   |
| 6 | Anormal                | Matche aussi "anormale", "anormaux"           |
| 7 | Cholestérol            |                                               |
| 8 | Vertige                | Matche aussi "Vertiges"                       |
| 9 | Rechute                |                                               |
|10 | Réaction               |                                               |
|11 | Anticorps              |                                               |

### Règles de détermination du risque

```
Âge < 30 ans — Homme :
  ≥ 5 déclencheurs → EarlyOnset
  ≥ 3 déclencheurs → InDanger
  sinon            → None

Âge < 30 ans — Femme :
  ≥ 7 déclencheurs → EarlyOnset
  ≥ 4 déclencheurs → InDanger
  sinon            → None

Âge ≥ 30 ans :
  ≥ 8 déclencheurs → EarlyOnset
  ≥ 6 déclencheurs → InDanger
  ≥ 2 déclencheurs → Borderline
  sinon            → None
```

---

## Tests

Chaque microservice back dispose de tests unitaires exécutables indépendamment avec Gradle (Java 21 requis) ou via Docker.

```bash
# Depuis le répertoire d'un microservice (back, notes ou risk)
./gradlew test
```

| Microservice | Classe de test           | Couverture                                        |
|--------------|--------------------------|---------------------------------------------------|
| back         | `UserServiceTest`        | CRUD service, 404 sur ID inexistant               |
| back         | `UserControllerTest`     | Codes HTTP (201, 404, 400), validation            |
| notes        | `NoteServiceTest`        | CRUD service, 404 sur ID inexistant               |
| risk         | `RiskServiceTest`        | 16 cas : 4 patients de test + cas limites         |

---

## Structure du projet

```
P9_MediLabo_Solutions/
│
├── back/                          # Microservice patients (Spring Boot + PostgreSQL)
│   ├── src/main/java/medilabo/back/
│   │   ├── config/SecurityConfig.java
│   │   ├── controller/
│   │   │   ├── UserController.java
│   │   │   └── ValidationExceptionHandler.java
│   │   ├── model/User.java
│   │   ├── repository/UserRepository.java
│   │   ├── service/UserService.java
│   │   └── DataLoader.java
│   ├── src/test/java/medilabo/back/
│   │   ├── controller/UserControllerTest.java
│   │   └── service/UserServiceTest.java
│   ├── build.gradle
│   └── Dockerfile
│
├── notes/                         # Microservice notes (Spring Boot + MongoDB)
│   ├── src/main/java/medilabo/notes/
│   │   ├── config/
│   │   │   ├── MongoConfig.java
│   │   │   └── SecurityConfig.java
│   │   ├── controller/NoteController.java
│   │   ├── model/Note.java
│   │   ├── repository/NoteRepository.java
│   │   ├── service/NoteService.java
│   │   └── DataLoader.java
│   ├── src/test/java/medilabo/notes/
│   │   └── service/NoteServiceTest.java
│   ├── build.gradle
│   └── Dockerfile
│
├── risk/                          # Microservice évaluation du risque (Spring Boot)
│   ├── src/main/java/medilabo/risk/
│   │   ├── config/
│   │   │   ├── RestClientConfig.java
│   │   │   └── SecurityConfig.java
│   │   ├── controller/RiskController.java
│   │   ├── dto/
│   │   │   ├── NoteDto.java
│   │   │   ├── PatientDto.java
│   │   │   └── RiskResponseDto.java
│   │   └── service/RiskService.java
│   ├── src/test/java/medilabo/risk/
│   │   └── service/RiskServiceTest.java
│   ├── build.gradle
│   └── Dockerfile
│
├── gateway/                       # Spring Cloud Gateway
│   ├── src/main/java/medilabo/gateway/
│   │   ├── config/
│   │   │   ├── GatewayConfig.java
│   │   │   └── SecurityConfig.java
│   │   └── controller/FallbackController.java
│   ├── build.gradle
│   └── Dockerfile
│
├── front/                         # Interface React
│   ├── src/
│   │   ├── api/
│   │   │   ├── userApi.js
│   │   │   ├── notesApi.js
│   │   │   └── riskApi.js
│   │   ├── components/
│   │   │   ├── UserList.jsx
│   │   │   ├── UserForm.jsx
│   │   │   └── NotesPanel.jsx
│   │   ├── App.jsx
│   │   └── App.css
│   ├── package.json
│   └── Dockerfile
│
└── docker-compose.yml
```
