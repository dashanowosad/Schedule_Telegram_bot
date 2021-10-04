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
        while (resultSet.next()){
            //если пользователь не существует
            if (resultSet.getInt(1) == 0){
                sqlCommand = "INSERT INTO User VALUES(" + UserId + ", '" + Language + "');";
                this.statement.executeUpdate(sqlCommand);
            }
        }
    }
}
