package mohamed.parko.hosam.deliveryshop.ui.addresses;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import mohamed.parko.hosam.deliveryshop.Callback.IZoneCallbackListener;
import mohamed.parko.hosam.deliveryshop.Common.Common;
import mohamed.parko.hosam.deliveryshop.Database.address.AddressDataSource;
import mohamed.parko.hosam.deliveryshop.Database.address.AddressDatabase;
import mohamed.parko.hosam.deliveryshop.Database.address.AddressItem;
import mohamed.parko.hosam.deliveryshop.Database.address.LocalAddressDatabase;
import mohamed.parko.hosam.deliveryshop.Model.ZoneModel;

public class AddressesViewModel extends ViewModel implements IZoneCallbackListener {

    private MutableLiveData<List<String>> mutableLiveData;
    private final MutableLiveData<String> messageError = new MutableLiveData<>();
    private MutableLiveData<List<AddressItem>> addressItemMutableLiveData;

    private final CompositeDisposable compositeDisposable;
    private AddressDataSource addressDataSource;


    private final IZoneCallbackListener zoneCallbackListener;

    public AddressesViewModel() {
        zoneCallbackListener = this;
        compositeDisposable = new CompositeDisposable();
    }

    public void initAddressDataSource(Context context) {
        addressDataSource = new LocalAddressDatabase(AddressDatabase.getInstance(context).addressDao());
    }

    public MutableLiveData<List<String>> getMutableLiveData() {

        if(mutableLiveData == null)
            mutableLiveData = new MutableLiveData<>();

        getZoneDetails();

        return mutableLiveData;
    }

    private void getZoneDetails() {

        List<String> list = new ArrayList<>();
        FirebaseDatabase.getInstance()
                .getReference("Zone")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot post : snapshot.getChildren()) {
                            ZoneModel model = post.getValue(ZoneModel.class);
                            list.add(model.getName());
                        }
                        zoneCallbackListener.onZoneLoadListener(list);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        zoneCallbackListener.onZoneLoadFailed(error.getMessage());
                    }});

    }

    public MutableLiveData<List<AddressItem>> getAddressItemMutableLiveData() {
        if(addressItemMutableLiveData == null)
            addressItemMutableLiveData = new MutableLiveData<>();

        loadAllAddress();

        return addressItemMutableLiveData;
    }

    private void loadAllAddress() {

        compositeDisposable.add(addressDataSource.getAllAddresses(Common.currentUser.getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(addressItems ->
                    addressItemMutableLiveData.setValue(addressItems),
                            throwable -> mutableLiveData.setValue(null)
                ));

    }

    public void onStop() {
        compositeDisposable.clear();
    }

    @Override
    public void onZoneLoadListener(List<String> zoneList) {
        mutableLiveData.setValue(zoneList);
    }

    @Override
    public void onZoneLoadFailed(String message) {
        messageError.setValue(message);
    }

}
