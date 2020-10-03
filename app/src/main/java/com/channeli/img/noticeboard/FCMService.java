package com.channeli.img.noticeboard;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class FCMService extends FirebaseMessagingService {

    private static final String TAG = "FCMService";
    private static final String APP_NAME = "Channel i NoticeBoard";
    private static final String CHANNEL_ID = "notices_channel_01";// The id of the channel.
    private static final CharSequence CHANNEL_NAME = "Notices channel";// The name of the channel.


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String title=remoteMessage.getNotification().getTitle();
        String body=remoteMessage.getNotification().getBody();

        sendNotification(generateNotification(title,body));

    }


    private Notification generateNotification(String title,String body){
        Intent intent = new Intent(this, NoticeListScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, ((int)System.currentTimeMillis()%1000), intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        return new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_for_notifications)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSound(defaultSoundUri)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.logo_for_notifications))
                .setShowWhen(true)
                .setTicker("New Notice!")
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(body)
                .build();
    }


    private void sendNotification(Notification notification) {

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        notificationManager.notify(APP_NAME, (int) (System.currentTimeMillis()%Integer.MAX_VALUE), notification);
    }
}