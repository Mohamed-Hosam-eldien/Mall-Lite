package mohamed.parko.hosam.deliveryshop.EventBus;

import com.google.firebase.auth.FirebaseAuth;

public class SendVerfiyCodeEvent {

    private boolean isVerify;
    private String phone, name, verificationId;
    private FirebaseAuth firebaseAuth;


    public SendVerfiyCodeEvent(boolean isVerify, String phone, String name, String verificationId, FirebaseAuth firebaseAuth) {
        this.isVerify = isVerify;
        this.phone = phone;
        this.name = name;
        this.verificationId = verificationId;
        this.firebaseAuth = firebaseAuth;
    }


    public String getVerificationId() {
        return verificationId;
    }

    public void setVerificationId(String verificationId) {
        this.verificationId = verificationId;
    }

    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    public void setFirebaseAuth(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    public boolean isVerify() {
        return isVerify;
    }

    public void setVerify(boolean verify) {
        isVerify = verify;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
