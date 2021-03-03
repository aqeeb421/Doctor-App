package in.doctor.doctorplus.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import in.doctor.doctorplus.model.DoctorInfoModel;
import in.doctor.doctorplus.model.Patient;

public class ConsultationModel {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("doctor_id")
    @Expose
    private DoctorInfoModel doctorId;
    @SerializedName("patient")
    @Expose
    private Patient patient;
    @SerializedName("patient_name")
    @Expose
    private String patientName;
    @SerializedName("patient_age")
    @Expose
    private String patientAge;
    @SerializedName("patient_gender")
    @Expose
    private Object patientGender;
    @SerializedName("patient_pic")
    @Expose
    private String patientPic;
    @SerializedName("consultation_date_time")
    @Expose
    private String consultationDateTime;
    @SerializedName("message")
    @Expose
    private Object message;
    @SerializedName("inv_number")
    @Expose
    private String invNumber;
    @SerializedName("video_audio_rating")
    @Expose
    private Integer videoAudioRating;
    @SerializedName("consultation_rating")
    @Expose
    private Integer consultationRating;
    @SerializedName("overall_rating")
    @Expose
    private Integer overallRating;
    @SerializedName("consultation_amt")
    @Expose
    private String consultationAmt;
    @SerializedName("patient_number")
    @Expose
    private String patientNumber;
    @SerializedName("problem")
    @Expose
    private String problem;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public DoctorInfoModel getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(DoctorInfoModel doctorId) {
        this.doctorId = doctorId;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientAge() {
        return patientAge;
    }

    public void setPatientAge(String patientAge) {
        this.patientAge = patientAge;
    }

    public Object getPatientGender() {
        return patientGender;
    }

    public void setPatientGender(Object patientGender) {
        this.patientGender = patientGender;
    }

    public String getPatientPic() {
        return patientPic;
    }

    public void setPatientPic(String patientPic) {
        this.patientPic = patientPic;
    }

    public String getConsultationDateTime() {
        return consultationDateTime;
    }

    public void setConsultationDateTime(String consultationDateTime) {
        this.consultationDateTime = consultationDateTime;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public String getInvNumber() {
        return invNumber;
    }

    public void setInvNumber(String invNumber) {
        this.invNumber = invNumber;
    }

    public Integer getVideoAudioRating() {
        return videoAudioRating;
    }

    public void setVideoAudioRating(Integer videoAudioRating) {
        this.videoAudioRating = videoAudioRating;
    }

    public Integer getConsultationRating() {
        return consultationRating;
    }

    public void setConsultationRating(Integer consultationRating) {
        this.consultationRating = consultationRating;
    }

    public Integer getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(Integer overallRating) {
        this.overallRating = overallRating;
    }

    public String getConsultationAmt() {
        return consultationAmt;
    }

    public void setConsultationAmt(String consultationAmt) {
        this.consultationAmt = consultationAmt;
    }

    public String getPatientNumber() {
        return patientNumber;
    }

    public void setPatientNumber(String patientNumber) {
        this.patientNumber = patientNumber;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }
}
