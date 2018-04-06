package com.example.xiaohou;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by 小侯同学 on 2018/2/22.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getStringExtra("action");
        Log.v("AlarmReceiver","action:"+action);
        if(action.equals("start")){
            Intent startAlarm = new Intent(context,AlarmService.class);
            context.startService(startAlarm);
        }else if(action.equals("finish")){
            Intent finishAlarm = new Intent(context,AlarmService.class);
            context.stopService(finishAlarm);
        }
    }
}
