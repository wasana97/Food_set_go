package com.shopping.item.mvp.presenters;

import com.shopping.item.mvp.views.View;

public interface Presenter {

    void onCreate();
    void onStart();
    void onDestroy();
    void attachView(View v);
    void onStop();
}
