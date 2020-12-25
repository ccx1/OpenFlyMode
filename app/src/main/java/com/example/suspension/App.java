package com.example.suspension;

import android.app.Application;

public class App extends Application {

    private static App sApp;

    public static App getInstance() {
        return sApp;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;
    }
}
