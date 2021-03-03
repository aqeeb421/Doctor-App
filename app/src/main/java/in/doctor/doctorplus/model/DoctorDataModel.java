package in.doctor.doctorplus.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DoctorDataModel {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("doctor")
    @Expose
    private Doctor doctor;
    @SerializedName("consultations_count")
    @Expose
    private Integer consultationsCount;
    @SerializedName("total_amt")
    @Expose
    private Integer totalAmt;
    @SerializedName("comm_amt")
    @Expose
    private Integer commAmt;
    @SerializedName("amount_payable")
    @Expose
    private Integer amountPayable;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("last_modied")
    @Expose
    private String lastModied;
    @SerializedName("balance")
    @Expose
    private Double balance;
    @SerializedName("paid_amount")
    @Expose
    private Double paidAmount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Integer getConsultationsCount() {
        return consultationsCount;
    }

    public void setConsultationsCount(Integer consultationsCount) {
        this.consultationsCount = consultationsCount;
    }

    public Integer getTotalAmt() {
        return totalAmt;
    }

    public void setTotalAmt(Integer totalAmt) {
        this.totalAmt = totalAmt;
    }

    public Integer getCommAmt() {
        return commAmt;
    }

    public void setCommAmt(Integer commAmt) {
        this.commAmt = commAmt;
    }

    public Integer getAmountPayable() {
        return amountPayable;
    }

    public void setAmountPayable(Integer amountPayable) {
        this.amountPayable = amountPayable;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getLastModied() {
        return lastModied;
    }

    public void setLastModied(String lastModied) {
        this.lastModied = lastModied;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(Double paidAmount) {
        this.paidAmount = paidAmount;
    }
}
