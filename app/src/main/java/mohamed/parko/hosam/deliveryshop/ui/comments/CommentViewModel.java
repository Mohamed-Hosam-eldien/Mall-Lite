package mohamed.parko.hosam.deliveryshop.ui.comments;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import mohamed.parko.hosam.deliveryshop.Model.CommentModel;

public class CommentViewModel extends ViewModel {

    private final MutableLiveData<List<CommentModel>> commentMutableLiveData;


    public CommentViewModel() {
        commentMutableLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<List<CommentModel>> getCommentMutableLiveData() {
        return commentMutableLiveData;
    }

    public void setCommentList(List<CommentModel> commentList) {
        commentMutableLiveData.setValue(commentList);
    }

}
