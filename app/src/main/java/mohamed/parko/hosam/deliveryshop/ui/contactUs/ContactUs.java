package mohamed.parko.hosam.deliveryshop.ui.contactUs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import mohamed.parko.hosam.deliveryshop.EventBus.OpenChatFromResumeEvent;
import mohamed.parko.hosam.deliveryshop.databinding.ActivityContactUsBinding;
import mohamed.parko.hosam.deliveryshop.ui.chat.Chat;

public class ContactUs extends AppCompatActivity {

    private ActivityContactUsBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactUsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.imgBack.setOnClickListener(view -> {
            EventBus.getDefault().postSticky(new OpenChatFromResumeEvent(true));
            finish();
        });


        binding.imgFacebook.setOnClickListener(view -> {
            Toast.makeText(this, "facebook", Toast.LENGTH_SHORT).show();
        });

        binding.imgWhatsApp.setOnClickListener(view -> {
            Toast.makeText(this, "whats", Toast.LENGTH_SHORT).show();
        });

        binding.imgLiveChat.setOnClickListener(view -> {
            startActivity(new Intent(this, Chat.class));
        });

        binding.imgCallPhone.setOnClickListener(view -> {
            Toast.makeText(this, "callPhone", Toast.LENGTH_SHORT).show();
        });

        binding.imgInstagram.setOnClickListener(view -> {
            Toast.makeText(this, "instagram", Toast.LENGTH_SHORT).show();
        });

        binding.imgYoutube.setOnClickListener(view -> {
            Toast.makeText(this, "youtube", Toast.LENGTH_SHORT).show();
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        EventBus.getDefault().postSticky(new OpenChatFromResumeEvent(true));
    }

}