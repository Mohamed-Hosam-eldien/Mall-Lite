package mohamed.parko.hosam.deliveryshop.Database.cart;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;


@Entity(tableName = "Cart", primaryKeys = {"uid", "foodId", "foodAddonWithPrice", "foodAddon"})
public class CartItem {

    @NonNull
    @ColumnInfo(name = "foodId")
    private String foodId;

    @ColumnInfo(name = "foodName")
    private String foodName;

    @ColumnInfo(name = "foodImage")
    private String foodImage;

    @ColumnInfo(name = "foodPrice")
    private String foodPrice;

    @ColumnInfo(name = "foodQuantity")
    private String foodQuantity;

    @ColumnInfo(name = "userPhone")
    private String userPhone;

    @ColumnInfo(name = "foodDeposit")
    private String foodDeposit;

    @NonNull
    @ColumnInfo(name = "uid")
    private String uid;

    @NonNull
    @ColumnInfo(name = "foodAddonWithPrice")
    private String foodAddonWithPrice;

    @NonNull
    @ColumnInfo(name = "foodAddon")
    private String foodAddon;

    @ColumnInfo(name = "foodMiniWeight")
    private String foodMiniWeight;


    @NonNull
    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(@NonNull String foodId) {
        this.foodId = foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getFoodImage() {
        return foodImage;
    }

    public String getFoodMiniWeight() {
        return foodMiniWeight;
    }

    public void setFoodMiniWeight(String foodMiniWeight) {
        this.foodMiniWeight = foodMiniWeight;
    }

    public void setFoodImage(String foodImage) {
        this.foodImage = foodImage;
    }

    public String getFoodPrice() {
        return foodPrice;
    }

    public void setFoodPrice(String foodPrice) {
        this.foodPrice = foodPrice;
    }

    public String getFoodQuantity() {
        return foodQuantity;
    }

    public void setFoodQuantity(String foodQuantity) {
        this.foodQuantity = foodQuantity;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(@NonNull String uid) {
        this.uid = uid;
    }


    public String getFoodAddonWithPrice() {
        return foodAddonWithPrice;
    }

    public void setFoodAddonWithPrice(@NonNull String foodAddonWithPrice) {
        this.foodAddonWithPrice = foodAddonWithPrice;
    }

    public String getFoodAddon() {
        return foodAddon;
    }

    public void setFoodAddon(@NonNull String foodAddon) {
        this.foodAddon = foodAddon;
    }


    public String getFoodDeposit() {
        return foodDeposit;
    }

    public void setFoodDeposit(String foodDeposit) {
        this.foodDeposit = foodDeposit;
    }

    @Override
    public boolean equals(@Nullable Object obj) {

        if(obj == this)
            return true;
        if(!(obj instanceof CartItem))
            return false;

        CartItem cartItem = (CartItem) obj;

        return cartItem.getFoodId().equals(this.foodId) &&
                cartItem.getFoodAddonWithPrice().equals(this.foodAddonWithPrice) &&
                cartItem.getFoodAddon().equals(this.foodAddon);
    }


}
