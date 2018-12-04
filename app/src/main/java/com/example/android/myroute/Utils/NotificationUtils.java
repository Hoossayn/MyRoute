package com.example.android.myroute.Utils;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import com.example.android.myroute.Activities.MapsActivity;
import com.example.android.myroute.R;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class NotificationUtils {

    private static final String ROUTE_UPDATE_NOTIFICATION = "route_update_notification";
    private static final int NOTIFICATOIN_ID = 123431;

    private Context context;
    private static PendingIntent contentIntent(Context context){

        Intent startMapActivityIntent  = new Intent(context, MapsActivity.class);

        return PendingIntent.getActivity(context, 1234,startMapActivityIntent,PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private static void showUserRecordUpdate(Context context){
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel mChannel = new NotificationChannel(ROUTE_UPDATE_NOTIFICATION,context.getString(R.string.Rooobi),NotificationManager.IMPORTANCE_HIGH);
        notificationManager.createNotificationChannel(mChannel);


      NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context,ROUTE_UPDATE_NOTIFICATION)
              .setColor(ContextCompat.getColor(context,R.color.colorPrimary))
              .setSmallIcon(R.drawable.expandabledown)
              .setLargeIcon(largeIcon(context))
              .setContentTitle(context.getString(R.string.Rooobi))
              .setContentText(context.getString(R.string.Route_is_saving))
              .setStyle(new NotificationCompat.BigTextStyle().bigText(
                      context.getString(R.string.Route_is_saving)
              ))
              .setContentIntent(contentIntent(context))
              .setAutoCancel(true);

       if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
          notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);


    }
    notificationManager.notify(NOTIFICATOIN_ID, notificationBuilder.build());
    }



    private static Bitmap largeIcon(Context context){

        Resources res = context.getResources();
        Bitmap largeicon = BitmapFactory.decodeResource(res, R.drawable.delete);
        return largeicon;
    }
}
