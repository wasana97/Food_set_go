package com.shopping.item.ui.activities;

import androidx.annotation.Nullable;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.shopping.item.R;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends Activity {

    final String TAG = SplashActivity.this.getClass().getSimpleName();
    private Class classObj;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            // Remove title bar
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            final SharedPreferences preferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);

            // Remove notification bar
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.activity_splash);


            //RelativeLayout parentLayout = (RelativeLayout) findViewById(R.id.background_layout);
            RelativeLayout appName = (RelativeLayout) findViewById(R.id.app_name_layout);
            // lottieAnimationView = (LottieAnimationView) findViewById(R.id.splash_layout);


            Animation animZoomIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in);
            appName.startAnimation(animZoomIn);


            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            classObj = MainActivity.class;
                            startActivity(new Intent(SplashActivity.this, classObj));
                            finish();
                        }
                    });
                }
            }, 2500);


        } catch (Exception ex) {
            Log.e(TAG, "onCreate: " + ex.toString());
        }
    }
}
