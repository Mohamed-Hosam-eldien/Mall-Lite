package mohamed.parko.hosam.deliveryshop.EventBus;

public class SendNotificationEvent {

    private boolean isSend;
    private String message;

    public SendNotificationEvent(boolean isSend, String message) {
        this.isSend = isSend;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSend() {
        return isSend;
    }

    public void setSend(boolean send) {
        isSend = send;
    }
}
