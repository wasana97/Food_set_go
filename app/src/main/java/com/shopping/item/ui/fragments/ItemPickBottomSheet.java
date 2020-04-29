package com.shopping.item.ui.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.shopping.item.BaseApplication;
import com.shopping.item.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.shopping.item.model.dto.Item;
import com.shopping.item.ui.activities.MainActivity;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ItemPickBottomSheet extends BottomSheetDialogFragment {


    final String TAG = ItemPickBottomSheet.this.getClass().getSimpleName();
    private static String BUNDLE_EXTRA = "BUNDLE_EXTRA";

    public static String getTAG() {
        return "ItemPickBottomSheet";
    }


    public ItemPickBottomSheet() { }

    public static ItemPickBottomSheet itemPickBottomSheet = null;

    private  Item mItem;
    private String[] mTypes = {"Black", "White", "Gold"};

    public static ItemPickBottomSheet newInstance() {
        ItemPickBottomSheet fragment = new ItemPickBottomSheet();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @BindView(R.id.btn_cart_layout) RelativeLayout btnCart;
    @BindView(R.id.btn_my_orders_layout) RelativeLayout btnOrders;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
        if (getArguments() != null) {
            mItem = Parcels.unwrap(getArguments().getParcelable(BUNDLE_EXTRA));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.bottom_sheet_item_pick, container, false);
        ButterKnife.bind(this, rootView);
        itemPickBottomSheet = this;
        setUpUI();
        return rootView;
    }



    private void setUpUI() {

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!BaseApplication.getBaseApplication().isLoadCartSheet()) {
                    BaseApplication.getBaseApplication().setLoadCartSheet(true);
                    dismiss();
                    gotoCartFragment();

                }
            }
        });

        btnOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!BaseApplication.getBaseApplication().isLoadMyOrderScreen()) {
                    BaseApplication.getBaseApplication().setLoadMyOrderScreen(true);
                    dismiss();
                    gotoMyOrdersFragment();

                }
            }
        });
    }

    private void gotoCartFragment(){
        ((MainActivity) getActivity()).addFragment(new CartFragment().newInstance(), CartFragment.getTAG());
    }

    private void gotoMyOrdersFragment(){
        ((MainActivity) getActivity()).addFragment(new MyOrderFragment().newInstance(), MyOrderFragment.getTAG());
    }

    @Override
    public void onDestroyView() {
        itemPickBottomSheet = null;
        super.onDestroyView();
        BaseApplication.getBaseApplication().setLoadBottomSheet(false);
    }

}
