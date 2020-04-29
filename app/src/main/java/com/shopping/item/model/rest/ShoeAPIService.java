package com.shopping.item.model.rest;

import com.shopping.item.BuildConfig;
import com.shopping.item.common.constants.DomainConstants;
import com.shopping.item.model.rest.exception.RxErrorHandlingCallAdapterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ShoeAPIService {

    private ShoeAPI shoeAPI;

    public ShoeAPIService() {
        String url = DomainConstants.SERVER_URL;
        Retrofit restAdapter = new Retrofit.Builder()
                .baseUrl(url)
                .client(getClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())
                .build();
        shoeAPI = restAdapter.create(ShoeAPI.class);
    }

    private OkHttpClient getClient() {
        OkHttpClient.Builder client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS);

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor(); // Log Requests and Responses
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            client.addInterceptor(logging);
        }

        return client.build();
    }

    public ShoeAPI getApi(){
        return  shoeAPI;
    }
}
