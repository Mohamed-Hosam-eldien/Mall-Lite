package mohamed.parko.hosam.deliveryshop.Database.favorite;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;


@Entity(tableName = "Favorite" , primaryKeys = {"uid", "foodId"})
public class FavoriteItem {

    @NonNull
    @ColumnInfo(name = "uid")
    private String uid;

    @NonNull
    @ColumnInfo(name = "foodId")
    private String foodId;

    @NonNull
    @ColumnInfo(name = "menuId")
    private String menuId;

    @ColumnInfo(name = "foodImage")
    private String foodImage;

    @ColumnInfo(name = "FoodName")
    private String FoodName;

    @NonNull
    public String getUid() {
        return uid;
    }

    public void setUid(@NonNull String uid) {
        this.uid = uid;
    }

    @NonNull
    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(@NonNull String foodId) {
        this.foodId = foodId;
    }

    public String getFoodImage() {
        return foodImage;
    }

    public void setFoodImage(String foodImage) {
        this.foodImage = foodImage;
    }

    public String getFoodName() {
        return FoodName;
    }

    public void setFoodName(String foodName) {
        FoodName = foodName;
    }

    @NonNull
    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(@NonNull String menuId) {
        this.menuId = menuId;
    }
}
