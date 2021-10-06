//отложенная отправка сообщений
package ru.app;

import java.util.Timer;
import java.util.TimerTask;

public class Remember {
    private Timer mTimer = new Timer();
    private MyTimerTask mMyTimerTask = new MyTimerTask();

    public void startTimer(){
        // 1000 - время в мс, через которое будет запущена задача
        mTimer.schedule(mMyTimerTask, 1000);
    }

    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            // Вот тут делаем всё что нужно (отправляем данные в телеграмм и т.п.)
        }
    }
}
