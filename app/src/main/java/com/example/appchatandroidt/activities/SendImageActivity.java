package com.example.appchatandroidt.activities;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.appchatandroidt.R;
import com.example.appchatandroidt.models.Message;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SendImageActivity extends AppCompatActivity {
    String url, receiver_name, sender_uid, receiver_uid;

    ImageView imageView;
    TextView textView;
    Uri imageurl;
    ProgressBar progressBar;
    Button button;
    UploadTask uploadTask;
    StorageReference storageReference;
    FirebaseStorage firebaseStorage;
    DatabaseReference rootRef1, rootRef2;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private Uri uri;
    Message message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_image);

        message = new Message();
        storageReference = FirebaseStorage.getInstance().getReference("message images");
        imageView = findViewById(R.id.iv_sendImage);
        button = findViewById(R.id.btn_send_image);
        progressBar = findViewById(R.id.pb_sendImage);
        textView = findViewById(R.id.txt_dont);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            url = bundle.getString("u");
            receiver_name = bundle.getString("n");
            receiver_uid = bundle.getString("ruid");
            sender_uid = bundle.getString("suid");
        } else {
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
        }
        Glide.with(this)
                .load(url)
                .into(imageView);
        imageurl = Uri.parse(url);
        rootRef1 = firebaseDatabase.getReference("message").child(sender_uid).child(receiver_uid);
        rootRef2 = firebaseDatabase.getReference("message").child(receiver_uid).child(sender_uid);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendImage();
            }
        });
    }

    private String getFileExt(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void sendImage() {
        if (imageurl != null) {
            progressBar.setVisibility(View.VISIBLE);
            final StorageReference reference = storageReference.child(System.currentTimeMillis() + "*" + getFileExt(imageurl));
            uploadTask = reference.putFile(imageurl);

            uploadTask.addOnFailureListener(e -> {
                progressBar.setVisibility(View.GONE);
                button.setEnabled(true);
                Toast.makeText(SendImageActivity.this, "Lỗi khi tải ảnh lên. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
            }).addOnSuccessListener(taskSnapshot -> {
                reference.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    // Tạo đối tượng Message và lưu vào database
                    Message message = new Message(imageUrl, "unsent", receiver_uid,getReadableDateTime(new Date()) , true);
                    rootRef1.push().setValue(message);
                    rootRef2.push().setValue(message);

                    progressBar.setVisibility(View.GONE);
                    button.setEnabled(true);
                    Toast.makeText(SendImageActivity.this, "Đã gửi ảnh thành công", Toast.LENGTH_SHORT).show();
                    finish(); // Đóng activity sau khi gửi ảnh thành công
                });
            });
        } else {
            Toast.makeText(this, "Chọn ảnh trước khi gửi", Toast.LENGTH_SHORT).show();
        }
    }
    private String getReadableDateTime(Date date) {
        return new SimpleDateFormat("MMMM dd,yyyy - hh:mm a", Locale.getDefault()).format(date);
    }
}
