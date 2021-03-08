package mohamed.parko.hosam.deliveryshop.Callback;

import java.util.List;

import mohamed.parko.hosam.deliveryshop.Model.CategoryModel;
import mohamed.parko.hosam.deliveryshop.Model.PopularModel;

public interface ICategoryCallbackListener {

    void onCategoryLoadSuccess(List<CategoryModel> categoryModelList);

    void onCategoryLoadFailed(String message);

}
