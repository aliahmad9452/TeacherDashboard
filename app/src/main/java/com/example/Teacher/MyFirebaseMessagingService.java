//package com.example.admin;
//
//import androidx.annotation.NonNull;
//import androidx.core.app.NotificationCompat;
//import androidx.core.app.NotificationManagerCompat;
//
//import com.example.easychat.R;
//import com.google.firebase.messaging.FirebaseMessagingService;
//import com.google.firebase.messaging.RemoteMessage;
//
//public class MyFirebaseMessagingService extends FirebaseMessagingService {
//    @Override
//    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
//        super.onMessageReceived(remoteMessage);
//
//        if (remoteMessage.getNotification() != null) {
//            String title = remoteMessage.getNotification().getTitle();
//            String message = remoteMessage.getNotification().getBody();
//            getFirebaseMessage(title, message);
//        }
//    }
//
//    private void getFirebaseMessage(String title, String message) {
//        // You can customize this method to show notifications or perform any other actions
//        // when a new FCM message is received.
//
//        // For example, you can create and display a notification:
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
//
//                .setContentTitle(title)
//                .setAutoCancel(true)
//                .setContentText(message)
//                .setSmallIcon(R.drawable.ic_notification);
//
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
//        notificationManager.notify(101, builder.build());
//    }
//
//    @Override
//    public void onNewToken(@NonNull String token) {
//        // Handle token refresh if needed
//    }
//}
