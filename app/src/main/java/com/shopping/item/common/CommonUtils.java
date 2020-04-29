package com.shopping.item.common;

import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;

import com.shopping.item.BaseApplication;
import com.shopping.item.common.constants.ApplicationConstants;

public class CommonUtils {
    private static CommonUtils instance = null;

    private CommonUtils() {}

    public static CommonUtils getInstance() {
        if (instance == null) instance = new CommonUtils();
        return instance;
    }

    public Typeface getFont(Context mContext, String font) {
        switch (font) {
            case ApplicationConstants.FONT_ACUMIN_PRO_BOLD:
                return Typeface.createFromAsset(mContext.getAssets(), "font/Acumin Pro Bold.otf");
            case ApplicationConstants.FONT_ACUMIN_PRO_SEMIBOLD:
                return Typeface.createFromAsset(mContext.getAssets(), "font/Acumin Pro Semibold.otf");
            case ApplicationConstants.FONT_ACUMIN_PRO_REGULAR:
                return Typeface.createFromAsset(mContext.getAssets(), "font/Acumin Pro Book.otf");
            default:
                return null;
        }
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) BaseApplication.getBaseApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}
