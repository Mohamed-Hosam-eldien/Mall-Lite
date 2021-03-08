package mohamed.parko.hosam.deliveryshop.Callback;

import java.util.List;

import mohamed.parko.hosam.deliveryshop.Model.OrderModel;

public interface ILoadOrderListener {

    void onLoadOrderSuccess(List<OrderModel> orderModel);

    void onLoadFailed(String message);

}
