package com.example.xiaohou;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by 小侯同学 on 2018/2/21.
 */
public class AlarmService extends Service {
    public static final String TAG = "AlarmService";
    private MyThread myThread = null;

    private static class MyThread extends Thread {
        private Context context;
        private boolean isRun = true;
        private MyThread(Context context) {
            this.context = context;
        }
        public void setStop() {
            isRun = false;
        }
        @Override
        public void run() {
            MyDBOpenHelper myDBOpenHelper = new MyDBOpenHelper(context,"goodnight.db",null,1);
            while (isRun) {
                try {
                    TimeUnit.SECONDS.sleep(2);
                    getTopApp(context,myDBOpenHelper);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            myDBOpenHelper.close();
//            Intent intent = new Intent(context,AlarmService.class);
//            context.stopService(intent);
        }

        private void getTopApp(Context context, SQLiteOpenHelper myDBOpenHelper) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                UsageStatsManager m = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
                if (m != null) {
                    long now = System.currentTimeMillis();
                    //获取60秒之内的应用数据
                    List<UsageStats> stats = m.queryUsageStats(UsageStatsManager.INTERVAL_BEST, now - 60 * 1000, now);
                    Log.i(TAG, "Running app number in last 60 seconds : " + stats.size());

                    String topActivity = "";

                    //取得最近运行的一个app，即当前运行的app
                    if ((stats != null) && (!stats.isEmpty())) {
                        int j = 0;
                        for (int i = 0; i < stats.size(); i++) {
                            if (stats.get(i).getLastTimeUsed() > stats.get(j).getLastTimeUsed()) {
                                j = i;
                            }
                        }
                        topActivity = stats.get(j).getPackageName();
                    }
                    Log.i(TAG, "top running app is : "+topActivity);
                    SQLiteDatabase db = myDBOpenHelper.getWritableDatabase();
                    Cursor cursor = db.rawQuery("SELECT * FROM blacklist",null);
                    if(cursor.moveToFirst()){
                        do{
                            String packageName = cursor.getString(cursor.getColumnIndex("package"));
                            if(packageName.equals(topActivity)){
                                Log.v(TAG,topActivity+"非法启动");
                                Intent home=new Intent(Intent.ACTION_MAIN);
                                home.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                home.addCategory(Intent.CATEGORY_HOME);
                                context.startActivity(home);
                            }
                        }while (cursor.moveToNext());
                    }
                    cursor.close();
                }
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myThread = new MyThread(this);
        myThread.start();
        Log.i(TAG, "Service is start.");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myThread.setStop();
        Log.i(TAG, "Service is stop.");
    }
}
