package com.shopping.item.mvp.views;

import com.shopping.item.model.entities.response.ItemListResponse;

public interface ShoeView extends View {

    void showShoeResponse(ItemListResponse itemListResponse);
}
