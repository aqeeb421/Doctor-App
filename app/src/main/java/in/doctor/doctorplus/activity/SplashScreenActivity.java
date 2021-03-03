package in.doctor.doctorplus.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import dmax.dialog.SpotsDialog;
import in.doctor.doctorplus.R;
import in.doctor.doctorplus.model.LoginDetailModel;
import in.doctor.doctorplus.model.User;
import in.doctor.doctorplus.network.RetrofitClient;
import in.doctor.doctorplus.service.MyService;
import in.doctor.doctorplus.utils.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreenActivity extends AppCompatActivity {


    private static final int SPLASH_DISPLAY_LENGTH = 5;
    private SharedPreferences preferences;
    private AlertDialog progressDoalog;
    private int STORAGE_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hide status bar https://developer.android.com/training/system-ui/status
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        if (ContextCompat.checkSelfPermission(SplashScreenActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(SplashScreenActivity.this, "You have already granted this permission!",
                    Toast.LENGTH_SHORT).show();
        } else {
            requestStoragePermission();
        }

        startService(new Intent(getBaseContext(), MyService.class));
        init();
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(SplashScreenActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                            Manifest.permission.CAMERA,}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA,
                    }, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void init() {

        progressDoalog = new SpotsDialog(this, R.style.CustomDialog);

        //Retrieve token wherever necessary
        preferences = getApplicationContext().getSharedPreferences("LogIn Details", Context.MODE_PRIVATE);
        String token = preferences.getString("token", null);
        int Id = preferences.getInt("Id", 0);

        new Handler().postDelayed(() -> {
           /* startActivity(new Intent(SplashScreenActivity.this, LogInActivity.class));
            finish();*/
            getUserDetails("Token " + token);
        }, SPLASH_DISPLAY_LENGTH * 1000);


        Constants.setToken(token);
        Constants.setDoc_id(Id);


    }

    private void getUserDetails(String s) {
        Log.d(s, "getUserDetails: " + s);
        System.out.print(s);

        Call<LoginDetailModel> call = RetrofitClient
                .getInstance()
                .getApi()
                .GetLoginDetails(s);

        call.enqueue(new Callback<LoginDetailModel>() {
            @Override
            public void onResponse(Call<LoginDetailModel> call, Response<LoginDetailModel> response) {

                if (response.code() == 200) {
                    Log.d("TAG", "onResponse: " + response.message());
                    int id = response.body().getId();
                    Constants.setDoctor_name(response.body().getFullName());
                    Constants.setRating(response.body().getRating());
                    Log.d("TAG", "onResponse: " + response.body() + Constants.getDoctor_name() + Constants.getRating());
                    launchHomeScreen();

                    Constants.setMouFile(response.body().getMouFile());

                } else {
                    startActivity(new Intent(SplashScreenActivity.this, LogInActivity.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<LoginDetailModel> call, Throwable t) {
                progressDoalog.dismiss();
            }
        });

    }


    private void launchHomeScreen() {
        Intent intent = new Intent(SplashScreenActivity.this, DashBoardActivity.class);
        startActivity(intent);
        finish();
    }

}