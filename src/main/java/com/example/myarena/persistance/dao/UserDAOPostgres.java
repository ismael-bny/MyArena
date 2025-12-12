package com.example.myarena.persistance.dao;

import com.example.myarena.domain.User;
import com.example.myarena.domain.UserRole;
import com.example.myarena.domain.UserStatus;

import java.sql.*;

public class UserDAOPostgres implements UserDAO {

    private static final String URL = "jdbc:postgresql://localhost:5432/myarena";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }



}
