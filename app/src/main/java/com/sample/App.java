package com.sample;

import android.app.Application;
import android.os.Handler;
import android.widget.Toast;

import com.amitshekhar.DebugDB;

import io.objectbox.BoxStore;

public class App extends Application {

    private BoxStore boxStore;
    private static App app;


    @Override
    public void onCreate() {
        super.onCreate();
        boxStore = MyObjectBox.builder().androidContext(this).build();
        app = this;
        DebugDB.setBoxStore(boxStore);


    }

    public static App get() {
        return app;
    }

    public BoxStore boxStore() {
        return boxStore;
    }

}