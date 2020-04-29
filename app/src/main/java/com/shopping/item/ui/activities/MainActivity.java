package com.shopping.item.ui.activities;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.shopping.item.R;
import com.shopping.item.model.dto.Item;
import com.shopping.item.ui.fragments.CartFragment;
import com.shopping.item.ui.fragments.HomeFragment;
import com.shopping.item.utils.BaseBackPressedListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    final String TAG = MainActivity.this.getClass().getSimpleName();
    public static MainActivity mainActivity;
    private static String MAIN_LEVEL = "MAIN_LEVEL";

    protected BaseBackPressedListener.OnBackPressedListener onBackPressedListener;
    private List<Item> mItemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Window w = getWindow();
//            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        }
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mainActivity = this;
        if (findViewById(R.id.fragment_container) != null) {
            setFragment(HomeFragment.newInstance());
        }
    }

    public void setFragment(Fragment fragment) {
        if (fragment == null) return;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commitAllowingStateLoss();
    }

    public void addFragment(Fragment fragment, String TAG) {
        if (fragment == null) return;
        FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
        fragTransaction.setCustomAnimations(R.anim.slide_in_left, 0, 0, R.anim.slide_out_left );
        fragTransaction.add(R.id.fragment_container, fragment, TAG);
        fragTransaction.addToBackStack(TAG);
        fragTransaction.commitAllowingStateLoss();
    }

    public void replaceFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public void onBackPressed() {
        if (onBackPressedListener != null){
            System.out.println("==========>back presss");
            onBackPressedListener.doBack();
            System.out.println("==========>back presss22");
            if(!popFragment()){
                finish();
            }
        }
        else super.onBackPressed();
    }

    public boolean popFragment() {
        Log.e("test", "pop fragment: " + getSupportFragmentManager().getBackStackEntryCount());
        boolean isPop = false;
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            isPop = true;
            getSupportFragmentManager().popBackStack();
        }
        return isPop;
    }

    public void setOnBackPressedListener(BaseBackPressedListener.OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }

//    public void addToCart(Item item){
//        mItemList.add(item);
//        System.out.println("=============>> add to cart");
//        Toast.makeText(this, "Item added to the cart", Toast.LENGTH_LONG).show();
//    }

//    public void removeItem(int posision){
//        if(mItemList.size() > 1){
//            mItemList.remove(posision);
//        }else {
//            if(CartFragment.cartFragment != null){
//                CartFragment.cartFragment.toggleView(true);
//            }
//        }
//
//    }

    public void clearCart(){
        mItemList.clear();
    }

    public List<Item> getCartList(){
        return mItemList;
    }

}
