package com.dhkhtn.xk.phuclongserverappver2.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.dhkhtn.xk.phuclongserverappver2.R;

public class OrderDetailViewHolder extends RecyclerView.ViewHolder {
    public ImageView image_order;
    public TextView quanity_order, name_order, hot_order, cold_order, price_order, sugar_order, ice_order, comment_order;
    public TextView milk_topping, seeds_topping, black_pearl_topping, white_pearl_topping, red_bean_topping;
    public OrderDetailViewHolder(View itemView) {
        super(itemView);
        image_order = itemView.findViewById(R.id.image_drink_history);
        quanity_order = itemView.findViewById(R.id.quanity_drink_history);
        name_order = itemView.findViewById(R.id.name_drink_history);
        hot_order = itemView.findViewById(R.id.hot_drink_history);
        cold_order = itemView.findViewById(R.id.cold_drink_history);
        price_order = itemView.findViewById(R.id.money_drink_history);
        sugar_order = itemView.findViewById(R.id.sugar_cart);
        ice_order = itemView.findViewById(R.id.ice_cart);
        comment_order = itemView.findViewById(R.id.comment_drink_detail_history);
        milk_topping = itemView.findViewById(R.id.milk_cream_topping);
        seeds_topping = itemView.findViewById(R.id.seeds_topping);
        black_pearl_topping = itemView.findViewById(R.id.black_pearl_topping);
        white_pearl_topping = itemView.findViewById(R.id.white_pearl_topping);
        red_bean_topping = itemView.findViewById(R.id.red_beans_topping);
    }
}
