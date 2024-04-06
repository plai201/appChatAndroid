package com.example.appchatandroidt.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.appchatandroidt.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

public class MainActivity extends AppCompatActivity {
    private TextView textViewName, textViewEmail;
    private FirebaseAuth mAuth;
    private AppCompatImageView imgDangXuat;
    private RoundedImageView imageViewAvatar;
    private FloatingActionButton buttonTimKiemView;
    private FloatingActionButton buttonListUser;
    private FloatingActionButton buttonListFriend;

    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        usersRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());

        textViewName = findViewById(R.id.txtName);
        imageViewAvatar = findViewById(R.id.imageProfile);
        imgDangXuat = findViewById(R.id.imgDangXuat);
        buttonTimKiemView = findViewById(R.id.ic_tim_kiem);
        buttonListUser = findViewById(R.id.ic_list_user);
        buttonListFriend = findViewById(R.id.ic_list_friend);


        buttonTimKiemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TimKiem.class);
                startActivity(intent);
            }
        });
        buttonListUser.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), UserActivity.class)));

        buttonListFriend.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), FriendActivity.class)));

        imgDangXuat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dangXuat();
            }
        });

        loadUserProfileFromDatabase(usersRef);
    }

    private void dangXuat() {
        mAuth.signOut();
        Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();

        // Chuyển hướng đến màn hình đăng nhập
        Intent intent = new Intent(MainActivity.this, DangNhap.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void loadUserProfileFromDatabase(DatabaseReference usersRef) {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String avatarUrl = dataSnapshot.child("avatarUrl").getValue(String.class);


                    // Kiểm tra dữ liệu trước khi hiển thị
                    if (name != null) {
                        // Hiển thị thông tin người dùng trên giao diện
                        textViewName.setText(name);if (avatarUrl != null && !avatarUrl.isEmpty()) {
                            Glide.with(MainActivity.this)
                                    .load(avatarUrl)
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(imageViewAvatar);
                        } else {
                            // Nếu không có ảnh đại diện, sử dụng ảnh mặc định
                            Glide.with(MainActivity.this)
                                    .load(R.drawable.default_avatar)
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(imageViewAvatar);
                        }
                     } else {
                        // Xử lý khi dữ liệu trả về null
                        Log.e("LoadProfile", "Dữ liệu người dùng null");
                    }
                } else {
                    // Xử lý khi không có dữ liệu
                    Log.e("LoadProfile", "Không tìm thấy dữ liệu người dùng");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý khi có lỗi xảy ra
                Log.e("LoadProfile", "Lỗi: " + databaseError.getMessage());
                Toast.makeText(MainActivity.this, "Lỗi: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
