package mohamed.parko.hosam.deliveryshop.Adapter;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import mohamed.parko.hosam.deliveryshop.Model.CommentModel;
import mohamed.parko.hosam.deliveryshop.R;
import mohamed.parko.hosam.deliveryshop.databinding.CommentItemLayoutBinding;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentHolder> {

    private final Context context;
    private final List<CommentModel> commentList;

    public CommentsAdapter(Context context, List<CommentModel> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new CommentHolder(LayoutInflater.from(context)
                .inflate(R.layout.comment_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CommentHolder holder, int position) {

        long timeStamp = Long.parseLong(commentList.get(position).getCommentTimeStamp().get("timeStamp").toString());
        holder.binding.timeStamp.setText(DateUtils.getRelativeTimeSpanString(timeStamp));

        holder.binding.comment.setText(commentList.get(position).getComment());
        holder.binding.userName.setText(commentList.get(position).getName());
        holder.binding.ratingValue.setRating(commentList.get(position).getRatingValue());


    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }


    public class CommentHolder extends RecyclerView.ViewHolder{
        CommentItemLayoutBinding binding;

        public CommentHolder(@NonNull View itemView) {
            super(itemView);

            binding = CommentItemLayoutBinding.bind(itemView);
            binding.getRoot();

        }

    }
}
