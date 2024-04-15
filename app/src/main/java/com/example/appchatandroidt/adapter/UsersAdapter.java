package com.example.appchatandroidt.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.appchatandroidt.R;
import com.example.appchatandroidt.models.User;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    private Context context;
    private List<User> userList;
    private OnUserClickListener onUserClickListener; // Interface

    public UsersAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }
    // Interface để xử lý sự kiện click
    public interface OnUserClickListener {
        void onUserClick(int position);
    }
    public void setOnUserClickListener(OnUserClickListener listener) {
        this.onUserClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_container_user,parent,false);
        return new UsersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);
        holder.name.setText(user.getName());
        holder.sdt.setText(user.getSdt());
        // Kiểm tra và hiển thị ảnh đại diện
        if(user.getAvatarUrl()!= null && !user.getAvatarUrl().isEmpty()){
            Glide.with(context)
                    .load(user.getAvatarUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.profile_image);
        } else {
            // Nếu không có ảnh đại diện, sử dụng ảnh mặc định
            Glide.with(context)
                    .load(R.drawable.default_avatar)
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.profile_image);
        }
        // Xử lý sự kiện click
        holder.itemView.setOnClickListener(view -> {
            if (onUserClickListener != null) {
                onUserClickListener.onUserClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView name;
        public TextView sdt;
        public ImageView profile_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.txt_name);
            sdt = itemView.findViewById(R.id.txt_sdt);
            profile_image = itemView.findViewById(R.id.image_profile);
        }
    }

}
