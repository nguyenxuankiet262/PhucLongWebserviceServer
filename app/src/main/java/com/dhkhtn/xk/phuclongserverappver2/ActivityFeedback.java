package com.dhkhtn.xk.phuclongserverappver2;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.dhkhtn.xk.phuclongserverappver2.Adapter.FeedbackAdapter;
import com.dhkhtn.xk.phuclongserverappver2.Model.Feedback;
import com.dhkhtn.xk.phuclongserverappver2.Retrofit.IPhucLongAPI;
import com.dhkhtn.xk.phuclongserverappver2.Utils.Common;

import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ActivityFeedback extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    IPhucLongAPI mService;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    FeedbackAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        mService = Common.getAPI();
        toolbar = findViewById(R.id.tool_bar_feedback);
        recyclerView = findViewById(R.id.listFeedback);
        swipeRefreshLayout = findViewById(R.id.swipe_layout_feedback);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        recyclerView.setHasFixedSize(true);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark,
                android.R.color.holo_green_dark,
                android.R.color.holo_blue_dark ,
                android.R.color.holo_orange_dark);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(Common.isConnectedToInternet(ActivityFeedback.this)) {
                    LoadFeedBack();
                    swipeRefreshLayout.setRefreshing(false);
                }
                else{
                    Toast.makeText(ActivityFeedback.this, "Không thể kết nối mạng!", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if(Common.isConnectedToInternet(ActivityFeedback.this)) {
                    LoadFeedBack();
                    swipeRefreshLayout.setRefreshing(false);
                }
                else{
                    Toast.makeText(ActivityFeedback.this, "Không thể kết nối mạng!", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    public void LoadFeedBack() {
        compositeDisposable.add(mService.getAllFeedback()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Feedback>>() {
                    @Override
                    public void accept(List<Feedback> feedbacks) throws Exception {
                        displayFeedBack(feedbacks);
                    }
                })
        );
    }

    private void displayFeedBack(List<Feedback> feedbacks) {
        adapter = new FeedbackAdapter(this,feedbacks);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }
}
