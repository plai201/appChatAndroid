package com.example.appchatandroidt.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.appchatandroidt.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ViewFriendActivity extends AppCompatActivity {
    private MaterialButton btnThem;
    private MaterialButton btnHuy;
    private ImageView img_profile;
    private TextView name;
    private DatabaseReference mUserRef, requestRef, friendRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private  String userId;
    private String CurrentStatus = "nothing_happen";
    private String img_profile_url,name_,sdt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_friend);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        btnThem = findViewById(R.id.btn_them);
        btnHuy = findViewById(R.id.btn_huy);

        img_profile = findViewById(R.id.profile_image_view);
        name = findViewById(R.id.txt_name_f);

        userId = getIntent().getStringExtra("userId");
        mUserRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        requestRef = FirebaseDatabase.getInstance().getReference().child("requests");
        friendRef = FirebaseDatabase.getInstance().getReference().child("friends");

        loadUser();

        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preformAction(userId);
            }
        });
        checkUserExisttance(userId);

        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unFriend(userId);
            }
        });
    }
    private void unFriend(String userId){
        if (CurrentStatus.equals("friend")){
            friendRef.child(mUser.getUid()).child(userId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        friendRef.child(userId).child(mUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(ViewFriendActivity.this, "Bạn đã huỷ kết bạn", Toast.LENGTH_SHORT).show();
                                    CurrentStatus= "nothing_happen";
                                    btnThem.setText("Thêm bạn bè");
                                    btnHuy.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                }
            });
        }
        if (CurrentStatus.equals("he_sent_pending")){
            HashMap hashMap = new HashMap();
            hashMap.put("status","decline");
            requestRef.child(userId).child(mUser.getUid()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        Toast.makeText(ViewFriendActivity.this, "Bạn đã từ chối kết bạn", Toast.LENGTH_SHORT).show();
                        CurrentStatus="he_sent_pending";
                        btnThem.setVisibility(View.GONE);
                        btnHuy.setVisibility(View.GONE);
                    }
                }
            });
        }
    }
    private void checkUserExisttance(String userId){
        friendRef.child(mUser.getUid()).child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    CurrentStatus="friend";
                    btnThem.setText("Gửi tin nhắn");
                    btnHuy.setText("Huỷ kết bạn");
                    btnHuy.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        friendRef.child(userId).child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    CurrentStatus="friend";
                    btnThem.setText("Gửi tin nhắn");
                    btnHuy.setText("Huỷ kết bạn");
                    btnHuy.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        requestRef.child(mUser.getUid()).child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    if(snapshot.child("status").getValue().toString().equals("pending")){
                        CurrentStatus="I_sent_pending";
                        btnThem.setText("Huỷ yêu cầu");
                         btnHuy.setVisibility(View.GONE);
                    }
                    if(snapshot.child("status").getValue().toString().equals("decline")){
                        CurrentStatus="I_sent_decline";
                        btnThem.setText("Huỷ yêu cầu");
                        btnHuy.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        requestRef.child(userId).child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    if(snapshot.child("status").getValue().toString().equals("pending")){
                        CurrentStatus="he_sent_pending";
                        btnThem.setText("Chấp nhận kết bạn");
                        btnHuy.setText("Từ chối kết bạn");
                        btnHuy.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if (CurrentStatus.equals("nothing_happen")){
            CurrentStatus="nothing_happen";
            btnThem.setText("Thêm bạn bè");
            btnHuy.setVisibility(View.GONE);
        }
    }

    private void preformAction(String userId) {
        if (CurrentStatus.equals("nothing_happen")) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("status", "pending");
            requestRef.child(mUser.getUid()).child(userId).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ViewFriendActivity.this, "Bạn đã gửi yêu cầu kết bạn", Toast.LENGTH_SHORT).show();
                        btnHuy.setVisibility(View.GONE);
                        CurrentStatus = "I_sent_pending";
                        btnThem.setText("Huỷ yêu cầu");
                    } else {
                        Toast.makeText(ViewFriendActivity.this, "" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else if (CurrentStatus.equals("I_sent_pending") || CurrentStatus.equals("I_sent_decline")) {
            requestRef.child(mUser.getUid()).child(userId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ViewFriendActivity.this, "Bạn đã huỷ yêu cầu kết bạn", Toast.LENGTH_SHORT).show();
                        CurrentStatus = "nothing_happen";
                        btnThem.setText("Thêm bạn bè");
                        btnHuy.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(ViewFriendActivity.this, "" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else if (CurrentStatus.equals("he_sent_pending")) {
            requestRef.child(userId).child(mUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("status", "friend");
                        hashMap.put("name", name_);
                        hashMap.put("avatarUrl",img_profile_url);
                        hashMap.put("sdt",sdt);
                        friendRef.child(mUser.getUid()).child(userId).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(ViewFriendActivity.this, "Đã thêm bạn bè", Toast.LENGTH_SHORT).show();
                                CurrentStatus = "friend";
                                btnThem.setText("Gửi tin nhắn");
                                btnHuy.setText("Huỷ kết bạn");
                                btnHuy.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }
            });
        }
       if (CurrentStatus.equals("friend")) {
            // Xử lý khi đã là bạn bè
           Intent intent = new Intent(ViewFriendActivity.this,ChatActivity.class);
           intent.putExtra("OtherUserId",userId);
           startActivity(intent);
        }
    }

    void loadUser() {
        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    img_profile_url = snapshot.child("avatarUrl").getValue().toString();
                    name_ = snapshot.child("name").getValue().toString();
                    sdt = snapshot.child("sdt").getValue().toString();

                    if (img_profile_url != null && !img_profile_url.isEmpty()) {
                        Glide.with(getApplicationContext())
                                .load(img_profile_url)
                                .apply(RequestOptions.circleCropTransform())
                                .into(img_profile);
                    } else {
                        Glide.with(getApplicationContext())
                                .load(R.drawable.default_avatar)
                                .apply(RequestOptions.circleCropTransform())
                                .into(img_profile);
                    }
                    name.setText(name_);
                } else {
                    Toast.makeText(ViewFriendActivity.this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewFriendActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
