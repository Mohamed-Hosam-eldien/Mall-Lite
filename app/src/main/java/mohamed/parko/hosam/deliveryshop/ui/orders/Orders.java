package mohamed.parko.hosam.deliveryshop.ui.orders;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;

import dmax.dialog.SpotsDialog;
import mohamed.parko.hosam.deliveryshop.Adapter.OrdersAdapter;
import mohamed.parko.hosam.deliveryshop.databinding.ActivityOrdersBinding;


public class Orders extends AppCompatActivity {

    private ActivityOrdersBinding binding;
    private AlertDialog waitingDialog;
    private OrdersViewModel orderViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.swip.post(this::init);

        binding.swip.setOnRefreshListener(() -> orderViewModel.loadUserOrder());

    }

    private void init() {

        waitingDialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        waitingDialog.setMessage("برجاء الإنتظار...");
        waitingDialog.show();

        binding.imgBack.setOnClickListener(view -> finish());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        binding.recyclerViewOrders.setLayoutManager(linearLayoutManager);
        binding.recyclerViewOrders.setHasFixedSize(true);
        binding.recyclerViewOrders.addItemDecoration(new DividerItemDecoration(this, linearLayoutManager.getOrientation()));

        orderViewModel = new ViewModelProvider(this).get(OrdersViewModel.class);

        orderViewModel.getOrderModelMutableLiveData().observe(this, orderModels -> {

            if(orderModels.size() > 0) {
                OrdersAdapter adapter = new OrdersAdapter(orderModels, this);
                binding.recyclerViewOrders.setAdapter(adapter);
                binding.recyclerViewOrders.setVisibility(View.VISIBLE);
                binding.txtAdd2.setVisibility(View.GONE);
            } else {
                binding.recyclerViewOrders.setVisibility(View.GONE);
                binding.txtAdd2.setVisibility(View.VISIBLE);
            }

            binding.swip.setRefreshing(false);
            waitingDialog.dismiss();

        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

}