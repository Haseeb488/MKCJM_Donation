package com.example.donation;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class MyService extends Service {

    MediaPlayer mp;

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        /*
        mp = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI);
        mp.setLooping(true);
        mp.start();*/
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d("service", "destroyed the app");

        // Send broadcast to restart the app
        Intent broadcastIntent = new Intent(this, RestartReceiver.class);
        sendBroadcast(broadcastIntent);

        super.onDestroy();
    }


    /*
    public void BackgroundPendingIntent()
    {
       // Toast.makeText(this, "background intent", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        // intent.setAction(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 100,
                intent, PendingIntent.FLAG_IMMUTABLE);

        final long DELAY_IN_MILLIS = 2000 + System.currentTimeMillis();
        AlarmManager alarmManager = (AlarmManager)
                getSystemService(Activity.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, DELAY_IN_MILLIS, pendingIntent);

    }

*/
    public void BackgroundPendingIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 100,
                intent, PendingIntent.FLAG_IMMUTABLE);

        final long DELAY_IN_MILLIS = System.currentTimeMillis() + 2000; // 2-second delay
        AlarmManager alarmManager = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, DELAY_IN_MILLIS, pendingIntent);
        }
    }

}