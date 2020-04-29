package com.shopping.item.model.rest;

import com.shopping.item.model.entities.response.ItemListResponse;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ShoeAPI {

    @GET("words/")
    Single<ItemListResponse> getShoeAPI(

            @Query("letterPattern") String letterPattern);
}
