package com.dhkhtn.xk.phuclongserverappver2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.dhkhtn.xk.phuclongserverappver2.Adapter.BannerAdapter;
import com.dhkhtn.xk.phuclongserverappver2.Model.Banner;
import com.dhkhtn.xk.phuclongserverappver2.Retrofit.IPhucLongAPI;
import com.dhkhtn.xk.phuclongserverappver2.Utils.Common;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import info.hoang8f.widget.FButton;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityBanners extends AppCompatActivity {
    private static final int GALLERY_PICK = 1;
    private RecyclerView list_banners;
    Toolbar toolbar;
    IPhucLongAPI mService;
    SwipeRefreshLayout swipeRefreshLayout;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    Uri imageUri;
    ImageView imageView;
    MaterialEditText nameCate;

    BannerAdapter adapter;
    FButton accept, cancel;
    ProgressDialog progressDialog;
    AlertDialog alertDialog;

    FloatingActionButton fab;

    FirebaseStorage firebaseStorage;
    StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banners);

        mService = Common.getAPI();
        mStorageRef = firebaseStorage.getInstance().getReference();

        list_banners = findViewById(R.id.listBanners);
        fab = findViewById(R.id.fab_banner);
        toolbar = findViewById(R.id.tool_bar_banner);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        list_banners.setLayoutManager(new LinearLayoutManager(ActivityBanners.this, LinearLayoutManager.VERTICAL, false));
        list_banners.setHasFixedSize(true);
        swipeRefreshLayout = findViewById(R.id.swipe_layout_banner);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark,
                android.R.color.holo_green_dark,
                android.R.color.holo_blue_dark ,
                android.R.color.holo_orange_dark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(Common.isConnectedToInternet(ActivityBanners.this)) {
                    getBanners();
                    swipeRefreshLayout.setRefreshing(false);
                }
                else{
                    Toast.makeText(ActivityBanners.this, "Không thể kết nối mạng!", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if(Common.isConnectedToInternet(ActivityBanners.this)) {
                    getBanners();
                    swipeRefreshLayout.setRefreshing(false);
                }
                else{
                    Toast.makeText(ActivityBanners.this, "Không thể kết nối mạng!", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageUri = null;
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityBanners.this);
                builder.setCancelable(true);
                View itemView = LayoutInflater.from(ActivityBanners.this).inflate(R.layout.popup_add_category, null);
                imageView = itemView.findViewById(R.id.choose_image);
                nameCate = itemView.findViewById(R.id.name_catelogy);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Common.checkChooseImageFromAdapter = false;
                        Intent gallery = new Intent();
                        gallery.setType("image/*");
                        gallery.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(gallery, "SELECT IMAGE"), GALLERY_PICK);
                    }
                });
                accept = itemView.findViewById(R.id.accept_btn);
                cancel = itemView.findViewById(R.id.cancel_dialog);
                builder.setView(itemView);
                alertDialog = builder.show();
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(Common.isConnectedToInternet(ActivityBanners.this)) {
                            if (imageUri != null && !TextUtils.isEmpty(nameCate.getText().toString())) {
                                progressDialog = new ProgressDialog(ActivityBanners.this);
                                progressDialog.setTitle("Uploading image...");
                                progressDialog.setMessage("Please wait for a minute!");
                                progressDialog.setCanceledOnTouchOutside(false);
                                progressDialog.show();

                                final StorageReference riversRef = mStorageRef.child("StorageWebService/" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                                riversRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                                        // Get a URL to the uploaded content
                                        riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                String downloadUrl = uri.toString();
                                                mService.insertBanner(nameCate.getText().toString().toUpperCase(), downloadUrl).enqueue(new Callback<String>() {
                                                    @Override
                                                    public void onResponse(Call<String> call, Response<String> response) {
                                                        progressDialog.dismiss();
                                                        alertDialog.dismiss();
                                                        Toast.makeText(ActivityBanners.this, "Thêm thành công!", Toast.LENGTH_SHORT).show();
                                                        getBanners();
                                                    }

                                                    @Override
                                                    public void onFailure(Call<String> call, Throwable t) {

                                                    }
                                                });
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        Toast.makeText(ActivityBanners.this, "Thêm thất bại!", Toast.LENGTH_SHORT).show();
                                        Log.d("EEE", exception.getMessage());
                                        progressDialog.dismiss();
                                    }
                                });
                            } else {
                                Toast.makeText(ActivityBanners.this, "Nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            Toast.makeText(ActivityBanners.this,"Không thể kết nối mạng!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(Common.checkChooseImageFromAdapter == true) {
            adapter.onActivityResult(requestCode, resultCode, data);
        }
        else {
            if (requestCode == GALLERY_PICK && resultCode == RESULT_OK && data != null && data.getData() != null) {
                try {
                    imageUri = data.getData();
                    InputStream imageStream = null;
                    imageStream = getContentResolver().openInputStream(imageUri);
                    Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    imageView.setImageBitmap(selectedImage);

                    imageUri = data.getData();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Lỗi chọn ảnh", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void getBanners() {
        compositeDisposable.add(mService.getBanner()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Banner>>() {
                    @Override
                    public void accept(List<Banner> banners) throws Exception {
                        displayBanner(banners);
                    }
                }));
    }

    private void displayBanner(List<Banner> banners) {
        Log.d("EEE",banners.size() +"");
        adapter = new BannerAdapter(ActivityBanners.this, banners);
        list_banners.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }
}
