package in.doctor.doctorplus.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;



import java.util.Objects;

import in.doctor.doctorplus.R;
import in.doctor.doctorplus.utils.Constants;

public class CallNotificationService extends Service {

    private String CHANNEL_ID = "CallChannel";
    private String CHANNEL_NAME = "Call Channel";


    public CallNotificationService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle data = null;
        String name = "", callType = "";
        int NOTIFICATION_ID = 120;

        if (intent != null && intent.getExtras() != null) {

            data = intent.getExtras();
            name = data.getString("inititator");
            if (Constants.getCall_type().equalsIgnoreCase(Constants.AUDIO_CALL)) {
                callType = "Audio";
            } else {
                callType = "Video";
            }

        }
        try {
            Intent receiveCallAction = new Intent(this, CallNotificationActionReceiver.class);

            receiveCallAction.putExtra("ConstantApp.CALL_RESPONSE_ACTION_KEY", "ConstantApp.CALL_RECEIVE_ACTION");
            receiveCallAction.putExtra("ACTION_TYPE", "RECEIVE_CALL");
            receiveCallAction.putExtra("NOTIFICATION_ID", NOTIFICATION_ID);
            receiveCallAction.setAction("RECEIVE_CALL");

            Intent cancelCallAction = new Intent(this, CallNotificationActionReceiver.class);
            cancelCallAction.putExtra("ConstantApp.CALL_RESPONSE_ACTION_KEY", "ConstantApp.CALL_CANCEL_ACTION");
            cancelCallAction.putExtra("ACTION_TYPE", "CANCEL_CALL");
            cancelCallAction.putExtra("NOTIFICATION_ID", NOTIFICATION_ID);
            cancelCallAction.setAction("CANCEL_CALL");

            Intent callDialogAction = new Intent(this, CallNotificationActionReceiver.class);
            callDialogAction.putExtra("ACTION_TYPE", "DIALOG_CALL");
            callDialogAction.putExtra("NOTIFICATION_ID", NOTIFICATION_ID);
            callDialogAction.setAction("DIALOG_CALL");

            PendingIntent receiveCallPendingIntent = PendingIntent.getBroadcast(this, 1200, receiveCallAction, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent cancelCallPendingIntent = PendingIntent.getBroadcast(this, 1201, cancelCallAction, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent callDialogPendingIntent = PendingIntent.getBroadcast(this, 1202, callDialogAction, PendingIntent.FLAG_UPDATE_CURRENT);

            createChannel();
            NotificationCompat.Builder notificationBuilder = null;
            if (data != null) {
                // Uri ringUri= Settings.System.DEFAULT_RINGTONE_URI;
                notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle(name)
                        .setContentText("Incoming " + callType + " Call")
                        .setSmallIcon(R.drawable.favicon)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setCategory(NotificationCompat.CATEGORY_CALL)
                        .addAction(R.drawable.answer_call, getString(R.string.reject_call), cancelCallPendingIntent)
                        .addAction(R.drawable.end_call, getString(R.string.answer_call), receiveCallPendingIntent)
                        .setAutoCancel(true)
                        .setSound(ringUri)
                        .setFullScreenIntent(callDialogPendingIntent, true);

            }

            Notification incomingCallNotification = null;
            if (notificationBuilder != null) {
                incomingCallNotification = notificationBuilder.build();
            }
            startForeground(NOTIFICATION_ID, incomingCallNotification);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();// release your media player here
    }
    Uri ringUri;
    public void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                 ringUri = Settings.System.DEFAULT_RINGTONE_URI;
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription("Call Notifications");
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            channel.setSound(ringUri,
                    new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setLegacyStreamType(AudioManager.STREAM_RING)
                            .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION).build());
                Objects.requireNonNull(this.getSystemService(NotificationManager.class)).createNotificationChannel(channel);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}