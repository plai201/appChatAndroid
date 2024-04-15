package com.example.appchatandroidt.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appchatandroidt.R;
import com.example.appchatandroidt.activities.ChatActivity;
import com.example.appchatandroidt.activities.ViewFriendActivity;
import com.example.appchatandroidt.models.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.ArrayList;
import java.util.List;

//public class TimKiemUserRecyckerAdapter extends FirebaseRecyclerAdapter<User,TimKiemUserRecyckerAdapter.UserModelViewHolder> {
public class TimKiemUserRecyckerAdapter extends RecyclerView.Adapter<TimKiemUserRecyckerAdapter.UserViewHolder> {

//    private Context context;

    Context context;

    private List<User> userList;

    public TimKiemUserRecyckerAdapter(Context context) {
        this.context = context;
        this.userList = new ArrayList<>();
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.tim_kiem_row, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.txtUserName.setText(user.getName());
        holder.txtUserPhone.setText(user.getSdt());
        if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
            Glide.with(context)
                    .load(user.getAvatarUrl())
                    .into(holder.profilePic);

        } else {
            // Nếu không có ảnh đại diện, sử dụng ảnh mặc định
            holder.profilePic.setImageResource(R.drawable.default_avatar);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewFriendActivity.class);
                intent.putExtra("userId", user.getId());
                // Thêm các dữ liệu khác cần thiết vào intent
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView txtUserName, txtUserPhone;
        ImageView profilePic;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUserName = itemView.findViewById(R.id.user_name_text);
            txtUserPhone = itemView.findViewById(R.id.phone_text);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
        }
    }

    //    class UserModelViewHolder extends RecyclerView.ViewHolder{
//        TextView usernameText;
//        TextView phoneText;
//        ImageView profilePic;
//        public UserModelViewHolder(@NonNull View itemView){
//            super(itemView);
//            usernameText = itemView.findViewById(R.id.user_name_text);
//            phoneText = itemView.findViewById(R.id.phone_text);
//            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
//        }
//
//    }

//    public TimKiemUserRecyckerAdapter(@NonNull FirebaseRecyclerOptions<User> options,Context context) {
//        super(options);
//        this.context = context;
//    }
//
//    @Override
//    protected void onBindViewHolder(@NonNull UserModelViewHolder userModelViewHolder, int i, @NonNull User user) {
//        userModelViewHolder.usernameText.setText(user.getName());
//        userModelViewHolder.phoneText.setText(user.getSdt());
//    }
//
//    @NonNull
//    @Override
//    public UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.tim_kiem_row,parent,false);
//        return new UserModelViewHolder(view);
//
//    }
//
//    class UserModelViewHolder extends RecyclerView.ViewHolder{
//        TextView usernameText;
//        TextView phoneText;
//        ImageView profilePic;
//        public UserModelViewHolder(@NonNull View itemView){
//            super(itemView);
//            usernameText = itemView.findViewById(R.id.user_name_text);
//            phoneText = itemView.findViewById(R.id.phone_text);
//            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
//        }
//
//    }
}
