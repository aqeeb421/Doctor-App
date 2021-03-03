package in.doctor.doctorplus.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import in.doctor.doctorplus.R;
import in.doctor.doctorplus.activity.PrescriptionActivity;
import in.doctor.doctorplus.activity.PrescriptionListActivity;
import in.doctor.doctorplus.model.PrescriptionListModel;
import in.doctor.doctorplus.network.RetrofitClient;
import in.doctor.doctorplus.utils.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class MyPrescriptionAdapter extends RecyclerView.Adapter<MyPrescriptionAdapter.MyPrescriptionHolder> {

    ArrayList<PrescriptionListModel> getPrescriptionModel;
    private Context context;

    public MyPrescriptionAdapter(ArrayList<PrescriptionListModel> getPrescriptionModel, Context context) {

        this.getPrescriptionModel = getPrescriptionModel;
        this.context = context;

    }

    @NonNull
    @Override
    public MyPrescriptionAdapter.MyPrescriptionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_prescription_row, parent, false);
        return new MyPrescriptionAdapter.MyPrescriptionHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyPrescriptionHolder holder, int position) {


        PrescriptionListModel prescriptionListModel = getPrescriptionModel.get(position);

        holder.patientName.setText(prescriptionListModel.getPatientName());
        holder.recordType.setText(prescriptionListModel.getRecordType());

        String desc = prescriptionListModel.getDescription();

        String desc1 = desc.replaceAll("<br />", "\n");

        holder.recordDescription.setText(desc1);


        holder.recordFile.setOnClickListener(v -> {

            try {

                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
                View dialogView = LayoutInflater.from(context).inflate(R.layout.show_image_pop, null);

                TextView filenameTextView = dialogView.findViewById(R.id.showImg_filename);
                Button button = dialogView.findViewById(R.id.showImg_btn_sub_dialog);
                ImageView dialog_image_close = dialogView.findViewById(R.id.dialog_sub_image_close1);
                ImageView selectedFile = dialogView.findViewById(R.id.selectedImgView);

                builder.setView(dialogView);
                builder.setCancelable(true);
                final androidx.appcompat.app.AlertDialog show = builder.show();

                File uploadFile = new File(prescriptionListModel.getRecordFiles().toString());
                filenameTextView.setText(uploadFile.getName());
                filenameTextView.setVisibility(View.VISIBLE);

                Picasso.get().load(prescriptionListModel.getRecordFiles().toString()).fit().into(selectedFile);


                button.setOnClickListener(v1 -> {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(prescriptionListModel.getRecordFiles().toString()));
                    context.startActivity(browserIntent);
                });


                dialog_image_close.setOnClickListener(v12 -> show.dismiss());


            } catch (NullPointerException e) {
                Toast.makeText(context, "No File Found", Toast.LENGTH_SHORT).show();
            }

        });
        holder.deleteFile.setOnClickListener(v -> {
            AlertDialog.Builder alertbox = new AlertDialog.Builder(v.getRootView().getContext());
            alertbox.setMessage("Do you want delete this file.");
            alertbox.setTitle("Warning");
            alertbox.setIcon(R.drawable.ic_warning);
            alertbox.setCancelable(true);

            alertbox.setPositiveButton("Yes",
                    (arg0, arg1) -> {
                        DeleteDocFile(getPrescriptionModel.get(position).getId());
                        arg0.dismiss();
                    });
            alertbox.setNegativeButton("Cancel", (arg0, arg2) -> {
                arg0.dismiss();
            });
            alertbox.show();
        });
        holder.recordUpdate.setOnClickListener(v -> {
            Intent intent = new Intent(context, PrescriptionActivity.class);
            intent.putExtra("patient_id", getPrescriptionModel.get(position).getPatient());
            intent.putExtra("doctorName", getPrescriptionModel.get(position).getDoctor());
            intent.putExtra("consultation_id", getPrescriptionModel.get(position).getConsultationId());
            intent.putExtra("PrescriptionPosition", position);
            intent.putExtra("PrescriptionID", getPrescriptionModel.get(position).getId());
            if (getPrescriptionModel.get(position).getRecordFiles() != null)
                intent.putExtra("PrescriptionPic", getPrescriptionModel.get(position).getRecordFiles().toString());
            context.startActivity(intent);

        });
    }

    private void DeleteDocFile(Integer id) {
        Log.d(TAG, "DeleteDocFile: " + Constants.getToken());
        Call<Void> call = RetrofitClient
                .getInstance()
                .getApi()
                .DeletePrescriptionRecordDetails("Token " + Constants.getToken(), id);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Delete successfully", Toast.LENGTH_SHORT).show();
                    context.startActivity(new Intent(context, PrescriptionListActivity.class));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return getPrescriptionModel.size();
    }

    public static class MyPrescriptionHolder extends RecyclerView.ViewHolder {
        TextView recordType, recordDescription, patientName, recordDate;
        Button recordFile, deleteFile, recordUpdate;

        public MyPrescriptionHolder(@NonNull View itemView) {
            super(itemView);
            patientName = itemView.findViewById(R.id.patientName);
            recordType = itemView.findViewById(R.id.recordType);
            recordDescription = itemView.findViewById(R.id.recordDescription);
            recordFile = itemView.findViewById(R.id.recordFile);
            deleteFile = itemView.findViewById(R.id.deleteFile);
            recordUpdate = itemView.findViewById(R.id.recordUpdate);
        }
    }
}

