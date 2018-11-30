package com.dhkhtn.xk.phuclongserverappver2.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.dhkhtn.xk.phuclongserverappver2.R;

public class FeedbackViewHolder extends RecyclerView.ViewHolder {
    public TextView id_feedback, content_feedback, notreply_feedback, replied_feedback;
    public FeedbackViewHolder(View itemView) {
        super(itemView);
        id_feedback = itemView.findViewById(R.id.id_feedback);
        content_feedback = itemView.findViewById(R.id.content_feedback);
        notreply_feedback = itemView.findViewById(R.id.notreply_text);
        replied_feedback = itemView.findViewById(R.id.reply_text);
    }
}
