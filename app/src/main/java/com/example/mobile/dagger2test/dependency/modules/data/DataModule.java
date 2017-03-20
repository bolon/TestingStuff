package com.example.mobile.dagger2test.dependency.modules.data;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by mobile on 05/01/2017.
 */

@Module
public class DataModule {
    private Application app;

    public DataModule(Application app){
        this.app = app;
    }

    @Singleton
    @Provides
    SharedPreferences provideSharedPreferences(){
        return PreferenceManager.getDefaultSharedPreferences(app);
    }
}
