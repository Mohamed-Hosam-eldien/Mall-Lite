package mohamed.parko.hosam.deliveryshop.Callback;


import java.util.List;

import mohamed.parko.hosam.deliveryshop.Model.LooperModel;

public interface LooperCallbackListener {

    void onLoadLooperSuccess(List<LooperModel> model);

    void onLoadLooperFailer(String message);

}
