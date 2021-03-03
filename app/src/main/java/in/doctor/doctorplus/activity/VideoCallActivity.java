package in.doctor.doctorplus.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.opentok.android.BaseVideoCapturer;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;

import de.hdodenhof.circleimageview.CircleImageView;
import in.doctor.doctorplus.R;
import in.doctor.doctorplus.model.MobAnswerCallModel;
import in.doctor.doctorplus.network.RetrofitClient;
import in.doctor.doctorplus.utils.Constants;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoCallActivity extends AppCompatActivity implements Session.SessionListener, PublisherKit.PublisherListener {

    private static String API_KEY = "47034434";
    private static String SESSION_ID = "1_MX40NzAzNDQzNH5-MTYxMDcwODAwMTA2MX5kVnJqS1NwUkluYXRjMExUMUJ3OEw5Q05-UH4";
    private static String TOKEN = "76ca83ec02f7c0ef904f536a2d0bc251df25d8a4";
    private static final String LOG_TAG = VideoCallActivity.class.getSimpleName();
    private static final int RC_SETTINGS_SCREEN_PERM = 123;
    private static final int RC_VIDEO_APP_PERM = 124;
    private static final String TAG = "VideoCallActivity";
    private Session mSession;
    private Publisher mPublisher;
    private Subscriber mSubscriber;
    private BaseVideoCapturer mCapturer;

    private FrameLayout mPublisherViewContainer;
    private FrameLayout mSubscriberViewContainer;

    private CircleImageView answer_call, reject_call;
    FrameLayout calling_screen;
    LinearLayout lnr_callScreen, lnr_callAction;
    private CircleImageView mute_voice, mute_video, end_call, camera;
    boolean flag;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_call);
        int sessionId = getIntent().getIntExtra("answerId", 0);

        answer_call = findViewById(R.id.answer_call);
        reject_call = findViewById(R.id.reject_call);
        calling_screen = findViewById(R.id.calling_screen);
        lnr_callScreen = findViewById(R.id.lnr_callScreen);
        lnr_callAction = findViewById(R.id.lnr_callAction);

        mute_voice = findViewById(R.id.mute_voice);
        mute_video = findViewById(R.id.mute_video);
        end_call = findViewById(R.id.end_call);
        camera = findViewById(R.id.camera);

        end_call.setOnClickListener(v -> {
            disconnectSession();
        });

        camera.setOnClickListener(v -> {
            mPublisher.cycleCamera();
        });

        mute_voice.setOnClickListener(v -> {
            if (flag == false) {
                Drawable color = new ColorDrawable(getResources().getColor(R.color.red));
                Drawable image = getResources().getDrawable(R.drawable.ic_baseline_mic_off_white);

                LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                mute_voice.setImageDrawable(ld);
                mute_voice.setBorderColor(getColor(R.color.red));

                mPublisher.setPublishAudio(false);
                flag = true;
            } else {
                Drawable color = new ColorDrawable(getResources().getColor(R.color.greylite));
                Drawable image = getResources().getDrawable(R.drawable.ic_baseline_mic_off_24);

                LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                mute_voice.setImageDrawable(ld);
                mute_voice.setBorderColor(getColor(R.color.greylite));

                mPublisher.setPublishAudio(true);
                flag = false;
            }
        });

        mute_video.setOnClickListener(v -> {
            if (flag == false) {
                Drawable color = new ColorDrawable(getResources().getColor(R.color.red));
                Drawable image = getResources().getDrawable(R.drawable.ic_baseline_videocam_off_white);

                LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                mute_video.setImageDrawable(ld);
                mute_video.setBorderColor(getColor(R.color.red));

                mPublisher.setPublishVideo(false);
                flag = true;
            } else {
                Drawable color = new ColorDrawable(getResources().getColor(R.color.greylite));
                Drawable image = getResources().getDrawable(R.drawable.ic_baseline_videocam_off_24);

                LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                mute_video.setImageDrawable(ld);
                mute_video.setBorderColor(getColor(R.color.greylite));

                mPublisher.setPublishVideo(true);
                flag = false;
            }
        });

        answer_call.setOnClickListener(v -> {
            MobAnswerCall(sessionId);
            lnr_callScreen.setVisibility(View.GONE);
            calling_screen.setVisibility(View.VISIBLE);
            lnr_callAction.setVisibility(View.VISIBLE);
        });

        reject_call.setOnClickListener(v -> {
            Log.d(TAG, "DeleteDocFile: " + Constants.getToken());
            Call<Void> call = RetrofitClient
                    .getInstance()
                    .getApi()
                    .EndCall("Token " + Constants.getToken(), sessionId);

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                       // startActivity(new Intent(getApplicationContext(), FeedBackActivity.class));
                       // finish();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {

                }
            });
        });

        requestPermissions();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");

        super.onPause();

        if (mSession == null) {
            return;
        }
        mSession.onPause();

        if (isFinishing()) {
            disconnectSession();
        }
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");

        super.onResume();

        if (mSession == null) {
            return;
        }
        mSession.onResume();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");

        disconnectSession();

        super.onDestroy();
    }

    private void disconnectSession() {
        if (mSession == null) {
            startActivity(new Intent(VideoCallActivity.this, DashBoardActivity.class));
            finish();
            return;
        }

        if (mSubscriber != null) {
            mSubscriberViewContainer.removeView(mSubscriber.getView());
            mSession.unsubscribe(mSubscriber);
            mSubscriber.destroy();
            mSubscriber = null;
        }

        if (mPublisher != null) {
            mPublisherViewContainer.removeView(mPublisher.getView());
            mSession.unpublish(mPublisher);
            mPublisher.destroy();
            mPublisher = null;
        }
        mSession.disconnect();
        startActivity(new Intent(VideoCallActivity.this, DashBoardActivity.class));
        finish();
    }

    private void MobAnswerCall(int id) {
        Call<MobAnswerCallModel> call = RetrofitClient
                .getInstance()
                .getApi()
                .MobAnswerCall(id);

        call.enqueue(new Callback<MobAnswerCallModel>() {
            @Override
            public void onResponse(Call<MobAnswerCallModel> call, Response<MobAnswerCallModel> response) {
                if (response.isSuccessful()) {
                    MobAnswerCallModel mobAnswerCallModel = new MobAnswerCallModel();
                    mobAnswerCallModel = response.body();

                    mSession = new Session.Builder(VideoCallActivity.this, API_KEY, response.body().getSessionId()).build();
                    mSession.setSessionListener(VideoCallActivity.this);
                    mSession.connect(response.body().getToken());

                }
            }

            @Override
            public void onFailure(Call<MobAnswerCallModel> call, Throwable t) {

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(RC_VIDEO_APP_PERM)
    private void requestPermissions() {
        String[] perms = {Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // initialize view objects from your layout
            mPublisherViewContainer = (FrameLayout) findViewById(R.id.publisher_container);
            mSubscriberViewContainer = (FrameLayout) findViewById(R.id.subscriber_container);

            // initialize and connect to the session
           /* mSession = new Session.Builder(this, API_KEY, SESSION_ID).build();
            mSession.setSessionListener(this);
            mSession.connect(TOKEN);*/

            // fetchSessionConnectionData();


        } else {
            EasyPermissions.requestPermissions(this, "This app needs access to your camera and mic to make video calls", RC_VIDEO_APP_PERM, perms);
        }
    }

   /* public void fetchSessionConnectionData() {
        String retrivedToken = Constants.getToken();

        Call<MobCallDoctorModel> call = RetrofitClient
                .getInstance()
                .getApi()
                .fetchSessionConnectionData("Token " + retrivedToken, docID, aptID);

        call.enqueue(new Callback<MobCallDoctorModel>() {
            @Override
            public void onResponse(Call<MobCallDoctorModel> call, Response<MobCallDoctorModel> response) {
                if (response.isSuccessful()) {

                    Log.d(TAG, "onResponse: " + response.body().getToken());

                    mSession = new Session.Builder(VideoCallActivity.this, API_KEY, response.body().getSessionId()).build();
                    mSession.setSessionListener(VideoCallActivity.this);
                    mSession.connect(response.body().getToken());

                    //mPublisher.cycleCamera();


                }
            }

            @Override
            public void onFailure(Call<MobCallDoctorModel> call, Throwable t) {
                Toast.makeText(VideoCallActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }*/

    // SessionListener methods

    @Override
    public void onConnected(Session session) {
        Log.i(LOG_TAG, "Session Connected");

        mPublisher = new Publisher.Builder(this).build();
        mPublisher.setPublisherListener(this);

        mPublisherViewContainer.addView(mPublisher.getView());

        if (mPublisher.getView() instanceof GLSurfaceView) {
            ((GLSurfaceView) mPublisher.getView()).setZOrderOnTop(true);
        }

        mSession.publish(mPublisher);
    }

    @Override
    public void onDisconnected(Session session) {
        Log.i(LOG_TAG, "Session Disconnected");
    }

    @Override
    public void onStreamReceived(Session session, Stream stream) {
        Log.i(LOG_TAG, "Stream Received");


        if (mSubscriber == null) {
            mSubscriber = new Subscriber.Builder(this, stream).build();
            mSession.subscribe(mSubscriber);
            mSubscriberViewContainer.addView(mSubscriber.getView());
        }
    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {
        Log.i(LOG_TAG, "Stream Dropped");

        if (mSubscriber != null) {
            mSubscriber = null;
            mSubscriberViewContainer.removeAllViews();
        }
    }

    @Override
    public void onError(Session session, OpentokError opentokError) {
        Log.e(LOG_TAG, "Session error: " + opentokError.getMessage());
    }

    // PublisherListener methods

    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {
        Log.i(LOG_TAG, "Publisher onStreamCreated");
    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {
        Log.i(LOG_TAG, "Publisher onStreamDestroyed");
    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {
        Log.e(LOG_TAG, "Publisher error: " + opentokError.getMessage());
    }
}