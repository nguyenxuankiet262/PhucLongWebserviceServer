package com.dhkhtn.xk.phuclongserverappver2.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dhkhtn.xk.phuclongserverappver2.Interface.ItemClickListener;
import com.dhkhtn.xk.phuclongserverappver2.R;

public class BannerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public ImageView img_banner;
    public TextView name_banner;
    ItemClickListener itemClickListener;

    public BannerViewHolder(View itemView) {
        super(itemView);
        img_banner = itemView.findViewById(R.id.image_banner);
        name_banner = itemView.findViewById(R.id.name_banner);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener)
    {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition());
    }
}
