package com.example.xiaohou;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 小侯同学 on 2018/2/12.
 * 扫描本地安装的应用,工具类
 */
public class ScanInstalledApp {
    static  String TAG = "ScanInstalledApp";

    public static List<AppInfo> scanLocalInstallAppList(PackageManager packageManager) {
        List<AppInfo> myAppInfos = new ArrayList<AppInfo>();
        try {
            List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
            for (int i = 0; i < packageInfos.size(); i++) {
                PackageInfo packageInfo = packageInfos.get(i);
                //过滤掉系统app
            if ((ApplicationInfo.FLAG_SYSTEM & packageInfo.applicationInfo.flags) != 0) {
                continue;
            }
                AppInfo myAppInfo = new AppInfo();
                myAppInfo.setAppName(packageInfo.applicationInfo.loadLabel(packageManager).toString());
                myAppInfo.setAppPackage(packageInfo.packageName);
                if (packageInfo.applicationInfo.loadIcon(packageManager) == null) {
                    continue;
                }
                myAppInfo.setImage(packageInfo.applicationInfo.loadIcon(packageManager));
                myAppInfos.add(myAppInfo);
            }
        }catch (Exception e){
            Log.e(TAG,"===============获取应用包信息失败");
        }
        return myAppInfos;
    }

    public static boolean isAppInstalled(PackageManager packageManager,String packageName){
        try {
            List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
            for (int i = 0; i < packageInfos.size(); i++) {
                PackageInfo packageInfo = packageInfos.get(i);
                if(packageName.equals(packageInfo.packageName)){
                    return true;
                }
            }
        }catch (Exception e){
            Log.e(TAG,"===============获取应用包信息失败");
        }
        return false;
    }

}
