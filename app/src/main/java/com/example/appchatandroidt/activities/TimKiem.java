package com.example.appchatandroidt.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchatandroidt.R;
import com.example.appchatandroidt.adapter.TimKiemUserRecyckerAdapter;
import com.example.appchatandroidt.models.User;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class TimKiem extends AppCompatActivity {
    EditText searchInput;
    ImageButton searchButton;
    ImageButton backButton;
    RecyclerView recyclerView;
    TimKiemUserRecyckerAdapter timKiemUserRecyckerAdapter;
    DatabaseReference usersRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_tim_kiem);
         searchInput = findViewById(R.id.seach_username_input);
         searchButton = findViewById(R.id.search_user_btn);
         backButton = findViewById(R.id.back_btn);
         recyclerView = findViewById(R.id.search_user_recycler_view);
         searchInput.requestFocus();
         
         backButton.setOnClickListener(v -> {
             onBackPressed();
         });
         searchButton.setOnClickListener(v -> {
             String searchTerm = searchInput.getText().toString();
             if(searchTerm.isEmpty() || searchTerm.length() <3){
                 searchInput.setError("Nhập tên người dùng");
                 return;
             }
             setupSearchRecyclerView(searchTerm);
         });
    }
    void setupSearchRecyclerView(String searchTerm){
        Log.e("SearchTermLog", "Search Term: " + searchTerm);

        // Tạo Query để tìm kiếm người dùng theo tên
        Query query = usersRef.child("sdt").startAt(searchTerm).endAt(searchTerm + "\uf8ff");

        // Log kết quả query
        Log.e("QueryLog", "Query: " + query.toString());
        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(query,User.class).build();
        timKiemUserRecyckerAdapter = new TimKiemUserRecyckerAdapter(options,getApplicationContext());

        // Set layout cho RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(timKiemUserRecyckerAdapter);

        // Bắt đầu lắng nghe dữ liệu
        timKiemUserRecyckerAdapter.startListening();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(timKiemUserRecyckerAdapter!=null)
            timKiemUserRecyckerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(timKiemUserRecyckerAdapter!=null)
            timKiemUserRecyckerAdapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(timKiemUserRecyckerAdapter!=null)
            timKiemUserRecyckerAdapter.startListening();
    }
}