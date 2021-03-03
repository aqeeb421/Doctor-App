package in.doctor.doctorplus.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import id.zelory.compressor.Compressor;
import in.doctor.doctorplus.R;
import in.doctor.doctorplus.model.PrescriptionAddModel;
import in.doctor.doctorplus.network.RetrofitClient;
import in.doctor.doctorplus.utils.Constants;
import in.doctor.doctorplus.utils.PathUtils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PrescriptionActivity extends AppCompatActivity {

    private static final int PICK_PHOTO_FOR_AVATAR = 1001;
    private static final String TAG = "MedicalRecordActivity";
    private RecyclerView mediRecord_recyclerView;
    private ImageView noData, selectedFile;
    private AlertDialog progressDialog;
    private Button add_medical_button;
    private String retrivedToken;
    private File thePath;
    private TextView recordType, recordDiscription, filenameTextView, tv_sub_dialog;
    String recordTypeValue, recordDiscriptionValue;


    final String[] okFileExtensions = new String[]{
            "jpg",
            "png",
            "gif",
            "jpeg",
            "pdf"
    };
    String flag = "add pres";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescription);

        String patientId = String.valueOf(getIntent().getIntExtra("patient_id", -1));
        //String doctorName = String.valueOf(getIntent().getIntExtra("doctorName", 0));
        String consultation_id = String.valueOf(getIntent().getIntExtra("consultation_id", -1));
        String PrescriptionPosition = String.valueOf(getIntent().getIntExtra("PrescriptionPosition", -1));
        String PrescriptionPic = String.valueOf(getIntent().getIntExtra("PrescriptionPic", -1));
        String PrescriptionID = String.valueOf(getIntent().getIntExtra("PrescriptionID", -1));

        String doctorName = Constants.getDoctor_name();

        recordType = findViewById(R.id.edt_recordType);
        recordDiscription = findViewById(R.id.edt_recordDescription);
        filenameTextView = findViewById(R.id.filename);
        Button button = findViewById(R.id.btn_sub_dialog);
        Button chooseButton = findViewById(R.id.btn_select_dialog);
        selectedFile = findViewById(R.id.selectedFile);
        tv_sub_dialog = findViewById(R.id.tv_sub_dialog);

        if (!PrescriptionPosition.equals("-1")) {
            tv_sub_dialog.setText("Update Prescription");
            flag = "update pres";
            if (!PrescriptionPic.equals("-1")) {
                Picasso.get().load(PrescriptionPic).into(selectedFile);
                selectedFile.setVisibility(View.VISIBLE);
            }
        }

        chooseButton.setOnClickListener(v1 -> {
            // pickFile();

            selectImage();
        });

        button.setOnClickListener(v1 -> {

            recordTypeValue = recordType.getText().toString();
            recordDiscriptionValue = recordDiscription.getText().toString();
            if (recordTypeValue.isEmpty()) {
                recordType.setError("Enter Record Type");
                return;
            }
            if (recordDiscriptionValue.isEmpty()) {
                recordDiscription.setError("Enter Record Type");
                return;
            }
            if (flag.equals("update pres")) {
                UpdatePrescription(PrescriptionID, recordTypeValue, recordDiscriptionValue, patientId, doctorName, consultation_id);
            } else {
                UploadPrescription(recordTypeValue, recordDiscriptionValue, patientId, doctorName, consultation_id);
            }

        });


    }


    // Select image from camera and gallery
    private void selectImage() {
        try {
            PackageManager pm = getPackageManager();
            int hasPerm = pm.checkPermission(Manifest.permission.CAMERA, getPackageName());
            if (hasPerm == PackageManager.PERMISSION_GRANTED) {
                final CharSequence[] options = {"Take Photo", "Choose From Gallery", "Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(PrescriptionActivity.this);
                builder.setTitle("Select Option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Take Photo")) {
                            dialog.dismiss();
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, PICK_IMAGE_CAMERA);
                        } else if (options[item].equals("Choose From Gallery")) {
                            dialog.dismiss();
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhoto, PICK_IMAGE_GALLERY);
                        } else if (options[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            } else
                Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void UpdatePrescription(String prescriptionID, String recordTypeValue, String recordDiscriptionValue, String patientId, String doctorName, String consultation_id) {

        retrivedToken = Constants.getToken();

        // add another part within the multipart request
        RequestBody record_type =
                RequestBody.create(MediaType.parse("multipart/form-data"), recordTypeValue);
        RequestBody description =
                RequestBody.create(MediaType.parse("multipart/form-data"), recordDiscriptionValue);

        RequestBody created_by =
                RequestBody.create(MediaType.parse("multipart/form-data"), doctorName);
        RequestBody patientId1 =
                RequestBody.create(MediaType.parse("multipart/form-data"), patientId);
        RequestBody consultation_id1 =
                RequestBody.create(MediaType.parse("multipart/form-data"), consultation_id);
        RequestBody prescriptionID1 =
                RequestBody.create(MediaType.parse("multipart/form-data"), prescriptionID);

        if (thePath != null) {
            File uploadFile = new File(String.valueOf(thePath));

            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), uploadFile);

            // MultipartBody.Part is used to send also the actual file name
            MultipartBody.Part body = MultipartBody.Part.createFormData("record_files", uploadFile.getName(), requestFile);

            Call<PrescriptionAddModel> call = RetrofitClient
                    .getInstance()
                    .getApi()
                    .UpdatePrescription("Token " + retrivedToken, prescriptionID, record_type, description, created_by, patientId1, consultation_id1, body);

            call.enqueue(new Callback<PrescriptionAddModel>() {
                @Override
                public void onResponse(Call<PrescriptionAddModel> call, Response<PrescriptionAddModel> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "onResponse: ");
                        startActivity(new Intent(PrescriptionActivity.this, PrescriptionListActivity.class));
                        finish();
                    }

                }

                @Override
                public void onFailure(Call<PrescriptionAddModel> call, Throwable t) {

                }
            });
        }


    }

    private void UploadPrescription(String recordTypeValue, String recordDiscriptionValue, String patientId, String doctorName, String consultation_id) {
//        progressDialog.show();
        retrivedToken = Constants.getToken();

        // add another part within the multipart request
        RequestBody record_type =
                RequestBody.create(MediaType.parse("multipart/form-data"), recordTypeValue);
        RequestBody description =
                RequestBody.create(MediaType.parse("multipart/form-data"), recordDiscriptionValue);

        RequestBody created_by =
                RequestBody.create(MediaType.parse("multipart/form-data"), doctorName);
        RequestBody patientId1 =
                RequestBody.create(MediaType.parse("multipart/form-data"), patientId);
        RequestBody consultation_id1 =
                RequestBody.create(MediaType.parse("multipart/form-data"), consultation_id);


        if (thePath != null) {
            File uploadFile = new File(String.valueOf(thePath));

            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), uploadFile);

            // MultipartBody.Part is used to send also the actual file name
            MultipartBody.Part body = MultipartBody.Part.createFormData("record_files", uploadFile.getName(), requestFile);

            Call<PrescriptionAddModel> call = RetrofitClient
                    .getInstance()
                    .getApi()
                    .UploadPrescription("Token " + retrivedToken, record_type, description, created_by, patientId1, consultation_id1, body);

            call.enqueue(new Callback<PrescriptionAddModel>() {
                @Override
                public void onResponse(Call<PrescriptionAddModel> call, Response<PrescriptionAddModel> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "onResponse: " + response.isSuccessful());
                        startActivity(new Intent(getApplicationContext(), DashBoardActivity.class));
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<PrescriptionAddModel> call, Throwable t) {
                    Log.d(TAG, "onFailure: " + t.getMessage());
                }
            });

        } else {
            Toast.makeText(this, "please select file", Toast.LENGTH_SHORT).show();
        }


    }

    private Button btnSelectImage;
    private Bitmap bitmap;
    private File destination = null;
    private InputStream inputStreamImg;
    private String imgPath = null;
    private final int PICK_IMAGE_CAMERA = 1, PICK_IMAGE_GALLERY = 2;

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
                        filenameTextView.setText(uploadFile.getName());
                        filenameTextView.setVisibility(View.VISIBLE);
                    }
                }
            }


        }

        inputStreamImg = null;
        if (requestCode == PICK_IMAGE_CAMERA) {
            try {
                Uri selectedImage = data.getData();
                bitmap = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);

                Log.e("Activity", "Pick from Camera::>>> ");

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                destination = new File(Environment.getExternalStorageDirectory() + "/doctorplus", "IMG_" + timeStamp + ".jpg");
                FileOutputStream fo;

                File mFolder = new File(getFilesDir() + "/sample");
                File imgFile = new File(mFolder.getAbsolutePath() + "/someimage.png");
                if (!mFolder.exists()) {
                    mFolder.mkdir();
                }
                if (!imgFile.exists()) {
                    imgFile.createNewFile();
                }
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(imgFile);
                    bitmap.compress(Bitmap.CompressFormat.PNG,70, fos);
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

               /* try {
                    destination.mkdir();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
*/
                imgPath = imgFile.getAbsolutePath();
                selectedFile.setImageBitmap(bitmap);
                selectedFile.setVisibility(View.VISIBLE);
                thePath = imgFile.getAbsoluteFile();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == PICK_IMAGE_GALLERY) {

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
                        filenameTextView.setText(uploadFile.getName());
                        filenameTextView.setVisibility(View.VISIBLE);
                    }
                }
            }
        }

    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void enterImageDocuments(Uri fromFile, File compressedImageFile) {
        String imageString = fromFile.toString();
        for (String extesion : okFileExtensions) {
            if (imageString.toLowerCase().endsWith(extesion)) {
                filenameTextView.setVisibility(View.GONE);
                selectedFile.setVisibility(View.VISIBLE);
                Picasso.get().load(imageString).fit().into(selectedFile);
            }
        }
    }

}