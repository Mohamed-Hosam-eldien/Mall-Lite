package mohamed.parko.hosam.deliveryshop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import android.os.Bundle;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import mohamed.parko.hosam.deliveryshop.Database.cart.CartItem;
import mohamed.parko.hosam.deliveryshop.EventBus.ShowOrderFoodDetailsEvent;
import mohamed.parko.hosam.deliveryshop.EventBus.ShowOrderFoodsEvent;
import mohamed.parko.hosam.deliveryshop.databinding.ActivityOrderFoodsBinding;

public class OrderFoods extends AppCompatActivity {

    private ActivityOrderFoodsBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderFoodsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }


    private void init(List<CartItem> cartList, String date) {
        int numAfterSomeDays = 0;

        for(CartItem cartItem : cartList) {
            if(!cartItem.getFoodDeposit().isEmpty())
                numAfterSomeDays +=1 ;
        }

        OrderTomorrowFragment tomorrowFragment = new OrderTomorrowFragment();
        AfterSomeDaysFragment someDaysFragment = new AfterSomeDaysFragment();

        binding.tabLayoutOrder.setupWithViewPager(binding.viewPagerOrder);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        viewPagerAdapter.addFragment(tomorrowFragment, "يتم استلامها في" + " (" + date + ") ");
        viewPagerAdapter.addFragment(someDaysFragment, "طلبات محجوزة" + " (" + numAfterSomeDays + ") ");
        binding.viewPagerOrder.setAdapter(viewPagerAdapter);

    }

    private static class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> fragments = new ArrayList<>();
        private final List<String> fragmentsTitle = new ArrayList<>();


        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentsTitle.get(position);
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            fragmentsTitle.add(title);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().removeAllStickyEvents();
        if(EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        super.onStop();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void showDetails(ShowOrderFoodsEvent event){

        if(event != null) {

            //binding.txtTotalPrice.setText(new StringBuilder("إجمالي المشتريات : ").append(event.getOrderModel().getTotal()).append(" ج.م"));
            binding.txtOrderNumber.setText(new StringBuilder("طلب رقم : ")
                    .append(event.getOrderModel().getOrderNumber()
                            .substring(event.getOrderModel().getOrderNumber().length() - 4)));

            setAddressDetails(event);


            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(event.getOrderModel().getTime());
            calendar.add(Calendar.DATE,1);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            Date date = new Date(calendar.getTimeInMillis());


            init(event.getOrderModel().getCartList(), dateFormat.format(date));

            EventBus.getDefault().postSticky(new ShowOrderFoodDetailsEvent(event.getOrderModel()));

        }

    }

    private void setAddressDetails(ShowOrderFoodsEvent event) {

        if(event.getOrderModel().getAddress().getBuildName().isEmpty()) {

            binding.txtAddress.setText(new StringBuilder(event.getOrderModel().getAddress().getBuildNumber())
                    .append(" ")
                    .append("شارع ")
                    .append(event.getOrderModel().getAddress().getStreetName())
                    .append(" - ")
                    .append(event.getOrderModel().getAddress().getZone())
                    .append(" شقة رقم : ")
                    .append(event.getOrderModel().getAddress().getFlatNumber())
                    .append(" -")
                    .append(" الدور : ")
                    .append(event.getOrderModel().getAddress().getFloorNumber()));

        } else {

            binding.txtAddress.setText(new StringBuilder(event.getOrderModel().getAddress().getBuildNumber())
                    .append(" مبنى ")
                    .append(event.getOrderModel().getAddress().getBuildName())
                    .append(" شارع ")
                    .append(event.getOrderModel().getAddress().getStreetName())
                    .append(" - ")
                    .append(event.getOrderModel().getAddress().getZone())
                    .append(" شقة رقم : ")
                    .append(event.getOrderModel().getAddress().getFlatNumber())
                    .append(" -")
                    .append(" الدور : ")
                    .append(event.getOrderModel().getAddress().getFloorNumber()));

        }

    }


}