package com.shopping.item.domain;

import com.shopping.item.model.entities.response.ItemListResponse;

import io.reactivex.Single;

public interface ShoeService extends Service {

    Single<ItemListResponse> getShoe();
}
