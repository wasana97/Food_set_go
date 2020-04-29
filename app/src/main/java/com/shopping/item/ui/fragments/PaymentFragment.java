package com.shopping.item.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.shopping.item.BaseApplication;
import com.shopping.item.R;
import com.shopping.item.common.CommonUtils;
import com.shopping.item.common.constants.ApplicationConstants;
import com.shopping.item.model.dto.Item;
import com.shopping.item.model.dto.ShippingDetails;
import com.shopping.item.ui.activities.MainActivity;
import com.shopping.item.utils.BaseBackPressedListener;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PaymentFragment extends BaseFragment implements BaseBackPressedListener.OnBackPressedListener {

    final String TAG = PaymentFragment.this.getClass().getSimpleName();
    private static String BUNDLE_EXTRA = "BUNDLE_EXTRA";
    private static String TOTAL = "TOTAL";

    public static PaymentFragment newInstance(List<Item> itemList, Double total) {
        PaymentFragment fragment = new PaymentFragment();
        Bundle args = new Bundle();
        args.putParcelable(BUNDLE_EXTRA, Parcels.wrap(itemList));
        args.putDouble(TOTAL, total);
        fragment.setArguments(args);
        return fragment;
    }

    public static String getTAG() {
        return "PaymentFragment";
    }

    public static PaymentFragment paymentFragment;
    private List<Item> mItemList = new ArrayList<>();
    private Double mTotal;


    @BindView(R.id.shipping_details_layout) RelativeLayout shippingDetailsLayout;
    @BindView(R.id.shipping_address_layout) RelativeLayout shippingAddressLayout;
    @BindView(R.id.payment_layout) RelativeLayout paymentLayout;
    @BindView(R.id.payment_success_layout) RelativeLayout paymentSuccessLayout;

    @BindView(R.id.txt_name) EditText txtName;
    @BindView(R.id.txt_mobile) EditText txtMobile;
    @BindView(R.id.txt_email) EditText txtEmail;
    @BindView(R.id.txt_address1) EditText txtAddress1;
    @BindView(R.id.txt_address2) EditText txtAddress2;
    @BindView(R.id.txt_city) EditText txtCity;
    @BindView(R.id.txt_state) EditText txtState;
    @BindView(R.id.txt_postal_code) EditText txtPostalCode;
    @BindView(R.id.txt_country) EditText txtCountry;
    @BindView(R.id.txt_card_no) EditText txtCardNo;
    @BindView(R.id.txt_cvc) EditText txtCVC;
    @BindView(R.id.btn_next) Button btnNext;

    @BindView(R.id.txt_total_payment) TextView txtTotalPayment;

    private String lblName;
    private String lblMobile;
    private String lblEmail;
    private String lblAddress1;
    private String lblAddress2;
    private String lblCity;
    private String lblState;
    private String lblPostalCode;
    private String lblCountry;
    private String lblCardNo;
    private String lblCVC;

    private ShippingDetails mShippingDetails;



    private DatabaseReference mDatabaseReference;
    private  int count = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mItemList = Parcels.unwrap(getArguments().getParcelable(BUNDLE_EXTRA));
            mTotal = getArguments().getDouble(TOTAL);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = null;
        try {
            rootView = inflater.inflate(R.layout.fragment_payment, container, false);
            Log.d(TAG, "onCreateView");
            ButterKnife.bind(this, rootView);
            paymentFragment = this;
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

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("user");
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performNext();

            }
        });
        getUser();
    }



    private void performNext(){
        switch (count){
            case 0:
                validateShippingDetails();
                break;
            case 1:
                validateShippingAddress();
                break;
            case 2:
                validatePayment();
                break;
            case 3:
                getFragmentManager().popBackStack();
                getFragmentManager().popBackStack();
                break;


        }
    }

    private void validateShippingDetails(){

            boolean status = false;
            if((txtName.getText().toString() != null && !txtName.getText().toString().isEmpty())
                    && (txtMobile.getText().toString() != null && !txtMobile.getText().toString().isEmpty()) &&
                    (txtEmail.getText().toString() != null && !txtEmail.getText().toString().isEmpty())){

                lblName = txtName.getText().toString();
                lblMobile = txtMobile.getText().toString();
                lblEmail = txtEmail.getText().toString();
                status = true;
            }
            if(!status){
                Toast.makeText(getActivity(), "Please fill all details", Toast.LENGTH_LONG).show();
            }else {
                count++;
                toggleView(count);
            }

    }

    private void validateShippingAddress(){
        boolean status = false;
        if((txtAddress1.getText().toString() != null && !txtAddress1.getText().toString().isEmpty()) &&
                (txtAddress2.getText().toString() != null && !txtAddress2.getText().toString().isEmpty()) &&
                (txtCity.getText().toString() != null && !txtCity.getText().toString().isEmpty()) &&
                (txtState.getText().toString() != null && !txtState.getText().toString().isEmpty()) &&
                (txtPostalCode.getText().toString() != null && !txtPostalCode.getText().toString().isEmpty()) &&
                (txtCountry.getText().toString() != null && !txtCountry.getText().toString().isEmpty())){
            lblAddress1 = txtAddress1.getText().toString();
            lblAddress2 = txtAddress2.getText().toString();
            lblCity = txtCity.getText().toString();
            lblState = txtState.getText().toString();
            lblPostalCode = txtPostalCode.getText().toString();
            lblCountry = txtCountry.getText().toString();
            status = true;
        }

        if(!status){
            Toast.makeText(getActivity(), "Shipping Details saved", Toast.LENGTH_LONG).show();
        }else {
            count++;
            addOrUpdateUseDetails();
            toggleView(count);
        }
    }



    private void validatePayment(){
        boolean status = false;
        if((txtCardNo.getText().toString() != null && !txtCardNo.getText().toString().isEmpty()) &&
                (txtCVC.getText().toString() != null && !txtCVC.getText().toString().isEmpty())){
            lblCardNo = txtCardNo.getText().toString();
            lblCVC = txtCVC.getText().toString();
            status = true;
        }

        if(!status){
            Toast.makeText(getActivity(), "Please fill all details", Toast.LENGTH_LONG).show();
        }else {
            count++;
            addOrder();
            toggleView(count);
        }
    }


    private void getUser(){
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println(dataSnapshot);
                mShippingDetails = dataSnapshot.getValue(ShippingDetails.class);
                populateShippingDetails();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void populateShippingDetails(){
        if((mShippingDetails.getLblName() != null && !mShippingDetails.getLblName().isEmpty()) &&
                (mShippingDetails.getLblEmail() != null && !mShippingDetails.getLblEmail().isEmpty()) &&
                (mShippingDetails.getLblMobile() != null && !mShippingDetails.getLblMobile().isEmpty())){
            txtName.setText(mShippingDetails.getLblName());
            txtEmail.setText(mShippingDetails.getLblEmail());
            txtMobile.setText(mShippingDetails.getLblMobile());
        }
    }

    private void populateShippingAddress(){
        if((mShippingDetails.getLblAddress1() != null && !mShippingDetails.getLblAddress1().isEmpty()) &&
                (mShippingDetails.getLblAddress2() != null && !mShippingDetails.getLblAddress2().isEmpty()) &&
                (mShippingDetails.getLblCity() != null && !mShippingDetails.getLblCity().isEmpty()) &&
                (mShippingDetails.getLblState() != null && !mShippingDetails.getLblState().isEmpty()) &&
                (mShippingDetails.getLblPostalCode() != null && !mShippingDetails.getLblPostalCode().isEmpty()) &&
                (mShippingDetails.getLblCountry() != null && !mShippingDetails.getLblCountry().isEmpty())){
            txtAddress1.setText(mShippingDetails.getLblAddress1());
            txtAddress2.setText(mShippingDetails.getLblAddress2());
            txtCity.setText(mShippingDetails.getLblCity());
            txtState.setText(mShippingDetails.getLblState());
            txtPostalCode.setText(mShippingDetails.getLblPostalCode());
            txtCountry.setText(mShippingDetails.getLblCountry());
        }
    }
    private void addOrUpdateUseDetails(){
        ShippingDetails shippingDetails = new ShippingDetails();
        shippingDetails.setLblName(lblName);
        shippingDetails.setLblMobile(lblMobile);
        shippingDetails.setLblEmail(lblEmail);
        shippingDetails.setLblAddress1(lblAddress1);
        shippingDetails.setLblAddress2(lblAddress2);
        shippingDetails.setLblCity(lblCity);
        shippingDetails.setLblState(lblState);
        shippingDetails.setLblPostalCode(lblPostalCode);
        shippingDetails.setLblCountry(lblCountry);
        mDatabaseReference.setValue(shippingDetails);
      //  Toast.makeText(getActivity(), "Item added to the cart", Toast.LENGTH_LONG).show();
    }

    private void addOrder(){
        if( mItemList.size() > 0){
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("order");

            for (Item item : mItemList) {
                String id = mDatabaseReference.push().getKey();
                item.setId(id);
                databaseReference.child(id).setValue(item);
            }
            Toast.makeText(getActivity(), "Order Successfull", Toast.LENGTH_LONG).show();
        }
        clearCart();
    }

    private void clearCart(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("cart");
        databaseReference.removeValue();
    }

    private void toggleView(int count){
        switch (count){
            case 1:
                shippingDetailsLayout.setVisibility(View.GONE);
                shippingAddressLayout.setVisibility(View.VISIBLE);
                paymentLayout.setVisibility(View.GONE);
                paymentSuccessLayout.setVisibility(View.GONE);
                populateShippingAddress();
                break;
            case 2:
                shippingDetailsLayout.setVisibility(View.GONE);
                shippingAddressLayout.setVisibility(View.GONE);
                paymentLayout.setVisibility(View.VISIBLE);
                paymentSuccessLayout.setVisibility(View.GONE);
                btnNext.setText("Pay Now");
                txtTotalPayment.setText("$ " + String.format("%.2f", mTotal));
                break;
            case 3:
                shippingDetailsLayout.setVisibility(View.GONE);
                shippingAddressLayout.setVisibility(View.GONE);
                paymentLayout.setVisibility(View.GONE);
                paymentSuccessLayout.setVisibility(View.VISIBLE);
                btnNext.setText("Done");
                break;
        }
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
        title.setText("Shipping Details");
        Toolbar parent =(Toolbar) mCustomView.getParent();
        parent.setPadding(0,0,0,0);//for tab otherwise give space in tab
        parent.setContentInsetsAbsolute(0,0);
    }

    @Override
    public void doBack() {

    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        paymentFragment = null;
        BaseApplication.getBaseApplication().setLoadPaymentScreen(false);
    }
}
