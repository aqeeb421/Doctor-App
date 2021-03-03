package in.doctor.doctorplus.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import in.doctor.doctorplus.R;
import in.doctor.doctorplus.activity.AvailabilityListActivity;
import in.doctor.doctorplus.model.DoctorTimingModel;
import in.doctor.doctorplus.network.RetrofitClient;
import in.doctor.doctorplus.utils.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyAvailListAdapter extends RecyclerView.Adapter<MyAvailListAdapter.MyAvailListHolder> {

    ArrayList<DoctorTimingModel> doctorTimingModels;
    Context context;

    public MyAvailListAdapter(ArrayList<DoctorTimingModel> doctorTimingModels, AvailabilityListActivity availabilityListActivity) {
        this.doctorTimingModels = doctorTimingModels;
        this.context = availabilityListActivity;
    }

    @NonNull
    @Override
    public MyAvailListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.availability_row, parent, false);
        return new MyAvailListAdapter.MyAvailListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAvailListHolder holder, int position) {

        holder.avail_day.setText(doctorTimingModels.get(position).getDay());
        holder.avail_time.setText(doctorTimingModels.get(position).getFromTime() + " - " + doctorTimingModels.get(position).getToTime());

        holder.button_delete.setOnClickListener(v -> {


            android.app.AlertDialog.Builder alertbox = new android.app.AlertDialog.Builder(v.getRootView().getContext());
            alertbox.setMessage("Do you want Delete");
            alertbox.setTitle("Warning");
            alertbox.setIcon(R.drawable.ic_warning);
            alertbox.setCancelable(true);

            alertbox.setPositiveButton("Yes",
                    (arg0, arg1) -> {
                        Call<Void> call = RetrofitClient
                                .getInstance()
                                .getApi()
                                .DeleteAvail("Token " + Constants.getToken(), doctorTimingModels.get(position).getId());

                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(context, "Deleted Successfully...", Toast.LENGTH_SHORT).show();
                                    context.startActivity(new Intent(context, AvailabilityListActivity.class));
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {

                            }
                        });
                        arg0.dismiss();
                    });
            alertbox.setNegativeButton("Cancel", (arg0, arg2) -> {
                arg0.dismiss();
            });
            alertbox.show();

        });


    }


    @Override
    public int getItemCount() {
        return doctorTimingModels.size();
    }

    public class MyAvailListHolder extends RecyclerView.ViewHolder {

        TextView avail_day, avail_time;
        Button button_delete;

        public MyAvailListHolder(@NonNull View itemView) {
            super(itemView);
            avail_day = itemView.findViewById(R.id.avail_day);
            avail_time = itemView.findViewById(R.id.avail_time);
            button_delete = itemView.findViewById(R.id.button_delete);
        }
    }
}
