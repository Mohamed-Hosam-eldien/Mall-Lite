package mohamed.parko.hosam.deliveryshop.Callback;

import mohamed.parko.hosam.deliveryshop.Model.OrderModel;

public interface ILoadTimeFromServer {

    void onLoadTimeSuccess(OrderModel order, long estimateTime);

    void onLoadFailed(String message);

}
