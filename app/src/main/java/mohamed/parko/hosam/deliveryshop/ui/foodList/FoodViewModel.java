package mohamed.parko.hosam.deliveryshop.ui.foodList;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import mohamed.parko.hosam.deliveryshop.Common.Common;
import mohamed.parko.hosam.deliveryshop.Model.FoodModel;

public class FoodViewModel extends ViewModel {

    private MutableLiveData<List<FoodModel>> foodList;


    public FoodViewModel() {
    }


    public MutableLiveData<List<FoodModel>> getFoodList() {
        if (foodList == null)
            foodList = new MutableLiveData<>();
        foodList.setValue(Common.Category_Selected.getFoods());
        return foodList;
    }


    public void setFoodList(List<FoodModel> foodModelList) {
        if(foodList != null)
            foodList.setValue(foodModelList);
    }

}
