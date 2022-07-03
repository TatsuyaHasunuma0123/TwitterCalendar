package com.example.y3033906.calendar_113033906;

import android.app.Application;
import android.content.Context;

//アプリのContextを取得するクラス
public class MyApplication extends Application {
    private static Context context;

    public void onCreate(){
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }

    public static Context getAppContext(){
        return MyApplication.context;
    }
}
