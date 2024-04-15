package com.example.appchatandroidt.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.appchatandroidt.R;
import com.example.appchatandroidt.models.Friends;
import com.example.appchatandroidt.models.User;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserDetailActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private String userId;
    DatabaseReference mUserRef,mRef;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseRecyclerOptions<Friends>options;
    FirebaseRecyclerAdapter<Friends, FriendViewHolder>adapter;
    private RecyclerView recyclerView;

    private EditText textName, textEmail, textPhone,txtPass;
    private Spinner spinner;
    private ImageView imageUser;
    private ImageButton btnEdit, btnDelete;
    private final static int PICK_IMAGE = 1;
    private Uri imageUri;
    private Bitmap selectedBitmap;
    private String avatarUrl;
    private String[] roles = {"Người dùng", "Admin"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        userId = getIntent().getStringExtra("userIdD");

        textName = findViewById(R.id.textName2);
        textEmail = findViewById(R.id.textEmail2);
        textPhone = findViewById(R.id.textPhone2);
        spinner = findViewById(R.id.textRole2);
        txtPass = findViewById(R.id.txtPassword2);
        imageUser = findViewById(R.id.imageUser);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
        recyclerView = findViewById(R.id.friend_recycler_view_detail);



        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference().child("friends");

        // Khởi tạo Firebase
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mUserRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        getUserDetails();
        loadFriends("");
        imageUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUser(userId);
            }
        });


    }
    private void loadRole(){
        spinner.setOnItemSelectedListener(this);

        ArrayAdapter ad
                = new ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                roles);

        // set simple layout resource file
        // for each item of spinner
        ad.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item);

        // Set the ArrayAdapter (ad) data on the
        // Spinner which binds data to spinner
        spinner.setAdapter(ad);
    }

    private int getIndexForRole(int userRole) {
        // Vai trò 0 và 1 tương ứng với vị trí trong mảng roles
        if (userRole == 0) {
            return 0; // Người dùng
        } else if (userRole == 1) {
            return 1; // Admin
        }
        return -1;
    }



    private void updateUser(String userId){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        if (selectedBitmap != null) {
            uploadImageToFirebaseStorage(storageRef, userId);
        }
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("name",textName.getText().toString());
        hashMap.put("email",textEmail.getText().toString());
        hashMap.put("sdt",textPhone.getText().toString());
        mUserRef.updateChildren(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Cập nhật thành công
                            Toast.makeText(getApplicationContext(), "Thông tin người dùng đã được cập nhật", Toast.LENGTH_SHORT).show();
                        } else {
                            // Xử lý khi cập nhật không thành công
                            Toast.makeText(getApplicationContext(), "Có lỗi xảy ra. Vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void loadFriends(String s){
        Query query = mRef.child(userId).orderByChild("name").startAt(s).endAt(s+"\uf8ff");
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
//                        Intent intent = new Intent(UserDetailActivity.this, UserDetailActivity.class);
//                        intent.putExtra("userId",getRef(position).getKey().toString());
//                        startActivity(intent);
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
     }
     private void getUserDetails() {
        mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        // Hiển thị thông tin người dùng
                        textName.setText(user.getName());
                        textEmail.setText(user.getEmail());
                        textPhone.setText(user.getSdt());
//                        if ( user.getRole() == 0){
//                            textRole.setText("Người dùng");
//                        }else {
//                            textRole.setText("Admin");
//
//                        }

                        // Hiển thị hình ảnh người dùng (sử dụng Picasso hoặc Glide)
                        if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
                            Glide.with(getApplicationContext())
                                    .load(user.getAvatarUrl())
                                    .into(imageUser);                        } else {
                            // Nếu không có hình ảnh, sử dụng hình ảnh mặc định
                            imageUser.setImageResource(R.drawable.default_avatar);
                        }
                    } else {
                        Toast.makeText(UserDetailActivity.this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UserDetailActivity.this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserDetailActivity.this, "Đã xảy ra lỗi: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void uploadImageToFirebaseStorage(StorageReference storageRef, String userId) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageData = baos.toByteArray();


        StorageReference imageRef = storageRef.child("avatars/" + userId + ".jpg");
        UploadTask uploadTask = imageRef.putBytes(imageData);
        uploadTask.addOnFailureListener(e -> {
            Log.e("UPLOAD_IMAGE_ERROR", "Error uploading image: " + e.getMessage());
            Toast.makeText(UserDetailActivity.this, "Lỗi khi tải ảnh lên. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
        }).addOnSuccessListener(taskSnapshot -> {
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                avatarUrl = uri.toString();
                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("name",textName.getText().toString());
                hashMap.put("email",textEmail.getText().toString());
                hashMap.put("sdt",textPhone.getText().toString());
                hashMap.put("avatarUrl", avatarUrl);
                mUserRef.updateChildren(hashMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // Cập nhật thành công
                                    Toast.makeText(getApplicationContext(), "Thông tin người dùng đã được cập nhật", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Xử lý khi cập nhật không thành công
                                    Toast.makeText(getApplicationContext(), "Có lỗi xảy ra. Vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            });
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                selectedBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imageUser.setImageBitmap(selectedBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getApplicationContext(),
                        roles[position],
                        Toast.LENGTH_LONG)
                .show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}