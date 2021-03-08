package mohamed.parko.hosam.deliveryshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gauravk.bubblenavigation.BubbleNavigationLinearView;
import com.gauravk.bubblenavigation.BubbleToggleView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import mohamed.parko.hosam.deliveryshop.Common.Common;
import mohamed.parko.hosam.deliveryshop.Database.cart.CartDataSource;
import mohamed.parko.hosam.deliveryshop.Database.cart.CartDatabase;
import mohamed.parko.hosam.deliveryshop.Database.cart.LocalCartDatabase;
import mohamed.parko.hosam.deliveryshop.EventBus.CartFromFoodDetails;
import mohamed.parko.hosam.deliveryshop.EventBus.CounterCartEvent;
import mohamed.parko.hosam.deliveryshop.EventBus.OpenChatFromResumeEvent;
import mohamed.parko.hosam.deliveryshop.EventBus.OrderFromDialog;
import mohamed.parko.hosam.deliveryshop.EventBus.PopularClickEvent;
import mohamed.parko.hosam.deliveryshop.Model.CategoryModel;
import mohamed.parko.hosam.deliveryshop.Model.FoodModel;
import mohamed.parko.hosam.deliveryshop.Model.UserModel;
import mohamed.parko.hosam.deliveryshop.ui.cart.CartFragment;
import mohamed.parko.hosam.deliveryshop.ui.chat.Chat;
import mohamed.parko.hosam.deliveryshop.ui.favorite.FavoriteFragment;
import mohamed.parko.hosam.deliveryshop.ui.foodDetails.FoodDetails;
import mohamed.parko.hosam.deliveryshop.ui.home.HomeFragment;
import mohamed.parko.hosam.deliveryshop.ui.more.MoreFragment;
import mohamed.parko.hosam.deliveryshop.ui.orders.Orders;


public class Home extends AppCompatActivity {

    private BubbleNavigationLinearView bubble;
    private BubbleToggleView bvCart, bvMore;
    private TextView txtBadge;

    private FragmentTransaction fragmentTransaction;
    private AlertDialog dialog;

    private CartDataSource cartDataSource;
    private UserModel userModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Paper.init(this);
        userModel = Paper.book().read("currentUser");

        if(Common.currentUser == null)
            Common.currentUser = userModel;

        cartDataSource = new LocalCartDatabase(CartDatabase.getInstance(this).cartDao());

        bubble = findViewById(R.id.bubble_nav);
        dialog = new SpotsDialog.Builder().setContext(this).build();
        dialog.setMessage("برجاء الانتظار...");


        BubbleToggleView bvHome = findViewById(R.id.bv_home);
        bvCart = findViewById(R.id.bv_cart);
        bvMore = findViewById(R.id.bv_more);

        txtBadge = findViewById(R.id.txtBadge);

        bvHome.activate();
        bvHome.setInitialState(true);

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, new HomeFragment());
        fragmentTransaction.commit();

        countItemInCart();

        bubble.setNavigationChangeListener((view, position) ->
                new Handler().postDelayed(() -> {

                    switch (position) {

                        case 0:

                            //b.activate();
                            //b.setInitialState(true);
                            fragmentTransaction = getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.frame_layout, new MoreFragment());
                            fragmentTransaction.commit();
                            break;

                        case 1:
                            fragmentTransaction = getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.frame_layout, new FavoriteFragment());
                            fragmentTransaction.commit();
                            break;

                        case 2:
                            fragmentTransaction = getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.frame_layout, new CartFragment());
                            fragmentTransaction.commit();
                            break;

                        case 3:
                            fragmentTransaction = getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.frame_layout, new HomeFragment());
                            fragmentTransaction.commit();

                            break;

                    }
                }, 300));

        checkIsOpenFromNotification();

    }

    private void checkIsOpenFromNotification() {

        boolean isOpenOrder = getIntent().getBooleanExtra(Common.IS_OPEN_ORDER_ACTIVITY,false);
        boolean isOpenMessage = getIntent().getBooleanExtra(Common.IS_OPEN_MessageActivity,false);

        if(isOpenOrder) {
            startActivity(new Intent(this, Orders.class));
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, new MoreFragment());
            fragmentTransaction.commit();
            bvMore.activate();
            bvMore.setInitialState(true);

        } else if(isOpenMessage) {
            startActivity(new Intent(this, Chat.class));
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, new MoreFragment());
            fragmentTransaction.commit();
            bvMore.activate();
            bvMore.setInitialState(true);

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
        super.onStop();
        EventBus.getDefault().removeAllStickyEvents();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }

    private void countItemInCart() {

        cartDataSource.countItemInCart(Common.currentUser.getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull Integer num) {

                        if (num == 0)
                            txtBadge.setVisibility(View.GONE);
                        else {
                            txtBadge.setVisibility(View.VISIBLE);
                            txtBadge.setText(String.valueOf(num));
                        }

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d("DEBUG_COUNT", "" + e.getMessage());
                    }
                });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Common.currentUser = userModel;
        countItemInCart();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onCartCounter(CounterCartEvent event) {
        if (event.isSuccess()) {
            countItemInCart();
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onPopularClick(PopularClickEvent event) {
        if (event.getModel() != null) {

            dialog.show();

            FirebaseDatabase.getInstance()
                    .getReference(Common.CATEGORY_REF)
                    .child(event.getModel().getMenu_id())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {

                                Common.Category_Selected = snapshot.getValue(CategoryModel.class);

                                FirebaseDatabase.getInstance()
                                        .getReference(Common.CATEGORY_REF)
                                        .child(event.getModel().getMenu_id())
                                        .child("foods")
                                        .orderByChild("id")
                                        .equalTo(event.getModel().getFood_id())
                                        .limitToLast(1)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                for (DataSnapshot post : snapshot.getChildren()) {

                                                    if (post.exists()) {

                                                        Common.SelectedFood = post.getValue(FoodModel.class);
                                                        Common.SelectedFood.setKey(post.getKey());
                                                        startActivity(new Intent(Home.this, FoodDetails.class));

                                                    } else {
                                                        Toast.makeText(Home.this, "هذا العنصر غير متوفر", Toast.LENGTH_SHORT).show();
                                                    }
                                                    dialog.dismiss();
                                                }

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                dialog.dismiss();
                                            }
                                        });


                            } else {
                                Toast.makeText(Home.this, "هذا الصنف غير متوفر", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            dialog.dismiss();
                        }
                    });


        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onBackFromOrder(OrderFromDialog val) {
        if(val.isSuccess()) {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, new MoreFragment());
            fragmentTransaction.commit();
            bubble.setCurrentActiveItem(0);
        }

    }


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onBackFromFoodDetails(CartFromFoodDetails val) {
        if(val.isSuccess()) {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, new CartFragment());
            fragmentTransaction.commit();
            //bubble.setCurrentActiveItem(2);
            bvCart.activate();
            bvCart.setInitialState(true);
        }

    }


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onBackFromChatResume(OpenChatFromResumeEvent val) {
        if(val.isTrue()) {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, new MoreFragment());
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onBackPressed() {

        switch (bubble.getCurrentActiveItemPosition()) {

            case 0:
            case 1:
            case 2: {
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, new HomeFragment());
                fragmentTransaction.commit();
                bubble.setCurrentActiveItem(3);
                break;
            }
            case 3:{
                super.onBackPressed();
            }

        }

    }


}