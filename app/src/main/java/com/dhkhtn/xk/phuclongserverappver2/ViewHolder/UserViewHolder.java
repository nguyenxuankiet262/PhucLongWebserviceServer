package com.dhkhtn.xk.phuclongserverappver2.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.dhkhtn.xk.phuclongserverappver2.R;

public class UserViewHolder extends RecyclerView.ViewHolder {
    public TextView number_user, name_user, address_user;
    public Switch active_switch;
    public UserViewHolder(View itemView) {
        super(itemView);
        number_user = itemView.findViewById(R.id.number_user);
        name_user = itemView.findViewById(R.id.name_user);
        address_user = itemView.findViewById(R.id.address_user);
        active_switch = itemView.findViewById(R.id.active_switch);
    }
}
