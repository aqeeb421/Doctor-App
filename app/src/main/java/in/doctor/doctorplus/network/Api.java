package in.doctor.doctorplus.network;

import java.util.List;

import in.doctor.doctorplus.model.AppointmentModel;
import in.doctor.doctorplus.model.CheckPhoneModel;
import in.doctor.doctorplus.model.ConsultationModel;
import in.doctor.doctorplus.model.DoctorDataModel;
import in.doctor.doctorplus.model.DoctorTimingModel;
import in.doctor.doctorplus.model.DoctorTimingsModel;
import in.doctor.doctorplus.model.LoginDetailModel;
import in.doctor.doctorplus.model.MobAnswerCallModel;
import in.doctor.doctorplus.model.MobCallDoctorModel;
import in.doctor.doctorplus.model.MobVideoModel;
import in.doctor.doctorplus.model.PasswordChange;
import in.doctor.doctorplus.model.PrescriptionAddModel;
import in.doctor.doctorplus.model.PrescriptionListModel;
import in.doctor.doctorplus.model.User;
import in.doctor.doctorplus.model.RegisterModel;
import in.doctor.doctorplus.model.SpecialitiesModel;
import in.doctor.doctorplus.model.VerifyOTPModel;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api {

    @GET("getLoggedInDoctor")
    Call<LoginDetailModel> GetLoginDetails(@Header("Authorization") String authToken);

    @GET("DoctorTimings/")
    Call<List<DoctorTimingModel>> GetDoctorTimings(@Header("Authorization") String authToken);

    @FormUrlEncoded
    @POST("auth/login")
    Call<RegisterModel> SendLogInUser(
            @Field("username") String username,
            @Field("password") String password,
            @Field("user_type") String userType
    );

    @FormUrlEncoded
    @POST("patient_check_phone/")
    Call<CheckPhoneModel> CheckPhoneNumber(
            @Field("ph_no") String ph_no
    );

    @GET("https://teleduce.in/sendsms/")
    Call<String> getSMS(
            @Query("key") String key,
            @Query("text") String text,
            @Query("route") String route,
            @Query("from") String from,
            @Query("to") String to
    );

    @GET("https://teleduce.in/sendsms/")
    Call<String> getSMSOTP(
            @Query("key") String key,
            @Query("text") String text,
            @Query("route") String route,
            @Query("from") String from,
            @Query("to") String to,
            @Query("otp") String otp,
            @Query("otp_length") int otp_length
    );

    @GET("https://teleduce.corefactors.in/validate-otp/a224db72-cafb-4cce-93ab-3d7f950c92e2/")
    Call<VerifyOTPModel> VerifyOTP(
            @Query("mobile") String mobile,
            @Query("otp") String otp
    );

    @FormUrlEncoded
    @POST("reset_forget_password/")
    Call<PasswordChange> ChangePwd(
            @Field("password") String password,
            @Field("id") String id
    );

    @GET("todaysAppoinment/")
    Call<List<AppointmentModel>> GetTodayAppointment(@Header("Authorization") String authToken);

    @GET("upComingAppoinment/")
    Call<List<AppointmentModel>> GetUpcomingAppointment(@Header("Authorization") String authToken);

    @GET("previousAppoinment/")
    Call<List<AppointmentModel>> GetPreviousAppointment(@Header("Authorization") String authToken);

    @GET("doctor_dataView/")
    Call<List<DoctorDataModel>> GetDoctorData(@Header("Authorization") String authToken);

    @POST("DoctorLogout")
    Call<ResponseBody> DoLogout(@Header("Authorization") String authToken);

    @POST("MobAnswerCall")
    Call<MobAnswerCallModel> MobAnswerCall(
            @Query("id") int id
    );

    @GET("MobVedioChatOparetion/")
    Call<List<MobVideoModel>> GetMobVideoOperation(@Header("Authorization") String authToken, @Query("user") int user);


    @FormUrlEncoded
    @POST("DoctorTimings/")
    Call<DoctorTimingsModel> SendDoctorTimings(
            @Header("Authorization") String authToken,
            @Field("day") String day,
            @Field("from_time") String from_time,
            @Field("to_time") String to_time
    );


    @GET("specialist_types/")
    Call<List<SpecialitiesModel>> GetSpecialist();

    @GET("getDoctorConsultations")
    Call<List<ConsultationModel>> GetDoctorConsultations(@Header("Authorization") String authToken);

    @GET("DoctorPrescription/")
    Call<List<PrescriptionListModel>> GetDoctorPrescription(@Header("Authorization") String authToken);

    @FormUrlEncoded
    @POST("changePassword")
    Call<PasswordChange> SendChangePassword(
            @Header("Authorization") String authToken,
            @Field("old_password") String old_password,
            @Field("new_password") String new_password,
            @Field("confirm_new_password") String confirm_new_password
    );

    @Multipart
    @PUT("DoctorUpdateProfile/{id}/")
    Call<PasswordChange> DoctorUpdateProfile(
            @Header("Authorization") String authToken,
            @Path("id") String id,
            @Part("username") RequestBody username,
            @Part("full_name") RequestBody full_name,
            @Part("email") RequestBody email,
            @Part("phone_number") RequestBody phone_number,
            @Part("about") RequestBody about,
            @Part MultipartBody.Part record_files

    );

    @DELETE("DoctorPrescriptionDelete/{id}/")
    Call<Void> DeletePrescriptionRecordDetails(@Header("Authorization") String authToken, @Path("id") int itemId);

    @DELETE("MobVedioChatOparetion/{id}/")
    Call<Void> EndCall(@Header("Authorization") String authToken, @Path("id") int itemId);

    @DELETE("DoctorTimings/{id}/")
    Call<Void> DeleteAvail(@Header("Authorization") String authToken, @Path("id") int itemId);

    @POST("MobCallDoctor")
    Call<MobCallDoctorModel> fetchSessionConnectionData(
            @Header("Authorization") String authToken,
            @Query("doctor") String doctor,
            @Query("app_id") String app_id
    );

    @Multipart
    @POST("DoctorPrescription/")
    Call<PrescriptionAddModel> UploadPrescription(
            @Header("Authorization") String authToken,
            @Part("record_type") RequestBody record_type,
            @Part("description") RequestBody description,
            @Part("created_by") RequestBody created_by,
            @Part("patient") RequestBody patient,
            @Part("consultation_id") RequestBody consultation_id,
            @Part MultipartBody.Part record_files
    );

    @Multipart
    @PUT("DoctorPrescription/{id}/")
    Call<PrescriptionAddModel> UpdatePrescription(
            @Header("Authorization") String authToken,
            @Path("id") String id,
            @Part("record_type") RequestBody record_type,
            @Part("description") RequestBody description,
            @Part("created_by") RequestBody created_by,
            @Part("patient") RequestBody patient,
            @Part("consultation_id") RequestBody consultation_id,
            @Part MultipartBody.Part record_files
    );


}
