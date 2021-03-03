package in.doctor.doctorplus.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import in.doctor.doctorplus.R;
import in.doctor.doctorplus.activity.VideoCallActivity;
import in.doctor.doctorplus.model.AppointmentModel;

import static android.content.ContentValues.TAG;

public class TodayAdapter extends RecyclerView.Adapter<TodayAdapter.AppointmentsHolder> {
    ArrayList<AppointmentModel> getAppointmentModels;
    private Context context;
    private String SelectedDay = "";
    private TextView tv_avail_doc;
    private int flag = 0;
    private String formattedTime = "";
    private boolean isActive = false;

    public TodayAdapter(ArrayList<AppointmentModel> getAppointmentModel, FragmentActivity activity, boolean isActive) {
        this.getAppointmentModels = getAppointmentModel;
        this.context = activity;
        this.isActive = isActive;

    }

    @NonNull
    @Override
    public AppointmentsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointed_doctors_row, parent, false);
        return new AppointmentsHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AppointmentsHolder holder, int position) {
        // String age = String.valueOf(getAppointmentModels.get(position).getPatient().ge);

        // Log.d(TAG, "onBindViewHolder: " + age);

        holder.Name.setText("Dr. " + getAppointmentModels.get(position).getDoctor().getFullName());
        holder.patientName.setText(getAppointmentModels.get(position).getPatientName());
        // holder.app_patient_age.setText(age);
        // holder.app_patient_sex.setText(getAppointmentModels.get(position).getPatientGender());
        holder.consultationAbout.setText(getAppointmentModels.get(position).getDescription());
        holder.scheduledOn.setText(getAppointmentModels.get(position).getAppointmentDate() + "    " + getAppointmentModels.get(position).getAppointmentTime());


        // Picasso.get().load(getAppointmentModels.get(position).getPatientPic()).resize(100, 100).into(holder.imageView);

        getAppointmentModels.get(position).getDoctor().setIsActive(true);

        if (getAppointmentModels.get(position).getDoctor().getIsActive()) {
            holder.videoCall.setImageResource(R.drawable.videocall);
        }

        if (isActive) {
            holder.videoCall.setVisibility(View.VISIBLE);
        } else {
            holder.videoCall.setVisibility(View.GONE);
        }

        holder.videoCall.setOnClickListener(v -> {
            if (getAppointmentModels.get(position).getDoctor().getIsActive()) {

                context.startActivity(new Intent(context, VideoCallActivity.class));

            }
        });
    }


    @Override
    public int getItemCount() {
        return getAppointmentModels.size();
    }

    public class AppointmentsHolder extends RecyclerView.ViewHolder {
        TextView Name, patientName, consultationAbout, scheduledOn, app_patient_age, app_patient_sex;
        ImageView videoCall;
        ImageView imageView;

        public AppointmentsHolder(@NonNull View itemView) {
            super(itemView);

            Name = itemView.findViewById(R.id.app_doctor_name);
            patientName = itemView.findViewById(R.id.app_patient_name);
            app_patient_age = itemView.findViewById(R.id.app_patient_age);
            app_patient_sex = itemView.findViewById(R.id.app_patient_sex);
            consultationAbout = itemView.findViewById(R.id.app_consultation_about);
            scheduledOn = itemView.findViewById(R.id.app_scheduled_on);
            videoCall = itemView.findViewById(R.id.app_videoCall);
            imageView = itemView.findViewById(R.id.app_doctor_image);
        }
    }
}
