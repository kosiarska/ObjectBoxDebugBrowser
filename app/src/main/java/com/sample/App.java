package com.sample;

import android.app.Application;

import com.amitshekhar.ObjectBoxBrowser;

import io.objectbox.BoxStore;

public class App extends Application {

    private BoxStore boxStore;
    private static App app;


    @Override
    public void onCreate() {
        super.onCreate();
        boxStore = MyObjectBox.builder().androidContext(this).build();
        app = this;
        ObjectBoxBrowser.setBoxStore(boxStore);


    }

    public static App get() {
        return app;
    }

    public BoxStore boxStore() {
        return boxStore;
    }

}