package com.example.appchatandroidt.activities;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchatandroidt.R;

public class UserDetailViewHolder extends RecyclerView.ViewHolder {

    public TextView textName, textEmail, textPhone, textRole;
    public ImageView imageUser;
    public ImageButton btnEdit, btnDelete;
    public UserDetailViewHolder(@NonNull View itemView) {
        super(itemView);

    }
}
