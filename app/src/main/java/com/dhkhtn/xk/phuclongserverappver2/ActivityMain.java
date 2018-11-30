package com.dhkhtn.xk.phuclongserverappver2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.dhkhtn.xk.phuclongserverappver2.Adapter.CategoryAdapter;
import com.dhkhtn.xk.phuclongserverappver2.Model.Banner;
import com.dhkhtn.xk.phuclongserverappver2.Model.Category;
import com.dhkhtn.xk.phuclongserverappver2.Retrofit.IPhucLongAPI;
import com.dhkhtn.xk.phuclongserverappver2.Utils.Common;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

public class ActivityMain extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //Value Camera
    private static final int GALLERY_PICK = 1;
    FirebaseStorage firebaseStorage;
    StorageReference mStorageRef;

    private RecyclerView list_menu;
    IPhucLongAPI mService;
    SwipeRefreshLayout swipeRefreshLayout;
    //Slider
    SliderLayout sliderLayout;

    //Adapter
    CategoryAdapter adapter;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    ImageView imageView;
    MaterialEditText nameCate;
    FButton accept, cancel;
    Uri imageUri;
    ProgressDialog progressDialog;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mStorageRef = firebaseStorage.getInstance().getReference();
        mService = Common.getAPI();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        sliderLayout = findViewById(R.id.slider);

        list_menu = findViewById(R.id.list_category);
        list_menu.setLayoutManager(new LinearLayoutManager(ActivityMain.this, LinearLayoutManager.VERTICAL, false));
        list_menu.setHasFixedSize(true);
        list_menu.setNestedScrollingEnabled(false);
        swipeRefreshLayout = findViewById(R.id.swipe_layout_main);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark,
                android.R.color.holo_green_dark,
                android.R.color.holo_blue_dark ,
                android.R.color.holo_orange_dark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(Common.isConnectedToInternet(ActivityMain.this)) {
                    loadMenu();
                    getBanner();
                    swipeRefreshLayout.setRefreshing(false);
                }
                else{
                    Toast.makeText(ActivityMain.this, "Không thể kết nối mạng!", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if(Common.isConnectedToInternet(ActivityMain.this)) {
                    loadMenu();
                    getBanner();
                    swipeRefreshLayout.setRefreshing(false);
                }
                else{
                    Toast.makeText(ActivityMain.this, "Không thể kết nối mạng!", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageUri = null;
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityMain.this);
                builder.setCancelable(true);
                View itemView = LayoutInflater.from(ActivityMain.this).inflate(R.layout.popup_add_category, null);
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
                        if(Common.isConnectedToInternet(ActivityMain.this)) {
                            if (imageUri != null && !TextUtils.isEmpty(nameCate.getText().toString())) {
                                progressDialog = new ProgressDialog(ActivityMain.this);
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
                                                mService.insertCategory(nameCate.getText().toString().toUpperCase(), downloadUrl).enqueue(new Callback<String>() {
                                                    @Override
                                                    public void onResponse(Call<String> call, Response<String> response) {
                                                        progressDialog.dismiss();
                                                        alertDialog.dismiss();
                                                        Toast.makeText(ActivityMain.this, "Thêm thành công!", Toast.LENGTH_SHORT).show();
                                                        loadMenu();
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
                                        Toast.makeText(ActivityMain.this, "Thêm thất bại!", Toast.LENGTH_SHORT).show();
                                        Log.d("EEE", exception.getMessage());
                                        progressDialog.dismiss();
                                    }
                                });
                            } else {
                                Toast.makeText(ActivityMain.this, "Nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            Toast.makeText(ActivityMain.this,"Không thể kết nối mạng!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorWhite));
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void getBanner() {
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
        sliderLayout.removeAllSliders();
        for(int i = 0; i < banners.size(); i++){
            TextSliderView textSliderView = new TextSliderView(ActivityMain.this);
            textSliderView.description(banners.get(i).getName())
                    .image(banners.get(i).getImage())
                    .setScaleType(BaseSliderView.ScaleType.Fit);
            sliderLayout.addSlider(textSliderView);
        }
        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Fade);
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderLayout.setCustomAnimation(new DescriptionAnimation());
        sliderLayout.setDuration(3000);
    }

    public void loadMenu() {
        compositeDisposable.add(mService.getCategory()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Category>>() {
                    @Override
                    public void accept(List<Category> categories) throws Exception {
                        displayCategory(categories);
                    }
                }));
    }

    private void displayCategory(List<Category> categories) {
        adapter = new CategoryAdapter(ActivityMain.this, categories);
        list_menu.setAdapter(adapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        sliderLayout.stopAutoCycle();
    }

    @Override
    public void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_order) {
            Intent orderIntent = new Intent(ActivityMain.this, ActivityOrder.class);
            startActivity(orderIntent);
        } else if (id == R.id.nav_feedback) {
            Intent feedbackIntent = new Intent(ActivityMain.this, ActivityFeedback.class);
            startActivity(feedbackIntent);

        } else if (id == R.id.nav_user) {
            Intent userIntent = new Intent(ActivityMain.this, ActivityUser.class);
            startActivity(userIntent);

        } else if (id == R.id.nav_send) {
            Intent contactusIntent = new Intent(ActivityMain.this,ActivityContactUs.class);
            startActivity(contactusIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(Common.checkChooseImageFromAdapter == true) {
            adapter.onActivityResult(requestCode, resultCode, data);
        }
        else{
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
}
