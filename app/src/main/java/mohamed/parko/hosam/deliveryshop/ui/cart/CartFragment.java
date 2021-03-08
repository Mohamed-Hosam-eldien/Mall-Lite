package mohamed.parko.hosam.deliveryshop.ui.cart;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import io.paperdb.Paper;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import mohamed.parko.hosam.deliveryshop.Adapter.CartAdapter;
import mohamed.parko.hosam.deliveryshop.Common.Common;
import mohamed.parko.hosam.deliveryshop.Common.SpacesItemDecoration;
import mohamed.parko.hosam.deliveryshop.Database.cart.CartDataSource;
import mohamed.parko.hosam.deliveryshop.Database.cart.CartDatabase;
import mohamed.parko.hosam.deliveryshop.Database.cart.LocalCartDatabase;
import mohamed.parko.hosam.deliveryshop.EventBus.OrderFromDialog;
import mohamed.parko.hosam.deliveryshop.EventBus.ReturnFromCheckoutEvent;
import mohamed.parko.hosam.deliveryshop.EventBus.UpdateCartItem;
import mohamed.parko.hosam.deliveryshop.Model.UserModel;
import mohamed.parko.hosam.deliveryshop.R;
import mohamed.parko.hosam.deliveryshop.databinding.FragmentCartBinding;
import mohamed.parko.hosam.deliveryshop.databinding.LayoutNotiesBinding;
import mohamed.parko.hosam.deliveryshop.ui.checkout.Checkout;
import mohamed.parko.hosam.deliveryshop.ui.orders.Orders;


public class CartFragment extends Fragment {

    private Parcelable recyclerViewState;
    private FragmentCartBinding binding;
    private CartDataSource cartDataSource;
    private CartViewModel cartViewModel;

    private CompositeDisposable compositeDisposable;

    private LayoutNotiesBinding notiBinding;

    private UserModel userModel;

    private Context thisContext;
    private CartAdapter adapter;


    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        thisContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCartBinding.bind(inflater.inflate(R.layout.fragment_cart, container, false));

        init();

        // for checkOut dialog
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        cartViewModel.initCartDataSource(thisContext);


        cartViewModel.getMutableLiveData().observe(getViewLifecycleOwner(), cartItems -> {

            if (cartItems.size() > 0) {
                adapter = new CartAdapter(thisContext, cartItems, binding.txtCartTotalPayment);

                binding.recyclerItem.setAdapter(adapter);
                binding.recyclerItem.setVisibility(View.VISIBLE);
                binding.buttonLinear.setVisibility(View.VISIBLE);
                binding.imgEmpty.setVisibility(View.GONE);
                binding.waitingProg.setVisibility(View.GONE);

            } else {
                binding.buttonLinear.setVisibility(View.GONE);
                binding.waitingProg.setVisibility(View.GONE);
                binding.recyclerItem.setVisibility(View.GONE);
                binding.imgEmpty.setVisibility(View.VISIBLE);
            }

        });


        return binding.getRoot();

    }

    // init views and others variable
    private void init() {

        Paper.init(thisContext);
        userModel = Paper.book().read("currentUser");
        if (Common.currentUser == null)
            Common.currentUser = userModel;

        cartDataSource = new LocalCartDatabase(CartDatabase.getInstance(thisContext).cartDao());
        compositeDisposable = new CompositeDisposable();

        calculateTotalPrice();

        // recycler view
        binding.recyclerItem.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(thisContext, RecyclerView.VERTICAL,false);
        //binding.recyclerItem.setNestedScrollingEnabled(false);
        binding.recyclerItem.setLayoutManager(layoutManager);
        binding.recyclerItem.addItemDecoration(new SpacesItemDecoration(8));

        binding.btnSend.setOnClickListener(view -> {
            startActivity(new Intent(thisContext, Checkout.class));
            getActivity().overridePendingTransition(R.anim.slide_in_right,
                    R.anim.slide_out_left);
        });

    }

    // update cart price when change value
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onUpdateCartItem(UpdateCartItem event) {
        if (event.getCartItem() != null) {
            // save state of recycler view
            if (binding.recyclerItem.getLayoutManager() != null)
                recyclerViewState = binding.recyclerItem.getLayoutManager().onSaveInstanceState();

            cartDataSource.updateCartItem(event.getCartItem())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<Integer>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {
                        }

                        @Override
                        public void onSuccess(@NonNull Integer integer) {
                            calculateTotalPrice();
                            binding.recyclerItem.getLayoutManager().onRestoreInstanceState(recyclerViewState);
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            Log.d("UPDATE_CART", "" + e.getMessage());
                        }
                    });

        }
    }

    // calculate total price after change item quantity
    private void calculateTotalPrice() {

        cartDataSource.sumPriceInCart(Common.currentUser.getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Double>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onSuccess(@NonNull Double price) {

                        binding.txtCartTotalPayment.setText(new DecimalFormat("##.##", new DecimalFormatSymbols(Locale.ENGLISH))
                                .format(price));

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        if (e.getMessage() != null && e.getMessage().contains("empty")) {
                            binding.txtCartTotalPayment.setText(R.string._0_00);
                            binding.btnSend.setEnabled(false);
                        } else
                            Log.d("CART_UPDATE2", "" + e.getMessage());
                    }
                });

    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onReturnFromCheckout(ReturnFromCheckoutEvent event) {

        if (event.isFromCheckout()) {

            cartViewModel.getAllCart();
            Dialog dialog = new Dialog(thisContext);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            View view = LayoutInflater.from(thisContext).inflate(R.layout.layout_noties, null);
            dialog.setContentView(view);
            notiBinding = LayoutNotiesBinding.bind(view);

            notiBinding.dialogImage.setImageResource(R.drawable.ic_baseline_correct_circle_green_24);
            notiBinding.dialogTitle.setText("تم إرسال طلبك بنجاح");
            notiBinding.dialogDescription.setText("عزيزنا العميل يتم الآن مراجعة طلبك وسيتم التواصل معك عقب الإنتهاء من عملية المراجعة.. برجاء الإنتظار");
            notiBinding.dialogAccept.setText("أضغط للذهاب إلى طلباتك");

            notiBinding.dialogAccept.setOnClickListener(view1 -> {
                dialog.dismiss();
                startActivity(new Intent(thisContext, Orders.class));
                EventBus.getDefault().postSticky(new OrderFromDialog(true));
            });

            dialog.show();

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
        cartViewModel.onStop();
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Common.currentUser == null)
            Common.currentUser = userModel;
        calculateTotalPrice();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
        notiBinding = null;
    }

}