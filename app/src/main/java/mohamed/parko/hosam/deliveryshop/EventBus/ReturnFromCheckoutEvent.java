package mohamed.parko.hosam.deliveryshop.EventBus;

public class ReturnFromCheckoutEvent {

    private boolean isFromCheckout;

    public ReturnFromCheckoutEvent(boolean isFromCheckout) {
        this.isFromCheckout = isFromCheckout;
    }

    public boolean isFromCheckout() {
        return isFromCheckout;
    }

    public void setFromCheckout(boolean fromCheckout) {
        isFromCheckout = fromCheckout;
    }
}
