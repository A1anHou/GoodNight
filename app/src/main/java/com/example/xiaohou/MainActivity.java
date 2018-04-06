package com.example.xiaohou;

import android.app.AlarmManager;
import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private Button selectBlacklistBtn;
    private Button selectTimeBtn;
    private TextView showTimeText;
    private MyDBOpenHelper myDBOpenHelper;
    private SQLiteDatabase db;
    private Calendar calendarStart = Calendar.getInstance();
    private Calendar calendarFinish = Calendar.getInstance();
    private AlarmManager alarmManagerStart;
    private AlarmManager alarmManagerFinish;
    private int state = 0;
    private static final int MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS = 1101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUserPermission();
        myDBOpenHelper = new MyDBOpenHelper(MainActivity.this,"goodnight.db",null,1);
        getTimeFromDataBase(myDBOpenHelper);

        selectBlacklistBtn = (Button)findViewById(R.id.select_blacklist_btn);
        selectBlacklistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,SelectBlacklistAppActivity.class);
                startActivity(intent);
                finish();
            }
        });
        selectTimeBtn = (Button)findViewById(R.id.select_time_btn);
        selectTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,SelectTimeActivity.class);
                startActivity(intent);
                finish();
            }
        });
        showTimeText = (TextView)findViewById(R.id.show_time_text);
        String timeInfo = "开始时间："+calendarStart.get(Calendar.HOUR_OF_DAY)+"时"+calendarStart.get(Calendar.MINUTE)
                +"分\n结束时间："+calendarFinish.get(Calendar.HOUR_OF_DAY)+"时"+calendarFinish.get(Calendar.MINUTE)
                +"分\n状态："+ (state==1 ? "开启" : "关闭");
        showTimeText.setText(timeInfo);
        setAlarm();
    }

    //若为设置“Apps with usage access”权限则引导用户设置
    public void setUserPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!hasPermission()) {
                startActivityForResult(
                        new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),
                        MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
            }
        }
    }

    //从数据库读取数据设置时间
    public void getTimeFromDataBase(SQLiteOpenHelper dbHelper){
        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM timerange",null);
        if(cursor.moveToNext()){
            String startTxt = cursor.getString(cursor.getColumnIndex("start"));
            String finishTxt = cursor.getString(cursor.getColumnIndex("finish"));
            String[] startStr = startTxt.split("_");
            String[] finishStr = finishTxt.split("_");
            calendarStart.set(calendarStart.get(Calendar.YEAR),calendarStart.get(Calendar.MONTH),calendarStart.get(Calendar.DAY_OF_MONTH),Integer.parseInt(startStr[0]),Integer.parseInt(startStr[1]));
            calendarFinish.set(calendarFinish.get(Calendar.YEAR),calendarFinish.get(Calendar.MONTH),calendarFinish.get(Calendar.DAY_OF_MONTH),Integer.parseInt(finishStr[0]),Integer.parseInt(finishStr[1]));
            state = cursor.getInt(cursor.getColumnIndex("inRange"));
        }
        cursor.close();
    }
    public void setAlarm(){
        Intent intentStart=new Intent();
        intentStart.setAction("com.example.xiaohou.ALARM");
        intentStart.putExtra("action","start");
        PendingIntent piStart=PendingIntent.getBroadcast(MainActivity.this,new Random().nextInt(),intentStart,0);
        Intent intentFinish=new Intent();
        intentFinish.setAction("com.example.xiaohou.ALARM");
        intentFinish.putExtra("action","finish");
        PendingIntent piFinish=PendingIntent.getBroadcast(MainActivity.this,new Random().nextInt(),intentFinish,0);
        alarmManagerStart = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManagerFinish = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManagerStart.setRepeating(AlarmManager.RTC_WAKEUP,calendarStart.getTimeInMillis(),24*60*60*1000,piStart);
        alarmManagerFinish.setRepeating(AlarmManager.RTC_WAKEUP,calendarFinish.getTimeInMillis(),24*60*60*1000,piFinish);
    }
    //检测用户是否对本app开启了“Apps with usage access”权限
    private boolean hasPermission() {
        AppOpsManager appOps = (AppOpsManager)
                getSystemService(Context.APP_OPS_SERVICE);
        int mode = 0;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(), getPackageName());
        }
        return mode == AppOpsManager.MODE_ALLOWED;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS) {
            if (!hasPermission()) {
                //若用户未开启权限，则引导用户开启“Apps with usage access”权限
                startActivityForResult(
                        new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),
                        MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(myDBOpenHelper!=null){
            myDBOpenHelper.close();
        }
    }

}