package mohamed.parko.hosam.deliveryshop.EventBus;

public class CartFromFoodDetails {

    private boolean success;

    public CartFromFoodDetails(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
