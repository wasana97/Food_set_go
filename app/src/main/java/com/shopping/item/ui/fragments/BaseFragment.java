package com.shopping.item.ui.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.shopping.item.R;
import com.shopping.item.model.rest.ShoeAPIService;
import com.shopping.item.mvp.presenters.Presenter;
import com.shopping.item.ui.activities.MainActivity;
import com.shopping.item.utils.AppScheduler;
import com.shopping.item.utils.IScheduler;
import com.google.android.material.snackbar.Snackbar;
import com.kaopiz.kprogresshud.KProgressHUD;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment  extends Fragment {

    protected ShoeAPIService oacService;
    protected IScheduler scheduler;
    protected Presenter presenter;

    protected Toolbar mToolBar;
    protected RelativeLayout loadingView;
    protected ImageView img = null;
    protected TextView textMessage = null;
    protected AnimationDrawable frameAnimation;
    private ProgressDialog progressDialog = null;
    private KProgressHUD pd = null;

    public static AlertDialog myAlertDialogTwo;

    private Unbinder unbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scheduler = new AppScheduler();
        oacService = new ShoeAPIService();
        initializePresenter();
        if (presenter != null) presenter.onCreate();
        mToolBar = null;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (presenter != null) presenter.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (presenter != null) presenter.onStart();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        unbinder = ButterKnife.bind(this, view);
        getActionBarToolbar(view);
        setupLoading(view);
        setUpUI();
        setUpToolBar();
    }

    @Override
    public void onDestroyView() {
        setProgressDialog(false);
        progressDialog = null;

        super.onDestroyView();
        unbinder.unbind();
    }

    protected abstract void initializePresenter();

    protected abstract void setUpUI();

    protected abstract void setUpToolBar();

    protected Toolbar getActionBarToolbar(View v) {
        mToolBar = (Toolbar) v.findViewById(R.id.toolbar);
        if (mToolBar != null) {
            ((MainActivity) getActivity()).setSupportActionBar(mToolBar);
            mToolBar.setContentInsetsAbsolute(0, 0); /** remove actionbar unnecessary left margin */

        }
        return mToolBar;
    }

    protected void setupLoading(View v) {
        loadingView = (RelativeLayout) v.findViewById(R.id.rl_progress);
    }

    public void setProgressDialog(boolean isLoading) {
        try {
            if (isLoading) {
                if (pd != null) pd.show();
                else pd = KProgressHUD.create(getActivity())
                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                        .setCancellable(true)
                        .setLabel("Please wait")
                        .setAnimationSpeed(1)
                        .setDimAmount(0.3f)
                        .show();

            } else {
                if (pd != null) pd.dismiss();
            }
        } catch (Exception e) {
            Log.e("BaseFragment", "setProgressDialog: " + e.toString());
        }
    }

    protected void showTopSnackBar(String message, int bColor) {
        Snackbar snack = Snackbar.make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        View snackbarView = snack.getView();
        snackbarView.setBackgroundColor(bColor);
        snack.show();
    }

    protected void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(getActivity().getWindow().getDecorView(), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public void showMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    protected void showAlertDialog(boolean setCancelable, String title, String message, DialogInterface.OnClickListener positiveListener) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity())
                .setCancelable(setCancelable)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ok", positiveListener);
        if (!getActivity().isFinishing()) myAlertDialogTwo = alertDialog.show();
    }

    @Override
    public void onDestroy() {
        if(pd != null)pd.dismiss();
        super.onDestroy();
    }

}
