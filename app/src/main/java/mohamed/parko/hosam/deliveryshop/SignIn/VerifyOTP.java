package mohamed.parko.hosam.deliveryshop.SignIn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.paperdb.Paper;
import mohamed.parko.hosam.deliveryshop.Common.Common;
import mohamed.parko.hosam.deliveryshop.EventBus.SendVerfiyCodeEvent;
import mohamed.parko.hosam.deliveryshop.Home;
import mohamed.parko.hosam.deliveryshop.Model.UserModel;
import mohamed.parko.hosam.deliveryshop.R;
import mohamed.parko.hosam.deliveryshop.databinding.ActivityVerifyOTPBinding;

public class VerifyOTP extends AppCompatActivity {

    private ActivityVerifyOTPBinding binding;
    private SendVerfiyCodeEvent detailsEvent;
    private final Date date = new Date();
    private DatabaseReference userRef;

    private CountDownTimer countDownTimer;
    private long timeInMilliseconds;
    private String verifyId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerifyOTPBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userRef = FirebaseDatabase.getInstance().getReference(Common.UserReference);

        binding.btnConfirm.setOnClickListener(view -> checkVerifyCode());

        binding.txtResend.setOnClickListener(view -> resendCode());


        initTimer();

    }

    private void resendCode() {

        binding.txtResend.setVisibility(View.INVISIBLE);
        binding.txtCount.setVisibility(View.INVISIBLE);
        binding.progressResend.setVisibility(View.VISIBLE);
        countDownTimer.cancel();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.setLanguageCode("ar");

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(detailsEvent.getPhone())       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)  // Activity (for callback binding)
                .setCallbacks(
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                String code = phoneAuthCredential.getSmsCode();
                                if (code != null)
                                    verifyAutoCode(verifyId, code);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(VerifyOTP.this, "حدث خطأ ما برجاء إعادة المحاولة مرة أخرى", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                verifyId = verificationId;
                                binding.txtResend.setText("تم إرسال الرمز بنجاح");
                                binding.txtResend.setVisibility(View.VISIBLE);
                                binding.txtCount.setVisibility(View.INVISIBLE);
                                binding.progressResend.setVisibility(View.INVISIBLE);
                                binding.txtResend.setEnabled(false);
                                binding.txtResend.setTextColor(getResources().getColor(R.color.green));
                            }
                        }
                ).build();

        PhoneAuthProvider.verifyPhoneNumber(options);

    }

    private void initTimer() {
        timeInMilliseconds = 65000;

        countDownTimer = new CountDownTimer(timeInMilliseconds, 1000) {
            @Override
            public void onTick(long l) {

                timeInMilliseconds = l;
                updateTimer();

            }

            @Override
            public void onFinish() {

                binding.txtCount.setVisibility(View.GONE);
                binding.txtResend.setTextColor(getResources().getColor(R.color.red));
                binding.txtResend.setEnabled(true);

                countDownTimer.cancel();

            }
        }.start();

    }

    private void updateTimer() {

        int seconds = (int) timeInMilliseconds / 1000;
        binding.txtCount.setText(String.valueOf(seconds));

    }

    private void checkVerifyCode() {

        if (detailsEvent.getVerificationId() != null) {

            if (!binding.pinCode.getText().toString().isEmpty()) {

                binding.progress.setVisibility(View.VISIBLE);
                binding.btnConfirm.setVisibility(View.INVISIBLE);
                verifyCode(detailsEvent.getVerificationId());

            } else {
                Toast.makeText(this, "برجاء كتابة رمز التفعيل", Toast.LENGTH_SHORT).show();
            }

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().removeAllStickyEvents();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onVerifyCode(SendVerfiyCodeEvent event) {

        if (event.isVerify()) {
            binding.txtPhone.setText(event.getPhone());
            detailsEvent = event;
        }

    }

    private void verifyCode(String verificationId) {

        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationId, binding.pinCode.getText().toString());

        signInWithPhoneAuthCredential(phoneAuthCredential);

    }

    private void verifyAutoCode(String verificationId, String code) {

        binding.pinCode.setText(code);
        binding.progress.setVisibility(View.VISIBLE);
        binding.btnConfirm.setVisibility(View.INVISIBLE);

        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationId, code);

        signInWithPhoneAuthCredential(phoneAuthCredential);

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        binding.btnConfirm.setVisibility(View.INVISIBLE);
        binding.progress.setVisibility(View.VISIBLE);

        detailsEvent.getFirebaseAuth().signInWithCredential(credential)
                .addOnCompleteListener(this, (OnCompleteListener<AuthResult>) task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information

                        FirebaseUser user = task.getResult().getUser();
                        signInNewUser(user);

                    } else {
                        // Sign in failed, display a message and update the UI
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                            Toast.makeText(this, "كود التفعيل غير صحيح!", Toast.LENGTH_SHORT).show();
                            binding.btnConfirm.setVisibility(View.VISIBLE);
                            binding.progress.setVisibility(View.INVISIBLE);

                        }
                    }
                });
    }

    private void signInNewUser(FirebaseUser user) {

        UserModel userModel = new UserModel();
        userModel.setName(detailsEvent.getName());
        userModel.setPhone(user.getPhoneNumber());
        userModel.setSignInHistory(new SimpleDateFormat("yyyy-MM-dd - HH:mm", Locale.ENGLISH).format(date));
        userModel.setTotalPayment("0.00");
        userModel.setDepositPayment("0.00");
        userModel.setDeptPayment("0.00");
        userModel.setOrderDeliveryNumber("0");
        userModel.setOrderDepositNumber("0");
        userModel.setUid(user.getUid());

        userRef.child(user.getUid()).setValue(userModel)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        binding.progress.setVisibility(View.INVISIBLE);
                        binding.imgDone.setVisibility(View.VISIBLE);

                        new Handler().postDelayed(() -> gotoHomeActivity(userModel), 2000);

                    }
                }).addOnFailureListener(e -> {
            binding.btnConfirm.setVisibility(View.VISIBLE);
            binding.progress.setVisibility(View.GONE);
        });

    }

    private void gotoHomeActivity(UserModel userModel) {

        Paper.book().write("currentUser",userModel);

        FirebaseInstanceId.getInstance()
                .getInstanceId()
                .addOnFailureListener(e -> {

                    countDownTimer.cancel();
                    Common.currentUser = userModel;
                    Common.currentToken = "";
                    Paper.book().write("uid", Common.currentUser.getUid());
                    startActivity(new Intent(VerifyOTP.this, Home.class));
                    finish();

                })
                .addOnCompleteListener(task -> {

                    countDownTimer.cancel();
                    Common.currentUser = userModel;
                    Common.currentToken = task.getResult().getToken();
                    Common.updateToken(Common.currentToken, Common.currentUser.getUid());

                    Paper.book().write("uid", Common.currentUser.getUid());

                    startActivity(new Intent(VerifyOTP.this, Home.class));
                    finish();

                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }


}