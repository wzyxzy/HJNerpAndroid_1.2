package com.hjnerp.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;

public class WorkService extends Service {
    private Timer timer;
    private TimerTask task;
    private int count;
    public WorkService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();

        final Intent intent = new Intent();
        intent.setAction("com.hjnerp.service.Work");
        timer = new Timer();
        task = new TimerTask() {

            @Override
            public void run() {
                sendBroadcast(intent);
            }
        };
        timer.schedule(task, 1000, 30000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

}
