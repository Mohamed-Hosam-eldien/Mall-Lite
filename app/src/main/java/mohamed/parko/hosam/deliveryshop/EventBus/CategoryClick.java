package mohamed.parko.hosam.deliveryshop.EventBus;

import mohamed.parko.hosam.deliveryshop.Model.CategoryModel;

public class CategoryClick {

    private boolean success;
    private CategoryModel categoryModel;

    public CategoryClick(boolean success, CategoryModel categoryModel) {
        this.success = success;
        this.categoryModel = categoryModel;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public CategoryModel getCategoryModel() {
        return categoryModel;
    }

    public void setCategoryModel(CategoryModel categoryModel) {
        this.categoryModel = categoryModel;
    }
}
