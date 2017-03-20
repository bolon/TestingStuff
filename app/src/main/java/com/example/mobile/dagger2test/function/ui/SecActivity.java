package com.example.mobile.dagger2test.function.ui;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.mobile.dagger2test.App;
import com.example.mobile.dagger2test.R;
import com.example.mobile.dagger2test.dependency.modules.network.ConnectionInterface;
import com.jakewharton.rxbinding.view.RxView;

import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class SecActivity extends AppCompatActivity {
    Button b;

    @Inject
    @Named("interceptorOn")
    ConnectionInterface connectionInterface;
    int key = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sec);

        App.getApplicationComp().inject(this);

        doA();
        doZip();
    }

    void doA() {
        connectionInterface.callFunctA("X")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(someResponse -> {
                })
                .buffer(3)
                .map(someResponses -> someResponses.get(someResponses.size()))
                .flatMap(someResponse -> {
                    View v = null;
                    if (v.isShown()) {

                    }
                    String x = String.valueOf(someResponse.getCode());
                    String y = someResponse.getSomeResponse();

                    return Observable.just(x, y);
                })
                .map(s -> {
                    try {
                        return Integer.valueOf(s);
                    } catch (Exception ex) {
                        return -99;
                    }
                })
                .map(cvrtRslt -> {
                    long cvrt = cvrtRslt;

                    cvrt += SystemClock.currentThreadTimeMillis();

                    return cvrt;
                })
                .doOnError(throwable -> Timber.i(throwable.getMessage()))
                .doOnCompleted(() -> Timber.i("its all done ma frendxz"));
    }

    /**
     * If u want to set multiple subscriber for single oberserable, consider using publish
     * and then use autoconnect
     */
    void doZip() {
        Observable<String> observable = Observable.just("event1", "event2");
        observable.map(s -> {
            Timber.i(s + " | do map once");
            return s + s;
        })
                .publish()
                .autoConnect(2);

        Observable<Integer> obs2 = Observable.just(1, 2, 3);
        obs2
                .publish()
                .autoConnect(1);

//-----------------------
        observable.subscribe(s -> {
            Timber.i(s + " from subscriber1");
        });

        observable.subscribe(s -> {
            Timber.i(s + " from subscriber2");
        });

        obs2.zipWith(observable, (integer, s) -> {
            HashMap<Integer, String> hashMap = new HashMap<>();
            hashMap.put(integer, s);
            key = integer;

            return hashMap;
        })
                .map(integerStringHashMap -> {
                    Timber.i(integerStringHashMap.get(key));
                    return key;
                })
                .doOnCompleted(() -> Timber.i("its done"));
    }

    void testReactiveView() {
        int phraseTarget = 5;
        int questionTarget = 7;

        double targetWeighing = 80, teacherWeighing = 80, currentShufflerLvl = 0.6;


        Subscriber<String> stringSubscriber = new Subscriber<String>() {
            @Override
            public void onCompleted() {
                Timber.i("its finish pt1");
            }

            @Override
            public void onError(Throwable e) {
                Timber.i(e.getMessage());
            }

            @Override
            public void onNext(String s) {
                Timber.i("from previous obersrvabke" + s);
            }
        };


    }

    void testParcelAble() {
        Subscription btnSub = RxView.clicks(b).subscribe(aVoid -> Timber.i("btn clicked"));
        btnSub.unsubscribe();

        b.setOnClickListener(view -> Timber.i("bt also clicked"));
    }
}

