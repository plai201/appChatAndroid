package com.example.appchatandroid.adapters;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchatandroid.databinding.ItemContainerReceivedMessageBinding;
import com.example.appchatandroid.databinding.ItemContainerSentMessageBinding;
import com.example.appchatandroid.models.ChatMessage;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private final List<ChatMessage> chatMessages;
    private Bitmap receiverProfileImage;
    private final String senderId;
    public static final int VIEW_TYPE_SEND = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;
    private static Context context;
     public void setReceiverProfileImage(Bitmap bitmap){
        receiverProfileImage = bitmap;
    }

    public ChatAdapter(List<ChatMessage> chatMessages, Bitmap receiverProfileImage, String senderId,Context context) {
        this.chatMessages = chatMessages;
        this.receiverProfileImage = receiverProfileImage;
        this.senderId = senderId;
        this.context = context;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_SEND){
            return new SentMessageViewHolder(
                    ItemContainerSentMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        }else {
            return new ReceiverMessageViewHolder(
                    ItemContainerReceivedMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position) == VIEW_TYPE_SEND){
            ((SentMessageViewHolder) holder).setData(chatMessages.get(position));
        }else {
            ((ReceiverMessageViewHolder)holder).setData(chatMessages.get(position),receiverProfileImage);
        }

    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (chatMessages.get(position).senderId.equals(senderId)){
            return VIEW_TYPE_SEND;
        }else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder{
        private final ItemContainerSentMessageBinding binding;
        SentMessageViewHolder(ItemContainerSentMessageBinding itemContainerSentMessageBinding){
            super(itemContainerSentMessageBinding.getRoot());
            binding = itemContainerSentMessageBinding;
        }
        void setData(ChatMessage chatMessage){
            if (chatMessage.message != null && !chatMessage.message.isEmpty()) {
                // Kiểm tra xem message là URI của hình ảnh không
                Uri messageUri = Uri.parse(chatMessage.message);
                if (isImageUri(messageUri)) {
                    // Nếu là hình ảnh, hiển thị ImageView và load hình ảnh từ URI
                    binding.imageSending.setVisibility(View.VISIBLE);
                    binding.txtMessage.setVisibility(View.GONE);
                    Picasso.get().load(messageUri).into(binding.imageSending);
                } else {
                    // Nếu không phải là hình ảnh, hiển thị message trong TextView
                    binding.imageSending.setVisibility(View.GONE);
                    binding.txtMessage.setVisibility(View.VISIBLE);
                    binding.txtMessage.setText(chatMessage.message);
                }
            } else {
                // Nếu message là null hoặc rỗng, ẩn cả ImageView và TextView
                binding.imageSending.setVisibility(View.GONE);
                binding.txtMessage.setVisibility(View.GONE);
            }
            // Hiển thị thời gian
            binding.txtDataTime.setText(chatMessage.dateTime);
        }
        private boolean isImageUri(Uri uri) {
            ContentResolver contentResolver = context.getContentResolver();
            String mimeType = contentResolver.getType(uri);
            if (mimeType != null && mimeType.startsWith("image/")) {
                // Đây là một URI của hình ảnh
                return true;
            } else {
                // Không phải là một URI của hình ảnh
                return false;
            }
        }

    }
    static class ReceiverMessageViewHolder extends  RecyclerView.ViewHolder{
        private  final ItemContainerReceivedMessageBinding binding;
        ReceiverMessageViewHolder(ItemContainerReceivedMessageBinding itemContainerReceivedMessageBinding){
            super(itemContainerReceivedMessageBinding.getRoot());
            binding = itemContainerReceivedMessageBinding;
        }
        void setData(ChatMessage chatMessage, Bitmap receiverProfileImage){
            binding.txtMessage.setText(chatMessage.message);
            binding.txtDataTime.setText(chatMessage.dateTime);
            if (receiverProfileImage !=null){
                binding.imageProfile.setImageBitmap(receiverProfileImage);
            }
        }
    }
}
