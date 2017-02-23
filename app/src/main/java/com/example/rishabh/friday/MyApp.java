package com.example.rishabh.friday;

import android.app.Application;
import android.content.Context;

/**
 * Created by rishabh on 22/02/17.
 */
public class MyApp extends Application {


    //private static MyApp instance;
    private static Context mContext;

    //public static MyApp getInstance() {
      //  return ;
    //}

    public static Context getContext() {
        //  return instance.getApplicationContext();
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //  instance = this;
        mContext = getApplicationContext();
    }
}
