package mohamed.parko.hosam.deliveryshop.EventBus;

import mohamed.parko.hosam.deliveryshop.Model.OrderModel;


public class ShowOrderFoodDetailsEvent {

    private OrderModel model;

    public ShowOrderFoodDetailsEvent(OrderModel model) {
        this.model = model;
    }

    public OrderModel getModel() {
        return model;
    }

    public void setModel(OrderModel model) {
        this.model = model;
    }
}
