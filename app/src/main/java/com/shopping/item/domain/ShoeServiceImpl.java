package com.shopping.item.domain;

import com.shopping.item.model.entities.response.ItemListResponse;
import com.shopping.item.model.rest.ShoeAPIService;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class ShoeServiceImpl implements  ShoeService {

   private ShoeAPIService shoeAPIService;

   public ShoeServiceImpl(ShoeAPIService shoeAPIService){
       this.shoeAPIService = shoeAPIService;
   }
    @Override
    public Single<ItemListResponse> getShoe() {
        return shoeAPIService.getApi().getShoeAPI("kk")
                .observeOn(AndroidSchedulers.mainThread());
    }
}
