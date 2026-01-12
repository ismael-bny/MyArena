You are right; the database password is essential for the remote connection to work. Here is the complete `README.md` with the database password and all user credentials included for your teacher.

---

# MyArena - Sport Complex Management Platform

**MyArena** is a JavaFX management platform for sports complexes. It supports multi-role interactions, allowing administrators, owners, organizers, and clients to interact within a single ecosystem for reservations, equipment management, and tournament organization.

## 🌐 Remote Database Connection

The application is pre-configured to connect to a **remote PostgreSQL database** hosted on Neon (AWS). No local database installation is required for testing.

### Connection Details:

* **Host:** `ep-gentle-term-ag1gorm7-pooler.c-2.eu-central-1.aws.neon.tech`
* **Database:** `myarena`
* **User:** `neondb_owner`
* **Password:** `npg_Zym8Fjrpg4iz`
* **Security:** SSL is mandatory for the connection (`sslmode=require`).

---

## 🔑 Test Credentials (Login)

Use the following accounts to test the specific features of each role:

| Role | Username (Email) | Password |
| --- | --- | --- |
| **ADMIN** | `admin@myarena.com` | `admin123` |
| **ORGANIZER** | `wassimtest@myarena.com` | `wassim04` |
| **CLIENT** | `syrine@myarena.com` | `syrine123` |
| **OWNER** | `fatima@myarena.com` | `fatima123` |

---

## 🛠️ Prerequisites

* **Java 21** or higher.
* **Maven** (or use the included Maven wrapper).
* **Internet Access:** Required to connect to the remote Neon database.

---

## 🚀 How to Run the Project

### 1. Build the Project

Open a terminal in the project root and run:

```bash
mvn clean install

```

### 2. Launch the Application

You can launch the application via Maven:

```bash
mvn javafx:run

```

*Or directly run the `main` method in:* `src/main/java/com/example/myarena/Launcher.java`.

---

## 🧪 Testing Use Cases

The project includes unit tests covering core functionality. You can run them to verify the business logic:

```bash
mvn test

```

### Key Features to Test:

* **Admin:** User management and tournament validation.
* **Owner:** Terrain (field) and equipment inventory management.
* **Organizer:** Tournament creation and registration management.
* **Client:** Field booking, tournament registration, and equipment purchase/rental.

---

**Author:** MyArena Team

