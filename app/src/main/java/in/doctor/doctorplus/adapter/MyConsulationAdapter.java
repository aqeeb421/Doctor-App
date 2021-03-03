package in.doctor.doctorplus.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import in.doctor.doctorplus.R;
import in.doctor.doctorplus.activity.PrescriptionActivity;
import in.doctor.doctorplus.model.ConsultationModel;

public class MyConsulationAdapter extends RecyclerView.Adapter<MyConsulationAdapter.MyConsultationHolder> {

    ArrayList<ConsultationModel> getConsultationModels;
    private Context context;

    public MyConsulationAdapter(ArrayList<ConsultationModel> getConsultationModels, Context context) {

        this.getConsultationModels = getConsultationModels;
        this.context = context;

    }

    @NonNull
    @Override
    public MyConsultationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_consult_doctors_row, parent, false);
        return new MyConsultationHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyConsultationHolder holder, int position) {

        String age = String.valueOf(getConsultationModels.get(position).getPatientAge());

        holder.Name.setText("Dr. " + getConsultationModels.get(position).getDoctorId().getFullName());
        holder.mc_patient_age.setText(age);
        holder.mc_patient_sex.setText((CharSequence) getConsultationModels.get(position).getPatientGender());
        holder.patientName.setText(getConsultationModels.get(position).getPatientName());
        holder.consultationAbout.setText(getConsultationModels.get(position).getDoctorId().getAbout());
        holder.scheduledOn.setText(getConsultationModels.get(position).getDoctorId().getCreatedAt());

        if (getConsultationModels.get(position).getPatientPic() != null && !getConsultationModels.get(position).getPatientPic().equals("")) {
            Picasso.get().load(getConsultationModels.get(position).getPatientPic()).fit().into(holder.imageView);
        }

        holder.chatButton.setEnabled(false);

        getConsultationModels.get(position).getDoctorId().setIsActive(true);


        if (getConsultationModels.get(position).getDoctorId().getIsActive()) {

            holder.chatButton.setEnabled(true);

            int id = getConsultationModels.get(position).getPatient().getId();
            String doctorName = getConsultationModels.get(position).getDoctorId().getFullName();
            int consultation_id = getConsultationModels.get(position).getId();

            holder.chatButton.setOnClickListener(v -> {
                Intent intent = new Intent(context, PrescriptionActivity.class);
                intent.putExtra("patient_id", id);
                intent.putExtra("doctorName", doctorName);
                intent.putExtra("consultation_id", consultation_id);
                context.startActivity(intent);
            });


        }

    }

    @Override
    public int getItemCount() {
        return getConsultationModels.size();
    }

    public static class MyConsultationHolder extends RecyclerView.ViewHolder {
        TextView Name, mc_patient_sex, mc_patient_age, patientName, consultationAbout, scheduledOn;
        ImageView chatButton;
        ImageView imageView;

        public MyConsultationHolder(@NonNull View itemView) {
            super(itemView);

            Name = itemView.findViewById(R.id.mc_doctor_name);
            mc_patient_sex = itemView.findViewById(R.id.mc_patient_sex);
            mc_patient_age = itemView.findViewById(R.id.mc_patient_age);
            patientName = itemView.findViewById(R.id.mc_patient_name);
            consultationAbout = itemView.findViewById(R.id.mc_consultation_about);
            scheduledOn = itemView.findViewById(R.id.mc_scheduled_on);
            chatButton = itemView.findViewById(R.id.mc_chat_button);
            imageView = itemView.findViewById(R.id.mc_doctor_image);
        }
    }
}
