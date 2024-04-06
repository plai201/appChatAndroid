package com.example.appchatandroidt.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.appchatandroidt.R;
import com.example.appchatandroidt.models.Message;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {
    ProgressBar progressBar;
    RecyclerView recyclerView;
    EditText inputSms;
    AppCompatImageView btnSend;
    AppCompatImageView btnSendImage;
    RoundedImageView imageProFile;
    TextView textViewName;
    String otherUserId;
    DatabaseReference mUserRef, smsRef;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String otherUserName, otherUserProFileUrl, OtherUserStatus;

    FirebaseRecyclerOptions<Message> options;
    FirebaseRecyclerAdapter<Message, ChatViewHolder> adapter;
    Uri uri;
    private final static int PICK_IMAGE =1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        progressBar = findViewById(R.id.progressBarChat);
        otherUserId = getIntent().getStringExtra("OtherUserId");

        recyclerView = findViewById(R.id.chatRecyclerView);
        inputSms = findViewById(R.id.inputMessage);
        btnSend = findViewById(R.id.iconSendMess);
        btnSendImage = findViewById(R.id.iconSendImage);
        imageProFile = findViewById(R.id.imageProfileChat);
        textViewName = findViewById(R.id.txtNameChat);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mUserRef = FirebaseDatabase.getInstance().getReference().child("users");
        smsRef = FirebaseDatabase.getInstance().getReference().child("message");

        loadUsers();
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessages();
            }
        });

        loadMessages();

        btnSendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();
            String url = uri.toString();
            Intent intent = new Intent(ChatActivity.this,SendImageActivity.class);
            intent.putExtra("u",url);
            intent.putExtra("n",otherUserName);
            intent.putExtra("ruid",otherUserId);
            intent.putExtra("suid",mUser.getUid());
            startActivity(intent);
        }else {
            Toast.makeText(this,"Không có file được chọn",Toast.LENGTH_SHORT).show();
        }
    }

    private void loadMessages() {
        options = new FirebaseRecyclerOptions.Builder<Message>()
                .setQuery(smsRef.child(mUser.getUid()).child(otherUserId), Message.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Message, ChatViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ChatViewHolder chatViewHolder, int i, @NonNull Message message) {
                if (message.isSentByCurrentUser()) {
                    if (message.isImageMessage()) {
                        // Tin nhắn gửi là tin nhắn hình ảnh
                        chatViewHolder.bindSentMessage(message.getSms(), "iv",getReadableDateTime(new Date()));
                    } else {
                        // Tin nhắn gửi là tin nhắn văn bản
                        chatViewHolder.bindSentMessage(message.getSms(), "text",getReadableDateTime(new Date()));
                    }
                } else {
                    if (message.isImageMessage()) {
                        // Tin nhắn nhận là tin nhắn hình ảnh
                        chatViewHolder.bindReceivedMessage(message.getSms(), otherUserProFileUrl, "iv",getReadableDateTime(new Date()));
                    } else {
                        // Tin nhắn nhận là tin nhắn văn bản
                        chatViewHolder.bindReceivedMessage(message.getSms(), otherUserProFileUrl, "text",getReadableDateTime(new Date()));
                    }
                }
            }

            @NonNull
            @Override
            public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view;
                if (viewType == 0) { // gửi tin nhắn
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_sent_message, parent, false);
                } else { // nhận tin nhắn
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_received_message, parent, false);
                }
                return new ChatViewHolder(view);
            }

            @Override
            public int getItemViewType(int position) {
                // Xác định loại tin nhắn (gửi hoặc nhận) tại vị trí
                Message message = getItem(position);
                if (message.isSentByCurrentUser()) {
                    return 0; // Tin nhắn gửi
                } else {
                    return 1; // Tin nhắn nhận
                }
            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private void sendMessages() {
        String sms = inputSms.getText().toString();
        if (sms.isEmpty()) {
            Toast.makeText(this, "Nhập tin nhắn", Toast.LENGTH_SHORT).show();
        } else {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("sms", sms);
            hashMap.put("status", "unseen");
            hashMap.put("userId", mUser.getUid());
            hashMap.put("datetime", getReadableDateTime(new Date()));
            smsRef.child(otherUserId).child(mUser.getUid()).push().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        smsRef.child(mUser.getUid()).child(otherUserId).push().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    inputSms.setText(null);
                                    Toast.makeText(ChatActivity.this, "Đã gửi tin nhắn", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    private void loadUsers() {
        mUserRef.child(otherUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    otherUserName = snapshot.child("name").getValue().toString();
                    otherUserProFileUrl = snapshot.child("avatarUrl").getValue().toString();
                    // OtherUserStatus = snapshot.child("status").getValue().toString(); online offline
                    if (otherUserProFileUrl != null && !otherUserProFileUrl.isEmpty()) {
                        Glide.with(ChatActivity.this)
                                .load(otherUserProFileUrl)
                                .apply(RequestOptions.circleCropTransform())
                                .into(imageProFile);
                    } else {
                        // Nếu không có ảnh đại diện, sử dụng ảnh mặc định
                        Glide.with(ChatActivity.this)
                                .load(R.drawable.default_avatar)
                                .apply(RequestOptions.circleCropTransform())
                                .into(imageProFile);
                    }
                    textViewName.setText(otherUserName);
                    //status.setText(otherStatus);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getReadableDateTime(Date date) {
        return new SimpleDateFormat("MMMM dd,yyyy - hh:mm a", Locale.getDefault()).format(date);
    }
}
