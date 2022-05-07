package com.example.tileshop;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationHelper {
    private Context context;
    private NotificationManager notificationManager;
    private static final String CHANNEL_ID = "tile_shop_channel";
    private final int NOTI_ID = 0;

    public NotificationHelper(Context c) {
        this.context = c;
        this.notificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        createChanenl();
    }

    private void createChanenl(){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            return;
        }

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Tile shop", NotificationManager.IMPORTANCE_HIGH);
        channel.enableVibration(true);
        channel.setDescription("It's time to buy something cool.");
        this.notificationManager.createNotificationChannel(channel);

    }

    public void send(String message){
        Intent intent = new Intent(context, TileListActivity.class);
        //PeddingIntent peddingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_MUTABLE);
        }else{
            pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID).setContentTitle("Tile shop").setContentText(message).setSmallIcon(R.drawable.ic_shopping_cart).setContentIntent(pendingIntent);
        notificationManager.notify(NOTI_ID, builder.build());
    }

    public void cancelNoti(){
        notificationManager.cancel(NOTI_ID);
    }
}
