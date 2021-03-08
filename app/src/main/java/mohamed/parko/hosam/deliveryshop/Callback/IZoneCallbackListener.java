package mohamed.parko.hosam.deliveryshop.Callback;

import java.util.List;


public interface IZoneCallbackListener {

    void onZoneLoadListener(List<String> zoneList);

    void onZoneLoadFailed(String message);

}
