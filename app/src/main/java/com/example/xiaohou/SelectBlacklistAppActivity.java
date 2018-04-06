package com.example.xiaohou;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by 小侯同学 on 2018/2/16.
 */

public class SelectBlacklistAppActivity extends Activity implements AdapterView.OnItemClickListener{
    private ListView appList;
    private AppAdapter appAdapter;
    private Handler handler = new Handler();
    private MyDBOpenHelper myDBOpenHelper;
    private SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceStats) {
        super.onCreate(savedInstanceStats);
        setContentView(R.layout.activity_selectblacklistapp);
        myDBOpenHelper = new MyDBOpenHelper(SelectBlacklistAppActivity.this,"goodnight.db",null,1);
        appList = (ListView)findViewById(R.id.app_list);
        appAdapter = new AppAdapter();
        appList.setAdapter(appAdapter);
        appList.setOnItemClickListener(this);
        initAppList();
    }

    private void initAppList(){
        new Thread(){
            @Override
            public void run(){
                super.run();
                //扫描得到App列表
                final List<AppInfo> appInfos = ScanInstalledApp.scanLocalInstallAppList(SelectBlacklistAppActivity.this.getPackageManager());
                db = myDBOpenHelper.getWritableDatabase();
                Cursor cursor = db.rawQuery("SELECT * FROM blacklist",null);
                if(cursor.moveToFirst()){
                    do{
                        String packageName = cursor.getString(cursor.getColumnIndex("package"));
                        if(ScanInstalledApp.isAppInstalled(SelectBlacklistAppActivity.this.getPackageManager(),packageName)){
                            for(AppInfo appInfo : appInfos){
                                if(appInfo.getAppPackage().equals(packageName)){
                                    appInfo.setChecked(true);
                                }
                            }
                        }else{
                            String sql = "DELETE FROM blacklist WHERE package = ?";
                            db.execSQL(sql,new String[]{packageName});
                        }
                    }while (cursor.moveToNext());
                }
                cursor.close();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        appAdapter.initAppAdapter(appInfos,SelectBlacklistAppActivity.this);
                    }
                });
            }
        }.start();
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        AppAdapter appAdapter = (AppAdapter) adapterView.getAdapter();
        AppInfo item = (AppInfo)appAdapter.getItem(i);
        String msg = item.getAppName();
        appAdapter.changeChecked(i);
        db = myDBOpenHelper.getWritableDatabase();
        if(item.getChecked()){
            String sql = "INSERT INTO  blacklist(package,name) VALUES(?,?)";
            db.execSQL(sql,new String[]{item.getAppPackage(),item.getAppName()});
            Toast.makeText(SelectBlacklistAppActivity.this,msg+"已加入肯德基豪华午餐",Toast.LENGTH_SHORT).show();
        }else {
            String sql = "DELETE FROM blacklist WHERE package = ?";
            db.execSQL(sql,new String[]{item.getAppPackage()});
            Toast.makeText(SelectBlacklistAppActivity.this,msg+"已退出肯德基豪华午餐",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //退出程序时关闭数据库
        if(myDBOpenHelper!=null){
            myDBOpenHelper.close();
        }
    }
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(SelectBlacklistAppActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
