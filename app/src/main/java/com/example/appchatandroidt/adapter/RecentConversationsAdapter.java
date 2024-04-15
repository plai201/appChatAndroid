package com.example.appchatandroidt.adapter;

import android.content.Context;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.appchatandroidt.R;
import com.example.appchatandroidt.activities.ChatActivity;
import com.example.appchatandroidt.models.Conversions;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class RecentConversationsAdapter extends RecyclerView.Adapter<RecentConversationsAdapter.MessageRecentViewHolder>{
    private final List<Conversions> chatMessages;
    Context context;

//    private final ConversionListener conversionListener;

    public RecentConversationsAdapter(List<Conversions> chatMessages,Context context) {
        this.context = context;
        this.chatMessages = chatMessages;
    }

    @NonNull
    @Override
    public MessageRecentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_container_recent_conversion, parent, false);
        return new MessageRecentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageRecentViewHolder holder, int position) {
        Conversions conversion = chatMessages.get(position);
        holder.textViewName.setText(conversion.conversionName);
        holder.lastMessage.setText(conversion.message);
        Log.d("ConversationData", "Number of children re: " + conversion.conversionName);
        Log.d("ConversationData", "Context: " + context.toString());


        if (conversion.conversionImage != null && !conversion.conversionImage.isEmpty()) {
            Glide.with(context)
                    .load(conversion.conversionImage)
                    .into(holder.imageProfile);

        } else {
            // Nếu không có ảnh đại diện, sử dụng ảnh mặc định
            holder.imageProfile.setImageResource(R.drawable.default_avatar);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý sự kiện khi item được click
                // Ví dụ: mở màn hình chat với người này
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("OtherUserId", conversion.conversionId);
                // Thêm các dữ liệu khác cần thiết vào intent
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

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
}
