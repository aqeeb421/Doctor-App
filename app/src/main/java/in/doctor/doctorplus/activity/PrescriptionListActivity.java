package in.doctor.doctorplus.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import in.doctor.doctorplus.R;
import in.doctor.doctorplus.adapter.MyConsulationAdapter;
import in.doctor.doctorplus.adapter.MyPrescriptionAdapter;
import in.doctor.doctorplus.model.ConsultationModel;
import in.doctor.doctorplus.model.PrescriptionListModel;
import in.doctor.doctorplus.network.RetrofitClient;
import in.doctor.doctorplus.utils.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PrescriptionListActivity extends AppCompatActivity {

    private static final String TAG = "PrescriptionListActivity";
    private RecyclerView recyclerView;
    private ImageView noData, back_img;
    private AlertDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescription_list);

        recyclerView = findViewById(R.id.myPrescription_recyclerView);
        noData = findViewById(R.id.noData);
        back_img = findViewById(R.id.back_img);
        progressDialog = new SpotsDialog(this, R.style.CustomDialog);

        back_img.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            finish();
        });

        GetDoctorPrescription();
    }

    private void GetDoctorPrescription() {
        progressDialog.show();
        //Retrieve token wherever necessary
        String token = Constants.getToken();
        Call<List<PrescriptionListModel>> call = RetrofitClient
                .getInstance()
                .getApi()
                .GetDoctorPrescription("Token " + token);

        call.enqueue(new Callback<List<PrescriptionListModel>>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(Call<List<PrescriptionListModel>> call, Response<List<PrescriptionListModel>> response) {
                progressDialog.hide();
                if (response.code() == 200) {
                    Log.d(TAG, "onResponse: " + response.code());

                    assert response.body() != null;
                    try {
                        int id = response.body().get(0).getId();
                        Log.d("TAG", "onResponse: " + id);
                        ArrayList<PrescriptionListModel> getPrescriptionModels = (ArrayList<PrescriptionListModel>) response.body();

                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(PrescriptionListActivity.this));
                        MyPrescriptionAdapter myPrescriptionAdapter = new MyPrescriptionAdapter(getPrescriptionModels, PrescriptionListActivity.this);
                        recyclerView.setAdapter(myPrescriptionAdapter);


                    } catch (IndexOutOfBoundsException error) {
                        // Output expected IndexOutOfBoundsExceptions.
                        noData.setVisibility(View.VISIBLE);
                        Log.d("TAG", "onResponse: " + error);
                    }
                }

            }

            @Override
            public void onFailure(Call<List<PrescriptionListModel>> call, Throwable t) {
                progressDialog.hide();
            }
        });

    }
}