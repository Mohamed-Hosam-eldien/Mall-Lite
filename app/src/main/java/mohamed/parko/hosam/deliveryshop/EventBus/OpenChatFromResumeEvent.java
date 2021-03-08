package mohamed.parko.hosam.deliveryshop.EventBus;

public class OpenChatFromResumeEvent {

    private boolean isTrue;

    public OpenChatFromResumeEvent(boolean isTrue) {
        this.isTrue = isTrue;
    }

    public boolean isTrue() {
        return isTrue;
    }

    public void setTrue(boolean aTrue) {
        isTrue = aTrue;
    }
}
