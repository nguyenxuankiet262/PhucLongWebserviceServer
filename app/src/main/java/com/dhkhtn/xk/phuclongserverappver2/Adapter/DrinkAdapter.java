package com.dhkhtn.xk.phuclongserverappver2.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dhkhtn.xk.phuclongserverappver2.ActivityDrink;
import com.dhkhtn.xk.phuclongserverappver2.Interface.ItemClickListener;
import com.dhkhtn.xk.phuclongserverappver2.Interface.ItemLongClickListener;
import com.dhkhtn.xk.phuclongserverappver2.Interface.OnActivityResult;
import com.dhkhtn.xk.phuclongserverappver2.Model.AverageRate;
import com.dhkhtn.xk.phuclongserverappver2.Model.Drink;
import com.dhkhtn.xk.phuclongserverappver2.Model.Rating;
import com.dhkhtn.xk.phuclongserverappver2.R;
import com.dhkhtn.xk.phuclongserverappver2.Retrofit.IPhucLongAPI;
import com.dhkhtn.xk.phuclongserverappver2.Utils.Common;
import com.dhkhtn.xk.phuclongserverappver2.ViewHolder.DrinkViewHolder;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import info.hoang8f.widget.FButton;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class DrinkAdapter extends RecyclerView.Adapter<DrinkViewHolder> implements OnActivityResult {

    Context context;
    List<Drink> drinkList;
    Button delete, update;
    FirebaseStorage firebaseStorage;
    StorageReference mStorageRef;
    ProgressDialog progressDialog;
    IPhucLongAPI mService;

    private static final int GALLERY_PICK = 1;

    RelativeLayout choose_image_hot_layout, choose_image_cold_layout;

    MaterialEditText name_drink_edt, price_drink_edt;

    FButton accept, cancel;

    AlertDialog alertDialog, commentDialog;

    Uri imageUriHot, imgageUriCold;

    int checkChoose;

    ImageView hot_image, cold_image;

    String HotUrl = "empty";
    String ColdUrl = "empty";

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    CommentAdapter adapter;
    RecyclerView listCmt;

    public DrinkAdapter(Context context, List<Drink> drinkList) {
        this.context = context;
        this.drinkList = drinkList;
    }

    @NonNull
    @Override
    public DrinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_drink_layout, parent, false);
        return new DrinkViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final DrinkViewHolder holder, final int position) {
        mService = Common.getAPI();
        holder.name_drink.setText(drinkList.get(position).getName());
        holder.price_drink.setText(Common.ConvertIntToMoney(drinkList.get(position).getPrice()));
        if (!drinkList.get(position).getImageCold().equals("empty")) {
            Picasso.with(context).load(drinkList.get(position).getImageCold()).into(holder.image_product);
        }
        else if (!drinkList.get(position).getImageHot().equals("empty")) {
            Picasso.with(context).load(drinkList.get(position).getImageHot()).into(holder.image_product);
        }

        if (drinkList.get(position).getImageCold().equals("empty")) {
            holder.cold_btn.setVisibility(View.INVISIBLE);
        }
        if (drinkList.get(position).getImageHot().equals("empty")) {
                holder.hot_btn.setVisibility(View.INVISIBLE);
        }
        holder.cold_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Picasso.with(context).load(drinkList.get(position).getImageCold()).into(holder.image_product);
            }
        });
        holder.hot_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Picasso.with(context).load(drinkList.get(position).getImageHot()).into(holder.image_product);
            }
        });
        mService.getAvgRate(drinkList.get(position).getID()).enqueue(new Callback<AverageRate>() {
            @Override
            public void onResponse(Call<AverageRate> call, Response<AverageRate> response) {
                AverageRate averageRate = response.body();
                if(averageRate.getAvg_rate() != null) {
                    holder.ratingBar.setRating(averageRate.getAvg_rate());
                }
            }

            @Override
            public void onFailure(Call<AverageRate> call, Throwable t) {
                Log.d("EEEE", t.getMessage());
            }
        });
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View v, int position) {
                if(Common.isConnectedToInternet(context)) {
                    LoadComment(position);
                }
                else{
                    Toast.makeText(context,"Không thể kết nối mạng!", Toast.LENGTH_SHORT).show();
                }
            }
        }, new ItemLongClickListener() {
            @Override
            public boolean onLongClick(View v, int position) {
                showDialogDeleteOrUpdate(position);
                return true;
            }
        });
    }

    private void LoadComment(int position) {
        compositeDisposable.add(mService.getRating(drinkList.get(position).getID())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Rating>>() {
                    @Override
                    public void accept(List<Rating> ratings) throws Exception {
                        if(ratings.size() > 0){
                            displayComment(ratings);
                        }else{
                            Toast.makeText(context,"Không có bình luận!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
        );
    }

    private void displayComment(List<Rating> ratings) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        View itemView = LayoutInflater.from(context).inflate(R.layout.popup_comment_layout, null);
        listCmt = itemView.findViewById(R.id.listComment);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        listCmt.setLayoutManager(mLayoutManager);
        adapter = new CommentAdapter(context,ratings);
        listCmt.setAdapter(adapter);
        builder.setView(itemView);
        commentDialog = builder.show();
    }

    private void showDialogDeleteOrUpdate(final int position) {
        mStorageRef = firebaseStorage.getInstance().getReference();
        final Dialog alertDialog = new Dialog(context);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.popup_category_layout);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        update = alertDialog.findViewById(R.id.update_btn);
        delete = alertDialog.findViewById(R.id.delete_btn);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                showDialogUpdate(position);
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                showDialogDelete(position);
            }
        });
        alertDialog.show();
    }

    private void showDialogUpdate(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        View itemView = LayoutInflater.from(context).inflate(R.layout.popup_add_drink, null);
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
        if(!drinkList.get(position).getImageHot().equals("empty")) {
            imageUriHot = Uri.parse(drinkList.get(position).getImageHot());
            HotUrl = drinkList.get(position).getImageHot();
        }
        else{
            imageUriHot = null;
            HotUrl = "empty";
        }
        if(!drinkList.get(position).getImageCold().equals("empty")) {
            imgageUriCold = Uri.parse(drinkList.get(position).getImageCold());
            ColdUrl = drinkList.get(position).getImageCold();
        }
        else{
            imgageUriCold = null;
            ColdUrl = "empty";
        }


        if (!drinkList.get(position).getImageCold().equals("empty")) {
            Picasso.with(context).load(drinkList.get(position).getImageCold()).into(cold_image);
        }
        if (!drinkList.get(position).getImageHot().equals("empty")) {
            Picasso.with(context).load(drinkList.get(position).getImageHot()).into(hot_image);
        }
        name_drink_edt.setText(drinkList.get(position).getName());
        price_drink_edt.setText(drinkList.get(position).getPrice()+"");
        choose_image_cold_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.checkChooseImageFromAdapter = true;
                checkChoose = 2;
                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                ((Activity) context).startActivityForResult(Intent.createChooser(gallery, "SELECT IMAGE"), GALLERY_PICK);
            }
        });
        choose_image_hot_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.checkChooseImageFromAdapter = true;
                checkChoose = 1;
                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                ((Activity) context).startActivityForResult(Intent.createChooser(gallery, "SELECT IMAGE"), GALLERY_PICK);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Common.isConnectedToInternet(context)) {
                    progressDialog = new ProgressDialog(context);
                    progressDialog.setTitle("Uploading image...");
                    progressDialog.setMessage("Please wait for a minute!");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    if (!TextUtils.isEmpty(name_drink_edt.getText().toString()) && !TextUtils.isEmpty(price_drink_edt.getText().toString())) {
                        if (imageUriHot != null) {
                            if (!imageUriHot.equals(Uri.parse(drinkList.get(position).getImageHot()))) {
                                final StorageReference riversRef = mStorageRef.child("StorageWebService/" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                                riversRef.putFile(imageUriHot).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                                        // Get a URL to the uploaded content
                                        riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                HotUrl = uri.toString();
                                                if (imgageUriCold != null) {
                                                    if (!imgageUriCold.equals(Uri.parse(drinkList.get(position).getImageCold()))) {
                                                        final StorageReference riversRef = mStorageRef.child("StorageWebService/" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                                                        riversRef.putFile(imgageUriCold).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                            @Override
                                                            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                                                                // Get a URL to the uploaded content
                                                                riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                    @Override
                                                                    public void onSuccess(Uri uri) {
                                                                        ColdUrl = uri.toString();
                                                                        UpdateDrink(drinkList.get(position).getID(), HotUrl, ColdUrl, name_drink_edt.getText().toString(), price_drink_edt.getText().toString());
                                                                    }
                                                                });
                                                            }
                                                        });
                                                    } else {
                                                        UpdateDrink(drinkList.get(position).getID(),HotUrl, ColdUrl, name_drink_edt.getText().toString(), price_drink_edt.getText().toString());
                                                    }
                                                } else {
                                                    UpdateDrink(drinkList.get(position).getID(), HotUrl, ColdUrl, name_drink_edt.getText().toString(), price_drink_edt.getText().toString());
                                                }
                                            }
                                        });
                                    }
                                });
                            } else {
                                if (imgageUriCold != null) {
                                    if (!imgageUriCold.equals(Uri.parse(drinkList.get(position).getImageCold()))) {
                                        final StorageReference riversRef = mStorageRef.child("StorageWebService/" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                                        riversRef.putFile(imgageUriCold).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                                                // Get a URL to the uploaded content
                                                riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        ColdUrl = uri.toString();
                                                        UpdateDrink(drinkList.get(position).getID(), HotUrl, ColdUrl, name_drink_edt.getText().toString(), price_drink_edt.getText().toString());
                                                    }
                                                });
                                            }
                                        });
                                    } else {
                                        UpdateDrink(drinkList.get(position).getID(), HotUrl, ColdUrl, name_drink_edt.getText().toString(), price_drink_edt.getText().toString());
                                    }
                                } else {
                                    UpdateDrink(drinkList.get(position).getID(), HotUrl, ColdUrl, name_drink_edt.getText().toString(), price_drink_edt.getText().toString());
                                }
                            }
                        } else {
                            if (imgageUriCold != null) {
                                if (!imgageUriCold.equals(Uri.parse(drinkList.get(position).getImageCold()))) {
                                    final StorageReference riversRef = mStorageRef.child("StorageWebService/" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                                    riversRef.putFile(imgageUriCold).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                                            // Get a URL to the uploaded content
                                            riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    ColdUrl = uri.toString();
                                                    UpdateDrink(drinkList.get(position).getID(), HotUrl, ColdUrl, name_drink_edt.getText().toString(), price_drink_edt.getText().toString());
                                                }
                                            });
                                        }
                                    });
                                } else {
                                    UpdateDrink(drinkList.get(position).getID(), HotUrl, ColdUrl, name_drink_edt.getText().toString(), price_drink_edt.getText().toString());
                                }
                            } else {
                                UpdateDrink(drinkList.get(position).getID(), HotUrl, ColdUrl, name_drink_edt.getText().toString(), price_drink_edt.getText().toString());
                            }
                        }
                    } else {
                        Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
                else{
                    Toast.makeText(context, "Không thể kết nối mạng!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void UpdateDrink(int id, String hotUrl, String coldUrl, String name, String price) {
        mService.updateDrink(id,hotUrl, coldUrl, name, Integer.parseInt(price)).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                progressDialog.dismiss();
                alertDialog.dismiss();
                Toast.makeText(context, "Update thành công!", Toast.LENGTH_SHORT).show();
                ((ActivityDrink)context).loadDrink(((ActivityDrink)context).id_cate);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private void showDialogDelete(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Cảnh báo!");
        builder.setIcon(R.drawable.ic_warning_black_16dp);
        builder.setMessage("Bạn có muốn xóa " + drinkList.get(position).getName() + " khỏi danh sách không?");
        builder.setCancelable(false);
        builder.setPositiveButton("Chấp nhận", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mService = Common.getAPI();
                progressDialog = new ProgressDialog(context);
                progressDialog.setTitle("Deleting...");
                progressDialog.setMessage("Please wait for a minute!");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                mService.deleteDrink(drinkList.get(position).getID()).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        progressDialog.dismiss();
                        ((ActivityDrink)context).loadDrink(((ActivityDrink)context).id_cate);
                        Toast.makeText(context, "Xóa thành công!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });
            }
        });

        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public int getItemCount() {
        return drinkList.size();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                if(checkChoose == 1){
                    imageUriHot = data.getData();
                    InputStream imageStream = null;
                    imageStream = context.getContentResolver().openInputStream(imageUriHot);
                    Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    hot_image.setImageBitmap(selectedImage);

                    imageUriHot = data.getData();
                }
                else{
                    imgageUriCold = data.getData();
                    InputStream imageStream = null;
                    imageStream = context.getContentResolver().openInputStream(imgageUriCold);
                    Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    cold_image.setImageBitmap(selectedImage);

                    imgageUriCold = data.getData();
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(context, "Lỗi chọn ảnh", Toast.LENGTH_LONG).show();
            }
        }
    }
}
