package com.example.xiaohou;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;


/**
 * Created by 小侯同学 on 2018/2/19.
 */

public class SelectTimeActivity extends Activity {
    private Button submitBtn;
    private TimePicker startPicker;
    private TimePicker finishPicker;
    private MyDBOpenHelper myDBOpenHelper;
    private SQLiteDatabase db;
    Calendar calendarStart = Calendar.getInstance();
    Calendar calendarFinish = Calendar.getInstance();
    @Override
    public void onCreate(Bundle savedInstanceStats) {
        super.onCreate(savedInstanceStats);
        setContentView(R.layout.activity_selecttime);
        myDBOpenHelper = new MyDBOpenHelper(SelectTimeActivity.this,"goodnight.db",null,1);
        getTimeFromDataBase(myDBOpenHelper);
        submitBtn = (Button)findViewById(R.id.submit_time_btn);
        startPicker = (TimePicker)findViewById(R.id.select_start_time_picker);
        finishPicker = (TimePicker)findViewById(R.id.select_finish_time_picker);
        startPicker.setIs24HourView(true);
        finishPicker.setIs24HourView(true);
        startPicker.setHour(calendarStart.get(Calendar.HOUR_OF_DAY));
        startPicker.setMinute(calendarStart.get(Calendar.MINUTE));
        finishPicker.setHour(calendarFinish.get(Calendar.HOUR_OF_DAY));
        finishPicker.setMinute(calendarFinish.get(Calendar.MINUTE));
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db = myDBOpenHelper.getWritableDatabase();
                String startTxt = startPicker.getHour()+"_"+startPicker.getMinute();
                String finishTxt = finishPicker.getHour()+"_"+finishPicker.getMinute();
                String sql = "UPDATE timerange SET start = ?,finish = ? WHERE timeId = 0";
                db.execSQL(sql,new String[]{startTxt,finishTxt});
                Toast.makeText(SelectTimeActivity.this, startTxt+"--"+finishTxt, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SelectTimeActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
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
        }
        cursor.close();
    }
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(SelectTimeActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        if(myDBOpenHelper!=null){
            myDBOpenHelper.close();
        }
    }
}
