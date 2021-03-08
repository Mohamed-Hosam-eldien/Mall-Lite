package mohamed.parko.hosam.deliveryshop.Callback;

import java.util.List;

import mohamed.parko.hosam.deliveryshop.Model.CommentModel;

public interface ICommentCallbackListener {

    void onCommentLoadSuccessful(List<CommentModel> commentModels);

    void onCommentLoadFailed(String message);

}
