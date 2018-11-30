package com.dhkhtn.xk.phuclongserverappver2.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.dhkhtn.xk.phuclongserverappver2.Model.User;
import com.dhkhtn.xk.phuclongserverappver2.R;
import com.dhkhtn.xk.phuclongserverappver2.Retrofit.IPhucLongAPI;
import com.dhkhtn.xk.phuclongserverappver2.Utils.Common;
import com.dhkhtn.xk.phuclongserverappver2.ViewHolder.UserViewHolder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserAdapter extends RecyclerView.Adapter<UserViewHolder> {
    Context context;
    List<User> userList;
    IPhucLongAPI mService;

    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemview = LayoutInflater.from(context).inflate(R.layout.item_user_layout,parent,false);
        return new UserViewHolder(itemview);
    }

    @Override
    public void onBindViewHolder(@NonNull final UserViewHolder holder, final int position) {
        mService = Common.getAPI();
        holder.number_user.setText(userList.get(position).getPhone());
        if(!TextUtils.isEmpty(userList.get(position).getAddress())){
            holder.name_user.setText(userList.get(position).getName());
            holder.address_user.setText(userList.get(position).getAddress());
        }

        if(userList.get(position).getActive() == 1){
            holder.active_switch.setChecked(true);
        }
        else{
            holder.active_switch.setChecked(false);
        }

        holder.active_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(Common.isConnectedToInternet(context)){
                    if(isChecked){
                        mService.updateActiveUser(userList.get(position).getPhone(), 1)
                                .enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        Toast.makeText(context, "Tài khoản " + userList.get(position).getPhone() + " đã mở khóa!", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {

                                    }
                                });
                    }else{
                        mService.updateActiveUser(userList.get(position).getPhone(), 0)
                                .enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        Toast.makeText(context, "Tài khoản " + userList.get(position).getPhone() + " đã bị khóa!", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {

                                    }
                                });
                    }
                }
                else{
                    Toast.makeText(context,"Kiểm tra kết nối mạng!", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
