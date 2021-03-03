package in.doctor.doctorplus.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import in.doctor.doctorplus.activity.VideoCallActivity;
import in.doctor.doctorplus.model.MobVideoModel;
import in.doctor.doctorplus.network.RetrofitClient;
import in.doctor.doctorplus.utils.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MyService extends Service {
    private static final String TAG = "MyService";

    //private NotificationManagerCompat notificationManager;

    private String CHANNEL_ID = "CallChannel";
    private String CHANNEL_NAME = "Call Channel";


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();

        //  notificationManager = NotificationManagerCompat.from(this);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        String NOTIFICATION_CHANNEL_ID = "example.permanence";
        String channelName = "Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        startTimer();
        return START_STICKY;
    }

    private Timer timer;
    private TimerTask timerTask;
    public int counter = 0;
    Context c = this;

    String token = Constants.getToken();
    int ID = Constants.getDoc_id();

    public void startTimer() {
        timer = new Timer();
        timerTask = new TimerTask() {
            public void run() {
                Log.i("Count", "=========  " + (counter++));

                getMobVideoChatOperation("Token " + token, ID);
            }
        };
        timer.schedule(timerTask, 1000, 5000); //
    }

    private void getMobVideoChatOperation(String token, int ID) {


        Call<List<MobVideoModel>> call = RetrofitClient
                .getInstance()
                .getApi()
                .GetMobVideoOperation(token, ID);

        call.enqueue(new Callback<List<MobVideoModel>>() {
            @Override
            public void onResponse(Call<List<MobVideoModel>> call, Response<List<MobVideoModel>> response) {
                if (response.isSuccessful()) {
                    try {
                        int id = response.body().get(0).getId();

                        Intent intent = new Intent(c, VideoCallActivity.class);
                        intent.putExtra("answerId", id);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                        stopSelf();

                        // startService(new Intent(getBaseContext(), CallNotificationService.class));

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            Intent serviceIntent = new Intent(getApplicationContext(), CallNotificationService.class);
                            Bundle mBundle = new Bundle();
                            mBundle.putString("inititator", "name");
                            mBundle.putString("call_type", "Video");
                            serviceIntent.putExtras(mBundle);
                            ContextCompat.startForegroundService(getApplicationContext(), serviceIntent);
                        }


                    } catch (Exception e) {
                        Log.d(TAG, "onResponse: " + e.getMessage());
                        // stopSelf();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<MobVideoModel>> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });


    }


    public void stoptimertask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        super.onDestroy();
        stoptimertask();
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, Restarter.class);
        this.sendBroadcast(broadcastIntent);

        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }
}
