package mohamed.parko.hosam.deliveryshop.Model;


public class MoreModel {

    private String name;
    private int image, backgroundColor;

    public MoreModel(String name, int image, int backgroundColor) {
        this.name = name;
        this.image = image;
        this.backgroundColor = backgroundColor;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
