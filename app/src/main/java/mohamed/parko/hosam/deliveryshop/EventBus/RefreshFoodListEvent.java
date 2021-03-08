package mohamed.parko.hosam.deliveryshop.EventBus;

import mohamed.parko.hosam.deliveryshop.Model.FoodModel;

public class RefreshFoodListEvent {

    private FoodModel foodModel;
    private boolean isRefresh;


    public RefreshFoodListEvent(FoodModel foodModel, boolean isRefresh) {
        this.foodModel = foodModel;
        this.isRefresh = isRefresh;
    }


    public boolean isRefresh() {
        return isRefresh;
    }

    public void setRefresh(boolean refresh) {
        isRefresh = refresh;
    }

    public FoodModel getFoodModel() {
        return foodModel;
    }

    public void setFoodModel(FoodModel foodModel) {
        this.foodModel = foodModel;
    }
}
