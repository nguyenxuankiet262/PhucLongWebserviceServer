package com.dhkhtn.xk.phuclongserverappver2.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dhkhtn.xk.phuclongserverappver2.ActivityFeedback;
import com.dhkhtn.xk.phuclongserverappver2.Model.Feedback;
import com.dhkhtn.xk.phuclongserverappver2.R;
import com.dhkhtn.xk.phuclongserverappver2.Retrofit.IPhucLongAPI;
import com.dhkhtn.xk.phuclongserverappver2.Utils.Common;
import com.dhkhtn.xk.phuclongserverappver2.ViewHolder.FeedbackViewHolder;
import com.rengwuxian.materialedittext.MaterialEditText;


import java.util.List;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackViewHolder> {
    Context context;
    List<Feedback> feedbackList;
    AlertDialog alertDialog;
    TextView feedback_text;
    Button edit_btn;
    MaterialEditText replied_text;
    IPhucLongAPI mService;
    public FeedbackAdapter(Context context, List<Feedback> feedbackList){
        this.context = context;
        this.feedbackList = feedbackList;
    }
    @NonNull
    @Override
    public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_feedback_layout, parent, false);
        return new FeedbackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FeedbackViewHolder holder, final int position) {
        holder.id_feedback.setText(position + 1 + "");
        holder.content_feedback.setText(feedbackList.get(position).getContent());
        if(!TextUtils.isEmpty(feedbackList.get(position).getReply())){
            holder.notreply_feedback.setVisibility(View.INVISIBLE);
            holder.replied_feedback.setVisibility(View.VISIBLE);
        }
        else{
            holder.notreply_feedback.setVisibility(View.VISIBLE);
            holder.replied_feedback.setVisibility(View.INVISIBLE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                View item = LayoutInflater.from(context).inflate(R.layout.popup_feedback_layout, null);
                feedback_text = item.findViewById(R.id.feedback_text);
                replied_text = item.findViewById(R.id.replied_text);
                edit_btn = item.findViewById(R.id.btn_save);
                if(!TextUtils.isEmpty(feedbackList.get(position).getReply())){
                    replied_text.setText(feedbackList.get(position).getReply());
                }
                feedback_text.setText(feedbackList.get(position).getContent());
                edit_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!replied_text.getText().toString().equals(feedbackList.get(position).getReply()) &&
                                !TextUtils.isEmpty(replied_text.getText().toString())){
                            UpdateFeedback(feedbackList.get(position).getId(),replied_text.getText().toString());
                        }
                    }
                });
                builder.setView(item);
                alertDialog = builder.show();

            }
        });

    }

    private void UpdateFeedback(int id, String s) {
        mService = Common.getAPI();
        final AlertDialog spotDialog = new SpotsDialog.Builder().setContext(context).build();
        spotDialog.setMessage("Please waiting...");
        spotDialog.show();
        mService.updateFeedback(id,s).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                spotDialog.dismiss();
                alertDialog.dismiss();
                Toast.makeText(context, "Update thành công!", Toast.LENGTH_SHORT).show();
                ((ActivityFeedback)context).LoadFeedBack();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return feedbackList.size();
    }
}
