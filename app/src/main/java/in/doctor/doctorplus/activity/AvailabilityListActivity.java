package in.doctor.doctorplus.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import in.doctor.doctorplus.R;
import in.doctor.doctorplus.adapter.MyAvailListAdapter;
import in.doctor.doctorplus.adapter.MyConsulationAdapter;
import in.doctor.doctorplus.model.ConsultationModel;
import in.doctor.doctorplus.model.DoctorTimingModel;
import in.doctor.doctorplus.model.LoginDetailModel;
import in.doctor.doctorplus.network.RetrofitClient;
import in.doctor.doctorplus.utils.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AvailabilityListActivity extends AppCompatActivity {

    ImageView img_back;
    RecyclerView availability_recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_availability_list);

        img_back = findViewById(R.id.img_back);
        availability_recyclerView = findViewById(R.id.availability_recyclerView);

        img_back.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), AvailabityActivity.class));
            finish();
        });

        String token = Constants.getToken();

        Call<List<DoctorTimingModel>> call = RetrofitClient
                .getInstance()
                .getApi()
                .GetDoctorTimings("Token " + token);

        call.enqueue(new Callback<List<DoctorTimingModel>>() {
            @Override
            public void onResponse(Call<List<DoctorTimingModel>> call, Response<List<DoctorTimingModel>> response) {
                if (response.code() == 200 && response.body().size() > 0) {

                    ArrayList<DoctorTimingModel> doctorTimingModels = (ArrayList<DoctorTimingModel>) response.body();
                    setRecyclerView(doctorTimingModels);

                }
            }

            @Override
            public void onFailure(Call<List<DoctorTimingModel>> call, Throwable t) {

            }
        });
    }

    private void setRecyclerView(ArrayList<DoctorTimingModel> doctorTimingModels) {
        availability_recyclerView.setLayoutManager(new LinearLayoutManager(AvailabilityListActivity.this));
        MyAvailListAdapter myAvailListAdapter = new MyAvailListAdapter(doctorTimingModels, AvailabilityListActivity.this);
        availability_recyclerView.setAdapter(myAvailListAdapter);
    }

   /* private void setRecyclerView(ArrayList<ConsultationModel> getConsultationModels) {

    }*/
}