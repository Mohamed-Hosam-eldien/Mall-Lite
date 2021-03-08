package mohamed.parko.hosam.deliveryshop.EventBus;

import mohamed.parko.hosam.deliveryshop.Model.PopularModel;

public class PopularClickEvent {

    private PopularModel model;

    public PopularClickEvent(PopularModel model) {
        this.model = model;
    }

    public PopularModel getModel() {
        return model;
    }

    public void setModel(PopularModel model) {
        this.model = model;
    }
}
