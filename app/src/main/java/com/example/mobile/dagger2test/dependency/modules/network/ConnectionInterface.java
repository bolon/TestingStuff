package com.example.mobile.dagger2test.dependency.modules.network;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by mobile on 05/01/2017.
 */

public interface ConnectionInterface {
    @GET("doA/{id}/test")
    Call<String> callFuncDoA(@Path("id") int id);

    @POST("doB")
    Call<String> callFuncDoB(@Body String X, @Body String Y, @Header("Access-Token") String access_token);

    /**
     * @param fileName -> Attached to base url in NetworkModule.
     * @return ResponseBody. ResponBody used to stream data in retrofit. Please read the doc.
     * Actually we can append the url using base url. no need this path.
     * <p>
     * Consider adding the method with @Streaming , when downloading large file.
     * And wrap the retrofit call in an Asyntask since Android will think that u call networking thread on main thread.
     * U read it right !.
     */
    @GET
    Call<ResponseBody> callFunctDoC(@Url String url);

    @POST("auth/signin")
    Call<ResponseLogin> requestLogin(@Body HashMap<String, String> usernamePair);

    @GET("doA")
    Observable<SomeResponse> callFunctA(@Body String X);
}

class ResponseLogin {
    int code;
    String response;
}

