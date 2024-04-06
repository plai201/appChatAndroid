package com.example.appchatandroidt.activities;

import android.view.View;
 import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchatandroidt.R;
import com.makeramen.roundedimageview.RoundedImageView;

public class FriendViewHolder extends RecyclerView.ViewHolder {
      RoundedImageView proFileImage;
      TextView name, sdt;

    public FriendViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.txt_name);
        sdt = itemView.findViewById(R.id.txt_sdt);
        proFileImage = itemView.findViewById(R.id.image_profile_url);
    }
}
