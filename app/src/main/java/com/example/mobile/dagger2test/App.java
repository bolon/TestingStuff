package com.example.mobile.dagger2test;

import android.app.Application;

import com.example.mobile.dagger2test.dependency.ApplicationComponent;
import com.example.mobile.dagger2test.dependency.DaggerApplicationComponent;
import com.example.mobile.dagger2test.dependency.modules.data.DataModule;
import com.example.mobile.dagger2test.dependency.modules.network.NetworkModule;

import timber.log.Timber;

/**
 * Created by mobile on 05/01/2017.
 * Put name in manifest
 */

public class App extends Application {
    private static ApplicationComponent appComp;

    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(new Timber.DebugTree());

        appComp = DaggerApplicationComponent.builder()
                .dataModule(new DataModule(this))
                .networkModule(new NetworkModule(this))
                .build();

        //should build any required things here
    }

    public static ApplicationComponent getApplicationComp() {
        return appComp;
    }
}
