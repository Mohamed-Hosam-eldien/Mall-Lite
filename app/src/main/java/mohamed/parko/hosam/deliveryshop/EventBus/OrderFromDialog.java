package mohamed.parko.hosam.deliveryshop.EventBus;

public class OrderFromDialog {

    private boolean success;


    public OrderFromDialog(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
