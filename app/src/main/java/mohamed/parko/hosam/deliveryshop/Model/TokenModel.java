package mohamed.parko.hosam.deliveryshop.Model;

public class TokenModel {

    private String phone, token, onlineState;
    private boolean isAdmin;

    public TokenModel() {
    }


    public TokenModel(String phone, String token, String onlineState, boolean isAdmin) {
        this.phone = phone;
        this.token = token;
        this.onlineState = onlineState;
        this.isAdmin = isAdmin;
    }

    public String getOnlineState() {
        return onlineState;
    }

    public void setOnlineState(String onlineState) {
        this.onlineState = onlineState;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
