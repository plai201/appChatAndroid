package com.example.appchatandroidt.models;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.appchatandroidt.R;
import com.example.appchatandroidt.activities.ChatActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        String title = message.getNotification().getTitle();
        String body = message.getNotification().getBody();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "CHAT");
        builder.setContentTitle(title);
        builder.setContentText(body);
        builder.setSmallIcon(R.drawable.ic_notifications);

        Intent intent = null;
        Log.d("MyFirebaseMessagingService", "User ID: " + message.getData().get("userId"));

        if (message.getData().get("type").equalsIgnoreCase("sms")) {
            String userId = message.getData().get("userId");

             intent = new Intent(this, ChatActivity.class);
             intent.putExtra("OtherUserId", userId);
        }

        PendingIntent pendingIntent;
        if (intent != null) {
            pendingIntent = PendingIntent.getActivity(this, 101, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            builder.setContentIntent(pendingIntent);
        }

        // Hiển thị thông báo
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(1234, builder.build());
    }
}
