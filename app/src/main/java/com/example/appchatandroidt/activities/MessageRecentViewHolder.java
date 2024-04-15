package com.example.appchatandroidt.activities;

import android.view.View;
import android.widget.TextView;
import com.example.appchatandroidt.R;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;

public class MessageRecentViewHolder extends RecyclerView.ViewHolder {
     TextView textViewName;
     TextView lastMessage;
     RoundedImageView imageProfile;
    public MessageRecentViewHolder(@NonNull View itemView) {
        super(itemView);
        textViewName = itemView.findViewById(R.id.txtNameRecent);
        lastMessage = itemView.findViewById(R.id.txtRecentMessage);
        imageProfile = itemView.findViewById(R.id.imageProfileRecent);
    }
}
