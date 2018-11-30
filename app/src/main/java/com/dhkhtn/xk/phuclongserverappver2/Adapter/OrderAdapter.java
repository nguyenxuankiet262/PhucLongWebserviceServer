package com.dhkhtn.xk.phuclongserverappver2.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.dhkhtn.xk.phuclongserverappver2.ActivityOrderDetail;
import com.dhkhtn.xk.phuclongserverappver2.ActivityOrder;
import com.dhkhtn.xk.phuclongserverappver2.Interface.ItemClickListener;
import com.dhkhtn.xk.phuclongserverappver2.Interface.ItemLongClickListener;
import com.dhkhtn.xk.phuclongserverappver2.Model.MyResponse;
import com.dhkhtn.xk.phuclongserverappver2.Model.Notification;
import com.dhkhtn.xk.phuclongserverappver2.Model.Order;
import com.dhkhtn.xk.phuclongserverappver2.Model.Sender;
import com.dhkhtn.xk.phuclongserverappver2.Model.Token;
import com.dhkhtn.xk.phuclongserverappver2.Model.User;
import com.dhkhtn.xk.phuclongserverappver2.R;
import com.dhkhtn.xk.phuclongserverappver2.Retrofit.IFCMService;
import com.dhkhtn.xk.phuclongserverappver2.Retrofit.IPhucLongAPI;
import com.dhkhtn.xk.phuclongserverappver2.Utils.Common;
import com.dhkhtn.xk.phuclongserverappver2.ViewHolder.OrderViewHolder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderAdapter extends RecyclerView.Adapter<OrderViewHolder> {

    Context context;
    List<Order> orderList;
    NumberPicker numberPicker;
    AlertDialog alertDialog;
    TextView btn_ok, btn_cancel;
    IPhucLongAPI mService;
    IFCMService apiService;
    //Firebase
    public OrderAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_order_layout, parent, false);
        return new OrderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        holder.time_order.setText(Common.getTimeAgo(Long.parseLong(orderList.get(position).getTimeorder()), context));
        holder.id_order.setText("#" + orderList.get(position).getTimeorder());
        if (orderList.get(position).getStatus() == 0) {
            holder.status_order.setText("New Order");
            holder.status_order.setTextColor(ContextCompat.getColor(context, R.color.colorOpenStore));
        }
        if (orderList.get(position).getStatus() == 1) {
            holder.status_order.setText("On the way");
            holder.status_order.setTextColor(ContextCompat.getColor(context, R.color.colorOTW));
        }
        if (orderList.get(position).getStatus() == 2) {
            holder.status_order.setText("Success");
            holder.status_order.setTextColor(ContextCompat.getColor(context, R.color.colorSc));
        }
        if (orderList.get(position).getStatus() == 3) {
            holder.status_order.setText("Delete");
            holder.status_order.setTextColor(ContextCompat.getColor(context, R.color.colorCancel));
        }

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View v, int position) {
                Intent drink_detail_intent = new Intent(context, ActivityOrderDetail.class);
                drink_detail_intent.putExtra("Order",orderList.get(position));
                context.startActivity(drink_detail_intent);
            }
        }, new ItemLongClickListener() {
            @Override
            public boolean onLongClick(View v, final int position) {
                mService = Common.getAPI();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                View itemView = LayoutInflater.from(context).inflate(R.layout.popup_wheelview, null);
                builder.setView(itemView);
                alertDialog = builder.show();
                numberPicker = itemView.findViewById(R.id.wheelview);
                btn_ok = itemView.findViewById(R.id.btn_ok);
                btn_cancel = itemView.findViewById(R.id.btn_cancel);

                String[] data = new String[]{"New", "On delivery", "Success", "Cancel"};
                numberPicker.setMaxValue(0);
                numberPicker.setMaxValue(data.length - 1);
                numberPicker.setDisplayedValues(data);
                numberPicker.setWrapSelectorWheel(false);
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mService.updateStatus(orderList.get(position).getId(), numberPicker.getValue())
                                .enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        Log.d("EEE", orderList.get(position).getPhone() +"");
                                        mService.getUser(orderList.get(position).getPhone())
                                                .enqueue(new Callback<User>() {
                                                    @Override
                                                    public void onResponse(Call<User> call, Response<User> response) {
                                                        User user = response.body();
                                                        Log.d("EEE", user.getNoti_news() +"");
                                                        if(user.getNoti_news() == 1){
                                                            sendNotification(orderList.get(position).getTimeorder(),numberPicker.getValue(),
                                                                    orderList.get(position).getPhone());
                                                        }
                                                        else{
                                                            alertDialog.dismiss();
                                                            Toast.makeText(context, "Update status thành công!", Toast.LENGTH_SHORT).show();
                                                            ((ActivityOrder)context).checkInternet(((ActivityOrder)context).idCurrentStatus);
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<User> call, Throwable t) {

                                                    }
                                                });

                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {

                                    }
                                });
                    }
                });
                return true;
            }
        });
    }

    private void sendNotification(String timeorder, int value, String phone) {
        apiService = Common.getFCMService();
        String body;
        if(value == 0){
            body = "Có order mới #" + timeorder;
        }
        else if(value == 1){
            body = "Order #" + timeorder + " đang trên đường tới!";
        }
        else if(value == 2){
            body = "Order #" + timeorder + " đã giao dịch thành công!";
        }
        else{
            body = "Đã hủy order #" + timeorder;
        }
        final Notification notification = new Notification(body,"New Order");
        mService.getToken(phone,0).enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                Token token = response.body();
                Log.d("EEE", token.getToken());
                Sender content = new Sender(token.getToken(),notification);
                apiService.sendNoti(content).enqueue(new Callback<MyResponse>() {
                    @Override
                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                        if(response.body().success == 1){
                            alertDialog.dismiss();
                            Toast.makeText(context, "Update status thành công!", Toast.LENGTH_SHORT).show();
                            ((ActivityOrder)context).checkInternet(((ActivityOrder)context).idCurrentStatus);
                        }
                    }

                    @Override
                    public void onFailure(Call<MyResponse> call, Throwable t) {
                    }
                });
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
}
