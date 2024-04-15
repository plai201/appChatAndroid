package com.example.appchatandroidt.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchatandroidt.R;
import com.example.appchatandroidt.adapter.UsersAdapter;
import com.example.appchatandroidt.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private RecyclerView recyclerView;
    private UsersAdapter usersAdapter;
    private ProgressBar progressBar;
    private AppCompatImageView imgDangXuat;
    private List<User> userList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);


        mAuth = FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.user_recycler_view);
        progressBar = findViewById(R.id.progressBar);
        imgDangXuat = findViewById(R.id.imgDangXuat);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        userList = new ArrayList<>();
        imgDangXuat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dangXuat();
            }
        });

        readUsers();

    }

    private void readUsers(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    userList.clear();

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        User user = dataSnapshot.getValue(User.class);

                        assert user != null;
                        if (!user.getId().equals(firebaseUser.getUid())){
                            userList.add(user);
                        }
                    }
                    // Khởi tạo adapter và đặt cho RecyclerView
                    usersAdapter = new UsersAdapter(getApplicationContext(), userList);
                    recyclerView.setAdapter(usersAdapter);
                    recyclerView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);

                    usersAdapter.setOnUserClickListener(new UsersAdapter.OnUserClickListener() {
                        @Override
                        public void onUserClick(int position) {
                            // Xử lý sự kiện click ở đây, ví dụ:
                            User clickedUser = userList.get(position);
                            String userId = clickedUser.getId();

                            // Ví dụ: Mở màn hình chi tiết của user khi click vào user
                            Intent intent = new Intent(getApplicationContext(), UserDetailActivity.class);
                            intent.putExtra("userIdD", userId);
                            startActivity(intent);
                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Xử lý khi có lỗi xảy ra
                    Log.e("UserActivity", "Lỗi: " + error.getMessage());
                    // Hiển thị thông báo hoặc xử lý lỗi khác tùy ý
                }
            });
        } else {
            // Đăng nhập không thành công, xử lý theo ý bạn
            Log.e("UserActivity", "Người dùng chưa đăng nhập");
        }

    }
    private void dangXuat() {
        mAuth.signOut();
        Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();

        // Chuyển hướng đến màn hình đăng nhập
        Intent intent = new Intent(AdminActivity.this, DangNhap.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}