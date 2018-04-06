package com.example.xiaohou;

import android.graphics.drawable.Drawable;

/**
 * Created by Mr.Hou on 2018/2/12.
 */

public class AppInfo {
    private Drawable image;
    private String appName;
    private String appPackage;
    private boolean isChecked;

    public AppInfo(Drawable image, String appName,String appPackage,boolean isChecked) {
        this.image = image;
        this.appName = appName;
        this.appPackage = appPackage;
        this.isChecked = isChecked;
    }
    public AppInfo() {
        isChecked = false;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppPackage() {
        return appPackage;
    }

    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
    }

    public boolean getChecked(){
        return isChecked;
    }

    public void setChecked(boolean isChecked){
        this.isChecked = isChecked;
    }
}
