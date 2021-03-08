package mohamed.parko.hosam.deliveryshop.EventBus;

public class RefreshAdapterEvent {

    private boolean isRefresh;


    public RefreshAdapterEvent(boolean isRefresh) {
        this.isRefresh = isRefresh;
    }

    public boolean isRefresh() {
        return isRefresh;
    }

    public void setRefresh(boolean refresh) {
        isRefresh = refresh;
    }
}
