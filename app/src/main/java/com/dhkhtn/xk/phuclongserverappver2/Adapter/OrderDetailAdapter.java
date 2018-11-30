package com.dhkhtn.xk.phuclongserverappver2.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dhkhtn.xk.phuclongserverappver2.Model.Cart;
import com.dhkhtn.xk.phuclongserverappver2.R;
import com.dhkhtn.xk.phuclongserverappver2.Utils.Common;
import com.dhkhtn.xk.phuclongserverappver2.ViewHolder.OrderDetailViewHolder;
import com.squareup.picasso.Picasso;

import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailViewHolder> {
    Context context;
    List<Cart> cartList;

    public OrderDetailAdapter(Context context, List<Cart> cartList){
        this.context = context;
        this.cartList = cartList;
    }
    @NonNull
    @Override
    public OrderDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_order_drink_detail, parent, false);
        return new OrderDetailViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailViewHolder holder, int position) {
        holder.name_order.setText(cartList.get(position).getcName());
        holder.quanity_order.setText(cartList.get(position).getcQuanity() +"");
        holder.price_order.setText(Common.ConvertIntToMoney(cartList.get(position).getcPrice()));
        if(cartList.get(position).getcStatus().equals("cold")){
            Picasso.with(context).load(cartList.get(position).getcImageCold()).into(holder.image_order);
            holder.hot_order.setVisibility(View.GONE);
            holder.cold_order.setVisibility(View.VISIBLE);
        }
        else{
            Picasso.with(context).load(cartList.get(position).getcImageHot()).into(holder.image_order);
        }

        holder.sugar_order.setText("Sugar: " + cartList.get(position).getcSugar());
        holder.ice_order.setText("Ice: " + cartList.get(position).getcIce());

        if(!TextUtils.isEmpty(cartList.get(position).getcTopping())){
            String[] parts = cartList.get(position).getcTopping().split("\n");
            for(int i = 0; i < parts.length; i++){
                if(parts[i].equals("Milk cream")){
                    holder.milk_topping.setVisibility(View.VISIBLE);
                }
                else if(parts[i].equals("White Pearl")){
                    holder.white_pearl_topping.setVisibility(View.VISIBLE);
                }
                else if(parts[i].equals("Black Pearl")){
                    holder.black_pearl_topping.setVisibility(View.VISIBLE);
                }
                else if(parts[i].equals("Red Beans")){
                    holder.red_bean_topping.setVisibility(View.VISIBLE);
                }
                else if(parts[i].equals("Seeds")){
                    holder.seeds_topping.setVisibility(View.VISIBLE);
                }
            }
        }
        if(!TextUtils.isEmpty(cartList.get(position).getcComment())) {
            holder.comment_order.setText(cartList.get(position).getcComment());
        }

    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }
}
