package ru.app;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import kotlin.Pair;

import javax.xml.crypto.Data;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.*;

public class Commands {
    private String Language;
    private String UserId;
    private String command;
    private Properties properties;

    private TelegramBot bot;
    private Update update;
//    private ArrayList<String> remember; //сохраняем 1 отложенное сообщение в виде: дата, время, текст
//    private PriorityQueue <Pair<Integer, String>> remembers; //все отложенные сообщения в порядке *какое раньше произойдет*

    public Commands(Parsing_JSON parsing_json, TelegramBot bot, Update update) throws IOException, SQLException {
        this.Language = parsing_json.GetLanguage();
        this.UserId = parsing_json.GetUserId();
        this.command = parsing_json.GetText();
        this.bot = bot;
        this.update = update;

//        this.remember = new ArrayList<>();

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
        String Result = this.properties.getProperty(this.Language + ".start");
        DataBase dataBase = new DataBase();
        dataBase.CheckUser(this.UserId, this.Language);

        this.bot.execute(new SendMessage(update.message().chat().id(), Result));
    }

    private void Help(){
        String Result = this.properties.getProperty(this.Language + ".help");
        this.bot.execute(new SendMessage(update.message().chat().id(), Result));
    }

    private void Add(String text){
        //TODO
        System.out.println(text);
    }

    private void Remember(String text){
        if ((this.Updates(this.properties.getProperty(this.Language + ".remember1")) < 0) ||
                (this.Updates(this.properties.getProperty(this.Language + ".remember2")) < 0) ||
                (this.Updates(this.properties.getProperty(this.Language + ".remember3")) < 0)) {
            this.bot.execute(new SendMessage(update.message().chat().id(), this.properties.getProperty(this.Language + ".timeout")));
            return;
        }
        else {
            //TODO отложенные сообщения
//            if (this.remembers == null)
//                this.remembers = new PriorityQueue<>();
//            Calendar calendar = Calendar.getInstance();
//            System.out.println(this.remember.get(0) + " " + this.remember.get(1) + " " + this.remember.get(2) + " ");
//            System.out.println(calendar.get(Calendar.DAY_OF_MONTH) + " " + calendar.get(Calendar.MONTH) + " " + calendar.get(Calendar.YEAR) + " " + calendar.getTime());
//            this.remember.clear();
///////////////////////
            this.bot.execute(new SendMessage(update.message().chat().id(), this.properties.getProperty(this.Language + ".remember4")));
        }
        GetUpdates getUpdates = new GetUpdates().offset(this.update.updateId() + 1);
        GetUpdatesResponse updatesResponse = bot.execute(getUpdates);


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
        String Result = this.properties.getProperty(this.Language + ".wrong");
        this.bot.execute(new SendMessage(update.message().chat().id(), Result));
    }

    private int Updates(String text){
        try {
            if (text != null)
                this.bot.execute(new SendMessage(update.message().chat().id(), text));

            GetUpdates getUpdates = new GetUpdates().limit(1).timeout(20000).offset(this.update.updateId() + 1);
            GetUpdatesResponse updatesResponse = bot.execute(getUpdates);
            this.update = updatesResponse.updates().get(0);
//            this.remember.add(this.update.message().text());
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return -1;
        }
        return 0;
    }

}
