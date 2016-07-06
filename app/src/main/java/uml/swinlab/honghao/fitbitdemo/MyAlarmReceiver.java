package uml.swinlab.honghao.fitbitdemo;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.util.Calendar;

import uml.swinlab.honghao.fitbitdemo.fileupload.FileUploader;

/**
 * Created by Honghao on 4/7/2016.
 */
public class MyAlarmReceiver extends WakefulBroadcastReceiver {
    private final String TAG = "AlarmReceiver";
    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "on Receive");
        Intent logService = new Intent(context, LogFitbitService.class);
        startWakefulService(context, logService);
        //new FileUploader(context).execute();
    }

    /* @function setAlarm(Context context)
     *
     */
    public void setAlarm(Context context){
        Log.d(TAG, "Set Alarm");
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, MyAlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent,0);

        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), alarmManager.INTERVAL_HOUR, alarmIntent);
    }

    public void cancleAlarm(Context context){
        if(alarmManager != null){
            alarmManager.cancel(alarmIntent);
        }

    }
}
