package ru.app;

import javax.xml.crypto.Data;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class DataBase {
    private Statement statement;
    private Connection connection;

    public DataBase() throws IOException, SQLException {
        Properties properties = new Properties();
        properties.load(new FileReader("src/main/resources/db.properties"));

        try {
            this.connection = DriverManager.getConnection(
                    properties.getProperty("db.url"),
                    properties.getProperty("db.user"),
                    properties.getProperty("db.password"));

            this.statement = connection.createStatement();

             }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void CheckUser(String UserId, String Language) throws SQLException {
        String sqlCommand = "SELECT count(*) FROM User WHERE id_user = " + UserId + ";";
        ResultSet resultSet = this.statement.executeQuery(sqlCommand);
        resultSet.next();
        //если пользователь не существует, то добавим его и язык
        if (resultSet.getInt(1) == 0){
            sqlCommand = "INSERT INTO User(id_user, language) VALUES(" + UserId + ", '" + Language + "');";
            this.statement.executeUpdate(sqlCommand);
        }

    }
    public String GetUserState(String UserId) throws SQLException {
        String sqlCommand = "SELECT state FROM User WHERE id_user = " + UserId + ";";
        ResultSet resultSet = this.statement.executeQuery(sqlCommand);
        resultSet.next();
        return resultSet.getString(1);
    }
    public void SetUserState(String UserId, String state) throws SQLException {
        String sqlCommand = "UPDATE User SET state = '" + state + "' WHERE id_user = " + UserId + ";";
        this.statement.executeUpdate(sqlCommand);
    }

/////Remember
    public void AddDate(String UserId, String date) throws SQLException {
        String sqlCommand = "SELECT MAX(number) FROM Remember;";
        ResultSet resultSet = this.statement.executeQuery(sqlCommand);
        resultSet.next();
        int number = resultSet.getInt(1);
        number++;

        sqlCommand = "INSERT INTO Remember(id_user, number, date) VALUES(" + UserId + ", " + number + ", '" + date +"');";
        this.statement.executeUpdate(sqlCommand);
    }

    public void AddTime(String UserId, String time) throws SQLException {
        String sqlCommand = "SELECT MAX(number) FROM Remember WHERE id_user =  " + UserId +";";
        ResultSet resultSet = this.statement.executeQuery(sqlCommand);
        resultSet.next();
        int number = resultSet.getInt(1);

         sqlCommand = "UPDATE Remember SET time = '" + time + "' WHERE id_user = " + UserId + " AND number = " + number + ";";
        this.statement.executeUpdate(sqlCommand);
    }
    public void AddMessage(String UserId, String message) throws SQLException {
        String sqlCommand = "SELECT MAX(number) FROM Remember WHERE id_user =  " + UserId +";";
        ResultSet resultSet = this.statement.executeQuery(sqlCommand);
        resultSet.next();
        int number = resultSet.getInt(1);

        sqlCommand = "UPDATE Remember SET message = '" + message + "' WHERE id_user = " + UserId + " AND number = " + number + ";";
        this.statement.executeUpdate(sqlCommand);
    }
}
