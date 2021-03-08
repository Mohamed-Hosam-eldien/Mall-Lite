package mohamed.parko.hosam.deliveryshop.ui.home;

import android.content.Intent;
import android.os.Bundle;


import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import io.reactivex.disposables.CompositeDisposable;
import mohamed.parko.hosam.deliveryshop.Adapter.CategoryAdapter;
import mohamed.parko.hosam.deliveryshop.Adapter.LooperAdapter;
import mohamed.parko.hosam.deliveryshop.Adapter.PopularAdapter;
import mohamed.parko.hosam.deliveryshop.Common.Common;
import mohamed.parko.hosam.deliveryshop.Common.SpacesItemDecoration;
import mohamed.parko.hosam.deliveryshop.Database.favorite.FavoriteDataSource;
import mohamed.parko.hosam.deliveryshop.Database.favorite.FavoriteDatabase;
import mohamed.parko.hosam.deliveryshop.Database.favorite.LocalFavoriteDatabase;
import mohamed.parko.hosam.deliveryshop.EventBus.CategoryClick;
import mohamed.parko.hosam.deliveryshop.R;
import mohamed.parko.hosam.deliveryshop.databinding.FragmentHomeBinding;
import mohamed.parko.hosam.deliveryshop.ui.addresses.Addresses;
import mohamed.parko.hosam.deliveryshop.ui.discount.Discount;
import mohamed.parko.hosam.deliveryshop.ui.foodList.FoodList;
import mohamed.parko.hosam.deliveryshop.ui.looper.LooperViewModel;
import mohamed.parko.hosam.deliveryshop.ui.orders.Orders;
import mohamed.parko.hosam.deliveryshop.ui.personal.ProfilePage;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private CategoryAdapter adapter;
    private CompositeDisposable compositeDisposable;
    private FavoriteDataSource favoriteDataSource;
    private PopularAdapter popularAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        binding = mohamed.parko.hosam.deliveryshop.databinding.FragmentHomeBinding.bind(view);
        binding.getRoot();

        init();

        return view;

    }

    private void init() {

        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        favoriteDataSource = new LocalFavoriteDatabase(FavoriteDatabase.getInstance(getActivity()).favoriteDao());
        compositeDisposable = new CompositeDisposable();


        LinearLayoutManager layoutManagerPop = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL,false);
        layoutManagerPop.setStackFromEnd(true);
        binding.recyclerPopular.setHasFixedSize(true);
        binding.recyclerPopular.addItemDecoration(new SpacesItemDecoration(8));
        binding.recyclerPopular.setLayoutManager(layoutManagerPop);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        layoutManager.setOrientation(RecyclerView.VERTICAL);

        binding.recyclerCategory.addItemDecoration(new SpacesItemDecoration(8));
        binding.recyclerCategory.setNestedScrollingEnabled(false);

        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(adapter != null) {

                    switch (adapter.getItemViewType(position)) {
                        case Common.DEFAULT_COMMON_COUNT: return 1;
                        case Common.FULL_WIDTH_COLUMN : return 2;
                        default: return -1;
                    }

                }
                return -1;
            }
        });

        binding.recyclerCategory.setLayoutManager(layoutManager);


        new ViewModelProvider(HomeFragment.this).get(LooperViewModel.class).getLooperList()
                .observe(getViewLifecycleOwner(), LooperModel -> {
            LooperAdapter adapter = new LooperAdapter(getContext(), LooperModel, true);
            binding.viewPager.setAdapter(adapter);
        });


        homeViewModel.getPopularList().observe(getViewLifecycleOwner(), popularModelList -> {

            popularAdapter = new PopularAdapter(getContext(), popularModelList,favoriteDataSource,compositeDisposable );
            binding.recyclerPopular.setAdapter(popularAdapter);

            new Handler().postDelayed(() -> {
                popularAdapter.showShimmer = false;
                popularAdapter.notifyDataSetChanged();
            },1200);

        });

        homeViewModel.getCategoryList().observe(getViewLifecycleOwner(), categoryModels -> {
            adapter = new CategoryAdapter(getContext(), categoryModels);
            binding.recyclerCategory.setAdapter(adapter);

            new Handler().postDelayed(() -> {
                adapter.showShimmer = false;
                adapter.notifyDataSetChanged();
            },1200);

        });


        binding.layoutAddress.setOnClickListener(view ->
                getContext().startActivity(new Intent(getContext(), Addresses.class)));
        binding.imgAddress.setOnClickListener(view ->
                getContext().startActivity(new Intent(getContext(), Addresses.class)));
        binding.layoutDiscount.setOnClickListener(view ->
                getContext().startActivity(new Intent(getContext(), Discount.class)));
        binding.imgDiscount.setOnClickListener(view ->
                getContext().startActivity(new Intent(getContext(), Discount.class)));
        binding.layoutOrders.setOnClickListener(view ->
                getContext().startActivity(new Intent(getContext(), Orders.class)));
        binding.imgOrders.setOnClickListener(view ->
                getContext().startActivity(new Intent(getContext(), Orders.class)));
        binding.layoutPersonalPage.setOnClickListener(view ->
                getContext().startActivity(new Intent(getContext(), ProfilePage.class)));
        binding.imgPersonal.setOnClickListener(view ->
                getContext().startActivity(new Intent(getContext(), ProfilePage.class)));


    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().removeAllStickyEvents();
        EventBus.getDefault().unregister(this);
        binding.viewPager.pauseAutoScroll();
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.viewPager.resumeAutoScroll();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onCategorySelected(CategoryClick event) {
        if(event.isSuccess())
            startActivity(new Intent(getContext(), FoodList.class));
    }


}