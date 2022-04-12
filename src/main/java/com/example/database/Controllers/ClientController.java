package com.example.database.Controllers;

import com.example.database.DataBaseHandler;
import com.example.database.Models.Client;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.stage.Stage;
import org.w3c.dom.Text;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ClientController {


    @FXML
    private Button currentRent;
    @FXML
    private TextField passport;
    @FXML
    private TextField days;
    @FXML
    private TextField fio;
    @FXML
    private ComboBox Combobox;
    @FXML
    private Button btn_refresh;
    @FXML
    private Label CostLabel;
    @FXML
    private TextField passportField;
    @FXML
    private Label model;
    @FXML
    private TextField returnPassport;
    @FXML
    private Label FullCost;
    @FXML
    private Label FullDays;
    static private Client client;
    static private int autoCode;
    static private int costDays;
    static private int fullCost;
    static private int CodeOrder;
    static private String modelAuto;
    @FXML
    private Label returnModel;
    @FXML
    private Label returnFullCost;
    @FXML
    private Label returnFullDays;
    @FXML private void AddNewOrderForm() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/example/database/add_client_form.fxml"));
        loader.load();
        Parent root = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setWidth(250);
        stage.setHeight(350);
        stage.showAndWait();
    }
    @FXML private void SaveClient() throws SQLException, IOException {
        boolean isNumber = false;
        for (char c: fio.getText().toCharArray())
        {
            if (Character.isDigit(c))
            {
                isNumber = true;
                break;
            }
        }
        if (!isNumber)
        {
            for (char c: passport.getText().toCharArray())
            {
                if (!Character.isDigit(c))
                {
                    isNumber = true;
                    break;
                }
            }
        }

        if (!isNumber)
        {

            boolean exist = isClientInOrder(Integer.parseInt(passport.getText()));
            if (!exist)
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "У данного клиента уже есть активный заказ. Если срок вашего заказа истек, обратитесь к работнику автопроката.");
                alert.show();
            }
            else if (passport.getText().length() != 6)
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Паспортные данные не может содержать больше 6 цифр");
                alert.show();
            }
            else
            {
                DataBaseHandler dataBaseHandler = new DataBaseHandler();
                Statement statement = dataBaseHandler.GetConnection().createStatement();
                ResultSet result = statement.executeQuery("SELECT * FROM Клиент");
                boolean pasFlag = false;
                boolean fioFlag = false;
                while (result.next())
                {
                    if (Integer.parseInt(passport.getText()) == Integer.parseInt(result.getString("Паспортные_данные")))
                    {
                        pasFlag = true;
                        if (fio.getText().equals(result.getString("ФИО")))
                        {
                            fioFlag = true;
                        }
                        break;
                    }
                }
                if (!pasFlag)
                {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Данного клиента не существует в системе, обратитесь к работнику, чтобы занес вас в систему.");
                    alert.show();
                }
                else if (!fioFlag)
                {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Вы ошиблись в написании ФИО. Повторите ввод.");
                    alert.show();
                }
                else
                {
                    client = new Client(passport.getText(), fio.getText());
                    dataBaseHandler.GetConnection().close();
                    fio.getScene().getWindow().hide();
                    openWindow();
                }

            }
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "FIO cannot consist numbers or Passport cannot consist symbols");
            alert.show();
        }

    }
    private void openWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/example/database/add_order_form.fxml"));
        loader.load();
        Parent root = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setWidth(250);
        stage.setHeight(400);
        stage.show();
    }
    @FXML private void Refresh() throws SQLException {
        DataBaseHandler dataBaseHandler = new DataBaseHandler();
        Statement statement = dataBaseHandler.GetConnection().createStatement();
        ResultSet result = statement.executeQuery("Select * from Автомобиль WHERE Статус_наличия = true");
        dataBaseHandler.GetConnection().close();
        ObservableList<String> results = FXCollections.observableArrayList();
        while (result.next())
        {
            results.add(result.getInt("Код_автомобиля") + ". " + result.getString("Модель_автомобиля"));
        }
        Combobox.setItems(results);
    }
    @FXML private void CalculateCost() throws SQLException {
        String number = Combobox.getValue().toString();
        number = "" + number.charAt(0);
        DataBaseHandler dataBaseHandler = new DataBaseHandler();
        Statement statement = dataBaseHandler.GetConnection().createStatement();
        ResultSet result = statement.executeQuery("Select * from Автомобиль");
        dataBaseHandler.GetConnection().close();
        while (result.next())
        {
            if (Integer.parseInt(number) == result.getInt("Код_автомобиля"))
            {
                break;
            }
        }
        int cost = result.getInt("Стоимость_за_сутки");
        costDays = cost;
        autoCode = result.getInt("Код_автомобиля");
        CostLabel.setText(CostLabel.getText() + cost*Integer.parseInt(days.getText()));
        fullCost = cost*Integer.parseInt(days.getText());
    }
    @FXML private void createOrder() throws SQLException {
        DataBaseHandler dataBaseHandler = new DataBaseHandler();
        Statement statement = dataBaseHandler.GetConnection().createStatement();
        int d = Integer.parseInt(days.getText());
        ResultSet result = statement.executeQuery("SELECT * from Автомобиль");
        while (result.next()) {
            if (autoCode == result.getInt("Код_автомобиля")) {
                break;
            }
        }
        statement.execute("UPDATE Автомобиль SET Статус_наличия = false WHERE Код_автомобиля = '"+autoCode+"' ");
        statement.execute("INSERT INTO Заказ (Код_автомобиля, Паспортные_данные, Стоимость_аренды) values ('" + autoCode + "', '" + client.Passport + "', '" + fullCost + "')");
        ResultSet orders = statement.executeQuery("SELECT * from Заказ");
        int orderNumber = -1;
        while (orders.next()) {
            orderNumber = orders.getInt("Код_заказа");
        }
        if (orderNumber != -1)
        {
            statement.execute("INSERT INTO Стоимость_аренды (Код_заказа, Количество_суток, Стоимость_за_сутки) values ('"+orderNumber+"', '"+d+"', '"+costDays+"')");
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Ошибка");
            alert.show();
        }

        dataBaseHandler.GetConnection().close();
        CostLabel.getScene().getWindow().hide();
    }
    @FXML private void PassportInput() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/example/database/current_rent.fxml"));
        loader.load();
        Parent root = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setWidth(250);
        stage.setHeight(200);
        stage.show();
    }
    @FXML private void AcceptPassport() throws SQLException, IOException, InterruptedException {
        DataBaseHandler dataBaseHandler = new DataBaseHandler();
        Statement statement = dataBaseHandler.GetConnection().createStatement();
        Statement statement2 = dataBaseHandler.GetConnection().createStatement();
        ResultSet result = statement.executeQuery("SELECT * FROM Заказ");
        int code = -1;
        boolean flag = false;
        while (result.next())
        {
            if (Integer.parseInt(result.getString("Паспортные_данные")) == Integer.parseInt(passportField.getText()))
            {
                code = result.getInt("Код_автомобиля");
                flag = true;
                break;
            }
        }
        if (!flag)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "У данного клиентов нет активных заказов.");
            alert.show();
        }
        else
        {
            ResultSet result2 = statement2.executeQuery("SELECT * FROM Стоимость_аренды");
            while (result2.next())
            {
                if (result2.getInt("Код_заказа") == result.getInt("Код_заказа"))
                {
                    break;
                }
            }

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/example/database/info_about_rent.fxml"));
            loader.load();
            Parent root = loader.getRoot();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setWidth(250);
            stage.setHeight(300);
            stage.show();
            costDays = result2.getInt("Количество_суток");
            fullCost = result.getInt("Стоимость_аренды");
            result = statement.executeQuery("SELECT * from Автомобиль");
            while (result.next())
            {
                if (code == result.getInt("Код_автомобиля"))
                {
                    modelAuto = result.getString("Модель_автомобиля");
                }
            }
        }


        dataBaseHandler.GetConnection().close();
    }
    @FXML private void OpenAccept() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/example/database/return_car.fxml"));
        loader.load();
        Parent root = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setWidth(250);
        stage.setHeight(300);
        stage.show();
    }
    @FXML private void AcceptAuto() throws SQLException, IOException {
      DataBaseHandler dataBaseHandler = new DataBaseHandler();
      Statement statement = dataBaseHandler.GetConnection().createStatement();
      ResultSet resultSet = statement.executeQuery("SELECT * from Заказ");
      boolean flag = false;
      while (resultSet.next())
      {
          if (Integer.parseInt(resultSet.getString("Паспортные_данные")) == Integer.parseInt(returnPassport.getText()))
          {
              flag = true;
              break;
          }
      }
      int code = -1;
      int rent_cost = -1;
      int orderCode = -1;
      if (!flag)
      {
          Alert alert = new Alert(Alert.AlertType.ERROR, "У данного клиентов нет активных заказов.");
          alert.show();
      }
      else
      {
          code = resultSet.getInt("Код_автомобиля");
          rent_cost = resultSet.getInt("Стоимость_аренды");
          orderCode = resultSet.getInt("Код_заказа");
          CodeOrder = orderCode;
          resultSet = statement.executeQuery("SELECT * from Автомобиль");
          while (resultSet.next())
          {
              if (resultSet.getInt("Код_автомобиля") == code)
              {
                  break;
              }
          }
          modelAuto = resultSet.getString("Модель_автомобиля");
          fullCost = rent_cost;
          resultSet = statement.executeQuery("SELECT * from Стоимость_аренды");
          while (resultSet.next())
          {
              if (resultSet.getInt("Код_заказа") == orderCode)
              {
                  break;
              }
          }
          costDays = resultSet.getInt("Количество_суток");
          FXMLLoader loader = new FXMLLoader();
          loader.setLocation(getClass().getResource("/com/example/database/return_accept.fxml"));
          loader.load();
          Parent root = loader.getRoot();
          Stage stage = new Stage();
          stage.setScene(new Scene(root));
          stage.setWidth(350);
          stage.setHeight(400);
          stage.show();
          dataBaseHandler.GetConnection().close();
      }
    }
    @FXML private void GetInfoReturn()
    {
        returnModel.setText(returnModel.getText() + modelAuto);
        returnFullDays.setText(returnFullDays.getText() + costDays);
        returnFullCost.setText(returnFullCost.getText() + fullCost);
    }
    @FXML private void returnCar() throws SQLException {
        DataBaseHandler dataBaseHandler = new DataBaseHandler();
        Statement statement = dataBaseHandler.GetConnection().createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM Заказ");
        while (resultSet.next())
        {
            if (resultSet.getInt("Код_заказа") == CodeOrder)
            {
                break;
            }
        }
        int code = resultSet.getInt("Код_автомобиля");
        statement.execute("UPDATE Автомобиль SET Статус_наличия = true WHERE Код_автомобиля = '"+code+"' ");
        statement.execute("DELETE from Стоимость_аренды where Код_заказа = '"+CodeOrder+"' ");
        statement.execute("DELETE from Заказ where Код_заказа = '"+CodeOrder+"' ");
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Заказ успешно удален");
        alert.show();
        returnModel.getScene().getWindow().hide();
    }
    @FXML private void GetInfo()
    {
        FullCost.setText(FullCost.getText() + fullCost);
        FullDays.setText(FullDays.getText() + costDays);
        model.setText(model.getText() + modelAuto);
    }
    boolean isClientInOrder(int passport) throws SQLException {
        DataBaseHandler dataBaseHandler = new DataBaseHandler();
        Statement statement = dataBaseHandler.GetConnection().createStatement();
        ResultSet resultSet = statement.executeQuery("select * from Заказ");
        while (resultSet.next()) {
            if (passport == Integer.parseInt(resultSet.getString("Паспортные_данные"))) {
                return false;
            }
        }
        return true;
    }
}
