package ru.app;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.Properties;

public class Commands {
    private String Result;
    private String Language;
    private String UserId;
    private String command;
    private Properties properties;

    public Commands(Parsing_JSON parsing_json) throws IOException, SQLException {
        this.Language = parsing_json.GetLanguage();
        this.UserId = parsing_json.GetUserId();
        this.command = parsing_json.GetText();

        ///TODO
        this.properties = new Properties();
        if (this.Language.equals("ru"))
            this.properties.load(new InputStreamReader(new FileInputStream("src/main/resources/lang_ru.properties"),Charset.forName("windows-1252")));
        else
            this.properties.load(new FileReader("src/main/resources/lang_en.properties"));
        ///

        if (command.contains("/start"))
            this.Start();
        else if (command.contains("/help"))
            this.Help();
        else if (command.contains("/add"))
            this.Add(command.replace("/add",""));
        else if (command.contains("/remember"))
            this.Remember(command.replace("/remember",""));
        else if (command.contains("/show_schedule"))
            this.Show_Schedule(command.replace("/show_schedule",""));
        else if (command.contains("/delete"))
            this.Delete(command.replace("/delete",""));
        else
            this.Wrong_Command(command);
    }

    private void Start() throws SQLException, IOException {
        this.Result = this.properties.getProperty(this.Language + ".start");
        DataBase dataBase = new DataBase();
        dataBase.CheckUser(this.UserId, this.Language);
    }

    private void Help(){
        this.Result = this.properties.getProperty(this.Language + ".help");
    }

    private void Add(String text){
        //TODO
        System.out.println(text);
    }

    private void Remember(String text){
        //TODO
        System.out.println(text);
    }
    private void Show_Schedule(String text){
        //TODO
        System.out.println(text);
    }
    private void Delete(String text){
        //TODO
        System.out.println(text);
    }
    private void Wrong_Command(String text){
        this.Result = this.properties.getProperty(this.Language + ".wrong");
    }

    public String GetResult(){
        return this.Result;
    }
}
