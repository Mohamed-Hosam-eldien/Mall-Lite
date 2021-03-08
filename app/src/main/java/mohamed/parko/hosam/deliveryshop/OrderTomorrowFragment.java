package mohamed.parko.hosam.deliveryshop;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import mohamed.parko.hosam.deliveryshop.Adapter.OrderFoodAdapter;
import mohamed.parko.hosam.deliveryshop.Common.SpacesItemDecoration;
import mohamed.parko.hosam.deliveryshop.Database.cart.CartItem;
import mohamed.parko.hosam.deliveryshop.EventBus.ShowOrderFoodDetailsEvent;
import mohamed.parko.hosam.deliveryshop.databinding.FragmentOrderTomorrowBinding;


public class OrderTomorrowFragment extends Fragment {

    private FragmentOrderTomorrowBinding binding;


    public OrderTomorrowFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentOrderTomorrowBinding.bind(inflater.inflate(R.layout.fragment_order_tomorrow, container, false));
        return binding.getRoot();
    }

    private void init(ShowOrderFoodDetailsEvent event) {

        binding.recyclerTomorrow.setHasFixedSize(true);
        binding.recyclerTomorrow.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerTomorrow.addItemDecoration(new SpacesItemDecoration(8));

        List<CartItem> list = new ArrayList<>();


        if (event.getModel().getCartList().size() > 0) {

            for (CartItem cartItem : event.getModel().getCartList()) {
                if (cartItem.getFoodDeposit().isEmpty())
                    list.add(cartItem);
            }

            if (list.size() > 0) {

                OrderFoodAdapter adapter = new OrderFoodAdapter(getContext(), list);
                binding.recyclerTomorrow.setAdapter(adapter);
                binding.recyclerTomorrow.setVisibility(View.VISIBLE);
                binding.progress.setVisibility(View.GONE);
                binding.txtNotFound.setVisibility(View.GONE);


                binding.total.setText(new StringBuilder("مشتريات : ")
                        .append(event.getModel().getTotalTomorrow()).append(" ج.م"));
                binding.totalDelivery.setText(new StringBuilder("خدمة توصيل : ")
                        .append(event.getModel().getDeliverySalary()).append(" ج.م"));

                if (event.getModel().getState() == 5)
                    binding.layoutComplete.setVisibility(View.VISIBLE);

            } else {
                binding.recyclerTomorrow.setVisibility(View.GONE);
                binding.progress.setVisibility(View.GONE);
                binding.txtNotFound.setVisibility(View.VISIBLE);
            }

        }

    }


    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().removeAllStickyEvents();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void showListFood(ShowOrderFoodDetailsEvent event) {
        if (event != null) {
            init(event);
        }
    }


}