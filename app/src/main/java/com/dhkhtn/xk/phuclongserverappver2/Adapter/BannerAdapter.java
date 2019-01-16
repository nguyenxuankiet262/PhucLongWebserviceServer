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
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.dhkhtn.xk.phuclongserverappver2.ActivityBanners;
import com.dhkhtn.xk.phuclongserverappver2.ActivityMain;
import com.dhkhtn.xk.phuclongserverappver2.Interface.ItemClickListener;
import com.dhkhtn.xk.phuclongserverappver2.Interface.OnActivityResult;
import com.dhkhtn.xk.phuclongserverappver2.Model.Banner;
import com.dhkhtn.xk.phuclongserverappver2.R;
import com.dhkhtn.xk.phuclongserverappver2.Retrofit.IPhucLongAPI;
import com.dhkhtn.xk.phuclongserverappver2.Utils.Common;
import com.dhkhtn.xk.phuclongserverappver2.ViewHolder.BannerViewHolder;
import com.dhkhtn.xk.phuclongserverappver2.ViewHolder.CategoryViewHolder;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class BannerAdapter extends RecyclerView.Adapter<BannerViewHolder> implements OnActivityResult {
    Context context;
    List<Banner> banners;
    Uri imageUri;
    FirebaseStorage firebaseStorage;
    StorageReference mStorageRef;
    ProgressDialog progressDialog;
    IPhucLongAPI mService;

    private static final int GALLERY_PICK = 1;

    ImageView imageView;
    MaterialEditText nameCate;

    Button delete, update;
    FButton accept, cancel;

    public BannerAdapter(Context context, List<Banner> banners) {
        this.context = context;
        this.banners = banners;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_banner_layout,parent,false);
        return new BannerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final BannerViewHolder holder, final int position) {
        Picasso picasso = Picasso.with(context);
        picasso.setIndicatorsEnabled(false);
        picasso.load(banners.get(position).getImage()).networkPolicy(NetworkPolicy.OFFLINE).into(holder.img_banner, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                Picasso picasso = Picasso.with(context);
                picasso.setIndicatorsEnabled(false);
                picasso.load(banners.get(position).getImage()).into(holder.img_banner);
            }
        });
        holder.name_banner.setText(banners.get(position).getName());
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View v, int position) {
                showDialogDeleteOrUpdate(position);
            }
        });
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
        mService = Common.getAPI();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        View itemView = LayoutInflater.from(context).inflate(R.layout.popup_add_category, null);
        imageView = itemView.findViewById(R.id.choose_image);
        nameCate = itemView.findViewById(R.id.name_catelogy);
        imageUri = Uri.parse(banners.get(position).getImage());

        Picasso.with(context).load(banners.get(position).getImage()).into(imageView);
        nameCate.setText(banners.get(position).getName());
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.checkChooseImageFromAdapter = true;
                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                ((Activity) context).startActivityForResult(Intent.createChooser(gallery, "SELECT IMAGE"), GALLERY_PICK);
            }
        });
        accept = itemView.findViewById(R.id.accept_btn);
        cancel = itemView.findViewById(R.id.cancel_dialog);
        builder.setView(itemView);
        final AlertDialog alertDialog = builder.show();
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
                    if (imageUri != null && !TextUtils.isEmpty(nameCate.getText().toString())) {
                        progressDialog = new ProgressDialog(context);
                        progressDialog.setTitle("Uploading image...");
                        progressDialog.setMessage("Please wait for a minute!");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();
                        if (imageUri.equals(Uri.parse(banners.get(position).getImage()))
                                && !nameCate.getText().toString().equals(banners.get(position).getName())) {
                            mService.updateBanner(banners.get(position).getID(),
                                    banners.get(position).getImage(),
                                    nameCate.getText().toString().toUpperCase()).enqueue(new retrofit2.Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    progressDialog.dismiss();
                                    alertDialog.dismiss();
                                    Toast.makeText(context, "Update thành công!", Toast.LENGTH_SHORT).show();
                                    ((ActivityBanners)context).getBanners();
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {

                                }
                            });
                        }
                        else if (!imageUri.equals(Uri.parse(banners.get(position).getImage()))
                                && nameCate.getText().toString().equals(banners.get(position).getName())){
                            final StorageReference riversRef = mStorageRef.child("StorageWebService/" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                            riversRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                                    // Get a URL to the uploaded content
                                    riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String downloadUrl = uri.toString();
                                            mService.updateBanner(banners.get(position).getID(),
                                                    downloadUrl,
                                                    banners.get(position).getName()).enqueue(new retrofit2.Callback<String>() {
                                                @Override
                                                public void onResponse(Call<String> call, Response<String> response) {
                                                    progressDialog.dismiss();
                                                    alertDialog.dismiss();
                                                    Toast.makeText(context, "Thêm thành công!", Toast.LENGTH_SHORT).show();
                                                    ((ActivityBanners)context).getBanners();
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
                                    Toast.makeText(context, "Thêm thất bại!", Toast.LENGTH_SHORT).show();
                                    Log.d("EEE", exception.getMessage());
                                    progressDialog.dismiss();
                                }
                            });
                        }
                        else if(!imageUri.equals(Uri.parse(banners.get(position).getImage()))
                                && !nameCate.getText().toString().equals(banners.get(position).getName())) {
                            final StorageReference riversRef = mStorageRef.child("StorageWebService/" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                            riversRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                                    // Get a URL to the uploaded content
                                    riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String downloadUrl = uri.toString();
                                            mService.updateBanner(banners.get(position).getID(),
                                                    downloadUrl,
                                                    nameCate.getText().toString().toUpperCase()).enqueue(new retrofit2.Callback<String>() {
                                                @Override
                                                public void onResponse(Call<String> call, Response<String> response) {
                                                    progressDialog.dismiss();
                                                    alertDialog.dismiss();
                                                    Toast.makeText(context, "Thêm thành công!", Toast.LENGTH_SHORT).show();
                                                    ((ActivityBanners)context).getBanners();
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
                                    Toast.makeText(context, "Thêm thất bại!", Toast.LENGTH_SHORT).show();
                                    Log.d("EEE", exception.getMessage());
                                    progressDialog.dismiss();
                                }
                            });
                        }
                        else{
                            progressDialog.dismiss();
                        }
                    } else {
                        Toast.makeText(context, "Nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(context, "Không thể kết nối mạng!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showDialogDelete(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Cảnh báo!");
        builder.setIcon(R.drawable.ic_warning_black_16dp);
        builder.setMessage("Bạn có muốn xóa " + banners.get(position).getName() + " khỏi danh sách không?");
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
                mService.deleteBaner(Integer.parseInt(banners.get(position).getID())).enqueue(new retrofit2.Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        progressDialog.dismiss();
                        Toast.makeText(context, "Xóa thành công!", Toast.LENGTH_SHORT).show();
                        ((ActivityBanners)context).getBanners();
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
        return banners.size();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                imageUri = data.getData();
                InputStream imageStream = null;
                imageStream = context.getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(selectedImage);
                imageUri = data.getData();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(context, "Lỗi chọn ảnh", Toast.LENGTH_LONG).show();
            }
        }
    }
}
