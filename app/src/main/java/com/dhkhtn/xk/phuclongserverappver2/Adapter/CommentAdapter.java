package com.dhkhtn.xk.phuclongserverappver2.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dhkhtn.xk.phuclongserverappver2.Model.Rating;
import com.dhkhtn.xk.phuclongserverappver2.R;
import com.dhkhtn.xk.phuclongserverappver2.ViewHolder.CommentViewHolder;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder> {

    Context context;
    List<Rating> ratingList;

    public CommentAdapter(Context context, List<Rating> ratingList) {
        this.context = context;
        this.ratingList = ratingList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment_layout,parent,false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        holder.name_cmt.setText(ratingList.get(position).getUserID().replaceAll("[^\\d\\+]", "").replaceAll("\\d(?=\\d{4})", "*"));
        holder.date_comment.setText("-"+ratingList.get(position).getDate()+"-");
        if(TextUtils.isEmpty(ratingList.get(position).getComment())){
            holder.comment.setVisibility(View.GONE);
        }
        else{
            holder.comment.setText(ratingList.get(position).getComment());
        }
        holder.ratingBar.setRating((float)ratingList.get(position).getRate());
    }

    @Override
    public int getItemCount() {
        return ratingList.size();
    }
}
