package in.doctor.doctorplus.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AppointmentModel {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("doctor")
    @Expose
    private DoctorInfoModel doctor;
    @SerializedName("patient")
    @Expose
    private Patient patient;
    @SerializedName("paid_amount")
    @Expose
    private String paidAmount;
    @SerializedName("patient_name")
    @Expose
    private String patientName;
    @SerializedName("appointment_date")
    @Expose
    private String appointmentDate;
    @SerializedName("appointment_time")
    @Expose
    private String appointmentTime;
    @SerializedName("Description")
    @Expose
    private String description;
    @SerializedName("patient_login_status")
    @Expose
    private Boolean patientLoginStatus;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public DoctorInfoModel getDoctor() {
        return doctor;
    }

    public void setDoctor(DoctorInfoModel doctor) {
        this.doctor = doctor;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public String getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(String paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getPatientLoginStatus() {
        return patientLoginStatus;
    }

    public void setPatientLoginStatus(Boolean patientLoginStatus) {
        this.patientLoginStatus = patientLoginStatus;
    }

}
