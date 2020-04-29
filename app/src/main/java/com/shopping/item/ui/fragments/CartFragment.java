package com.shopping.item.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.shopping.item.ui.activities.MainActivity;
import com.shopping.item.ui.adapters.CartListAdapter;
import com.shopping.item.ui.adapters.ItemListAdapter;
import com.shopping.item.utils.BaseBackPressedListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CartFragment extends BaseFragment implements BaseBackPressedListener.OnBackPressedListener {

    final String TAG = CartFragment.this.getClass().getSimpleName();

    public static CartFragment newInstance() {
        CartFragment fragment = new CartFragment();
        return fragment;
    }

    public static String getTAG() {
        return "CartFragment";
    }

    public static CartFragment cartFragment;

    private CartListAdapter cartListAdapter;
    private List<Item> mItemList = new ArrayList<>();

    RecyclerView mRecyclerView;
    private DatabaseReference mDatabaseReference;
    Double mTotal;


    @BindView(R.id.txt_total)
    TextView txtTotal;
    @BindView(R.id.btn_checkout)
    Button btnCheckout;
    // @BindView(R.id.checkout_layout) RelativeLayout checkLayout;
    @BindView(R.id.list_layout)
    RelativeLayout listLayout;
    @BindView(R.id.empty_cart_layout)
    RelativeLayout emptyLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = null;
        try {
            rootView = inflater.inflate(R.layout.fragment_cart, container, false);
            Log.d(TAG, "onCreateView");
            ButterKnife.bind(this, rootView);
            cartFragment = this;
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

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("cart");
        initRecyclerViwe();
        performGetCartRequest();

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


    private void performGetCartRequest() {
        if (CommonUtils.getInstance().isNetworkConnected()) {
            setProgressDialog(true);
            getCartItemList();
        } else {
            showAlertDialog(false, ApplicationConstants.WARNING,
                    ApplicationConstants.ERROR_MSG_CONNECTION_LOST, null);
        }
    }

    public void performAddOrReduce(String itemId, int qty, boolean isReduce){
        setProgressDialog(true);
        int quantity;
        quantity = qty;
        if(isReduce){
            if(quantity > 1){
                quantity = quantity -1;
            }
        }else {
            quantity = quantity + 1;
        }
        updateQuantity(itemId, quantity);
        getCartItemList();
    }

    private void updateQuantity(String id, int qty){

        mDatabaseReference.child(id).child("itemQty").setValue(qty);
        mItemList.clear();
    }
    private void getCartItemList() {

        cartListAdapter.updateData(null, 1);
        if (cartFragment != null) {
            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Item item = ds.getValue(Item.class);
                        if (item != null) {
                            mItemList.add(item);
                        }
                    }
                    if (mItemList != null && mItemList.size() > 0) {


                        if (cartFragment != null) {
                            toggleView(false);
                            System.out.println("==============>>" + mItemList);
                            cartListAdapter.updateData(mItemList, 0);

                            btnCheckout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (!BaseApplication.getBaseApplication().isLoadPaymentScreen()) {
                                        BaseApplication.getBaseApplication().setLoadPaymentScreen(true);
                                        gotoPaymentFragment();
                                    }
                                }
                            });
                            getTotalPrice();
                        }


                        // showItemList(mItemList);
                    } else {

                        if (cartFragment != null) {
                            toggleView(true);
                        }

                        //checkLayout.setVisibility(View.GONE);
                    }
                    setProgressDialog(false);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    setProgressDialog(false);
                }
            });
        }

    }

//    private void showItemList(List<Item> itemList){
//        //mItemList.clear();
//        // mArticleList = articles;
//        initRecyclerViwe();
//        cartListAdapter.updateData(itemList, 0);
//    }

    private void gotoPaymentFragment() {
        ((MainActivity) getActivity()).addFragment(new PaymentFragment().newInstance(mItemList, mTotal), PaymentFragment.getTAG());
    }

    public void getTotalPrice() {

        Double total = 0.0;
        for (Item item : mItemList) {
            String price = item.getItemPrice();
            int quantity = item.getItemQty();
            total = total + (Double.parseDouble(price) * quantity);
        }
        mTotal = total;
        txtTotal.setText("Total $" + String.format("%.2f", total));
    }

    public void removeItem(int position) {

        if (mItemList.size() > 1) {
            mItemList.remove(position);
            getTotalPrice();
        } else {
           toggleView(true);
        }
    }

    public void removeFromCart(String id) {
        Query deleteQuery = mDatabaseReference.orderByChild("id").equalTo(id);
        deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot delData : dataSnapshot.getChildren()) {
                    delData.getRef().removeValue();
                }
                Toast.makeText(getActivity(), "Item removed from cart", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initRecyclerViwe() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLayoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setNestedScrollingEnabled(false);

        cartListAdapter = new CartListAdapter(getActivity(), new ArrayList<Item>());
        mRecyclerView.setAdapter(cartListAdapter);
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
        title.setText("Cart");
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
        cartFragment = null;
        BaseApplication.getBaseApplication().setLoadCartSheet(false);
    }
}
