package mohamed.parko.hosam.deliveryshop.ui.discount;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import mohamed.parko.hosam.deliveryshop.Adapter.FoodListAdapter;
import mohamed.parko.hosam.deliveryshop.Common.SpacesItemDecoration;
import mohamed.parko.hosam.deliveryshop.EventBus.FoodItemClick;
import mohamed.parko.hosam.deliveryshop.databinding.ActivityDiscountBinding;
import mohamed.parko.hosam.deliveryshop.ui.foodDetails.FoodDetails;


public class Discount extends AppCompatActivity {

    private ActivityDiscountBinding binding;
    private FoodListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDiscountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();

    }

    private void init() {

        binding.imgBack.setOnClickListener(view -> finish());

        binding.recyclerDiscount.setHasFixedSize(true);
        binding.recyclerDiscount.addItemDecoration(new SpacesItemDecoration(8));
        binding.recyclerDiscount.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL,false));

        DiscountViewModel foodViewModel = new ViewModelProvider(this).get(DiscountViewModel.class);

        foodViewModel.getMutableLiveData().observe(this, foodModels -> {

            if(foodModels.size() > 0) {
                adapter = new FoodListAdapter(this, foodModels, true);
                binding.recyclerDiscount.setAdapter(adapter);
                binding.recyclerDiscount.setVisibility(View.VISIBLE);
                binding.txtNotFound.setVisibility(View.GONE);

                new Handler().postDelayed(() -> {
                    adapter.showShimmer = false;
                    adapter.notifyDataSetChanged();
                },1500);

            } else {
                binding.recyclerDiscount.setVisibility(View.GONE);
                binding.txtNotFound.setVisibility(View.VISIBLE);
            }

        });

    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onFoodSelected(FoodItemClick event) {
        if(event.isSuccess())
            startActivity(new Intent(this, FoodDetails.class));
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().removeAllStickyEvents();
        if(EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

}