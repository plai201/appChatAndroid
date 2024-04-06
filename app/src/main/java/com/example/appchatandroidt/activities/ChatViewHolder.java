package com.example.appchatandroidt.activities;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appchatandroidt.R;
import com.makeramen.roundedimageview.RoundedImageView;

public class ChatViewHolder extends RecyclerView.ViewHolder {
    private TextView textMessageSent,textViewDatetimeT, textViewDatetimeR;
    private TextView textMessageReceived, textViewDatetimeRT, getTextViewDatetimeRR;;
    private RoundedImageView imageProfileReceivedUrl;
    private ImageView imageSend;
    private ImageView imageReceive;


    public ChatViewHolder(@NonNull View itemView) {
        super(itemView);
        textMessageSent = itemView.findViewById(R.id.txtMessageSent);
        textMessageReceived = itemView.findViewById(R.id.txtMessageReceived);
        imageProfileReceivedUrl = itemView.findViewById(R.id.imageProfileReceived);
        imageSend = itemView.findViewById(R.id.imageSending);
        imageReceive = itemView.findViewById(R.id.imageReceive);
        textViewDatetimeT = itemView.findViewById(R.id.txtDataTimeS);
        textViewDatetimeR = itemView.findViewById(R.id.txtDataTimeImageS);
        textViewDatetimeRT = itemView.findViewById(R.id.txtDataTimeR);
        getTextViewDatetimeRR = itemView.findViewById(R.id.txtDataTimeImageR);
    }

    public void bindSentMessage(String message,String type,String datetime) {
        if (type.equals("text")){
            textMessageSent.setVisibility(View.VISIBLE);
            textViewDatetimeT.setVisibility(View.VISIBLE);
            textViewDatetimeT.setText(datetime);
            textMessageSent.setText(message);
        } else if (type.equals("iv")) {
            imageSend.setVisibility(View.VISIBLE);
            textMessageSent.setVisibility(View.GONE);
            textViewDatetimeT.setVisibility(View.GONE);
            textViewDatetimeR.setVisibility(View.VISIBLE);
            textViewDatetimeR.setText(datetime);
            Glide.with(itemView.getContext())
                    .load(message)
                    .into(imageSend);
        }
    }

    public void bindReceivedMessage(String message,String imageUrl,String type,String datetime) {
        // Kiểm tra và hiển thị ảnh đại diện
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(itemView.getContext())
                    .load(imageUrl)
                    .into(imageProfileReceivedUrl);

        } else {
            // Nếu không có ảnh đại diện, sử dụng ảnh mặc định
            imageProfileReceivedUrl.setImageResource(R.drawable.default_avatar);
        }
        if (type.equals("text")){
            textMessageReceived.setVisibility(View.VISIBLE);
            textViewDatetimeRT.setVisibility(View.VISIBLE);
            textViewDatetimeRT.setText(datetime);
            textMessageReceived.setText(message);
        } else if (type.equals("iv")) {
            imageReceive.setVisibility(View.VISIBLE);
            textMessageReceived.setVisibility(View.GONE);
            textViewDatetimeRT.setVisibility(View.GONE);
            getTextViewDatetimeRR.setVisibility(View.VISIBLE);
            getTextViewDatetimeRR.setText(datetime);
            Glide.with(itemView.getContext())
                    .load(message)
                    .into(imageReceive);
        }


    }
}
