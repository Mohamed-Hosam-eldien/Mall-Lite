package mohamed.parko.hosam.deliveryshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import io.paperdb.Paper;
import mohamed.parko.hosam.deliveryshop.Common.Common;
import mohamed.parko.hosam.deliveryshop.Model.UserModel;
import mohamed.parko.hosam.deliveryshop.SignIn.SignIn;
import mohamed.parko.hosam.deliveryshop.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private String uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Paper.init(this);
        uid = Paper.book().read("uid");

        if (uid != null && !uid.isEmpty()) {
            binding.btnSignIn.setVisibility(View.GONE);
            binding.waitingProg.setVisibility(View.VISIBLE);

            new Handler().postDelayed(this::goToHomeActivity, 2000);

        } else {
            binding.btnSignIn.setVisibility(View.VISIBLE);
            binding.waitingProg.setVisibility(View.GONE);
        }


        binding.btnSignIn.setOnClickListener(view -> {

            startActivity(new Intent(this, SignIn.class));
            overridePendingTransition(R.anim.slide_in_right,
                    R.anim.slide_out_left);
            finish();

        });

    }

    private void goToHomeActivity() {

        FirebaseDatabase.getInstance()
                .getReference(Common.UserReference)
                .child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        UserModel currentUser = snapshot.getValue(UserModel.class);
                        Paper.book().write("currentUser",currentUser);

                        FirebaseInstanceId.getInstance()
                                .getInstanceId()
                                .addOnFailureListener(e -> {

                                    Common.currentUser = currentUser;
                                    Common.currentToken = "";
                                    Intent intent = new Intent(MainActivity.this, Home.class);
                                    intent.putExtra(Common.IS_OPEN_ORDER_ACTIVITY, getIntent().getBooleanExtra(Common.IS_OPEN_ORDER_ACTIVITY, false));
                                    intent.putExtra(Common.IS_OPEN_MessageActivity, getIntent().getBooleanExtra(Common.IS_OPEN_MessageActivity, false));
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_in_right,
                                            R.anim.slide_out_left);
                                    finish();

                                }).addOnCompleteListener(task -> {

                                    Common.currentUser = currentUser;
                                    Common.currentToken = task.getResult().getToken();
                                    Common.updateToken(Common.currentToken, Common.currentUser.getUid());

                                    Paper.book().write("uid", Common.currentUser.getUid());

                                    Intent intent = new Intent(MainActivity.this, Home.class);
                                    intent.putExtra(Common.IS_OPEN_ORDER_ACTIVITY, getIntent().getBooleanExtra(Common.IS_OPEN_ORDER_ACTIVITY, false));
                                    intent.putExtra(Common.IS_OPEN_MessageActivity, getIntent().getBooleanExtra(Common.IS_OPEN_MessageActivity, false));
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_in_right,
                                            R.anim.slide_out_left);
                                    finish();

                                });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        binding.waitingProg.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, "حدث خطأ ما برجاء المحاولة مرة أخرى", Toast.LENGTH_SHORT).show();

                    }
                });

    }

}