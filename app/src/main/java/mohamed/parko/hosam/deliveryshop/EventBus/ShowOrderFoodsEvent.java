package mohamed.parko.hosam.deliveryshop.EventBus;


import mohamed.parko.hosam.deliveryshop.Model.OrderModel;

public class ShowOrderFoodsEvent {

    private OrderModel orderModel;

    public ShowOrderFoodsEvent(OrderModel orderModel) {
        this.orderModel = orderModel;
    }

    public OrderModel getOrderModel() {
        return orderModel;
    }

    public void setOrderModel(OrderModel orderModel) {
        this.orderModel = orderModel;
    }
}
