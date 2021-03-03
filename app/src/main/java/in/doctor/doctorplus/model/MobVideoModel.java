package in.doctor.doctorplus.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MobVideoModel {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("Call_from")
    @Expose
    private Integer callFrom;
    @SerializedName("Call_for")
    @Expose
    private Integer callFor;
    @SerializedName("session_id")
    @Expose
    private String sessionId;
    @SerializedName("user_token")
    @Expose
    private String userToken;
    @SerializedName("caller_name")
    @Expose
    private String callerName;
    @SerializedName("caller_ID")
    @Expose
    private Integer callerID;
    @SerializedName("appoinment_id")
    @Expose
    private Integer appoinmentId;
    @SerializedName("is_answered")
    @Expose
    private Boolean isAnswered;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCallFrom() {
        return callFrom;
    }

    public void setCallFrom(Integer callFrom) {
        this.callFrom = callFrom;
    }

    public Integer getCallFor() {
        return callFor;
    }

    public void setCallFor(Integer callFor) {
        this.callFor = callFor;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getCallerName() {
        return callerName;
    }

    public void setCallerName(String callerName) {
        this.callerName = callerName;
    }

    public Integer getCallerID() {
        return callerID;
    }

    public void setCallerID(Integer callerID) {
        this.callerID = callerID;
    }

    public Integer getAppoinmentId() {
        return appoinmentId;
    }

    public void setAppoinmentId(Integer appoinmentId) {
        this.appoinmentId = appoinmentId;
    }

    public Boolean getIsAnswered() {
        return isAnswered;
    }

    public void setIsAnswered(Boolean isAnswered) {
        this.isAnswered = isAnswered;
    }

}