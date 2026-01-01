package com.example.myarena.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe utilitaire pour centraliser la configuration et les connexions à PostgreSQL.
 * Cette classe évite de dupliquer les credentials dans chaque DAO.
 */

public class DatabaseConfig {
    private static final String URL = "jdbc:postgresql://ep-gentle-term-ag1gorm7-pooler.c-2.eu-central-1.aws.neon.tech/myarena?sslmode=require&channel_binding=require";
    private static final String USER = "neondb_owner";
    private static final String PASSWORD = "npg_Zym8Fjrpg4iz";

    /**
     * Constructeur privé pour empêcher l'instanciation.
     */
    private DatabaseConfig() {
        throw new UnsupportedOperationException("Classe utilitaire non instanciable");
    }

    /**
     * Obtient une nouvelle connexion à la base de données PostgreSQL.
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Charge le driver PostgreSQL (optionnel depuis JDBC 4.0, mais bonne pratique)
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver PostgreSQL introuvable. Vérifie ton pom.xml", e);
        }

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Teste si la connexion à la base de données fonctionne.
     * Utile pour vérifier la configuration au démarrage de l'application.
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println(" Erreur de connexion à PostgreSQL : " + e.getMessage());
            return false;
        }
    }
}
