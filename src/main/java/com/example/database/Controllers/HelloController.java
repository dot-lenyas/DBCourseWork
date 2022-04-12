package com.example.database.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class HelloController {
    private static final String URL = "jdbc:postgresql://localhost:5432/autorent";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "qwerasdfzxcvb";
    @FXML
    private Label Welcome_Text;
    @FXML
    private Button btn_start;
    @FXML
    private Button AdminButton;

    @FXML
    private void OnClientButton() throws IOException {
        AdminButton.getScene().getWindow().hide();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/example/database/client_view.fxml"));
        loader.load();
        Parent root = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.showAndWait();

    }
    @FXML
    private void OnAdminButton() throws IOException {
        AdminButton.getScene().getWindow().hide();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/example/database/admin_view.fxml"));
        loader.load();
        Parent root = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.showAndWait();
    }
}