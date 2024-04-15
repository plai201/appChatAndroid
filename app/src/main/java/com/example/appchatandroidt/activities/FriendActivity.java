package com.example.appchatandroidt.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.appchatandroidt.R;
import com.example.appchatandroidt.models.Friends;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class FriendActivity extends BaseActivity {
    FirebaseRecyclerOptions<Friends>options;
    FirebaseRecyclerAdapter<Friends, FriendViewHolder>adapter;
    private RecyclerView recyclerView;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private FirebaseUser mUser;
    private ProgressBar progressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_friend);
         progressBar = findViewById(R.id.progressBar);
         recyclerView = findViewById(R.id.friend_recycler_view);

         recyclerView.setLayoutManager(new LinearLayoutManager(this));
         mAuth = FirebaseAuth.getInstance();
         mUser = mAuth.getCurrentUser();
         mRef = FirebaseDatabase.getInstance().getReference().child("friends");


         loadFriends("");

    }
    private void loadFriends(String s){
        Query query = mRef.child(mUser.getUid()).orderByChild("name").startAt(s).endAt(s+"\uf8ff");
        options = new FirebaseRecyclerOptions.Builder<Friends>().setQuery(query, Friends.class).build();

        adapter= new FirebaseRecyclerAdapter<Friends, FriendViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FriendViewHolder friendViewHolder, int i, @NonNull Friends friends) {
                if(friends.getAvatarUrl()!= null && !friends.getAvatarUrl().isEmpty()){
                    Glide.with(friendViewHolder.itemView.getContext())
                            .load(friends.getAvatarUrl())
                            .apply(RequestOptions.circleCropTransform())
                            .into(friendViewHolder.proFileImage);
                } else {
                    // Nếu không có ảnh đại diện, sử dụng ảnh mặc định
                    Glide.with(friendViewHolder.itemView.getContext())
                            .load(R.drawable.default_avatar)
                            .apply(RequestOptions.circleCropTransform())
                            .into(friendViewHolder.proFileImage);
                }
                friendViewHolder.name.setText(friends.getName());
                friendViewHolder.sdt.setText(friends.getSdt());

                final int position = i;
                friendViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(FriendActivity.this,ChatActivity.class);
                        intent.putExtra("OtherUserId",getRef(position).getKey().toString());
                        startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_friend,parent,false);
                return new  FriendViewHolder(view);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }
}