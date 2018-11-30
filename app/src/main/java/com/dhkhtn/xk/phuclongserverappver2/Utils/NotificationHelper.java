package com.dhkhtn.xk.phuclongserverappver2.Utils;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.dhkhtn.xk.phuclongserverappver2.R;


public class NotificationHelper extends ContextWrapper{
    private static final String CHANNEL_ID = "com.phuclongappv2.xk.phuclongappver2.XKDev";
    private static final String CHANNGEL_NAME = "Phuc Long";

    private NotificationManager notificationManager;

    public NotificationHelper(Context context){
        super(context);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,CHANNGEL_NAME,NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(false);
        channel.enableVibration(true);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getNotificationManager().createNotificationChannel(channel);

    }

    public NotificationManager getNotificationManager() {
        if(notificationManager == null){
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getAppNoti(String titile, String message, Uri uriSound){
        return new Notification.Builder(getApplicationContext(),CHANNEL_ID)
                .setContentText(message)
                .setContentTitle(titile)
                .setSmallIcon(R.drawable.ic_local_cafe_black_24dp)
                .setSound(uriSound)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setTicker("Phúc Long Coffee & Tea Exprees");
    }
}
