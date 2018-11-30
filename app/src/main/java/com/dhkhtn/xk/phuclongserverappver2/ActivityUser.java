package com.dhkhtn.xk.phuclongserverappver2;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dhkhtn.xk.phuclongserverappver2.Adapter.UserAdapter;
import com.dhkhtn.xk.phuclongserverappver2.Model.User;
import com.dhkhtn.xk.phuclongserverappver2.Retrofit.IPhucLongAPI;
import com.dhkhtn.xk.phuclongserverappver2.Utils.Common;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ActivityUser extends AppCompatActivity {
    MaterialSearchBar searchBar;
    ImageView btn_back;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView listUser, search_listUser;
    UserAdapter adapter, searchAdapter;
    RelativeLayout btn_back_layout, empty_user_layout, content_user_layout;
    IPhucLongAPI mService;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        mService = Common.getAPI();

        swipeRefreshLayout = findViewById(R.id.swipe_layout_user);
        btn_back = findViewById(R.id.btn_back_search_user);
        searchBar = findViewById(R.id.search_bar_user);
        search_listUser = findViewById(R.id.search_user_list);

        btn_back_layout = findViewById(R.id.back_btn_user_layout);
        empty_user_layout = findViewById(R.id.empty_user_layout);
        content_user_layout = findViewById(R.id.content_list_user);
        listUser = findViewById(R.id.listUser);

        empty_user_layout.setVisibility(View.GONE);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        search_listUser.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        search_listUser.setHasFixedSize(true);

        listUser.setLayoutManager(new LinearLayoutManager(ActivityUser.this, LinearLayoutManager.VERTICAL,false));
        listUser.setHasFixedSize(true);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark,
                android.R.color.holo_green_dark,
                android.R.color.holo_blue_dark ,
                android.R.color.holo_orange_dark);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(Common.isConnectedToInternet(ActivityUser.this)) {
                    LoadUser();
                    swipeRefreshLayout.setRefreshing(false);
                }
                else{
                    Toast.makeText(ActivityUser.this, "Không thể kết nối mạng!", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if(Common.isConnectedToInternet(ActivityUser.this)) {
                    LoadUser();
                    swipeRefreshLayout.setRefreshing(false);
                }
                else{
                    Toast.makeText(ActivityUser.this, "Không thể kết nối mạng!", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if(!enabled){
                    btn_back_layout.setVisibility(View.VISIBLE);
                    search_listUser.setVisibility(View.GONE);
                    content_user_layout.setVisibility(View.VISIBLE);
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
                    content_user_layout.setVisibility(View.GONE);
                    search_listUser.setVisibility(View.VISIBLE);
                    if(Common.isConnectedToInternet(getBaseContext())) {
                        startSearch(s);
                        //Log.d("EEE", s.toString());
                    }
                    else{
                        Toast.makeText(ActivityUser.this,"Kiểm tra kết nối mạng!", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    search_listUser.setVisibility(View.GONE);
                    content_user_layout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void startSearch(CharSequence s) {
        compositeDisposable.add(mService.getUserByString(s.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<User>>() {
                    @Override
                    public void accept(List<User> users) throws Exception {
                        if(users.size() > 0) {
                            displaySearchUser(users);
                        }
                    }
                })
        );
    }

    private void displaySearchUser(List<User> users) {
        searchAdapter = new UserAdapter(this,users);
        search_listUser.setAdapter(searchAdapter);
    }

    private void LoadUser() {
        compositeDisposable.add(mService.getAllUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<User>>() {
                    @Override
                    public void accept(List<User> users) throws Exception {
                        if(users.size() > 0) {
                            displayUser(users);
                        }
                    }
                })
        );
    }

    private void displayUser(List<User> users) {
        adapter = new UserAdapter(this,users);
        listUser.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }
}
