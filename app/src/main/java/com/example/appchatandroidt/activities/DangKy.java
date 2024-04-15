package com.example.appchatandroidt.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.example.appchatandroidt.R;
import com.example.appchatandroidt.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class DangKy extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private AppCompatImageView imageViewAvatar;
    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextSdt;
    private EditText editTextPass;
    private EditText editTextPassCf;
    private MaterialButton buttonDangKy;
    private ProgressBar progressBar;
    private TextView textViewDangNhap;
    private final  static int role =0;

    private Uri imageUri;
    private Bitmap selectedBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_ky);

        imageViewAvatar = findViewById(R.id.imageProfile);
        editTextName = findViewById(R.id.inputName);
        editTextEmail = findViewById(R.id.inputEmail);
        editTextSdt = findViewById(R.id.inputSdt);
        editTextPass = findViewById(R.id.inputPass);
        editTextPassCf = findViewById(R.id.inputPassCf);
        buttonDangKy = findViewById(R.id.btnDangKy);
        progressBar = findViewById(R.id.progressBar);
        textViewDangNhap = findViewById(R.id.txtDangNhap);

        imageViewAvatar.setOnClickListener(v -> chooseImage());

        buttonDangKy.setOnClickListener(v -> dangKy());
        textViewDangNhap.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn ảnh đại diện"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                selectedBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imageViewAvatar.setImageBitmap(selectedBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void dangKy() {
        if (!isValidSignUpDetails()) {
            return;
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String sdt = editTextSdt.getText().toString().trim();
        String password = editTextPass.getText().toString().trim();

        progressBar.setVisibility(View.VISIBLE);
        buttonDangKy.setEnabled(false);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();

                            // Upload ảnh đại diện lên Firebase Storage
                            if (selectedBitmap != null) {
                                uploadImageToFirebaseStorage(storageRef, userId);
                            } else {
                                saveUserToDatabase(userId,name, email,sdt, "");
                            }
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        buttonDangKy.setEnabled(true);
                        Log.e("Đăng ký thất bại", task.getException().getMessage());
                        Toast.makeText(DangKy.this, "Đăng ký thất bại. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
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
            progressBar.setVisibility(View.GONE);
            buttonDangKy.setEnabled(true);
            Log.e("UPLOAD_IMAGE_ERROR", "Error uploading image: " + e.getMessage());
            Toast.makeText(DangKy.this, "Lỗi khi tải ảnh lên. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
        }).addOnSuccessListener(taskSnapshot -> {
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String avatarUrl = uri.toString();
                saveUserToDatabase(userId,editTextName.getText().toString().trim(), editTextEmail.getText().toString().trim(),editTextSdt.getText().toString().trim(), avatarUrl);
            });
        });
    }

    private void saveUserToDatabase(String userId,String name, String email,String sdt, String avatarUrl) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        User newUser = new User(userId,avatarUrl,name, email,sdt,role);
        usersRef.child(userId).setValue(newUser)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    buttonDangKy.setEnabled(true);
                    if (task.isSuccessful()) {
                        Toast.makeText(DangKy.this, "Đăng ký thành công.", Toast.LENGTH_SHORT).show();
                        finish(); // Kết thúc activity sau khi đăng ký thành công
                    } else {
                        Toast.makeText(DangKy.this, "Lỗi khi lưu thông tin người dùng.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private Boolean isValidSignUpDetails() {
        if (editTextName.getText().toString().trim().isEmpty()) {
            showToast("Nhập tên");
            return false;
        } else if (editTextEmail.getText().toString().trim().isEmpty()) {
            showToast("Nhập email");
            return false;
        }else if (editTextSdt.getText().toString().trim().isEmpty()) {
            showToast("Nhập số điện thoại");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(editTextEmail.getText().toString()).matches()) {
            showToast("Email không hợp lệ");
            return false;
        } else if (editTextPass.getText().toString().trim().isEmpty()) {
            showToast("Nhập mật khẩu");
            return false;
        } else if (editTextPassCf.getText().toString().trim().isEmpty()) {
            showToast("Nhập lại mật khẩu");
            return false;
        } else if (!editTextPass.getText().toString().equals(editTextPassCf.getText().toString())) {
            showToast("Mật khẩu phải giống nhau");
            return false;
        } else {
            return true;
        }
    }
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
