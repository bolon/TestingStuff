package com.example.mobile.dagger2test.dependency;

import com.example.mobile.dagger2test.App;
import com.example.mobile.dagger2test.dependency.modules.data.DataModule;
import com.example.mobile.dagger2test.dependency.modules.network.NetworkModule;
import com.example.mobile.dagger2test.function.ui.MainActivity;
import com.example.mobile.dagger2test.function.ui.SecActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by mobile on 05/01/2017.
 */

/**
 * Dagger 2 provide injection method in 1 component | the hierarchy :
 * modules -(wrapped in)-> component -(storing components need something | in his case an Application extends
 * class is suffice)->
 */
@Singleton
@Component(modules = {DataModule.class, NetworkModule.class})
public interface ApplicationComponent {
    void inject(MainActivity activity);

    void inject(SecActivity secActivity);
}
