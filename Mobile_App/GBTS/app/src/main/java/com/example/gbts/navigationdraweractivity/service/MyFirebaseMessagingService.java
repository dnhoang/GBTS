package com.example.gbts.navigationdraweractivity.service;

/**
 * Created by HoangDN on 10/3/2016.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.gbts.navigationdraweractivity.MainActivity;
import com.example.gbts.navigationdraweractivity.R;
import com.example.gbts.navigationdraweractivity.activity.LoginActivity;
import com.example.gbts.navigationdraweractivity.fragment.MainContent;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by cafe on 11/08/2016.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

// TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("Truongtq msg", "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            final String body = remoteMessage.getData().get("body");
            final String title = remoteMessage.getData().get("title");

//            Log.d("Truongtqmsg", "Message data payload: " + remoteMessage.getData());
            Log.d("Truongtqmsg", "sendBody");
            if (body.equals("PLEASEUPDATECARDLIST")) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("notiUpdateCard", body);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                Log.d("Truongtqmsgcard", "update card " + body);
            } else {
                showNotification(remoteMessage.getData().get("body"), remoteMessage.getData().get("title"));
                MainContent mainContent = new MainContent();
                Bundle bundle = new Bundle();
                bundle.putString("notiBody", body);
                bundle.putString("notiTitle", title);
                mainContent.setArguments(bundle);
                Log.d("Truongtqmsg1", "body: " + mainContent.getArguments().getString("notiBody"));
                Log.d("Truongtqmsg1", "title " + mainContent.getArguments().getString("notiTitle"));
            }
        }

//         Check if message contains a notification payload.
//        if (remoteMessage.getData() != null) {
//
//            showNotification(remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle());
//
//            Log.d("Truongtq msg", "Message Notification Body: " + remoteMessage.getNotification().getBody());
//        }
//        showNotification(remoteMessage.getData().get("body"), remoteMessage.getData().get("title"));
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void showNotification(String messageBody, String title) {
        Intent intent = null;
        SharedPreferences sharedPreferences = getSharedPreferences("Info", MODE_PRIVATE);
        boolean isLogout = sharedPreferences.getBoolean("isLogout", true);
        if (isLogout) {
            intent = new Intent(this, LoginActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }
        intent.putExtra("messageBody", messageBody);
        intent.putExtra("messageTile", title);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(sound)
                .setContentIntent(pendingIntent);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }

    private void sendBody(String strBody, String strTitle) {
        Log.d("Truongtqmsg", "sendBody");
        MainContent mainContent = new MainContent();
        Bundle bundle = new Bundle();
        bundle.putString("notiBody", strBody);
        bundle.putString("notiTitle", strTitle);
        mainContent.setArguments(bundle);
    }
}

