package mohamed.parko.hosam.deliveryshop.ui.foodList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import mohamed.parko.hosam.deliveryshop.Adapter.FoodListAdapter;
import mohamed.parko.hosam.deliveryshop.Common.Common;
import mohamed.parko.hosam.deliveryshop.Common.SpacesItemDecoration;
import mohamed.parko.hosam.deliveryshop.EventBus.FoodItemClick;

import mohamed.parko.hosam.deliveryshop.EventBus.RefreshFoodListEvent;
import mohamed.parko.hosam.deliveryshop.MainActivity;
import mohamed.parko.hosam.deliveryshop.databinding.ActivityFoodListBinding;
import mohamed.parko.hosam.deliveryshop.ui.foodDetails.FoodDetails;


public class FoodList extends AppCompatActivity {

    private ActivityFoodListBinding binding;
    private FoodViewModel foodViewModel;
    private FoodListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFoodListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if(Common.Category_Selected != null) {
            init();
            foodViewModel = new ViewModelProvider(this).get(FoodViewModel.class);
            foodViewModel.getFoodList().observe(this, foodModels -> {
                adapter = new FoodListAdapter(this, foodModels, false);
                binding.recyclerFoodList.setAdapter(adapter);
                new Handler().postDelayed(() -> {
                    adapter.showShimmer = false;
                    adapter.notifyDataSetChanged();
                },2000);
            });
        } else
            startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));

    }

    private void init() {

        binding.imgBack.setOnClickListener(view -> {
            EventBus.getDefault().removeAllStickyEvents();
            finish();
        });

        binding.txtCategoryName.setText(Common.Category_Selected.getName());
        binding.recyclerFoodList.setHasFixedSize(true);
        binding.recyclerFoodList.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerFoodList.addItemDecoration(new SpacesItemDecoration(8));

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Common.Category_Selected == null)
            startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
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

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onFoodSelected(FoodItemClick event) {
        if(event.isSuccess())
            startActivity(new Intent(this, FoodDetails.class).putExtra("fromPop", "no"));
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onFoodListRefresh(RefreshFoodListEvent event) {
        if(event.isRefresh()) {

            Common.Category_Selected.getFoods().set(Integer.parseInt(event.getFoodModel().getKey()),event.getFoodModel());
            foodViewModel.setFoodList(Common.Category_Selected.getFoods());

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        EventBus.getDefault().removeAllStickyEvents();
    }

}