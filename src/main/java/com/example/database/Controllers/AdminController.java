package com.example.database.Controllers;

import com.example.database.DataBaseHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;

public class AdminController {

    @FXML private TextField model_auto;
    @FXML private TextField volume_auto;
    @FXML private TextField power_auto;
    @FXML private TextField transmission_auto;
    @FXML private TextField cost_auto;



    @FXML
    private void AddAutoForm() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/example/database/add_auto_form.fxml"));
        loader.load();
        Parent root = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setWidth(250);
        stage.showAndWait();
    }
    @FXML
    private void AddAutoToDB() throws SQLException {
        DataBaseHandler dataBaseHandler = new DataBaseHandler();
        Statement statement = dataBaseHandler.GetConnection().createStatement();
        boolean flag = true;
        statement.execute("INSERT INTO Автомобиль (Модель_автомобиля , Объем_двигателя, Мощность_двигателя, Коробка_передач, Стоимость_за_сутки, Статус_наличия) values('"+model_auto.getText()+"', '"+volume_auto.getText()+"', '"+power_auto.getText()+"', '"+transmission_auto.getText()+"', '"+cost_auto.getText()+"', '"+flag+"')");
        model_auto.getScene().getWindow().hide();
        dataBaseHandler.GetConnection().close();
    }
}