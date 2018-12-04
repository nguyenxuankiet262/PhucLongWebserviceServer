package com.dhkhtn.xk.phuclongserverappver2.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.dhkhtn.xk.phuclongserverappver2.Interface.ItemClickListener;
import com.dhkhtn.xk.phuclongserverappver2.Interface.ItemLongClickListener;
import com.dhkhtn.xk.phuclongserverappver2.R;


public class DrinkViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
    public ImageView image_product, cold_btn, hot_btn;
    public RatingBar ratingBar;
    public TextView name_drink, price_drink;

    ItemClickListener itemClickListener;
    ItemLongClickListener itemLongClickListener;
    public DrinkViewHolder(View itemView) {
        super(itemView);
        cold_btn = itemView.findViewById(R.id.cold_drink_image);
        hot_btn = itemView.findViewById(R.id.hot_drink_image);
        ratingBar = itemView.findViewById(R.id.rating_bar);
        image_product = itemView.findViewById(R.id.image_drink);
        name_drink = itemView.findViewById(R.id.name_drink);
        price_drink = itemView.findViewById(R.id.price_drink);
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }
    public void setItemClickListener(ItemClickListener itemClickListener, ItemLongClickListener itemLongClickListener)
    {
        this.itemClickListener = itemClickListener;
        this.itemLongClickListener = itemLongClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition());
    }

    @Override
    public boolean onLongClick(View v) {
        return itemLongClickListener.onLongClick(v,getAdapterPosition());
    }
}
