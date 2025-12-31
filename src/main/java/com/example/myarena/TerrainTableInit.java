package com.example.myarena;

import java.sql.*;

public class TerrainTableInit {

    private static final String URL = "jdbc:postgresql://ep-gentle-term-ag1gorm7-pooler.c-2.eu-central-1.aws.neon.tech/myarena?sslmode=require&channel_binding=require";
    private static final String USER = "neondb_owner";
    private static final String PASSWORD = "npg_Zym8Fjrpg4iz";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {

            boolean exists = false;

            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT EXISTS (" +
                            "SELECT FROM information_schema.tables " +
                            "WHERE table_schema = 'public' AND table_name = 'terrains')")) {

                ResultSet rs = stmt.executeQuery();
                if (rs.next()) exists = rs.getBoolean(1);
            }

            if (exists) {
                System.out.println("✅ Table 'terrains' already exists.");
                return;
            }

            String sql = """
                CREATE TABLE terrains (
                    id SERIAL PRIMARY KEY,
                    name VARCHAR(255) NOT NULL,
                    type VARCHAR(50),
                    description TEXT,
                    location VARCHAR(255),
                    price_per_hour DOUBLE PRECISION NOT NULL,
                    capacity INT NOT NULL,
                    available BOOLEAN NOT NULL DEFAULT TRUE,
                    owner_id BIGINT
                );
            """;

            try (Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
                System.out.println("🆕 Table 'terrains' created successfully!");
            }

        } catch (SQLException e) {
            System.out.println("❌ DB Error");
            e.printStackTrace();
        }
    }
}
