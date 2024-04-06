package com.example.appchatandroidt.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

public class UserActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private UsersAdapter usersAdapter;
    private ProgressBar progressBar;
    private List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        recyclerView = findViewById(R.id.user_recycler_view);
        progressBar = findViewById(R.id.progressBar);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        userList = new ArrayList<>();

        readUsers();
        // Xử lý sự kiện click


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
                            Intent intent = new Intent(getApplicationContext(), ViewFriendActivity.class);
                            intent.putExtra("userId", userId);
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
}
