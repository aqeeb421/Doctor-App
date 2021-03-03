package in.doctor.doctorplus.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.List;

import dmax.dialog.SpotsDialog;
import in.doctor.doctorplus.R;
import in.doctor.doctorplus.model.AppointmentModel;
import in.doctor.doctorplus.model.DoctorDataModel;
import in.doctor.doctorplus.model.LoginDetailModel;
import in.doctor.doctorplus.network.RetrofitClient;
import in.doctor.doctorplus.service.MyService;
import in.doctor.doctorplus.utils.BaseBottomActivity;
import in.doctor.doctorplus.utils.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashBoardActivity extends BaseBottomActivity {

    @Override
    protected int getContentViewId() {
        return R.layout.activity_dash_board;
    }

    private static String TAG = "DashBoardActivity";
    String token, balance_amount;
    int todayAppt = 0;
    private TextView tv_userName, tv_doc_rating, tv_balance, tv_today_app;
    double balance = 0.0;
    private AlertDialog progressDoalog;
    private ImageView home_profile_image;
    private RelativeLayout relative_app;

    @Override
    protected int getNavigationMenuItemId() {
        return R.id.menu_home;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDoalog = new SpotsDialog(this, R.style.CustomDialog);
        token = Constants.getToken();
        init();
        getDoctorData("Token " + token);
        getTodayAppointments("Token " + token);
    }

    private void init() {
        tv_userName = findViewById(R.id.userName);
        tv_doc_rating = findViewById(R.id.doc_rating);
        tv_balance = findViewById(R.id.balance);
        tv_today_app = findViewById(R.id.today_app);
        home_profile_image = findViewById(R.id.home_profile_image);
        relative_app = findViewById(R.id.relative_app);
        tv_userName.setText(Constants.getDoctor_name());
        Log.d(TAG, "init: " + Constants.getRating());
        Double rating = Constants.getRating();

        DecimalFormat df = new DecimalFormat("0.0");
        String gg = df.format(rating);
        tv_doc_rating.setText(gg);

        relative_app.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), AppointmentActivity.class));
            finish();
        });

        home_profile_image.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            finish();
        });
    }

    private void getDoctorData(String token) {
        Call<List<DoctorDataModel>> call = RetrofitClient
                .getInstance()
                .getApi()
                .GetDoctorData(token);
        call.enqueue(new Callback<List<DoctorDataModel>>() {
            @Override
            public void onResponse(@NotNull Call<List<DoctorDataModel>> call, @NotNull Response<List<DoctorDataModel>> response) {
                if (response.code() == 200) {
                    balance = response.body().get(0).getBalance();
                    tv_balance.setText(String.valueOf(balance));

                    tv_userName.setText(response.body().get(0).getDoctor().getFullName());
                    //tv_doc_rating.setText(response.body().get(0).getDoctor().getRating().toString());
                    Picasso.get().load(response.body().get(0).getDoctor().getProfilePic()).fit().into(home_profile_image);

                    Double rating = response.body().get(0).getDoctor().getRating();

                    DecimalFormat df = new DecimalFormat("0.0");
                    String gg = df.format(rating);
                    tv_doc_rating.setText(gg);
                }
            }

            @Override
            public void onFailure(Call<List<DoctorDataModel>> call, Throwable t) {
                Toast.makeText(DashBoardActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTodayAppointments(String token) {
        Call<List<AppointmentModel>> call = RetrofitClient
                .getInstance()
                .getApi()
                .GetTodayAppointment(token);
        call.enqueue(new Callback<List<AppointmentModel>>() {
            @Override
            public void onResponse(Call<List<AppointmentModel>> call, Response<List<AppointmentModel>> response) {
                if (response.code() == 200) {
                    try {
                        todayAppt = response.body().size();
                        tv_today_app.setText(String.valueOf(todayAppt));
                        Log.d(TAG, "onResponse: " + todayAppt);

                    } catch (Exception e) {
                        Log.e(TAG, "onResponse: " + e.getMessage());
                        tv_today_app.setText("0");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<AppointmentModel>> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
}