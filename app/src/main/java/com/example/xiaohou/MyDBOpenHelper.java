package com.example.xiaohou;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 小侯同学 on 2018/2/19.
 */

public class MyDBOpenHelper extends SQLiteOpenHelper {

    public MyDBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //创建应用黑名单表
        sqLiteDatabase.execSQL("CREATE TABLE blacklist(appId INTEGER PRIMARY KEY AUTOINCREMENT,package VARCHAR(100),name VARCHAR(50))");
        //创建闹钟区间表
        sqLiteDatabase.execSQL("CREATE TABLE timerange(timeId INTEGER PRIMARY KEY,start VARCHAR(20),finish VARCHAR(20),inRange INTEGER)");
        sqLiteDatabase.execSQL("INSERT INTO timerange(timeId,start,finish,inRange) VALUES(0,'0_0','0_0',2)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
