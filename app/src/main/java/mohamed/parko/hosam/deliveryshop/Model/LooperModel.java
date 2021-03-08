package mohamed.parko.hosam.deliveryshop.Model;

public class LooperModel {

    private String Name, foodId, Image, menuId;

    public LooperModel() {
    }


    public LooperModel(String name, String foodId, String image, String menuId) {
        Name = name;
        this.foodId = foodId;
        Image = image;
        this.menuId = menuId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
}
