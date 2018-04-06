package com.example.xiaohou;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 小侯同学 on 2018/2/16.
 */

public class AppAdapter extends BaseAdapter {
    private List<AppInfo> appInfos = new ArrayList<AppInfo>();
    private Context context;

    public void initAppAdapter(List<AppInfo> appInfos,Context context){
        this.appInfos = appInfos;
        this.context = context;
        notifyDataSetChanged();
    }

    public void changeChecked(int i){
        AppInfo appInfo = appInfos.get(i);
        if(appInfo.getChecked()){
            appInfo.setChecked(false);
        }else{
            appInfo.setChecked(true);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if(appInfos!=null&&appInfos.size()>0){
            return appInfos.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int i) {
        if(appInfos!=null&&appInfos.size()>0){
            return appInfos.get(i);
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        if (appInfos!=null&&appInfos.size()>0){
            return i;
        }
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        AppInfo appInfo = appInfos.get(i);
        view = LayoutInflater.from(context).inflate(R.layout.item_app_info,null);
        ImageView img_icon = (ImageView)view.findViewById(R.id.app_icon);
        TextView txt_appName = (TextView)view.findViewById(R.id.app_name);
        img_icon.setImageDrawable(appInfo.getImage());
        txt_appName.setText(appInfo.getAppName());
        if(appInfo.getChecked()){
            view.setBackgroundColor(view.getResources().getColor(R.color.colorAccent));
        }
        return view;
    }
}
