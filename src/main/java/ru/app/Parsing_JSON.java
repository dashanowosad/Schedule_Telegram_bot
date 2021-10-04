package ru.app;

import java.io.IOException;

public class Parsing_JSON {
    private String Text; //текст персонажа
    private String UserId; //id пользователя
    private String Language; //язык пользователя

    public Parsing_JSON(String JSON) throws IOException {
        this.Text = JSON.substring(JSON.indexOf("text="), JSON.indexOf(", entities"))
                .replace("text='","")
                .replace("'","");
        this.UserId = JSON.substring(JSON.indexOf("User{id="), JSON.indexOf(", is_bot"))
                .replace("User{id=","");
        this.Language = JSON.substring(JSON.indexOf("language_code='"), JSON.indexOf(", can_join_groups"))
                .replace("language_code='","")
                .replace("'","");
    }

    public String GetText(){
        return this.Text;
    }

    public String GetUserId(){
        return this.UserId;
    }
    public String GetLanguage(){
        return this.Language;
    }


}
