package in.doctor.doctorplus.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import in.doctor.doctorplus.R;
import in.doctor.doctorplus.adapter.MyConsulationAdapter;
import in.doctor.doctorplus.adapter.TodayAdapter;
import in.doctor.doctorplus.model.ConsultationModel;
import in.doctor.doctorplus.network.RetrofitClient;
import in.doctor.doctorplus.utils.BaseBottomActivity;
import in.doctor.doctorplus.utils.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyConsultationActivity extends BaseBottomActivity {

    SharedPreferences preferences;
    private RecyclerView recyclerView;
    private ImageView noData;
    private AlertDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();

        GetDoctorConsultations();
    }

    private void init() {
        recyclerView = findViewById(R.id.myconsultation_recyclerView);
        noData = findViewById(R.id.noData);
        progressDialog = new SpotsDialog(this, R.style.CustomDialog);
    }


    private void GetDoctorConsultations() {
        progressDialog.show();
        //Retrieve token wherever necessary
        String token = Constants.getToken();
        Call<List<ConsultationModel>> call = RetrofitClient
                .getInstance()
                .getApi()
                .GetDoctorConsultations("Token " + token);
        call.enqueue(new Callback<List<ConsultationModel>>() {
            @Override
            public void onResponse(Call<List<ConsultationModel>> call, Response<List<ConsultationModel>> response) {

                if (response.isSuccessful()) {

                    assert response.body() != null;
                    try {
                        int id = response.body().get(0).getId();
                        Log.d("TAG", "onResponse: " + id);
                        ArrayList<ConsultationModel> getConsultationModels = (ArrayList<ConsultationModel>) response.body();
                        //setRecyclerView(getConsultationModels);

                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(MyConsultationActivity.this));
                        MyConsulationAdapter myConsulationAdapter = new MyConsulationAdapter(getConsultationModels, MyConsultationActivity.this);
                        recyclerView.setAdapter(myConsulationAdapter);


                    } catch (IndexOutOfBoundsException error) {
                        // Output expected IndexOutOfBoundsExceptions.
                        noData.setVisibility(View.VISIBLE);
                        Log.d("TAG", "onResponse: " + error);
                    }
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<ConsultationModel>> call, Throwable t) {
                Toast.makeText(MyConsultationActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

    }

    private void setRecyclerView(ArrayList<ConsultationModel> getConsultationModels) {
        recyclerView.setLayoutManager(new LinearLayoutManager(MyConsultationActivity.this));
        MyConsulationAdapter myConsulationAdapter = new MyConsulationAdapter(getConsultationModels, MyConsultationActivity.this);
        recyclerView.setAdapter(myConsulationAdapter);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_my_consultation;
    }

    @Override
    protected int getNavigationMenuItemId() {
        return R.id.menu_my_consultation;
    }
}