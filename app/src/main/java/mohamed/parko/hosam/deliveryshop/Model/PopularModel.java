package mohamed.parko.hosam.deliveryshop.Model;

public class PopularModel {

    private String menu_id, food_id, image, name;


    public PopularModel(String menu_id, String food_id, String image, String name) {
        this.menu_id = menu_id;
        this.food_id = food_id;
        this.image = image;
        this.name = name;
    }

    public PopularModel() {
    }

    public String getMenu_id() {
        return menu_id;
    }

    public void setMenu_id(String menu_id) {
        this.menu_id = menu_id;
    }

    public String getFood_id() {
        return food_id;
    }

    public void setFood_id(String food_id) {
        this.food_id = food_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
