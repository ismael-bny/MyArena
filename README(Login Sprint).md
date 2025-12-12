# MyArena - Guide de Configuration pour Login Sprint

## Prérequis

- **Java 21** installé
- **Maven** installé (ou utiliser le Maven wrapper inclus)
- **Docker** installé et en cours d'exécution
    - **Windows :** Docker Desktop
    - **Linux :** Docker Engine (`sudo apt install docker.io` ou équivalent selon votre distribution)
- **IntelliJ IDEA** (recommandé) ou un autre IDE Java

---

## 1. Installation de la Base de Données PostgreSQL avec Docker

### Étape 1 : Vérifier que Docker est actif

**Windows :**
- Lancez Docker Desktop et attendez qu'il soit prêt

**Linux :**
```bash
sudo systemctl start docker
sudo systemctl status docker  # Devrait afficher "active (running)"
```

### Étape 2 : Créer et démarrer le conteneur PostgreSQL

Ouvrez un terminal et exécutez :

**Windows (PowerShell/CMD) :**
```bash
docker run --name myarena-postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=myarena -p 5432:5432 -d postgres:16
```

**Linux :**
```bash
sudo docker run --name myarena-postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=myarena -p 5432:5432 -d postgres:16
```

### Étape 3 : Vérifier que le conteneur tourne

**Windows :**
```bash
docker ps
```

**Linux :**
```bash
sudo docker ps
```

Vous devriez voir une ligne avec `myarena-postgres` et le statut `Up`.

---

## 2. Créer la Table `users`

Exécutez la commande suivante pour vous connecter à PostgreSQL :

**Windows :**
```bash
docker exec -it myarena-postgres psql -U postgres -d myarena
```

**Linux :**
```bash
sudo docker exec -it myarena-postgres psql -U postgres -d myarena
```

Puis copiez-collez ce script SQL :

```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    role VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL
);

INSERT INTO users (name, email, password_hash, phone, role, status) 
VALUES 
    ('Admin Test', 'admin@myarena.com', 'admin123', '0600000000', 'ADMIN', 'ACTIVE'),
    ('Client Test', 'client@myarena.com', 'client123', '0600000001', 'CLIENT', 'ACTIVE');
```

Tapez `\q` pour quitter.

### Vérifier que les données sont présentes

**Windows :**
```bash
docker exec -it myarena-postgres psql -U postgres -d myarena -c "SELECT * FROM users;"
```

**Linux :**
```bash
sudo docker exec -it myarena-postgres psql -U postgres -d myarena -c "SELECT * FROM users;"
```

Vous devriez voir les 2 utilisateurs de test.

---

## 3. Configuration du Projet

> **Note :** Cette section est pour les personnes externes à l'équipe. Si vous êtes membre de l'équipe et avez déjà cloné le projet, passez directement à la section 4.

### Cloner le projet

```bash
git clone <URL_DU_REPO>
cd MyArena
```

### Charger les dépendances Maven

Dans IntelliJ :
1. Ouvrez le projet
2. Maven → Reload Project (icône de refresh)
3. Attendez que toutes les dépendances se téléchargent

---

## 4. Lancer l'Application

### Option A : Via IntelliJ

1. Ouvrez le fichier `src/main/java/com/example/myarena/Launcher.java`
2. Clic droit → **Run 'Launcher.main()'**

---

## 5. Tester l'Application

Une fenêtre de login devrait s'ouvrir. Testez avec ces credentials :

### Compte Admin
- **Username :** `admin@myarena.com`
- **Password :** `admin123`

### Compte Client
- **Username :** `client@myarena.com`
- **Password :** `client123`

---


## Problèmes Fréquents

### Erreur : "Port 5432 already in use"

Un autre PostgreSQL tourne déjà sur votre machine. Solutions :

**Option 1 :** Arrêtez l'autre PostgreSQL

**Option 2 :** Utilisez un autre port pour Docker :
```bash
docker run --name myarena-postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=myarena -p 5433:5432 -d postgres:16
```

Puis modifiez dans `UserDAOPostgres.java` :
```java
private static final String URL = "jdbc:postgresql://localhost:5433/myarena";
```

### Erreur : "Connection refused"

Docker n'est pas lancé ou le conteneur n'est pas démarré :

**Windows :**
```bash
docker start myarena-postgres
```

**Linux :**
```bash
sudo systemctl start docker
sudo docker start myarena-postgres
```

### Erreur : "Table users does not exist"

Vous n'avez pas créé la table. Suivez la section 2.

### Erreur : "ClassNotFoundException: org.postgresql.Driver"

Le driver PostgreSQL n'est pas chargé. Faites :
```bash
mvn clean install
```

Puis dans IntelliJ : Maven → Reload Project

---

## Structure du Projet

```
MyArena/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/myarena/
│   │   │       ├── domain/          # Entités métier (User, UserRole, UserStatus)
│   │   │       ├── persistance/     # Accès aux données (DAO, Factory)
│   │   │       ├── services/        # Logique métier (UserManager)
│   │   │       ├── facade/          # Façade (SessionFacade)
│   │   │       ├── ui/              # Interface JavaFX (LoginFrame, LoginController)
│   │   │       └── Launcher.java    # Point d'entrée de l'application
│   │   └── resources/
│   │       └── com/example/myarena/
│   │           └── login-page.fxml  # Interface de login
│   └── test/
├── pom.xml
└── README.md
```

## Contact

Pour toute question, contactez moi sur mon adresse mail : www.bennacef@gmail.com

BEN NACEF Wassim 
