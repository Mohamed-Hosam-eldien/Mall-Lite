package mohamed.parko.hosam.deliveryshop.ui.orders;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import mohamed.parko.hosam.deliveryshop.Callback.ILoadOrderListener;
import mohamed.parko.hosam.deliveryshop.Common.Common;
import mohamed.parko.hosam.deliveryshop.Model.OrderModel;

public class OrdersViewModel extends ViewModel implements ILoadOrderListener {

    public MutableLiveData<List<OrderModel>> orderModelMutableLiveData;
    public MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private final ILoadOrderListener listener;

    public OrdersViewModel() {
        listener = this;
    }


    public MutableLiveData<List<OrderModel>> getOrderModelMutableLiveData() {

        if(orderModelMutableLiveData == null)
            orderModelMutableLiveData = new MutableLiveData<>();

        loadUserOrder();

        return orderModelMutableLiveData;
    }


    public void loadUserOrder() {

        List<OrderModel> orderList = new ArrayList<>();

        FirebaseDatabase.getInstance()
                .getReference(Common.orderCustomerRef)
                .child(Common.currentUser.getUid())
                .limitToLast(100)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for(DataSnapshot post : snapshot.getChildren()) {
                            OrderModel orderModel = post.getValue(OrderModel.class);
                            orderModel.setOrderNumber(post.getKey());
                            orderList.add(orderModel);
                        }

                        listener.onLoadOrderSuccess(orderList);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        listener.onLoadFailed(error.getMessage());
                    }});

    }

    @Override
    public void onLoadOrderSuccess(List<OrderModel> orderModel) {
        orderModelMutableLiveData.setValue(orderModel);
    }

    @Override
    public void onLoadFailed(String message) {
        errorMessage.setValue(message);
    }


}
