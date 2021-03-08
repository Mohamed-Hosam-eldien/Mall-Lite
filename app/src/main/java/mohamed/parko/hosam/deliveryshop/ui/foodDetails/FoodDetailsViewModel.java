package mohamed.parko.hosam.deliveryshop.ui.foodDetails;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import mohamed.parko.hosam.deliveryshop.Common.Common;
import mohamed.parko.hosam.deliveryshop.Model.CommentModel;
import mohamed.parko.hosam.deliveryshop.Model.FoodModel;

public class FoodDetailsViewModel extends ViewModel {

    private MutableLiveData<FoodModel> mutableLiveData;
    private final MutableLiveData<CommentModel> commentModelMutableLiveData;


    public FoodDetailsViewModel() {
        commentModelMutableLiveData = new MutableLiveData<>();
    }

    public void setCommentModel(CommentModel commentModel) {
        if (commentModelMutableLiveData != null)
            commentModelMutableLiveData.setValue(commentModel);
    }


    public MutableLiveData<CommentModel> getCommentModelMutableLiveData() {
        return commentModelMutableLiveData;
    }

    public MutableLiveData<FoodModel> getMutableLiveData() {
        if (mutableLiveData == null)
            mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(Common.SelectedFood);
        return mutableLiveData;
    }

    public void setFoodModel(FoodModel foodModel) {

        if(mutableLiveData != null)
            mutableLiveData.setValue(foodModel);

    }

}
