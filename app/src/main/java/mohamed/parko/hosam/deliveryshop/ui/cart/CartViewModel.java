package mohamed.parko.hosam.deliveryshop.ui.cart;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import mohamed.parko.hosam.deliveryshop.Common.Common;
import mohamed.parko.hosam.deliveryshop.Database.cart.CartDataSource;
import mohamed.parko.hosam.deliveryshop.Database.cart.CartDatabase;
import mohamed.parko.hosam.deliveryshop.Database.cart.CartItem;
import mohamed.parko.hosam.deliveryshop.Database.cart.LocalCartDatabase;

public class CartViewModel extends ViewModel {

    private MutableLiveData<List<CartItem>> mutableLiveData;
    private final CompositeDisposable compositeDisposable;
    private CartDataSource cartDataSource;

    private MutableLiveData<String> zoneMutableLifeData;

    public CartViewModel() {
        compositeDisposable = new CompositeDisposable();
    }

    public void initCartDataSource(Context context) {
        cartDataSource = new LocalCartDatabase(CartDatabase.getInstance(context).cartDao());
    }

    public void onStop() {
        compositeDisposable.clear();
    }

    public MutableLiveData<List<CartItem>> getMutableLiveData() {
        if(mutableLiveData == null)
            mutableLiveData = new MutableLiveData<>();

        getAllCart();

        return mutableLiveData;
    }

    public void getAllCart() {

        compositeDisposable.add(cartDataSource.getAllCart(Common.currentUser.getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cartItems -> mutableLiveData.setValue(cartItems),
                        throwable -> mutableLiveData.setValue(null)
                ));
    }

    public MutableLiveData<String> getZoneMutableLifeData(String name) {
        if(zoneMutableLifeData == null)
            zoneMutableLifeData = new MutableLiveData<>();

        getZonePriceByName(name);

        return zoneMutableLifeData;
    }

    private void getZonePriceByName(String name) {

        FirebaseDatabase.getInstance()
                .getReference("Zone")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot post : snapshot.getChildren()) {
                            if(String.valueOf(post.child("name").getValue()).equals(name)) {
                                zoneMutableLifeData.setValue(String.valueOf(post.child("price").getValue()));
                                break;
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}});

    }


}
