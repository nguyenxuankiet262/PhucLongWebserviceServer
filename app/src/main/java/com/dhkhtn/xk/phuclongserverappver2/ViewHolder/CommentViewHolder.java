package com.dhkhtn.xk.phuclongserverappver2.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.dhkhtn.xk.phuclongserverappver2.R;

public class CommentViewHolder extends RecyclerView.ViewHolder{
    public TextView name_cmt, comment, date_comment;
    public RatingBar ratingBar;
    public CommentViewHolder(View itemview){
        super(itemview);
        name_cmt = itemview.findViewById(R.id.name_comment);
        comment = itemview.findViewById(R.id.comment_in_comment);
        date_comment = itemview.findViewById(R.id.date_comment);
        ratingBar = itemview.findViewById(R.id.rating_bar_comment);
    }
}
