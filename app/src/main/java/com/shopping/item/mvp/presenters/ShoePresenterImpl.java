package com.shopping.item.mvp.presenters;

import android.app.Activity;
import android.util.Log;

import com.shopping.item.common.constants.ApplicationConstants;
import com.shopping.item.domain.Service;
import com.shopping.item.domain.ShoeService;
import com.shopping.item.model.entities.response.ItemListResponse;
import com.shopping.item.model.rest.exception.RetrofitException;
import com.shopping.item.mvp.views.ShoeView;
import com.shopping.item.mvp.views.View;
import com.shopping.item.utils.IScheduler;

import java.io.IOException;

import io.reactivex.Single;
import io.reactivex.observers.DisposableSingleObserver;

public class ShoePresenterImpl extends BasePresenter implements ShoePresenter  {

    private final static String TAG = "ShoePresenterImpl";
    private ShoeView mShoeView;

    public ShoePresenterImpl(Activity activityContext, Service pService, IScheduler scheduler) {
        super(activityContext, pService, scheduler);
    }

    @Override
    public void attachView(View v) {
        if(v instanceof ShoeView){
            mShoeView = (ShoeView) v;
            mView = mShoeView;
        }
    }
    @Override
    public void getShoe() {
        disposable.add(getObservable().subscribeWith(getSubscriber()));

    }

    private Single<ItemListResponse> getObservable(){
        try {
            return getService().getShoe()
                    .subscribeOn(scheduler.backgroundThread())
                    .observeOn(scheduler.mainThread());


        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return null;
    }

    private DisposableSingleObserver<ItemListResponse> getSubscriber(){
        return  new DefaultSubscriber<ItemListResponse>(this.mView){

            @Override
            public void onError(Throwable e) {
                try {
                    RetrofitException error = (RetrofitException) e;
                    if(error.getKind() == RetrofitException.Kind.EXPIRED){
                        ItemListResponse exceptionResponse = new ItemListResponse();
                        exceptionResponse.setSuccess(false);
                        exceptionResponse.setTokenExpired(true);
                        exceptionResponse.setMessage(ApplicationConstants.ERROR_MSG_TOKEN_EXPIRED);
                        mShoeView.showShoeResponse(exceptionResponse);
                    }else {
                        ItemListResponse response = error.getErrorBodyAs(ItemListResponse.class);
                        if(response == null){
                            response = new ItemListResponse();
                            response.setMessage(getExceptionMessage(e));
                            response.setAPIError(false);
                        }else {
                            response.setAPIError(true);
                        }
                        response.setSuccess(false);
                        mShoeView.showShoeResponse(response);
                    }
                }catch (IOException ex){
                    ItemListResponse exceptionResponse = new ItemListResponse();
                    exceptionResponse.setSuccess(false);
                    exceptionResponse.setAPIError(false);
                    exceptionResponse.setMessage(ApplicationConstants.ERROR_MSG_REST_UNEXPECTED);
                    mShoeView.showShoeResponse(exceptionResponse);

                    ex.printStackTrace();
                }
                super.onError(e);
            }

            @Override
            public void onSuccess(ItemListResponse response) {
                if(response != null){
                    response.setSuccess(true);
                    mShoeView.showShoeResponse(response);
                }
            }
        };
    }

    private ShoeService getService (){
        return (ShoeService) mService;
    }
}
