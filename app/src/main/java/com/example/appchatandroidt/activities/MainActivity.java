package com.example.appchatandroidt.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.appchatandroidt.R;
import com.example.appchatandroidt.adapter.RecentConversationsAdapter;
import com.example.appchatandroidt.models.Conversions;
import com.example.appchatandroidt.models.Friends;
import com.example.appchatandroidt.models.Message;
import com.example.appchatandroidt.utilities.Constants;
import com.example.appchatandroidt.utilities.PreferenceManager;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.makeramen.roundedimageview.RoundedImageView;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MainActivity extends BaseActivity {
    FirebaseRecyclerOptions<Message> options;
    FirebaseRecyclerAdapter<Message, MessageRecentViewHolder> adapter;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    private TextView textViewName, textViewEmail;
    private FirebaseAuth mAuth;
    private AppCompatImageView imgDangXuat;
    private RoundedImageView imageViewAvatar;
    private AppCompatImageView buttonTimKiemView;
    private AppCompatImageView buttonListUser;
    private AppCompatImageView buttonListFriend;

    private DatabaseReference usersRef,messageRef;
    private String currentUserId;
    private PreferenceManager preferenceManager;
    private List<Conversions> conversations;
    private RecentConversationsAdapter conversationsAdapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        currentUserId = user.getUid();
        usersRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
        messageRef = FirebaseDatabase.getInstance().getReference().child("message");


        recyclerView = findViewById(R.id.conversationsRecyclerView);
        progressBar = findViewById(R.id.progressBarMain);
        textViewName = findViewById(R.id.txtName);
        imageViewAvatar = findViewById(R.id.imageProfile);
        imgDangXuat = findViewById(R.id.imgDangXuat);
        buttonTimKiemView = findViewById(R.id.ic_tim_kiem);
        buttonListUser = findViewById(R.id.ic_list_friend_find);
        buttonListFriend = findViewById(R.id.ic_list_friend);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseMessaging.getInstance().subscribeToTopic(user.getUid());
        init();


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
        //loadRecentConversations();
        listenConversations();

    }
    private void init(){
        conversations = new ArrayList<>();
        conversationsAdapter = new RecentConversationsAdapter(conversations,this);
        recyclerView.setAdapter(conversationsAdapter);
    }
    private void loadRecentConversations() {
        options = new FirebaseRecyclerOptions.Builder<Message>()
                .setQuery(messageRef, Message.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Message, MessageRecentViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MessageRecentViewHolder messageRecentViewHolder, int i, @NonNull Message message) {
                String otherUserId = getRef(i).getKey();
                DatabaseReference conversionsRef = FirebaseDatabase.getInstance().getReference()
                        .child("conversions")
                        .child(currentUserId + otherUserId);

                conversionsRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            String name = dataSnapshot.child("receiverName").getValue(String.class);
                            String avatarUrl = dataSnapshot.child("receiverImage").getValue(String.class);
                            String lastMessage = dataSnapshot.child("lastMessage").getValue(String.class);

                            // Hiển thị tên người gửi
                            messageRecentViewHolder.textViewName.setText(name);
                            messageRecentViewHolder.textViewName.setVisibility(View.VISIBLE);

                            messageRecentViewHolder.lastMessage.setText(lastMessage);
                            messageRecentViewHolder.lastMessage.setVisibility(View.VISIBLE);

                            messageRecentViewHolder.imageProfile.setVisibility(View.VISIBLE);

                            // Load hình ảnh từ URL và hiển thị trong ImageView
                            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                                Glide.with(messageRecentViewHolder.itemView.getContext())
                                        .load(avatarUrl)
                                        .into(messageRecentViewHolder.imageProfile);
                            } else {
                                // Nếu không có hình ảnh, sử dụng hình ảnh mặc định
                                messageRecentViewHolder.imageProfile.setImageResource(R.drawable.default_avatar);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("LoadUser", "Lỗi: " + databaseError.getMessage());
                    }
                });
                final int position = i;
                messageRecentViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this,ChatActivity.class);
                        intent.putExtra("OtherUserId",getRef(position).getKey().toString());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public MessageRecentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_recent_conversion, parent, false);
                return new MessageRecentViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

        adapter.startListening();
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

                    if (name != null) {
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
    private void listenConversations() {
        DatabaseReference conversationsRef = FirebaseDatabase.getInstance()
                .getReference("conversions");

        Query senderConversationsQuery = conversationsRef
                .orderByChild("senderId")
                .equalTo(currentUserId);
        Log.d("senderChatRef", "senderChatRef"+senderConversationsQuery );


        Query receiverConversationsQuery = conversationsRef
                .orderByChild("receiverId")
                .equalTo(currentUserId);

        senderConversationsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    String senderId = snapshot1.child("senderId").getValue(String.class);
                    String receiverId = snapshot1.child("receiverId").getValue(String.class);
                    Conversions conversion = new Conversions();
                    conversion.senderId  =senderId;
                    conversion.receiverId = receiverId;
                    conversion.conversionImage = snapshot1.child("receiverImage").getValue(String.class);
                    conversion.conversionName = snapshot1.child("receiverName").getValue(String.class);
                    conversion.conversionId = snapshot1.child("receiverId").getValue(String.class);
                    conversion.message = snapshot1.child("lastMessage").getValue(String.class);
                    conversion.dataObject = snapshot1.child("datetime").getValue(Date.class);
                    conversations.add(conversion);
                }
                conversationsAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(0);
                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this,"Khong thanh cong",Toast.LENGTH_SHORT).show();
            }
        });
      receiverConversationsQuery.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot snapshot) {

              for (DataSnapshot snapshot1 : snapshot.getChildren()){
                  String senderId = snapshot1.child("senderId").getValue(String.class);
                  String receiverId = snapshot1.child("receiverId").getValue(String.class);
                  Conversions conversion = new Conversions();
                  conversion.senderId  =senderId;
                  conversion.receiverId = receiverId;
                  conversion.conversionImage = snapshot1.child("senderImage").getValue(String.class);
                  conversion.conversionName = snapshot1.child("senderName").getValue(String.class);
                  conversion.conversionId = snapshot1.child("senderId").getValue(String.class);
                  conversion.message = snapshot1.child("lastMessage").getValue(String.class);
                  conversion.dataObject = snapshot1.child("datetime").getValue(Date.class);
                  conversations.add(conversion);
              }
              conversationsAdapter.notifyDataSetChanged();
              recyclerView.smoothScrollToPosition(0);
              recyclerView.setVisibility(View.VISIBLE);
              progressBar.setVisibility(View.GONE);
          }

          @Override
          public void onCancelled(@NonNull DatabaseError error) {

          }
      });
    }

    private final ValueEventListener conversationsEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            conversations.clear(); // Xóa dữ liệu cũ trước khi thêm dữ liệu mới

            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                String senderId = snapshot.child(Constants.KEY_SENDER_ID).getValue(String.class);
                String receiverId = snapshot.child(Constants.KEY_RECEIVER_ID).getValue(String.class);
                String sms = snapshot.child("lastMessage").getValue(String.class);

                Conversions chatMessage = new Conversions();
                chatMessage.senderId = senderId;
                chatMessage.receiverId = receiverId;


                if (currentUserId.equals(senderId)) {
                    chatMessage.conversionImage = snapshot.child(Constants.KEY_RECEIVER_IMAGE).getValue(String.class);
                    chatMessage.conversionName = snapshot.child(Constants.KEY_RECEIVER_NAME).getValue(String.class);
                    chatMessage.conversionId = snapshot.child(Constants.KEY_RECEIVER_ID).getValue(String.class);

                } else {
                    chatMessage.conversionImage = snapshot.child(Constants.KEY_SENDER_IMAGE).getValue(String.class);
                    chatMessage.conversionName = snapshot.child(Constants.KEY_SENDER_NAME).getValue(String.class);
                    chatMessage.conversionId = snapshot.child(Constants.KEY_SENDER_ID).getValue(String.class);
                }

                chatMessage.message = sms;
                chatMessage.dataObject = snapshot.child("datetime").getValue(Date.class);;

                conversations.add(chatMessage);
            }


            Collections.sort(conversations, (o1, o2) -> o2.dataObject.compareTo(o1.dataObject));
            conversationsAdapter.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(0);
            recyclerView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            // Xử lý khi có lỗi xảy ra
        }
    };

    @Override
    protected void onStart() {
        super.onStart();

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Integer role = dataSnapshot.child("role").getValue(Integer.class);
                    // Kiểm tra và chuyển hướng dựa vào vai trò của người dùng
                    if (role != null) {
                        if (role == 1) {
                            Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    }
                } else {
//                    Toast.makeText(MainActivity.this, "Không tìm thấy tài khoản ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(DangNhap.this, "Đăng nhập thất bại: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
