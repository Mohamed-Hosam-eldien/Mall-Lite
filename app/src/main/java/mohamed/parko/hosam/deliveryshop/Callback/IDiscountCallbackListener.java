package mohamed.parko.hosam.deliveryshop.Callback;

import java.util.List;

import mohamed.parko.hosam.deliveryshop.Model.FoodModel;

public interface IDiscountCallbackListener {

    void onDiscountLoadSuccess(List<FoodModel> discountModelList);

    void onDiscountLoadFailed(String message);

}
