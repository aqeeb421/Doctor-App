package in.doctor.doctorplus.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import in.doctor.doctorplus.R;
import in.doctor.doctorplus.adapter.TodayAdapter;
import in.doctor.doctorplus.model.AppointmentModel;
import in.doctor.doctorplus.network.RetrofitClient;
import in.doctor.doctorplus.utils.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class UpcomingFragment extends Fragment {

    public UpcomingFragment() {
        // Required empty public constructor
    }

    String token = "Token " + Constants.getToken();
    final ArrayList<AppointmentModel> getAppointmentModel = new ArrayList<>();
    private ImageView noData;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_upcoming, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.upcoming_recycler_view);
        noData = rootView.findViewById(R.id.noData);


        Call<List<AppointmentModel>> call = RetrofitClient
                .getInstance()
                .getApi()
                .GetUpcomingAppointment(token);

        call.enqueue(new Callback<List<AppointmentModel>>() {
            @Override
            public void onResponse(Call<List<AppointmentModel>> call, Response<List<AppointmentModel>> response) {
                if (response.code() == 200) {
                    assert response.body() != null;
                    try {
                        int id = response.body().get(0).getId();
                        Log.d("TAG", "onResponse: " + id);
                        ArrayList<AppointmentModel> getAppointmentModel = (ArrayList<AppointmentModel>) response.body();
                        //setRecyclerView(getAppointmentModel);

                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        TodayAdapter appointmentsAdapter = new TodayAdapter(getAppointmentModel, getActivity(),false);
                        recyclerView.setAdapter(appointmentsAdapter);


                    } catch (IndexOutOfBoundsException error) {
                        // Output expected IndexOutOfBoundsExceptions.
                        noData.setVisibility(View.VISIBLE);
                        Log.d("TAG", "onResponse: " + error);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<AppointmentModel>> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
        return rootView;
    }
}