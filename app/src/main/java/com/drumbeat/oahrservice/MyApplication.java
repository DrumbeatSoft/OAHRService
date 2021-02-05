package com.drumbeat.oahrservice;

import android.app.Application;

import androidx.multidex.MultiDex;

/**
 * @author ZhangYuhang
 * @describe
 * @date 2021/1/11
 * @updatelog
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化MultiDex
        MultiDex.install(this);
    }
}
