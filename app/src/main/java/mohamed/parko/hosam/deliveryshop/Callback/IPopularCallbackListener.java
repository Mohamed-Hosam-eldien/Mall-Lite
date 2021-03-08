package mohamed.parko.hosam.deliveryshop.Callback;

import java.util.List;

import mohamed.parko.hosam.deliveryshop.Model.PopularModel;

public interface IPopularCallbackListener {

    void onPopularLoadSuccess(List<PopularModel> popularModelList);

    void onPopularLoadFailed(String message);

}
