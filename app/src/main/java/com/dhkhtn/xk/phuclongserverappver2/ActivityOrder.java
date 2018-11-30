package com.dhkhtn.xk.phuclongserverappver2;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dhkhtn.xk.phuclongserverappver2.Adapter.OrderAdapter;
import com.dhkhtn.xk.phuclongserverappver2.Model.Order;
import com.dhkhtn.xk.phuclongserverappver2.Retrofit.IFCMService;
import com.dhkhtn.xk.phuclongserverappver2.Retrofit.IPhucLongAPI;
import com.dhkhtn.xk.phuclongserverappver2.Utils.Common;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ActivityOrder extends AppCompatActivity {
    MaterialSearchBar searchBar;
    ImageView btn_back;
    RecyclerView search_orderList, orderList;
    SwipeRefreshLayout swipeRefreshLayout;
    RelativeLayout btn_back_layout, empty_order_layout, content_order_layout;
    MaterialSpinner spinner;
    BottomNavigationView bottomNavigationView;
    IPhucLongAPI mService;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    OrderAdapter adapter, searchAdapter;
    ArrayAdapter<String> storeAdapter;
    List<String> idStore;
    String idCurrentStore;
    public int idCurrentStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        mService = Common.getAPI();

        btn_back = findViewById(R.id.btn_back_search);
        searchBar = findViewById(R.id.search_bar);
        search_orderList = findViewById(R.id.search_order_list);
        orderList = findViewById(R.id.order_list);
        swipeRefreshLayout = findViewById(R.id.swipe_layout_order);
        btn_back_layout = findViewById(R.id.back_btn_layout);
        empty_order_layout = findViewById(R.id.empty_order_layout);
        content_order_layout = findViewById(R.id.content_order);
        spinner = findViewById(R.id.spinner_address);
        bottomNavigationView = findViewById(R.id.bot_bar);

        empty_order_layout.setVisibility(View.GONE);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark,
                android.R.color.holo_green_dark,
                android.R.color.holo_blue_dark ,
                android.R.color.holo_orange_dark);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        orderList.setLayoutManager(mLayoutManager);

        search_orderList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        search_orderList.setHasFixedSize(true);

        List<String> addressList = new ArrayList<>();
        idStore = new ArrayList<>();
        for (int i = 0; i < Common.CurrentStore.size(); i++) {
            addressList.add(Common.CurrentStore.get(i).getAddress());
            idStore.add(Common.CurrentStore.get(i).getId());
        }
        storeAdapter = new ArrayAdapter<String>(ActivityOrder.this, R.layout.support_simple_spinner_dropdown_item, addressList);
        idCurrentStore = idStore.get(0);
        spinner.setText(addressList.get(0));
        spinner.setAdapter(storeAdapter);

        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                idCurrentStore = idStore.get(position);
                checkInternet(idCurrentStatus);
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        checkInternet(0);

        idCurrentStatus = 0;

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.order_new){
                    idCurrentStatus = 0;
                    checkInternet(idCurrentStatus);
                }
                else if(item.getItemId() == R.id.order_ontheway){
                    idCurrentStatus = 1;
                    checkInternet(idCurrentStatus);
                }
                else if(item.getItemId() == R.id.order_paid){
                    idCurrentStatus = 2;
                    checkInternet(idCurrentStatus);
                }
                else if(item.getItemId() == R.id.order_remove){
                    idCurrentStatus = 3;
                    checkInternet(idCurrentStatus);
                }
                return true;
            }
        });

        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if(!enabled){
                    btn_back_layout.setVisibility(View.VISIBLE);
                    search_orderList.setVisibility(View.GONE);
                    content_order_layout.setVisibility(View.VISIBLE);
                }
                else{
                    btn_back_layout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {

            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count != 0) {
                    content_order_layout.setVisibility(View.GONE);
                    search_orderList.setVisibility(View.VISIBLE);
                    if(Common.isConnectedToInternet(getBaseContext())) {
                        startSearch(s);
                        //Log.d("EEE", s.toString());
                    }
                    else{
                        Toast.makeText(ActivityOrder.this,"Kiểm tra kết nối mạng!", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    search_orderList.setVisibility(View.GONE);
                    content_order_layout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void startSearch(CharSequence s) {
        compositeDisposable.add(mService.getOrderById(s.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Order>>() {
                    @Override
                    public void accept(List<Order> orders) throws Exception {
                        if(orders.size() > 0) {
                            loadSearchOrder(orders);
                            //Log.d("EEE", orders.get(0).getName());
                        }
                    }
                })
        );
    }

    private void loadSearchOrder(List<Order> orders) {
        searchAdapter = new OrderAdapter(this, orders);
        search_orderList.setAdapter(searchAdapter);
    }

    public void checkInternet(final int status) {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(Common.isConnectedToInternet(ActivityOrder.this)) {
                    loadOrder(status);
                    swipeRefreshLayout.setRefreshing(false);
                }
                else{
                    Toast.makeText(ActivityOrder.this, "Không thể kết nối mạng!", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if(Common.isConnectedToInternet(ActivityOrder.this)) {
                    loadOrder(status);
                    swipeRefreshLayout.setRefreshing(false);
                }
                else{
                    Toast.makeText(ActivityOrder.this, "Không thể kết nối mạng!", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    private void loadOrder(int status) {
        compositeDisposable.add(mService.getOrder(status, Integer.parseInt(idCurrentStore))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Order>>() {
                    @Override
                    public void accept(List<Order> orders) throws Exception {
                        if(orders.size() > 0) {
                            empty_order_layout.setVisibility(View.GONE);
                            orderList.setVisibility(View.VISIBLE);
                            displayOrder(orders);
                        }
                        else{
                            orderList.setVisibility(View.GONE);
                            empty_order_layout.setVisibility(View.VISIBLE);
                        }
                    }
                })
        );
    }

    private void displayOrder(List<Order> orders) {
        adapter = new OrderAdapter(ActivityOrder.this, orders);
        orderList.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }
}
