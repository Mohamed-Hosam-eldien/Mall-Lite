package mohamed.parko.hosam.deliveryshop.Model;

import java.util.List;

import mohamed.parko.hosam.deliveryshop.Database.address.AddressItem;
import mohamed.parko.hosam.deliveryshop.Database.cart.CartItem;

public class OrderModel {

    private String uid, name, phone, lat, lon, comment,historyArrivedOrder,moneyPaid,moneyRemaining,arrived,
            total, totalTomorrow, totalAfterSomeDays, orderNumber, deliverySalary,stateDeposit;
    private List<CartItem> cartList;
    private AddressItem address;
    private long time;
    private int state;
    private boolean isDeposit;


    public String getArrived() {
        return arrived;
    }

    public void setArrived(String arrived) {
        this.arrived = arrived;
    }

    public boolean isDeposit() {
        return isDeposit;
    }

    public void setDeposit(boolean deposit) {
        isDeposit = deposit;
    }

    public String getStateDeposit() {
        return stateDeposit;
    }

    public void setStateDeposit(String stateDeposit) {
        this.stateDeposit = stateDeposit;
    }

    public String getMoneyPaid() {
        return moneyPaid;
    }

    public void setMoneyPaid(String moneyPaid) {
        this.moneyPaid = moneyPaid;
    }

    public String getMoneyRemaining() {
        return moneyRemaining;
    }

    public void setMoneyRemaining(String moneyRemaining) {
        this.moneyRemaining = moneyRemaining;
    }

    public String getHistoryArrivedOrder() {
        return historyArrivedOrder;
    }

    public void setHistoryArrivedOrder(String historyArrivedOrder) {
        this.historyArrivedOrder = historyArrivedOrder;
    }

    public String getTotalTomorrow() {
        return totalTomorrow;
    }

    public void setTotalTomorrow(String totalTomorrow) {
        this.totalTomorrow = totalTomorrow;
    }

    public String getTotalAfterSomeDays() {
        return totalAfterSomeDays;
    }

    public void setTotalAfterSomeDays(String totalAfterSomeDays) {
        this.totalAfterSomeDays = totalAfterSomeDays;
    }

    public String getDeliverySalary() {
        return deliverySalary;
    }

    public void setDeliverySalary(String deliverySalary) {
        this.deliverySalary = deliverySalary;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public OrderModel() {}

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
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

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<CartItem> getCartList() {
        return cartList;
    }

    public void setCartList(List<CartItem> cartList) {
        this.cartList = cartList;
    }

    public AddressItem getAddress() {
        return address;
    }

    public void setAddress(AddressItem address) {
        this.address = address;
    }


}
