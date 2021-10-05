package ru.app;

import com.pengrad.telegrambot.model.Update;

import java.io.IOException;

public class Parsing_JSON {
    private String Text; //текст персонажа
    private String UserId; //id пользователя
    private String Language; //язык пользователя

    public Parsing_JSON(Update JSON) throws IOException {
        this.Text = JSON.message().text();
        this.UserId = JSON.message().from().id().toString();
        this.Language = JSON.message().from().languageCode();
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
