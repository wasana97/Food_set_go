package com.shopping.item.ui.fragments;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shopping.item.BaseApplication;
import com.shopping.item.R;
import com.shopping.item.common.CommonUtils;
import com.shopping.item.common.constants.ApplicationConstants;
import com.shopping.item.model.dto.Item;
import com.shopping.item.model.entities.response.ItemListResponse;
import com.shopping.item.ui.activities.MainActivity;
import com.shopping.item.ui.adapters.ItemListAdapter;
import com.shopping.item.utils.BaseBackPressedListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeFragment extends BaseFragment implements BaseBackPressedListener.OnBackPressedListener {

    final String TAG = HomeFragment.this.getClass().getSimpleName();

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    public static HomeFragment homeFragment;
    private static String SCORE_PREFERENCE = "SCORE_PREFERENCE";
    private static String SCORE = "SCORE";

    private ItemListAdapter itemListAdapter;
    private List<Item> mItemList = new ArrayList<>();

    RecyclerView mRecyclerView;
    private DatabaseReference mDatabaseReference;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = null;
        try {
            rootView = inflater.inflate(R.layout.fragment_home, container, false);
            Log.d(TAG, "onCreateView");
            ButterKnife.bind(this, rootView);
            homeFragment = this;
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
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("itemList");
        performGetItemsRequest();
    }



    @Override
    protected void setUpToolBar() {
        View mCustomView = getLayoutInflater().inflate(R.layout.custom_action_bar_home, null);
        TextView title = (TextView) mCustomView.findViewById(R.id.title);
        mToolBar.addView(mCustomView);
        mCustomView.findViewById(R.id.shopping_cart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!BaseApplication.getBaseApplication().isLoadBottomSheet()) {
                    BaseApplication.getBaseApplication().setLoadBottomSheet(true);
                    //gotoCartFragment();
                    loadBottomSheet();
                }

            }
        });
        title.setTypeface(CommonUtils.getInstance().getFont(getActivity(), ApplicationConstants.FONT_ROBOTO_BOLD));
        title.setText("Online Food Order");
        Toolbar parent =(Toolbar) mCustomView.getParent();
        parent.setPadding(0,0,0,0);//for tab otherwise give space in tab
        parent.setContentInsetsAbsolute(0,0);
    }

    private void gotoCartFragment(){
        ((MainActivity) getActivity()).addFragment(new CartFragment().newInstance(), CartFragment.getTAG());
    }

    @Override
    public void doBack() {

    }

    private void performGetItemsRequest() {
        if (CommonUtils.getInstance().isNetworkConnected()) {
                setProgressDialog(true);
                getItemList();
        } else {
            showAlertDialog(false, ApplicationConstants.WARNING,
                    ApplicationConstants.ERROR_MSG_CONNECTION_LOST, null);
        }
    }

    private void getItemList(){

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    Item item = ds.getValue(Item.class);
                    if(item != null){
                        mItemList.add(item);
                    }
                }
                if(mItemList.size() > 0 ){
                    showItemList(mItemList);
                }
                setProgressDialog(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                setProgressDialog(false);
            }
        });
    }

    private  void initRecyclerViwe(){
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLayoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setNestedScrollingEnabled(false);

        itemListAdapter = new ItemListAdapter(getActivity(), new ArrayList<Item>());
        mRecyclerView.setAdapter(itemListAdapter);
    }
    private void showItemList(List<Item> itemList){
        //mItemList.clear();
       // mArticleList = articles;
        initRecyclerViwe();
        itemListAdapter.updateData(itemList);
    }

    public void gotoItemDetailScreen(Item item){
            ((MainActivity) getActivity()).addFragment(new ItemDetailsFragment().newInstance(item), ItemDetailsFragment.getTAG());
    }

    public  void loadBottomSheet(){
        ItemPickBottomSheet bottomSheet = new ItemPickBottomSheet().newInstance();
        bottomSheet.show(getFragmentManager(), ItemPickBottomSheet.getTAG());
    }
}
