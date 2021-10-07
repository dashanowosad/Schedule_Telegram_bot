package ru.app;


import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

public class App {
    public static void main(String[] args) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileReader("src/main/resources/app.properties"));
        String token = properties.getProperty("bot.token");
        TelegramBot bot = new TelegramBot(token);
        bot.setUpdatesListener(updates -> {
            updates.forEach(update -> {

                    Thread thread = new Thread(new MyThreads(update, bot));
                    thread.start();
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(thread.getId());

            });
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });

    }
}

class MyThreads implements Runnable{
    private Update update;
    private TelegramBot bot;
    public MyThreads(Update update, TelegramBot bot){
        this.update = update;
        this.bot = bot;
    }
    @Override
    public void run() {
        try {
            Parsing_JSON parsing_json = new Parsing_JSON(this.update);
            Commands commands = new Commands(parsing_json, this.bot, this.update);
        } catch (IOException | SQLException e) {
            this.bot.execute(new SendMessage(this.update.message().chat().id(), e.getMessage()));
        }
    }
}
