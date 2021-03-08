package mohamed.parko.hosam.deliveryshop.Model;

public class UserModel {

    private String uid, name, phone, signInHistory, totalPayment, depositPayment, deptPayment,
            orderDepositNumber, orderDeliveryNumber;

    public UserModel() {
    }


    public String getDepositPayment() {
        return depositPayment;
    }

    public void setDepositPayment(String depositPayment) {
        this.depositPayment = depositPayment;
    }

    public String getDeptPayment() {
        return deptPayment;
    }

    public void setDeptPayment(String deptPayment) {
        this.deptPayment = deptPayment;
    }

    public String getOrderDepositNumber() {
        return orderDepositNumber;
    }

    public void setOrderDepositNumber(String orderDepositNumber) {
        this.orderDepositNumber = orderDepositNumber;
    }

    public String getOrderDeliveryNumber() {
        return orderDeliveryNumber;
    }

    public void setOrderDeliveryNumber(String orderDeliveryNumber) {
        this.orderDeliveryNumber = orderDeliveryNumber;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSignInHistory() {
        return signInHistory;
    }

    public void setSignInHistory(String signInHistory) {
        this.signInHistory = signInHistory;
    }

    public String getTotalPayment() {
        return totalPayment;
    }

    public void setTotalPayment(String totalPayment) {
        this.totalPayment = totalPayment;
    }
}
