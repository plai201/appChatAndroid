package com.example.appchatandroid.utilities;

import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;


public class ImageUpload {
    private static final String Tag  = "ImageUpload";

    public void uploadImage(Uri imageUri){
        // Tạo một tham chiếu tới nơi lưu trữ trên Cloud Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("images/" +System.currentTimeMillis() +".jpg");
        // Upload hình ảnh lên Cloud Storage
        storageReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Lấy đường dẫn của hình ảnh sau khi tải lên thành công
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUrl = uri.toString();
                                Log.d(Tag, "Uploaded image with URL: " + imageUrl);

                                // Lưu đường dẫn của hình ảnh vào Cloud Firestore
                                saveImageUrlToFirestore(imageUrl);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(Tag, "Error uploading image: " + e.getMessage());
                    }
                });
    }
    private void saveImageUrlToFirestore(String imageUrl) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> image = new HashMap<>();
        image.put("imageUrl", imageUrl);

        db.collection("images")
                .add(image)
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        Log.d(Tag, "Image URL saved to Firestore");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(Tag, "Error saving image URL to Firestore: " + e.getMessage());
                    }
                });
    }
}
