package in.doctor.doctorplus.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import id.zelory.compressor.Compressor;
import in.doctor.doctorplus.R;
import in.doctor.doctorplus.model.DoctorDataModel;
import in.doctor.doctorplus.model.PasswordChange;
import in.doctor.doctorplus.network.RetrofitClient;
import in.doctor.doctorplus.utils.BaseBottomActivity;
import in.doctor.doctorplus.utils.Constants;
import in.doctor.doctorplus.utils.PathUtils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends BaseBottomActivity {

    private static final String TAG = "SettingsActivity";
    private static final int PICK_PHOTO_FOR_AVATAR = 123;
    CircleImageView profile_image1, update_profile_image;
    TextView tvProfileName, tvProfileAbout, editPassword, profileMedicalRecording;

    String UserName, PhoneNumber, Email, FullName, About, docImage;
    String ID;
    private android.app.AlertDialog progressDialog;
    TextView profilePrescription, profileTC, profilePrivacy;
    final String[] okFileExtensions = new String[]{
            "jpg",
            "png",
            "gif",
            "jpeg",
            "pdf"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String token = Constants.getToken();
        getDoctorData("Token " + token);

        init();
    }

    private void init() {
        profile_image1 = findViewById(R.id.profile_image1);
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileAbout = findViewById(R.id.tvProfileAbout);
        editPassword = findViewById(R.id.editPassword);
        profilePrescription = findViewById(R.id.profilePrescription);
        profileTC = findViewById(R.id.profileTC);
        profilePrivacy = findViewById(R.id.profilePrivacy);
        profileMedicalRecording = findViewById(R.id.profileMedicalRecording);
        progressDialog = new SpotsDialog(this, R.style.CustomDialog);
        String token = Constants.getToken();

        profilePrescription.setOnClickListener(v -> {
            startActivity(new Intent(SettingsActivity.this, PrescriptionListActivity.class));
        });

        profilePrivacy.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.doctor-plus.in/privacypolicy"));
            startActivity(browserIntent);
        });
        profileTC.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.doctor-plus.in/termsandcondition"));
            startActivity(browserIntent);
        });

        profileMedicalRecording.setOnClickListener(v -> {
            Log.d(TAG, "init: " + Constants.getMouFile());
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.getMouFile()));
            startActivity(browserIntent);
        });

        findViewById(R.id.logout).setOnClickListener(v -> {

            android.app.AlertDialog.Builder alertbox = new android.app.AlertDialog.Builder(v.getRootView().getContext());
            alertbox.setMessage("Do you want logout");
            alertbox.setTitle("Warning");
            alertbox.setIcon(R.drawable.ic_warning);
            alertbox.setCancelable(true);

            alertbox.setPositiveButton("Yes",
                    (arg0, arg1) -> {
                        DoLogout("Token " + token);
                        arg0.dismiss();
                    });
            alertbox.setNegativeButton("Cancel", (arg0, arg2) -> {
                arg0.dismiss();
            });
            alertbox.show();


        });

        editPassword.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
            View dialogView = LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.custom_password_pop, null);
            EditText edt_old_password = dialogView.findViewById(R.id.edt_old_password);
            EditText edt_new_password = dialogView.findViewById(R.id.edt_new_password);
            EditText edt_confirm_password = dialogView.findViewById(R.id.edt_confirm_password);
            Button button = dialogView.findViewById(R.id.btn_dialog);
            ImageView dialog_image_close = dialogView.findViewById(R.id.dialog_image_close);
            button.setOnClickListener(v1 -> {
                String old_password = edt_old_password.getText().toString();
                String new_password = edt_new_password.getText().toString();
                String confirm_password = edt_confirm_password.getText().toString();
                if (!TextUtils.isEmpty(old_password)) {
                    if (!TextUtils.isEmpty(new_password)) {
                        if (!TextUtils.isEmpty(confirm_password)) {
                            if (new_password.equals(confirm_password)) {
                                SendChangePassword(old_password, new_password, confirm_password);
                            } else
                                edt_new_password.setError("Password mismatched");
                        } else
                            edt_confirm_password.setError("Enter Confirm Password");
                    } else
                        edt_new_password.setError("Enter New Password");
                } else
                    edt_old_password.setError("Enter Old Password");

            });
            builder.setView(dialogView);
            builder.setCancelable(false);
            final AlertDialog show = builder.show();


            dialog_image_close.setOnClickListener(v12 -> show.dismiss());
        });

        findViewById(R.id.editProfile).setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
            View dialogView = LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.update_pop, null);

            // EditText edt_userName = dialogView.findViewById(R.id.edt_userName);
            EditText edt_fullname = dialogView.findViewById(R.id.edt_fullname);
            EditText edt_phoneNumber = dialogView.findViewById(R.id.edt_phoneNumber);
            EditText edt_email = dialogView.findViewById(R.id.edt_email);
            EditText edt_about = dialogView.findViewById(R.id.edt_about);
            Button btn_Cancel = dialogView.findViewById(R.id.btn_Cancel);
            Button btn_Update = dialogView.findViewById(R.id.btn_Update);
            update_profile_image = dialogView.findViewById(R.id.update_profile_image);
            //setting
            // edt_userName.setText(UserName);
            edt_fullname.setText(FullName);
            edt_phoneNumber.setText(PhoneNumber);
            edt_email.setText(Email);
            edt_about.setText(About);

            Picasso.get().load(docImage).fit().into(update_profile_image);

            update_profile_image.setOnClickListener(v1 -> {
                pickFile();
            });

            builder.setView(dialogView);
            builder.setCancelable(false);
            final AlertDialog show = builder.show();
            btn_Update.setOnClickListener(v1 -> {

                //String UserName = edt_userName.getText().toString().trim();
                String FullName = edt_fullname.getText().toString().trim();
                String PhoneNumber = edt_phoneNumber.getText().toString().trim();
                String Email = edt_email.getText().toString().trim();
                String About = edt_about.getText().toString().trim();

                UpdateProfile(ID, UserName, FullName, Email, PhoneNumber, About);
                show.dismiss();

            });

            btn_Cancel.setOnClickListener(v1 -> show.dismiss());
        });
    }

    private File thePath;

    private void UpdateProfile(String id, String userName, String fullName, String email, String phoneNumber, String about) {
        progressDialog.show();

        String retrivedToken = Constants.getToken();//second parameter default value.


        // add another part within the multipart request
        RequestBody fullName1 =
                RequestBody.create(MediaType.parse("multipart/form-data"), fullName);
        RequestBody userName1 =
                RequestBody.create(MediaType.parse("multipart/form-data"), userName);
        RequestBody email1 =
                RequestBody.create(MediaType.parse("multipart/form-data"), email);
        RequestBody phoneNumber1 =
                RequestBody.create(MediaType.parse("multipart/form-data"), phoneNumber);
        RequestBody about1 =
                RequestBody.create(MediaType.parse("multipart/form-data"), about);
        RequestBody ID1 =
                RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(id));

        File imgFile = new File(String.valueOf(thePath));

        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), imgFile);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("image", imgFile.getName(), requestFile);


        Call<PasswordChange> call = RetrofitClient.getInstance()
                .getApi()
                .DoctorUpdateProfile("Token " + retrivedToken, ID, userName1, fullName1, email1, phoneNumber1, about1, body);

        call.enqueue(new Callback<PasswordChange>() {
            @Override
            public void onResponse(Call<PasswordChange> call, Response<PasswordChange> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<PasswordChange> call, Throwable t) {
                progressDialog.dismiss();
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });

    }

    private void SendChangePassword(String old_password, String new_password, String confirm_password) {
        progressDialog.show();

        String retrivedToken = Constants.getToken();//second parameter default value.

        Call<PasswordChange> appointmentModelCall = RetrofitClient.getInstance()
                .getApi()
                .SendChangePassword("Token " + retrivedToken, old_password, new_password, confirm_password);

        appointmentModelCall.enqueue(new Callback<PasswordChange>() {
            @Override
            public void onResponse(Call<PasswordChange> call, Response<PasswordChange> response) {

                if (response.code() == 200) {
                    Log.d("TAG", "onResponse: " + response.body().getDone());
                    startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                    Toast.makeText(SettingsActivity.this, "Password Updated Successfully...", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();

            }

            @Override
            public void onFailure(Call<PasswordChange> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void DoLogout(String token) {
        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .DoLogout(token);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {

                    SharedPreferences sharedPreferences = getSharedPreferences("LogIn Details", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("token");
                    editor.remove("Id");
                    editor.apply();

                    Intent intent = new Intent(SettingsActivity.this, LogInActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_settings;
    }

    @Override
    protected int getNavigationMenuItemId() {
        return R.id.menu_profile;
    }

    private void getDoctorData(String token) {
        Call<List<DoctorDataModel>> call = RetrofitClient
                .getInstance()
                .getApi()
                .GetDoctorData(token);

        call.enqueue(new Callback<List<DoctorDataModel>>() {
            @Override
            public void onResponse(@NotNull Call<List<DoctorDataModel>> call, @NotNull Response<List<DoctorDataModel>> response) {
                if (response.code() == 200) {
                    Picasso.get().load(response.body().get(0).getDoctor().getProfilePic()).fit().into(profile_image1);

                    UserName = response.body().get(0).getDoctor().getFullName();
                    FullName = response.body().get(0).getDoctor().getFullName();
                    Email = response.body().get(0).getDoctor().getUser().getEmail();
                    About = response.body().get(0).getDoctor().getAbout();
                    PhoneNumber = response.body().get(0).getDoctor().getPhoneNumber();
                    ID = String.valueOf(response.body().get(0).getDoctor().getId());
                    docImage = response.body().get(0).getDoctor().getProfilePic();

                    tvProfileName.setText(response.body().get(0).getDoctor().getFullName());
                    tvProfileAbout.setText(response.body().get(0).getDoctor().getAbout());
                }
            }

            @Override
            public void onFailure(Call<List<DoctorDataModel>> call, Throwable t) {
                Toast.makeText(SettingsActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void pickFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, PICK_PHOTO_FOR_AVATAR);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == RESULT_OK) {
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
            }
            Uri path = Uri.parse(PathUtils.getMediaFilePathForN(uri, getApplicationContext()));
            thePath = new File(Objects.requireNonNull(path.getPath()));
            String url = path.toString();

            try {
                File compressedImageFile = new Compressor(this).compressToFile(new File(path.getPath()));
                Log.e(TAG, "Commpressed image file size : " + compressedImageFile.length());
                enterImageDocuments(Uri.fromFile(compressedImageFile), compressedImageFile);
            } catch (Exception ex) {
                ex.printStackTrace();
                for (String extension : okFileExtensions) {
                    if (Objects.equals(extension, "pdf")) {
                        //do here
                        File uploadFile = new File(String.valueOf(thePath));

                    }
                }
            }


        }

    }

    private void enterImageDocuments(Uri fromFile, File compressedImageFile) {
        String imageString = fromFile.toString();
        for (String extesion : okFileExtensions) {
            if (imageString.toLowerCase().endsWith(extesion)) {
                // filenameTextView.setVisibility(View.GONE);
                // selectedFile.setVisibility(View.VISIBLE);
                Picasso.get().load(imageString).fit().into(update_profile_image);
            }
        }
    }

}