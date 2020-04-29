package com.shopping.item.ui.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.shopping.item.BaseApplication;
import com.shopping.item.R;
import com.shopping.item.model.dto.Item;
import com.shopping.item.ui.fragments.HomeFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ItemListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Item> itemList = new ArrayList<>();
    private ImageLoader imageLoader;


    public ItemListAdapter(Context mContext, List<Item> itemList) {
        this.mContext = mContext;
        this.itemList = itemList;
        this.imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_shopping_item, parent, false);
        ItemRowHolder itemRowHolder = new ItemRowHolder(inflate);
        return itemRowHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemRowHolder) {
            final ItemRowHolder itemRowHolder = (ItemRowHolder) holder;
            final Item item = itemList.get(position);


            if (item.getItemName() != null && !item.getItemName().isEmpty())
                itemRowHolder.itemName.setText(item.getItemName());

            if (item.getItemPrice() != null && !item.getItemPrice().isEmpty())
                itemRowHolder.itemPrice.setText(item.getItemPrice());


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
        }

    }

    public void updateData(List<Item> messageList) {
        for (int i = 0; i < messageList.size(); i++) {
            itemList.add(messageList.get(i));
            notifyItemInserted(getItemCount());
        }

    }

    @Override
    public int getItemCount() {
        return (null != itemList ? itemList.size() : 0);
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder {
        //@BindView(R.id.txt_source) TextView sourceName;
        @BindView(R.id.txt_item_name)
        TextView itemName;
        @BindView(R.id.txt_item_price)
        TextView itemPrice;
        @BindView(R.id.image_url)
        RoundedImageView imageUrl;
        @BindView(R.id.image_progress)
        ProgressBar imageProgress;
        @BindView(R.id.parent_layout)
        RelativeLayout parentLayout;


        public ItemRowHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
