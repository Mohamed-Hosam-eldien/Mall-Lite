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
import mohamed.parko.hosam.deliveryshop.databinding.FragmentAfterSomeDaysBinding;

import mohamed.parko.hosam.deliveryshop.Adapter.OrderFoodAdapter;
import mohamed.parko.hosam.deliveryshop.Common.SpacesItemDecoration;
import mohamed.parko.hosam.deliveryshop.Database.cart.CartItem;
import mohamed.parko.hosam.deliveryshop.EventBus.ShowOrderFoodDetailsEvent;


public class AfterSomeDaysFragment extends Fragment {

    private FragmentAfterSomeDaysBinding binding;

    public AfterSomeDaysFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAfterSomeDaysBinding.bind(inflater.inflate(R.layout.fragment_after_some_days, container, false));

        return binding.getRoot();
    }

    private void init(ShowOrderFoodDetailsEvent event) {

        binding.recycler.setHasFixedSize(true);
        binding.recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recycler.addItemDecoration(new SpacesItemDecoration(8));

        binding.btnShowDetails.setOnClickListener(v -> {
            if (binding.expandableLayout.isExpanded()) {
                binding.btnShowDetails.setText("عرض التفاصيل");
                binding.btnShowDetails.setDrawableResource(R.drawable.ic_baseline_arrow_up_24);
            } else {
                binding.btnShowDetails.setText("إخفاء التفاصيل");
                binding.btnShowDetails.setDrawableResource(R.drawable.ic_baseline_arrow_down_24);
            }
            binding.expandableLayout.toggle();
        });

        List<CartItem> list = new ArrayList<>();

        if(event != null && event.getModel().getCartList().size() > 0) {

            for(CartItem cartItem : event.getModel().getCartList()) {
                if(!cartItem.getFoodDeposit().isEmpty())
                    list.add(cartItem);
            }


            if(list.size() > 0) {
                OrderFoodAdapter adapter = new OrderFoodAdapter(getContext(), list);
                binding.recycler.setAdapter(adapter);
                binding.recycler.setVisibility(View.VISIBLE);
                binding.progress.setVisibility(View.GONE);
                binding.txtNotFound.setVisibility(View.GONE);
                binding.exLayout.setVisibility(View.VISIBLE);

                binding.layoutDetails.setVisibility(View.VISIBLE);

                binding.total.setText(new StringBuilder("إجمالي الطلب : ")
                        .append(event.getModel().getTotalAfterSomeDays()).append(" ج.م"));
                binding.totalDelivery.setText(new StringBuilder("خدمة توصيل : ")
                        .append(event.getModel().getDeliverySalary()).append(" ج.م"));
                if(!event.getModel().getHistoryArrivedOrder().isEmpty())
                    binding.history.setText(new StringBuilder("تاريخ توصيل الطلب : ")
                            .append(event.getModel().getHistoryArrivedOrder()));
                else
                    binding.history.setText(new StringBuilder("تاريخ توصيل الطلب : ")
                            .append("سيتم التحديد لاحقاً.."));

                if(!event.getModel().getMoneyPaid().isEmpty())
                    binding.depositPaid.setText(event.getModel().getMoneyPaid());
                else
                    binding.depositPaid.setText("0");

                if(!event.getModel().getMoneyRemaining().isEmpty())
                    binding.depositRemaining.setText(new StringBuilder("- ").append(event.getModel().getMoneyRemaining()));
                else
                    binding.depositRemaining.setText(new StringBuilder("-").append(event.getModel().getTotalAfterSomeDays()));

                if (event.getModel().getArrived() != null) {
                    binding.layoutComplete.setVisibility(View.VISIBLE);
                    binding.layoutRemaining.setVisibility(View.GONE);
                    binding.depositPaid.setText(event.getModel().getTotalAfterSomeDays());
                }

            } else {
                binding.recycler.setVisibility(View.GONE);
                binding.progress.setVisibility(View.GONE);
                binding.layoutDetails.setVisibility(View.GONE);
                binding.txtNotFound.setVisibility(View.VISIBLE);
                binding.exLayout.setVisibility(View.GONE);
            }

        }

    }


    @Override
    public void onStart() {
        super.onStart();
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().removeAllStickyEvents();
        if(EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void showListFood(ShowOrderFoodDetailsEvent event){
        if(event != null) {
            init(event);
        }
    }

}