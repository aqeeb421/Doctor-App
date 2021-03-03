package in.doctor.doctorplus.utils;

public class Constants {
    public static final String AUDIO_CALL = "Audio Call";
    public static String userType_Patient = "P";
    public static String userType_Doctor = "D";

    private static String token;
    private static int doc_id;
    private static String call_type = "Video";
    private static String doctor_name = "";
    private static double rating = 0;
    private static String MouFile="";

    public static String getMouFile() {
        return MouFile;
    }

    public static void setMouFile(String mouFile) {
        MouFile = mouFile;
    }

    public static double getRating() {
        return rating;
    }

    public static void setRating(double rating) {
        Constants.rating = rating;
    }

    public static String getDoctor_name() {
        return doctor_name;
    }

    public static void setDoctor_name(String doctor_name) {
        Constants.doctor_name = doctor_name;
    }

    public static String getCall_type() {
        return call_type;
    }

    public static void setCall_type(String call_type) {
        Constants.call_type = call_type;
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        Constants.token = token;
    }

    public static int getDoc_id() {
        return doc_id;
    }

    public static void setDoc_id(int doc_id) {
        Constants.doc_id = doc_id;
    }
}
