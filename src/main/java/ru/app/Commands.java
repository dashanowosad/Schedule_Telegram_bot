package ru.app;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.util.*;

public class Commands {
    private String Language;
    private String UserId;
    private String command;
    private Properties properties;
    private DataBase dataBase;


    private TelegramBot bot;
    private Update update;
//    private ArrayList<String> remember; //сохраняем 1 отложенное сообщение в виде: дата, время, текст
//    private PriorityQueue <Pair<Integer, String>> remembers; //все отложенные сообщения в порядке *какое раньше произойдет*

    public Commands(Parsing_JSON parsing_json, TelegramBot bot, Update update) throws IOException, SQLException, ParseException {
        this.Language = parsing_json.GetLanguage();
        this.UserId = parsing_json.GetUserId();
        this.command = parsing_json.GetText();
        this.bot = bot;
        this.update = update;
        this.dataBase = new DataBase();

        this.dataBase.CheckUser(this.UserId, this.Language);
        String state = this.dataBase.GetUserState(this.UserId);

        ///TODO использование языка из базы данных (выбор языка пользователем)
        this.properties = new Properties();
        if (this.Language.equals("ru"))
            this.properties.load(new InputStreamReader(new FileInputStream("src/main/resources/lang_ru.properties"),Charset.forName("windows-1252")));
        else
            this.properties.load(new FileReader("src/main/resources/lang_en.properties"));
        ///

        //TODO сделать через switch внутри if
        if (state.equals("-1")) {
            if (command.contains("/start"))
                this.Start();
            else if (command.contains("/help"))
                this.Help();
            else if (command.contains("/add"))
                this.Add(command.replace("/add", ""));
        //remember
            else if (command.contains("/remember"))
                this.Remember();
            else if (command.contains("/show_remember"))
                this.Show_Remembers();
            else if (command.contains("/show_schedule"))
                this.Show_Schedule(command.replace("/show_schedule", ""));
            else if (command.contains("/delete"))
                this.Delete(command.replace("/delete", ""));
            else
                this.Wrong_Command(command);
        }

        //диалог с пользователем, исходя из его состояния
        else {
            switch (state) {
                case "STATE_REMEMBER" -> this.Remember1(this.command);
                case "STATE_REMEMBER1" -> this.Remember2(this.command);
                case "STATE_REMEMBER2" -> this.Remember3(this.command);
            }
        }

    }

    private void Start() {
        String Result = this.properties.getProperty(this.Language + ".start");
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

    private void Remember() throws SQLException {
        this.dataBase.SetUserState(this.UserId, "STATE_REMEMBER");
        this.bot.execute(new SendMessage(this.update.message().chat().id(), this.properties.getProperty(this.Language + ".remember1")));
    }
    private void Remember1(String text) throws SQLException {
        try {
            String[] d = new String[3];
            Calendar calendar = Calendar.getInstance();
            if (text.contains("/"))
                d = text.split("/");
            else if (text.contains("-"))
                d = text.split("-");
            else if (text.contains("."))
                d = text.split("\\.");
            //проверка на формат
            else
                throw new DateTimeException(this.properties.getProperty(this.Language + ".date_error"));
            //проверка на корректность месяца
            if (Integer.parseInt(d[1]) > 12 || Integer.parseInt(d[1]) < 1)
                throw new DateTimeException(this.properties.getProperty(this.Language + ".date2_error"));
            //проверка на год
            if (Integer.parseInt(d[2]) < calendar.get(Calendar.YEAR))
                throw new DateTimeException(this.properties.getProperty(this.Language + ".date3_error"));
            //проверка на месяц
            if ((Integer.parseInt(d[1]) < (calendar.get(Calendar.MONTH) + 1)) &&
                    (Integer.parseInt(d[2]) <= (calendar.get(Calendar.YEAR))))
                throw new DateTimeException(this.properties.getProperty(this.Language + ".date3_error"));
            //проверка на день
            if ((Integer.parseInt(d[0]) < (calendar.get(Calendar.DAY_OF_MONTH))) &&
                    (Integer.parseInt(d[1]) <= (calendar.get(Calendar.MONTH) + 1))&&
                    (Integer.parseInt(d[2]) <= (calendar.get(Calendar.YEAR))))
                throw new DateTimeException(this.properties.getProperty(this.Language + ".date3_error"));
            text = d[0] + "/" + d[1] + "/" + d[2];

            Date date = new SimpleDateFormat("dd/MM/yyyy").parse(text);
            String resDate = new SimpleDateFormat("yyyy-MM-dd").format(date);

            this.dataBase.AddDate(this.UserId, resDate);
            this.dataBase.SetUserState(this.UserId, "STATE_REMEMBER1");
            this.bot.execute(new SendMessage(this.update.message().chat().id(), this.properties.getProperty(this.Language + ".remember2")));
        } catch (ParseException | DateTimeException e) {
            if (e.getClass().getCanonicalName().contains("ParseException"))
                this.bot.execute(new SendMessage(this.update.message().chat().id(), this.properties.getProperty(this.Language + ".date_error")));
            else if (e.getClass().getCanonicalName().contains("DateTimeException"))
                this.bot.execute(new SendMessage(this.update.message().chat().id(),e.getMessage()));
        }
    }
    private void Remember2(String time){
        try {
            //TODO проверки времени (не забыть сравнить с датой)
            this.dataBase.AddTime(this.UserId, time);
            this.dataBase.SetUserState(this.UserId, "STATE_REMEMBER2");
            this.bot.execute(new SendMessage(this.update.message().chat().id(), this.properties.getProperty(this.Language + ".remember3")));
        } catch (SQLException e) {
            this.bot.execute(new SendMessage(this.update.message().chat().id(), this.properties.getProperty(this.Language + ".time_error")));
        }
    }

    private void Remember3(String message){
        try {
            this.dataBase.AddMessage(this.UserId, message);
            this.dataBase.SetUserState(this.UserId, "-1");
            this.bot.execute(new SendMessage(this.update.message().chat().id(), this.properties.getProperty(this.Language + ".remember4")));
        } catch (SQLException e) {
            this.bot.execute(new SendMessage(this.update.message().chat().id(), this.properties.getProperty(this.Language + ".error")));
        }
    }

    private void Show_Remembers() throws SQLException, ParseException {
        String Result = this.dataBase.ShowRemembers(this.UserId);
        this.bot.execute(new SendMessage(update.message().chat().id(), Result));
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

//    private int Updates(String text){
//        try {
//            if (text != null)
//                this.bot.execute(new SendMessage(this.update.message().chat().id(), text));
//
//            GetUpdates getUpdates = new GetUpdates().limit(1).timeout(20000).offset(this.update.updateId() + 1);
//            GetUpdatesResponse updatesResponse = this.bot.execute(getUpdates);
//            this.update = updatesResponse.updates().get(0);
////            this.remember.add(this.update.message().text());
//        }
//        catch (Exception e){
//            System.out.println(e.getMessage());
//            return -1;
//        }
//        return 0;
//    }

}
