package com.shopping.item.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shopping.item.BaseApplication;
import com.shopping.item.R;
import com.shopping.item.common.CommonUtils;
import com.shopping.item.common.constants.ApplicationConstants;
import com.shopping.item.model.dto.Item;
import com.shopping.item.ui.activities.MainActivity;
import com.shopping.item.ui.adapters.ItemListAdapter;
import com.shopping.item.ui.adapters.MyOrderAdapter;
import com.shopping.item.utils.BaseBackPressedListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyOrderFragment  extends BaseFragment implements BaseBackPressedListener.OnBackPressedListener {

    final String TAG = MyOrderFragment.this.getClass().getSimpleName();

    public static MyOrderFragment newInstance() {
        MyOrderFragment fragment = new MyOrderFragment();
        return fragment;
    }

    public static MyOrderFragment myOrderFragment;
    private MyOrderAdapter myOrderAdapter;
    private List<Item> mItemList = new ArrayList<>();

    RecyclerView mRecyclerView;
    private DatabaseReference mDatabaseReference;
    @BindView(R.id.list_layout)
    RelativeLayout listLayout;
    @BindView(R.id.empty_cart_layout)
    RelativeLayout emptyLayout;

    public static String getTAG() {
        return "MyOrderFragment";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = null;
        try {
            rootView = inflater.inflate(R.layout.fragment_my_orders, container, false);
            Log.d(TAG, "onCreateView");
            ButterKnife.bind(this, rootView);
            myOrderFragment = this;
            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
            ((MainActivity) getActivity()).setOnBackPressedListener(this);
        } catch (Exception e) {
            Log.e(TAG, "onCreateView: " + e.toString());
        }
        return rootView;
    }


    @Override
    protected void initializePresenter() {

    }

    @Override
    protected void setUpUI() {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("order");
        performGetOrderRequest();
    }

    public void toggleView(boolean isEmpty) {
        if (isEmpty) {
            emptyLayout.setVisibility(View.VISIBLE);
            listLayout.setVisibility(View.GONE);
        } else {
            listLayout.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
        }
    }

    private void performGetOrderRequest() {
        if (CommonUtils.getInstance().isNetworkConnected()) {
            setProgressDialog(true);
            getItemList();
        } else {
            showAlertDialog(false, ApplicationConstants.WARNING,
                    ApplicationConstants.ERROR_MSG_CONNECTION_LOST, null);
        }
    }

    private void getItemList(){

        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    Item item = ds.getValue(Item.class);
                    if(item != null){
                        mItemList.add(item);
                    }
                }
                if(mItemList.size() > 0 ){
                    toggleView(false);
                    showItemList(mItemList);
                }else {
                    toggleView(true);
                }
                setProgressDialog(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                setProgressDialog(false);
            }
        });
    }

    private void showItemList(List<Item> itemList){
        //mItemList.clear();
        // mArticleList = articles;
        initRecyclerViwe();
        myOrderAdapter.updateData(itemList);
    }

    private  void initRecyclerViwe(){
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLayoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setNestedScrollingEnabled(false);

        myOrderAdapter = new MyOrderAdapter(getActivity(), new ArrayList<Item>());
        mRecyclerView.setAdapter(myOrderAdapter);
    }
    @Override
    protected void setUpToolBar() {
        View mCustomView = getLayoutInflater().inflate(R.layout.custom_action_bar_with_back, null);
        TextView title = (TextView) mCustomView.findViewById(R.id.title);
        mToolBar.addView(mCustomView);
        mCustomView.findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
            }
        });
        title.setTypeface(CommonUtils.getInstance().getFont(getActivity(), ApplicationConstants.FONT_ROBOTO_BOLD));
        title.setText("My Orders");
        Toolbar parent = (Toolbar) mCustomView.getParent();
        parent.setPadding(0, 0, 0, 0);//for tab otherwise give space in tab
        parent.setContentInsetsAbsolute(0, 0);
    }

    @Override
    public void doBack() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        myOrderFragment = null;
        BaseApplication.getBaseApplication().setLoadMyOrderScreen(false);
    }
}
