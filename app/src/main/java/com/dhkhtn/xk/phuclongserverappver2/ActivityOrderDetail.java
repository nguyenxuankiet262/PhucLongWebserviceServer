package com.dhkhtn.xk.phuclongserverappver2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dhkhtn.xk.phuclongserverappver2.Adapter.OrderDetailAdapter;
import com.dhkhtn.xk.phuclongserverappver2.Model.Cart;
import com.dhkhtn.xk.phuclongserverappver2.Model.Order;
import com.dhkhtn.xk.phuclongserverappver2.Utils.Common;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class ActivityOrderDetail extends AppCompatActivity {
    Order order;
    Toolbar toolbar;
    TextView address, comment, price;
    RecyclerView recyclerView;
    List<Cart> cartList;
    OrderDetailAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        toolbar = findViewById(R.id.tool_bar_drink_detail);
        address = findViewById(R.id.address_drink_history);
        comment = findViewById(R.id.comment_drink_history);
        price = findViewById(R.id.price_drink_history);
        recyclerView = findViewById(R.id.list_order);
        recyclerView.setLayoutManager(new LinearLayoutManager(ActivityOrderDetail.this, LinearLayoutManager.VERTICAL,false));
        recyclerView.setHasFixedSize(true);

        Intent intent = getIntent();
        order = (Order) intent.getSerializableExtra("Order");

        address.setText(order.getAddress());
        if(!TextUtils.isEmpty(order.getNote())) {
            comment.setText(order.getNote());
        }
        price.setText(order.getPrice());

        toolbar.setTitle("Order #" + order.getTimeorder());
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if(Common.isConnectedToInternet(ActivityOrderDetail.this)){
            LoadOrder();
        }
        else{
            Toast.makeText(ActivityOrderDetail.this, "Không thể kết nối mạng!", Toast.LENGTH_SHORT).show();
        }
    }

    private void LoadOrder() {
        cartList = new Gson().fromJson(order.getDrinkdetail(), new TypeToken<List<Cart>>(){}.getType());
        adapter = new OrderDetailAdapter(ActivityOrderDetail.this, cartList);
        recyclerView.setAdapter(adapter);
    }
}
