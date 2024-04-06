package com.example.appchatandroidt.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.appchatandroidt.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DangNhap extends AppCompatActivity {
    private TextView textViewDangKy;
    private EditText editTextEmail, editTextPassword;
    private MaterialButton buttonDangNhap;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_nhap);

        mAuth = FirebaseAuth.getInstance();


        editTextEmail = findViewById(R.id.inputEmail);
        editTextPassword = findViewById(R.id.inputPass);
        buttonDangNhap = findViewById(R.id.btnDangNhap);
        textViewDangKy = findViewById(R.id.txtDangKy);

        //Kiểm tra xem người dùng đã đăng nhập chưa
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Nếu đã đăng nhập, chuyển hướng đến MainActivity
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish(); // Kết thúc activity hiện tại
        } else {
            buttonDangNhap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dangNhap();
                }
            });
             setListeners();
        }
      }

    void setListeners() {
        textViewDangKy.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), DangKy.class)));
    }
    private void dangNhap() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty()) {
            editTextEmail.setError("Email không được để trống");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Email không hợp lệ");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Mật khẩu không được để trống");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Mật khẩu phải nhiều hơn 6 ký tự");
            editTextPassword.requestFocus();
            return;
        }

        // Đăng nhập người dùng bằng email và password
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(DangNhap.this, "Đăng nhập thành công.", Toast.LENGTH_SHORT).show();
                            // Đăng nhập thành công, chuyển hướng đến màn hình chính
                            Intent intent = new Intent(DangNhap.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        } else {
                            Exception exception = task.getException();
                            if (exception != null) {
                                Log.e("LOGIN_ERROR", "Đăng nhập thất bại: " + exception.getMessage());
                                Toast.makeText(DangNhap.this, "Đăng nhập thất bại: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}