package in.doctor.doctorplus.activity;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatRadioButton;

import java.util.Calendar;

import in.doctor.doctorplus.R;
import in.doctor.doctorplus.model.DoctorTimingsModel;
import in.doctor.doctorplus.network.RetrofitClient;
import in.doctor.doctorplus.utils.BaseBottomActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AvailabityActivity extends BaseBottomActivity {

    @Override
    protected int getContentViewId() {
        return R.layout.activity_availabity;
    }

    AppCompatRadioButton simpleSwitch1, simpleSwitch2, simpleSwitch3;
    AppCompatRadioButton simpleSwitch4, simpleSwitch5, simpleSwitch6;
    AppCompatRadioButton selectedRadioBtn;
    Button addAvailButton, addAvailListButton;
    TimePickerDialog picker;
    String statusSwitch;
    EditText eText_start, eText_end;
    String startTime, endTime, token;
    SharedPreferences sharedPreferences;
    RadioGroup dayGroup;
    int Id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        eText_start.setInputType(InputType.TYPE_NULL);
        eText_start.setOnClickListener(v -> {
            final Calendar cldr = Calendar.getInstance();
            int hour = cldr.get(Calendar.HOUR_OF_DAY);
            int minutes = cldr.get(Calendar.MINUTE);
            // time picker dialog

            picker = new TimePickerDialog(AvailabityActivity.this,
                    (tp, sHour, sMinute) -> {
                        String strHour = String.valueOf(sHour), strMin = String.valueOf(sMinute), strAM_PM;
                        if (strMin.length() == 1) strMin = "0" + strMin;
                        eText_start.setText(sHour + ":" + strMin);
                    }, hour, minutes, false);
            picker.show();
        });
        eText_end.setInputType(InputType.TYPE_NULL);
        eText_end.setOnClickListener(v -> {
            final Calendar cldr = Calendar.getInstance();
            int hour = cldr.get(Calendar.HOUR_OF_DAY);
            int minutes = cldr.get(Calendar.MINUTE);
            // time picker dialog
            picker = new TimePickerDialog(AvailabityActivity.this,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                            String strHour = String.valueOf(sHour), strMin = String.valueOf(sMinute), strAM_PM;
                            if (strMin.length() == 1) strMin = "0" + strMin;
                            eText_end.setText(sHour + ":" + strMin);
                        }
                    }, hour, minutes, false);
            picker.show();
        });

    }

    private void init() {
        simpleSwitch1 = findViewById(R.id.simpleSwitch1);
        simpleSwitch2 = findViewById(R.id.simpleSwitch2);
        simpleSwitch3 = findViewById(R.id.simpleSwitch3);
        simpleSwitch4 = findViewById(R.id.simpleSwitch4);
        simpleSwitch5 = findViewById(R.id.simpleSwitch5);
        simpleSwitch6 = findViewById(R.id.simpleSwitch6);
        addAvailButton = findViewById(R.id.addAvailButton);
        addAvailListButton = findViewById(R.id.addAvailListButton);
        dayGroup = findViewById(R.id.dayGroup);

        //edttext
        eText_end = findViewById(R.id.editText_endTime);
        eText_start = findViewById(R.id.editText_startTime);

        sharedPreferences = getSharedPreferences("LogIn Details", Context.MODE_PRIVATE);
        Id = sharedPreferences.getInt("Id", 0);
        token = sharedPreferences.getString("token", "Default");

        addAvailListButton.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), AvailabilityListActivity.class));
        });


        dayGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.simpleSwitch1:
                        statusSwitch = "monday";
                        break;
                    case R.id.simpleSwitch2:
                        statusSwitch = "tuesday";
                        break;
                    case R.id.simpleSwitch3:
                        statusSwitch = "wednesday";
                        break;
                    case R.id.simpleSwitch4:
                        statusSwitch = "thursday";
                        break;
                    case R.id.simpleSwitch5:
                        statusSwitch = "friday";
                        break;
                    case R.id.simpleSwitch6:
                        statusSwitch = "saturday";
                        break;

                    case R.id.simpleSwitch7:
                        statusSwitch = "sunday";
                        break;
                }
            }
        });


        addAvailButton.setOnClickListener(v -> {
            startTime = eText_start.getText().toString();
            endTime = eText_end.getText().toString();
            if (statusSwitch == null) {
                Toast.makeText(this, "Please select any day", Toast.LENGTH_SHORT).show();
                return;
            }
            if (startTime.isEmpty()) {
                eText_start.setError("start time is required");
                return;
            }
            if (endTime.isEmpty()) {
                eText_end.setError("end time is required");
                return;
            }
            SendDoctorTimings();
        });
    }

    private void SendDoctorTimings() {
        // Do API  call here
        Call<DoctorTimingsModel> call = RetrofitClient
                .getInstance()
                .getApi()
                .SendDoctorTimings("Token " + token, statusSwitch, startTime, endTime);

        call.enqueue(new Callback<DoctorTimingsModel>() {
            @Override
            public void onResponse(Call<DoctorTimingsModel> call, Response<DoctorTimingsModel> response) {
                if (response.isSuccessful()) {
                    startActivity(new Intent(getApplicationContext(), DashBoardActivity.class));
                }
            }

            @Override
            public void onFailure(Call<DoctorTimingsModel> call, Throwable t) {
                Log.e("TAG", "onFailure: " + t.getMessage());
            }
        });
    }

    @Override
    protected int getNavigationMenuItemId() {
        return R.id.menu_new_consult;
    }
}