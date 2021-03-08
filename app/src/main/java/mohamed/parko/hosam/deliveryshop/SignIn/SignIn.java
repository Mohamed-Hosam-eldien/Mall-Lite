package mohamed.parko.hosam.deliveryshop.SignIn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

import mohamed.parko.hosam.deliveryshop.EventBus.SendVerfiyCodeEvent;
import mohamed.parko.hosam.deliveryshop.R;
import mohamed.parko.hosam.deliveryshop.databinding.ActivitySignInBinding;

public class SignIn extends AppCompatActivity {

    private ActivitySignInBinding binding;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.setLanguageCode("ar");

        binding.btnSendCode.setOnClickListener(view -> {

            if(binding.editUserName.getEditText().getText().toString().isEmpty()) {
                binding.editUserName.getEditText().setError("محتوى فارغ");
            } else if(binding.edtPhoneNumber.getEditText().getText().toString().isEmpty()) {
                binding.edtPhoneNumber.getEditText().setError("محتوى فارغ");
            } else {
                startVerifySystem();
            }

        });

    }

    private void startVerifySystem() {

        String phoneNumber = "+20" + binding.edtPhoneNumber.getEditText().getText().toString();
        String userName = binding.editUserName.getEditText().getText().toString();

        binding.btnSendCode.setVisibility(View.GONE);
        binding.progress.setVisibility(View.VISIBLE);

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNumber)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)  // Activity (for callback binding)
                .setCallbacks(
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                binding.progress.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                binding.progress.setVisibility(View.INVISIBLE);
                                binding.btnSendCode.setVisibility(View.VISIBLE);
                                Toast.makeText(SignIn.this, "حدث خطأ ما برجاء إعادة المحاولة مرة أخرى", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                binding.progress.setVisibility(View.INVISIBLE);
                                EventBus.getDefault().postSticky(new SendVerfiyCodeEvent(true, phoneNumber, userName, verificationId, firebaseAuth));
                                startActivity(new Intent(SignIn.this, VerifyOTP.class));
                                overridePendingTransition(R.anim.slide_in_right,
                                        R.anim.slide_out_left);
                                finish();
                            }
                        }
                ).build();


        PhoneAuthProvider.verifyPhoneNumber(options);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }


}