package ru.app;

import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
        String sqlCommand = "SELECT MAX(number) FROM Remember WHERE id_user =  " + UserId +";";
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
        this.SortedRemembers(UserId);
    }

    private void SortedRemembers(String UserId) throws SQLException {
        String sqlCommand = "SELECT * FROM Remember WHERE id_user =  " + UserId +" ORDER BY date, time;";
        ResultSet resultSet = this.statement.executeQuery(sqlCommand);

        sqlCommand = "DELETE FROM Remember;";
        this.statement.executeUpdate(sqlCommand);
        int i = 1;
        while (resultSet.next()) {
            sqlCommand = "INSERT INTO Remember VALUES(" + i + ", '" +
                    resultSet.getString(2) + "', '" +
                    resultSet.getString(3) + "', '" +
                    resultSet.getString(4) + "', " +
                    resultSet.getInt(5) + ");";
            this.statement.executeUpdate(sqlCommand);
            ++i;
        }
    }
    public String ShowRemembers(String UserId) throws SQLException, ParseException {
        String Response = "";
        String sqlCommand = "SELECT * FROM Remember WHERE id_user =  " + UserId +" ORDER BY date, time;";
        ResultSet resultSet = this.statement.executeQuery(sqlCommand);
        while (resultSet.next()){
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(resultSet.getString(2));
            String resultDate = new SimpleDateFormat("dd/MM/yyyy").format(date);
            Date time = new SimpleDateFormat("hh:mm:ss").parse(resultSet.getString(3));
            String resultTime = new SimpleDateFormat("HH:mm").format(time);
            Response += this.parseNumberToSmile(resultSet.getString(1)) + " " +
                    resultDate + " " +
                    resultTime + " " +
                    resultSet.getString(4) + "\n";
        }
        return Response;
    }

    private String parseNumberToSmile(String number){
        StringBuilder result = new StringBuilder();
        while(number.length() != 0){
            switch (number.charAt(0)){
                case '0' -> result.append("\u0030\u20E3");
                case '1' -> result.append("\u0031\u20E3");
                case '2' -> result.append("\u0032\u20E3");
                case '3' -> result.append("\u0033\u20E3");
                case '4' -> result.append("\u0034\u20E3");
                case '5' -> result.append("\u0035\u20E3");
                case '6' -> result.append("\u0036\u20E3");
                case '7' -> result.append("\u0037\u20E3");
                case '8' -> result.append("\u0038\u20E3");
                case '9' -> result.append("\u0039\u20E3");
            }
            number = number.substring(1, number.length());
        }
        return result.toString();
    }
}
