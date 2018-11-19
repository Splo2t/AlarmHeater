package com.causoft.heatersetter;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class MyAlarmService extends Service {
    public MyAlarmService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId){
        Toast.makeText(this, "알람이 울립니다.", Toast.LENGTH_LONG).show();
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
