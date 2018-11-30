package com.dhkhtn.xk.phuclongserverappver2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dhkhtn.xk.phuclongserverappver2.Adapter.DrinkAdapter;
import com.dhkhtn.xk.phuclongserverappver2.Model.Drink;
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

public class ActivityDrink extends AppCompatActivity {
    private static final int GALLERY_PICK = 1;
    FirebaseStorage firebaseStorage;
    StorageReference mStorageRef;

    private Toolbar toolbar;

    public String name_cate, id_cate;

    RecyclerView list_drink;
    //Adapter
    DrinkAdapter adapter;

    IPhucLongAPI mService;

    SwipeRefreshLayout swipeRefreshLayout;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    FloatingActionButton fab;

    RelativeLayout choose_image_hot_layout, choose_image_cold_layout;

    MaterialEditText name_drink_edt, price_drink_edt;

    FButton accept, cancel;

    AlertDialog alertDialog;

    Uri imageUriHot, imgageUriCold;

    ProgressDialog progressDialog;

    int checkChoose;

    ImageView hot_image, cold_image;

    String HotUrl = "empty";
    String ColdUrl = "empty";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink);
        mService = Common.getAPI();
        mStorageRef = firebaseStorage.getInstance().getReference();

        fab = findViewById(R.id.fab_drink);
        toolbar = findViewById(R.id.tool_bar_drink);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        list_drink = findViewById(R.id.listDrink);
        list_drink.setLayoutManager(new LinearLayoutManager(ActivityDrink.this, LinearLayoutManager.VERTICAL,false));
        list_drink.setHasFixedSize(true);


        if(getIntent() != null){
            id_cate = getIntent().getStringExtra("CategoryId");
            Log.d("EEE", id_cate);
            name_cate = getIntent().getStringExtra("CategoryName");
        }
        if(!id_cate.isEmpty() && id_cate != null){
            toolbar.setTitle(name_cate);
        }

        swipeRefreshLayout = findViewById(R.id.swipe_layout_drink);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark,
                android.R.color.holo_green_dark,
                android.R.color.holo_blue_dark ,
                android.R.color.holo_orange_dark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(Common.isConnectedToInternet(ActivityDrink.this)) {
                    loadDrink(id_cate);
                    swipeRefreshLayout.setRefreshing(false);
                }
                else{
                    Toast.makeText(ActivityDrink.this, "Không thể kết nối mạng!", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if(Common.isConnectedToInternet(ActivityDrink.this)) {
                    loadDrink(id_cate);
                    swipeRefreshLayout.setRefreshing(false);
                }
                else{
                    Toast.makeText(ActivityDrink.this, "Không thể kết nối mạng!", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HotUrl = "empty";
                ColdUrl = "empty";
                imageUriHot = null;
                imgageUriCold = null;
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityDrink.this);
                builder.setCancelable(true);
                View itemView = LayoutInflater.from(ActivityDrink.this).inflate(R.layout.popup_add_drink, null);

                choose_image_hot_layout = itemView.findViewById(R.id.choose_image_hot);
                choose_image_cold_layout = itemView.findViewById(R.id.choose_image_cold);

                name_drink_edt = itemView.findViewById(R.id.edt_name_drink);
                price_drink_edt = itemView.findViewById(R.id.edt_price_drink);
                accept = itemView.findViewById(R.id.accept_btn_drink);
                cancel = itemView.findViewById(R.id.cancel_dialog_drink);
                hot_image = itemView.findViewById(R.id.image_hot);
                cold_image = itemView.findViewById(R.id.image_cold);

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
                        progressDialog = new ProgressDialog(ActivityDrink.this);
                        progressDialog.setTitle("Uploading image...");
                        progressDialog.setMessage("Please wait for a minute!");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();
                        if(!TextUtils.isEmpty(name_drink_edt.getText().toString()) && !TextUtils.isEmpty(price_drink_edt.getText().toString())){
                            if(imageUriHot != null){
                                final StorageReference riversRef = mStorageRef.child("StorageWebService/" + String.valueOf(System.currentTimeMillis())+".jpg");
                                riversRef.putFile(imageUriHot).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                                        // Get a URL to the uploaded content
                                        riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                HotUrl = uri.toString();
                                                if(imgageUriCold != null){
                                                    final StorageReference riversRef = mStorageRef.child("StorageWebService/" + String.valueOf(System.currentTimeMillis())+".jpg");
                                                    riversRef.putFile(imgageUriCold).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                        @Override
                                                        public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                                                            // Get a URL to the uploaded content
                                                            riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                @Override
                                                                public void onSuccess(Uri uri) {
                                                                    ColdUrl = uri.toString();
                                                                    if(HotUrl.equals("empty") && ColdUrl.equals("empty")){
                                                                        progressDialog.dismiss();
                                                                        Toast.makeText(ActivityDrink.this, "Vui lòng chọn ảnh!", Toast.LENGTH_SHORT).show();
                                                                    }else{
                                                                        InsertDrink(HotUrl, ColdUrl, name_drink_edt.getText().toString(), price_drink_edt.getText().toString());
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception exception) {
                                                            Toast.makeText(ActivityDrink.this, "Thêm thất bại!", Toast.LENGTH_SHORT).show();
                                                            Log.d("EEE", exception.getMessage());
                                                            progressDialog.dismiss();
                                                        }
                                                    });
                                                }
                                                else{
                                                    if(HotUrl.equals("empty") && ColdUrl.equals("empty")){
                                                        progressDialog.dismiss();
                                                        Toast.makeText(ActivityDrink.this, "Vui lòng chọn ảnh!", Toast.LENGTH_SHORT).show();
                                                    }else{
                                                        InsertDrink(HotUrl, ColdUrl, name_drink_edt.getText().toString(), price_drink_edt.getText().toString());
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        Toast.makeText(ActivityDrink.this, "Thêm thất bại!", Toast.LENGTH_SHORT).show();
                                        Log.d("EEE", exception.getMessage());
                                        progressDialog.dismiss();
                                    }
                                });
                            }
                            else{
                                if(imgageUriCold != null){
                                    final StorageReference riversRef = mStorageRef.child("StorageWebService/" + String.valueOf(System.currentTimeMillis())+".jpg");
                                    riversRef.putFile(imgageUriCold).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                                            // Get a URL to the uploaded content
                                            riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    ColdUrl = uri.toString();
                                                    if(HotUrl.equals("empty") && ColdUrl.equals("empty")){
                                                        progressDialog.dismiss();
                                                        Toast.makeText(ActivityDrink.this, "Vui lòng chọn ảnh!", Toast.LENGTH_SHORT).show();
                                                    }else{
                                                        InsertDrink(HotUrl, ColdUrl, name_drink_edt.getText().toString(), price_drink_edt.getText().toString());
                                                    }
                                                }
                                            });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            Toast.makeText(ActivityDrink.this, "Thêm thất bại!", Toast.LENGTH_SHORT).show();
                                            Log.d("EEE", exception.getMessage());
                                            progressDialog.dismiss();
                                        }
                                    });
                                }else{
                                    if(HotUrl.equals("empty") && ColdUrl.equals("empty")){
                                        progressDialog.dismiss();
                                        Toast.makeText(ActivityDrink.this, "Vui lòng chọn ảnh!", Toast.LENGTH_SHORT).show();
                                    }else{
                                        InsertDrink(HotUrl, ColdUrl, name_drink_edt.getText().toString(), price_drink_edt.getText().toString());
                                    }
                                }
                            }
                        }
                        else{
                            progressDialog.dismiss();
                            Toast.makeText(ActivityDrink.this,"Nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                        }



                    }
                });
                choose_image_hot_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Common.checkChooseImageFromAdapter = false;
                        checkChoose = 1;
                        Intent gallery = new Intent();
                        gallery.setType("image/*");
                        gallery.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(gallery, "SELECT IMAGE"), GALLERY_PICK);
                    }
                });
                choose_image_cold_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Common.checkChooseImageFromAdapter = false;
                        checkChoose = 2;
                        Intent gallery = new Intent();
                        gallery.setType("image/*");
                        gallery.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(gallery, "SELECT IMAGE"), GALLERY_PICK);
                    }
                });
            }
        });
    }

    private void InsertDrink(String hotUrl, String coldUrl, String name, String price) {
        mService.insertDrink(Integer.parseInt(id_cate),hotUrl, coldUrl, name, Integer.parseInt(price)).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                progressDialog.dismiss();
                alertDialog.dismiss();
                Toast.makeText(ActivityDrink.this, "Thêm thành công!", Toast.LENGTH_SHORT).show();
                loadDrink(id_cate);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(Common.checkChooseImageFromAdapter == true) {
            adapter.onActivityResult(requestCode, resultCode, data);
        }
        else{
            if (requestCode == GALLERY_PICK && resultCode == RESULT_OK && data != null && data.getData() != null) {
                try {
                    if(checkChoose == 1){
                        imageUriHot = data.getData();
                        InputStream imageStream = null;
                        imageStream = getContentResolver().openInputStream(imageUriHot);
                        Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        hot_image.setImageBitmap(selectedImage);

                        imageUriHot = data.getData();
                    }
                    else{
                        imgageUriCold = data.getData();
                        InputStream imageStream = null;
                        imageStream = getContentResolver().openInputStream(imgageUriCold);
                        Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        cold_image.setImageBitmap(selectedImage);

                        imgageUriCold = data.getData();
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Lỗi chọn ảnh", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void loadDrink(String id_cate) {
        compositeDisposable.add(mService.getDrink(id_cate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Drink>>() {
                    @Override
                    public void accept(List<Drink> drinks) throws Exception {
                        if(drinks.size() != 0) {
                            displayDrink(drinks);
                        }
                    }
                }));
    }
    private void displayDrink(List<Drink> drinks) {
        adapter = new DrinkAdapter(ActivityDrink.this, drinks);
        list_drink.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }

}
