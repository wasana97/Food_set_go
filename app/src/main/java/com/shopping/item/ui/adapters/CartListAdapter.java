package com.shopping.item.ui.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.shopping.item.BaseApplication;
import com.shopping.item.R;
import com.shopping.item.model.dto.Item;
import com.shopping.item.ui.activities.MainActivity;
import com.shopping.item.ui.fragments.CartFragment;
import com.shopping.item.ui.fragments.HomeFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CartListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Item> itemList = new ArrayList<>();
    private ImageLoader imageLoader;
    public AlertDialog myAlertDialogOne;


    public CartListAdapter(Context mContext, List<Item> itemList) {
        this.mContext = mContext;
        this.itemList = itemList;
        this.imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_cart_item, parent, false);
        CartListAdapter.ItemRowHolder itemRowHolder = new CartListAdapter.ItemRowHolder(inflate);
        return itemRowHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CartListAdapter.ItemRowHolder) {
            final CartListAdapter.ItemRowHolder itemRowHolder = (CartListAdapter.ItemRowHolder) holder;
            final Item item = itemList.get(position);


            if (item.getItemName() != null && !item.getItemName().isEmpty())
                itemRowHolder.itemName.setText(item.getItemName());

            if (item.getItemPrice() != null && !item.getItemPrice().isEmpty())
                itemRowHolder.itemPrice.setText("$ "+item.getItemPrice());
            if(item.getItemQty() != 0){
                itemRowHolder.txtQuantity.setText(Integer.toString(item.getItemQty()));
            }




            itemRowHolder.imageUrl.setImageResource(R.drawable.icon);
            if (item.getItemImg() != null && !item.getItemImg().isEmpty()) {
                ImageLoader.getInstance().displayImage(item.getItemImg(), itemRowHolder.imageUrl, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        if (itemRowHolder.imageProgress != null)
                            itemRowHolder.imageProgress.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        if (itemRowHolder.imageProgress != null)
                            itemRowHolder.imageProgress.setVisibility(View.GONE);
                        if (item.getItemImg().isEmpty())
                            itemRowHolder.imageUrl.setImageResource(R.drawable.icon); // clear imageview for no images
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        if (itemRowHolder.imageProgress != null)
                            itemRowHolder.imageProgress.setVisibility(View.GONE);
                    }
                });
            } else {
                if (itemRowHolder.imageProgress != null)
                    itemRowHolder.imageProgress.setVisibility(View.GONE);
                if (itemRowHolder.imageUrl != null && item.getItemImg() != null && item.getItemImg().isEmpty())
                    itemRowHolder.imageUrl.setImageResource(R.drawable.icon); // clear imageview for no images
            }


            itemRowHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (HomeFragment.homeFragment != null) {
                        if (!BaseApplication.getBaseApplication().isLoadItemDetailsScreen()) {
                            BaseApplication.getBaseApplication().setLoadItemDetailsScreen(true);
                            HomeFragment.homeFragment.gotoItemDetailScreen(item);
                        }
                    }
                }
            });

            itemRowHolder.removeItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  grantDeleteItemAlertDialog(itemRowHolder.getAdapterPosition(), item);
                }
            });

            itemRowHolder.btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(CartFragment.cartFragment != null){
                        CartFragment.cartFragment.performAddOrReduce(item.getId(),item.getItemQty(), false );
                    }
                }
            });

            itemRowHolder.btnReduce.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(CartFragment.cartFragment != null){
                        CartFragment.cartFragment.performAddOrReduce(item.getId(),item.getItemQty(), true );
                    }
                }
            });
        }

    }

    public void updateData(List<Item> messageList, int flag) {
        if(flag == 0){
            for (int i = 0; i < messageList.size(); i++) {
                itemList.add(messageList.get(i));
                notifyItemInserted(getItemCount());
            }
        }else {
            itemList.clear();
            notifyDataSetChanged();
        }


    }

    private void grantDeleteItemAlertDialog(final int position, Item item) {

        if (myAlertDialogOne != null) {
            myAlertDialogOne.dismiss();
            myAlertDialogOne = null;
        }

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setTitle("Information");
        dialogBuilder.setMessage("Do you need to remove this Item from cart ?");


        dialogBuilder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                    //remove from the array list
                //((MainActivity) mContext).removeItem(position);
                if(CartFragment.cartFragment != null){
                    CartFragment.cartFragment.removeItem(position);
                    CartFragment.cartFragment.removeFromCart(item.getId());
                    //CartFragment.cartFragment.getTotalPrice();
                }
                //remove from the recycler view
                removeItem(position);

            }
        });

        dialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        myAlertDialogOne = dialogBuilder.create();
        myAlertDialogOne.show();
    }

    public void removeItem(int position){
        itemList.remove(position);
        notifyItemRemoved(position);
        notifyItemChanged(position, itemList.size());

    }

    @Override
    public int getItemCount() {
        return (null != itemList ? itemList.size() : 0);
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder {
        //@BindView(R.id.txt_source) TextView sourceName;
        @BindView(R.id.txt_item_name) TextView itemName;
        @BindView(R.id.txt_item_price) TextView itemPrice;
        @BindView(R.id.image_url) RoundedImageView imageUrl;
        @BindView(R.id.image_progress) ProgressBar imageProgress;
        @BindView(R.id.parent_layout) RelativeLayout parentLayout;
        @BindView(R.id.remove_item) RelativeLayout removeItem;
        @BindView(R.id.btn_add) Button btnAdd;
        @BindView(R.id.btn_reduce) Button btnReduce;
        @BindView(R.id.txt_quantity) TextView txtQuantity;



        public ItemRowHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
