package mohamed.parko.hosam.deliveryshop.Model;

import java.util.List;

public class FoodModel {

    private String deliveryTime, description, diposit, discountPrice,
                    id, image, miniWeight, name, price, productShow, productState, key, sellState, menuId;

    private List<AddonModel> addon;
    private List<AddonWithPrice> addonWithPrice;
    private Double ratingValue;
    private Long ratingCount;
    private int like;

    public FoodModel() {
    }

    public FoodModel(String deliveryTime, String description, String diposit, String discountPrice, String id, String image, String miniWeight, String name, String price, String productShow, String productState, String key, String sellState, List<AddonModel> addon, List<AddonWithPrice> addonWithPrice, Double ratingValue, Long ratingCount, int like) {
        this.deliveryTime = deliveryTime;
        this.description = description;
        this.diposit = diposit;
        this.discountPrice = discountPrice;
        this.id = id;
        this.image = image;
        this.miniWeight = miniWeight;
        this.name = name;
        this.price = price;
        this.productShow = productShow;
        this.productState = productState;
        this.key = key;
        this.sellState = sellState;
        this.addon = addon;
        this.addonWithPrice = addonWithPrice;
        this.ratingValue = ratingValue;
        this.ratingCount = ratingCount;
        this.like = like;
    }


    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public String getSellState() {
        return sellState;
    }

    public void setSellState(String sellState) {
        this.sellState = sellState;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public Double getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(Double ratingValue) {
        this.ratingValue = ratingValue;
    }

    public Long getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Long ratingCount) {
        this.ratingCount = ratingCount;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDiposit() {
        return diposit;
    }

    public void setDiposit(String diposit) {
        this.diposit = diposit;
    }

    public String getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(String discountPrice) {
        this.discountPrice = discountPrice;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMiniWeight() {
        return miniWeight;
    }

    public void setMiniWeight(String miniWeight) {
        this.miniWeight = miniWeight;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getProductShow() {
        return productShow;
    }

    public void setProductShow(String productShow) {
        this.productShow = productShow;
    }

    public String getProductState() {
        return productState;
    }

    public void setProductState(String productState) {
        this.productState = productState;
    }

    public List<AddonModel> getAddon() {
        return addon;
    }

    public void setAddon(List<AddonModel> addon) {
        this.addon = addon;
    }

    public List<AddonWithPrice> getAddonWithPrice() {
        return addonWithPrice;
    }

    public void setAddonWithPrice(List<AddonWithPrice> addonWithPrice) {
        this.addonWithPrice = addonWithPrice;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


}
