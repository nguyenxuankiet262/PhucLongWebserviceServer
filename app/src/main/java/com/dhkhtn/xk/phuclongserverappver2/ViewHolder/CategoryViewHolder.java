package com.dhkhtn.xk.phuclongserverappver2.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dhkhtn.xk.phuclongserverappver2.Interface.ItemClickListener;
import com.dhkhtn.xk.phuclongserverappver2.Interface.ItemLongClickListener;
import com.dhkhtn.xk.phuclongserverappver2.R;

public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    public ImageView img_product;
    public TextView name_product;
    ItemClickListener itemClickListener;
    ItemLongClickListener itemLongClickListener;

    public CategoryViewHolder(View itemView) {
        super(itemView);
        img_product = itemView.findViewById(R.id.image_product);
        name_product = itemView.findViewById(R.id.text_product);
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
