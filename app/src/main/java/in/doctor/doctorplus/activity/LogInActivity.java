package in.doctor.doctorplus.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;

import dmax.dialog.SpotsDialog;
import in.doctor.doctorplus.R;
import in.doctor.doctorplus.model.CheckPhoneModel;
import in.doctor.doctorplus.model.PasswordChange;
import in.doctor.doctorplus.model.RegisterModel;
import in.doctor.doctorplus.model.VerifyOTPModel;
import in.doctor.doctorplus.network.RetrofitClient;
import in.doctor.doctorplus.utils.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener {


    public static String token;
    private Button btn_Log_in_user;
    private EditText editTextEmail, editTextPassword;
    private AlertDialog progressDoalog;
    private SharedPreferences sharedPreferences;

    private TextView tvRegister;
    TextView forgetPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT > 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_log_in);


        init();
    }

    private void init() {
        btn_Log_in_user = findViewById(R.id.btn_Log_in_user);
        tvRegister = findViewById(R.id.tvRegister);

        editTextEmail = findViewById(R.id.edt_login_email);
        editTextPassword = findViewById(R.id.edt_login_password);
        forgetPwd = findViewById(R.id.tv_forgotpwd);

        btn_Log_in_user.setOnClickListener(this);
        tvRegister.setOnClickListener(this);

        forgetPwd.setOnClickListener(v -> {
            sendUserPWD();
        });

        progressDoalog = new SpotsDialog(this, R.style.CustomDialog);
    }

    String PatID = "";

    private void sendUserPWD() {
        sharedPreferences = getSharedPreferences("User Details", Context.MODE_PRIVATE);
        String fullPhoneNumber = sharedPreferences.getString("fullPhoneNumber", "STRING_DEFAULT");

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.custom_forget_password_pop, null);
        EditText edt_PhoneNUmber = dialogView.findViewById(R.id.edt_PhoneNUmber);
        Button button = dialogView.findViewById(R.id.btn_dialog);
        ImageView dialog_image_close = dialogView.findViewById(R.id.dialog_image_close);
        PinView otpTextView = dialogView.findViewById(R.id.otpText);
        Button btn_dialog1 = dialogView.findViewById(R.id.btn_dialog1);
        TextView tv_dialog = dialogView.findViewById(R.id.tv_dialog);
        EditText edt_new_password = dialogView.findViewById(R.id.edt_new_password);
        Button btn_dialog2 = dialogView.findViewById(R.id.btn_dialog2);

        builder.setView(dialogView);
        builder.setCancelable(false);
        final androidx.appcompat.app.AlertDialog show = builder.show();

        button.setOnClickListener(v1 -> {
            String phoneNumber = edt_PhoneNUmber.getText().toString();
            if (!TextUtils.isEmpty(phoneNumber)) {
                    Call<CheckPhoneModel> call = RetrofitClient
                            .getInstance()
                            .getApi()
                            .CheckPhoneNumber(phoneNumber);

                    call.enqueue(new Callback<CheckPhoneModel>() {
                        @Override
                        public void onResponse(Call<CheckPhoneModel> call, Response<CheckPhoneModel> response) {
                            if (response.code() == 200) {
                                otpTextView.setVisibility(View.VISIBLE);
                                btn_dialog1.setVisibility(View.VISIBLE);
                                button.setVisibility(View.GONE);
                                edt_PhoneNUmber.setVisibility(View.GONE);

                                PatID = response.body().getPatient().getId().toString();


                                String otpText = "is your verification code for Doctor Plus";
                                Call<String> sendOtp = RetrofitClient
                                        .getInstance()
                                        .getApi()
                                        .getSMSOTP(
                                                "a224db72-cafb-4cce-93ab-3d7f950c92e2",
                                                "Welcome <OTP> " + otpText,
                                                "0",
                                                "BANDSS",
                                                fullPhoneNumber,
                                                "1",
                                                4
                                        );

                                sendOtp.enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        if (response.isSuccessful()) {
                                            Toast.makeText(LogInActivity.this, "We have successfully send OTP to this number", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(LogInActivity.this, "Something went wrong...", Toast.LENGTH_SHORT).show();
                                            show.dismiss();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        Log.e("TAG", "onFailure: " + t.getMessage());
                                        show.dismiss();
                                    }
                                });


                            } else {
                                Toast.makeText(LogInActivity.this, "Please Enter valid phone Number.", Toast.LENGTH_SHORT).show();
                                show.dismiss();
                            }

                        }

                        @Override
                        public void onFailure(Call<CheckPhoneModel> call, Throwable t) {
                            show.dismiss();
                            Toast.makeText(LogInActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });



            } else {
                Toast.makeText(this, "Enter valid number", Toast.LENGTH_SHORT).show();
                edt_PhoneNUmber.setError("Enter Phone Number");

            }


        });

        btn_dialog1.setOnClickListener(v -> {

            Call<VerifyOTPModel> verifyOTP = RetrofitClient
                    .getInstance()
                    .getApi()
                    .VerifyOTP(fullPhoneNumber, otpTextView.getText().toString().trim());

            verifyOTP.enqueue(new Callback<VerifyOTPModel>() {
                @Override
                public void onResponse(Call<VerifyOTPModel> call, Response<VerifyOTPModel> response) {
                    if (response.body().getResponseCode().equals("8000")) {
                        Toast.makeText(LogInActivity.this, "OTP Verified Successfully", Toast.LENGTH_SHORT).show();
                        tv_dialog.setText("Enter New Password");
                        otpTextView.setVisibility(View.GONE);
                        btn_dialog1.setVisibility(View.GONE);
                        edt_new_password.setVisibility(View.VISIBLE);
                        btn_dialog2.setVisibility(View.VISIBLE);

                    } else if (response.body().getResponseCode().equals("8003")) {
                        Toast.makeText(LogInActivity.this, "Invalid Mobile Number", Toast.LENGTH_SHORT).show();
                    } else if (response.body().getResponseCode().equals("8006")) {
                        Toast.makeText(LogInActivity.this, "OTP Verification failed", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<VerifyOTPModel> call, Throwable t) {
                    Log.e("TAG", "onFailure: " + t.getMessage());

                }
            });

        });

        btn_dialog2.setOnClickListener(v -> {

            if (!edt_new_password.getText().toString().equals("")) {
                if (edt_new_password.getText().toString().length() >= 4) {

                    Call<PasswordChange> call = RetrofitClient
                            .getInstance()
                            .getApi()
                            .ChangePwd(edt_new_password.getText().toString(), PatID);

                    call.enqueue(new Callback<PasswordChange>() {
                        @Override
                        public void onResponse(Call<PasswordChange> call, Response<PasswordChange> response) {
                            if (response.code() == 200) {
                                Toast.makeText(LogInActivity.this, "Password successfully changed", Toast.LENGTH_SHORT).show();
                                show.dismiss();
                            } else {
                                show.dismiss();
                                Toast.makeText(LogInActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<PasswordChange> call, Throwable t) {
                            Log.d("TAG", "onFailure: " + t.getMessage());
                            show.dismiss();
                        }
                    });


                } else {
                    Toast.makeText(this, "Enter at least 4 digit password", Toast.LENGTH_SHORT).show();
                }

            }

        });


        dialog_image_close.setOnClickListener(v12 -> show.dismiss());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_Log_in_user:
                UserLogIn();
                break;

            case R.id.tvRegister:
               /* startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                finish();*/
                Toast.makeText(this, "open in browser", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void UserLogIn() {
        progressDoalog.show();

        String DoctorID = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (DoctorID.isEmpty()) {
            editTextEmail.setError("Doctor ID is required");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextEmail.setError("Password is required");
            editTextEmail.requestFocus();
            return;
        }

        if (password.length() < 4) {
            editTextEmail.setError("Password should be atleast 5 char long");
            editTextEmail.requestFocus();
            return;
        }

        /*Do API  call here*/
        Call<RegisterModel> call = RetrofitClient
                .getInstance()
                .getApi()
                .SendLogInUser(DoctorID, password, "D");

        call.enqueue(new Callback<RegisterModel>() {
            @Override
            public void onResponse(Call<RegisterModel> call, Response<RegisterModel> response) {
                if (response.code() == 200) {
                    assert response.body() != null;
                    token = response.body().getToken();
                    int Id = response.body().getUser().getId();

                    Constants.setToken(token);
                    Constants.setDoc_id(Id);

                    sharedPreferences = getSharedPreferences("LogIn Details", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("token", token);
                    editor.putInt("Id", Id);
                    editor.apply();
                    progressDoalog.dismiss();
                    startActivity(new Intent(getApplicationContext(), DashBoardActivity.class));
                    finish();

                } else if (response.code() == 500) {
                    Toast.makeText(getApplicationContext(), "User already exist...", Toast.LENGTH_SHORT).show();
                    progressDoalog.dismiss();
                }
                progressDoalog.dismiss();
                //  startActivity(new Intent(getApplicationContext(), DashBoardActivity.class));
            }

            @Override
            public void onFailure(Call<RegisterModel> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                progressDoalog.dismiss();

            }
        });
    }
}