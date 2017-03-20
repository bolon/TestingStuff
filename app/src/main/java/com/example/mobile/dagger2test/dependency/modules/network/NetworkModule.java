package com.example.mobile.dagger2test.dependency.modules.network;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by mobile on 05/01/2017.
 */

@Module
public class NetworkModule {
    private static String BASE_URL = "https://plusapi.dyned.com/v1/";

    private Context context;

    public NetworkModule(Context context) {
        this.context = context;
    }

    /**
     * consider using interceptor for repeated called header
     *
     * @return Call.Factory. Sequence in dependency injection
     */
    @Singleton
    @Named("callFactoryInterceptor")
    @Provides
    Call.Factory providesCallFactoryInterceptor() {
        return new OkHttpClient.Builder()
                .readTimeout(10, TimeUnit.SECONDS)
                .addNetworkInterceptor(new CustomHeaderInterceptor(context))
                .build();
    }

    @Singleton
    @Named("callFactory")
    @Provides
    Call.Factory providesCallFactory() {
        return new OkHttpClient.Builder()
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    @Singleton
    @Provides
    Gson providesGson() {
        return new GsonBuilder()
                .create();
    }

    /**
     * Used for multiple baseUrl
     *
     * @param callFactory
     * @param gson
     * @return
     */
    @Provides
    @Named("retrofitInterceptor")
    Retrofit providesRetrofit(@Named("callFactoryInterceptor") Call.Factory callFactory, Gson gson) {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .callFactory(callFactory)
                .build();
    }


    @Provides
    @Named("retrofitNoInterceptor")
    Retrofit providesRetrofit2(@Named("callFactory") Call.Factory callFactory, Gson gson) {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .callFactory(callFactory)
                .build();
    }

    @Named("interceptorOff")
    @Provides
    ConnectionInterface providesConnectionInterface(@Named("retrofitNoInterceptor") Retrofit retrofit) {
        return retrofit.create(ConnectionInterface.class);
    }

    @Named("interceptorOn")
    @Provides
    ConnectionInterface providesConnectionInterface2(@Named("retrofitInterceptor") Retrofit retrofit) {
        return retrofit.create(ConnectionInterface.class);
    }

}
