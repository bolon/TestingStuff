package com.example.mobile.dagger2test.dependency.modules.network;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.provider.Settings;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by mobile on 05/01/2017.
 * Interceptor for adding repeated Header.
 * Consider using dynamic Header for non-repetitive data
 */

public class CustomHeaderInterceptor implements Interceptor {
    private static String CONTENT_TYPE_HEADER = "application/x-www-form-urlencoded";
    private Context context;

    public CustomHeaderInterceptor(Context context) {
        this.context = context;

    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = new Request.Builder()
                //.addHeader("Content-type", CONTENT_TYPE_HEADER)   //I think we dont need this, since retrofit have specify the Header type
                .addHeader("X-Device-Token", "DeviceTokenHere")     //consider using these as dynamic. since in every device its changed
                .addHeader("Access-Token", "ACCTOK_here")
                .addHeader("X-Device-Id", getDeviceID())
                .addHeader("X-Device-Time", setLocalTime())
                .addHeader("X-App-Name", getAppName())   //consider using crashlytics to do these
                .addHeader("X-OS-Version", Build.VERSION.RELEASE)
                .build();

        Response response = chain.proceed(request);
        return response;
    }

    private String setLocalTime() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"),
                Locale.getDefault());
        Date currentLocalTime = calendar.getTime();
        DateFormat date = new SimpleDateFormat("Z");
        String localTime = date.format(currentLocalTime).replace("0", "").replace("+", "");

        return localTime;
    }

    private String getDeviceID() {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private String getAppName(){
        Resources res = context.getResources();
        String appName = (String) res.getText(res.getIdentifier("app_name", "string", context.getPackageName()));
        return appName;
    }
}
