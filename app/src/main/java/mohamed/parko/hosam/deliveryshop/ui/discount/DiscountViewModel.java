package mohamed.parko.hosam.deliveryshop.ui.discount;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import mohamed.parko.hosam.deliveryshop.Callback.IDiscountCallbackListener;
import mohamed.parko.hosam.deliveryshop.Common.Common;
import mohamed.parko.hosam.deliveryshop.Model.FoodModel;

public class DiscountViewModel extends ViewModel implements IDiscountCallbackListener {

    private MutableLiveData<List<FoodModel>> mutableLiveData;
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final IDiscountCallbackListener listener;

    public DiscountViewModel() {
        listener = this;
    }

    public MutableLiveData<List<FoodModel>> getMutableLiveData() {

        if (mutableLiveData == null)
            mutableLiveData = new MutableLiveData<>();

        loadAllDiscount();

        return mutableLiveData;
    }

    private void loadAllDiscount() {

        List<FoodModel> list = new ArrayList<>();

        FirebaseDatabase.getInstance()
                .getReference(Common.discount_ref)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for(DataSnapshot post : snapshot.getChildren()) {
                                FoodModel model = post.getValue(FoodModel.class);
                                list.add(model);
                            }
                            listener.onDiscountLoadSuccess(list);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}});


    }

    @Override
    public void onDiscountLoadSuccess(List<FoodModel> discountModelList) {
        mutableLiveData.setValue(discountModelList);
    }

    @Override
    public void onDiscountLoadFailed(String message) {
        errorMessage.setValue(message);
    }
}
