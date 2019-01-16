package com.dhkhtn.xk.phuclongserverappver2;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.dhkhtn.xk.phuclongserverappver2.Model.Store;
import com.dhkhtn.xk.phuclongserverappver2.Model.Token;
import com.dhkhtn.xk.phuclongserverappver2.Retrofit.IPhucLongAPI;
import com.dhkhtn.xk.phuclongserverappver2.Utils.Common;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.dhkhtn.xk.phuclongserverappver2.Utils.Common.isConnectedToInternet;

public class ActivityStart extends AppCompatActivity {
    private ImageView logoView;
    private Animation anim_alpha;
    private SwipeRefreshLayout swipeRefreshLayout;

    IPhucLongAPI mService;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mService = Common.getAPI();

        logoView = (ImageView) findViewById(R.id.logo_Image);
        anim_alpha = AnimationUtils.loadAnimation(this,R.anim.anim_alpha);
        swipeRefreshLayout = findViewById(R.id.swipe_layout_start);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark
                , android.R.color.holo_green_dark
                , android.R.color.holo_blue_dark
                , android.R.color.holo_orange_dark);
        anim_alpha.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        if(isConnectedToInternet(ActivityStart.this)) {
                            loadStoreList();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        else{
                            Toast.makeText(ActivityStart.this, "Không thể kết nối mạng!", Toast.LENGTH_SHORT).show();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                });
                swipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        if(isConnectedToInternet(ActivityStart.this)) {
                            loadStoreList();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        else{
                            Toast.makeText(ActivityStart.this, "Không thể kết nối mạng!", Toast.LENGTH_SHORT).show();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                });

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        logoView.startAnimation(anim_alpha);
    }

    private void loadStoreList() {
        compositeDisposable.add(mService.getLocation()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Store>>() {
                    @Override
                    public void accept(List<Store> stores) throws Exception {
                        Common.CurrentStore = stores;
                        updateTokenToServer();
                    }
                }));
    }

    private void updateTokenToServer() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                IPhucLongAPI mSerVice = Common.getAPI();
                mSerVice.insertToken("admin", instanceIdResult.getToken(), 1)
                        .enqueue(new Callback<Token>() {
                            @Override
                            public void onResponse(Call<Token> call, Response<Token> response) {
                                Common.CurrentToken = response.body();
                                Intent intent = new Intent(ActivityStart.this, ActivityMain.class);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onFailure(Call<Token> call, Throwable t) {

                            }
                        });
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("EEE", e.getMessage());
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }
}
