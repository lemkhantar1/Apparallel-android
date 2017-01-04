package com.lemkhantar.apparallel.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.lemkhantar.apparallel.R;
import com.lemkhantar.apparallel.activity.MainActivity;
import com.lemkhantar.apparallel.database.DBManager;
import com.lemkhantar.apparallel.entity.Tache;

import java.util.Timer;
import java.util.TimerTask;

public class TimeService extends Service {

    public static final long NOTIFY_INTERVAL = 5000; // 1 secondes
    public static  int idTache = -1;
    private final IBinder myBinder = new MyLocalBinder();
    boolean isStopped = false;

    private Handler mHandler = new Handler();
    private Timer mTimer = null;

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public void onCreate() {
        if (mTimer != null)
        {
            mTimer.cancel();
        }
        else
        {
            mTimer = new Timer();
        }
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        DBManager dbManager = new DBManager(getApplicationContext());
        dbManager.openDataBase();
        idTache = dbManager.getTacheActive();
        return START_STICKY;
    }

    public class MyLocalBinder extends Binder {
        public TimeService getService() {
            return TimeService.this;
        }
    }

    class TimeDisplayTimerTask extends TimerTask
    {

        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {


                    DBManager dbManager = new DBManager(getApplicationContext());
                    dbManager.openDataBase();
                    Tache tache = dbManager.getTacheById(idTache);
                    if(tache != null)
                    {
                        if(tache.get_tempsecoule()<tache.get_duree())
                        {
                            dbManager.incrementerTache(idTache);
                            if(tache.get_tempsecoule()+5 == tache.get_duree())
                            {
                                createNotification(tache.get_titre());
                                mTimer.cancel();
                                isStopped = true;

                            }

                        }

                    }


                }
            });
        }

    }

    public void updateId(int newIdTache)
    {

        idTache = newIdTache;
        if(isStopped)
        {
            mTimer = new Timer();
            mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL);
            isStopped = false;
        }
    }

    public static int getIdTache() {
        return idTache;
    }

    public final void createNotification(String tache)
    {

        PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo_time_manager)
                .setContentTitle("APParallel")
                .setContentText("tache: "+tache+" est terminée")
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();
        notification.sound = Uri.parse("android.resource://"
                + getApplicationContext().getPackageName() + "/" + R.raw.alarm);
        long[] vibrate = { 0,300,200,300 };
        notification.vibrate = vibrate;
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);

    }
}