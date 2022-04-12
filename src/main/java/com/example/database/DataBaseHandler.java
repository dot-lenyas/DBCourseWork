package com.example.database;


import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseHandler {
    private static final String URL = "jdbc:postgresql://localhost:5432/autorent";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "qwerasdfzxcvb";

    private Connection connection;

    public DataBaseHandler() throws SQLException {
        connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    public Connection GetConnection() {
        return connection;
    }
}

