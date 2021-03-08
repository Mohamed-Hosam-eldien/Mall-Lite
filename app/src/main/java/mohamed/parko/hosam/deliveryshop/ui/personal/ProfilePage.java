package mohamed.parko.hosam.deliveryshop.ui.personal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;

import dmax.dialog.SpotsDialog;
import mohamed.parko.hosam.deliveryshop.Model.UserModel;
import mohamed.parko.hosam.deliveryshop.databinding.ActivityProfilePageBinding;


public class ProfilePage extends AppCompatActivity {

    private ActivityProfilePageBinding binding;
    private AlertDialog waitingDialog;
    private ProfileViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfilePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        waitingDialog = new SpotsDialog.Builder().setContext(this).setMessage("برجاء الانتظار..").build();
        waitingDialog.show();

        binding.swip.post(this::init);

        binding.swip.setOnRefreshListener(() -> viewModel.getCurrentUserDetails());

    }


    private void init() {

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        viewModel.getMutableLiveData().observe(this, this::setUserDetails);

        binding.imgBack.setOnClickListener(view -> finish());

    }

    private void setUserDetails(UserModel userModel) {

        binding.orderDeliveryNumber.setText(userModel.getOrderDeliveryNumber());
        binding.orderDepositNumber.setText(userModel.getOrderDepositNumber());
        binding.txtDeposit.setText(userModel.getDepositPayment());
        binding.txtDeptPayment.setText(userModel.getDeptPayment());
        binding.txtTotalPayment.setText(userModel.getTotalPayment());

        binding.profileDate.setText(new StringBuilder("مشترك منذ :  ").append(userModel.getSignInHistory()));
        binding.profileName.setText(userModel.getName());
        binding.profileNumber.setText(userModel.getPhone());
        binding.swip.setRefreshing(false);

        new Handler().postDelayed(() -> waitingDialog.dismiss(),500);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }


}