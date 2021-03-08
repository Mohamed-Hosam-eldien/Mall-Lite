package mohamed.parko.hosam.deliveryshop.ui.comments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import dmax.dialog.SpotsDialog;
import mohamed.parko.hosam.deliveryshop.Adapter.CommentsAdapter;
import mohamed.parko.hosam.deliveryshop.Callback.ICommentCallbackListener;
import mohamed.parko.hosam.deliveryshop.Common.Common;
import mohamed.parko.hosam.deliveryshop.Model.CommentModel;
import mohamed.parko.hosam.deliveryshop.R;
import mohamed.parko.hosam.deliveryshop.databinding.BottomSheetCommentLayoutBinding;

public class CommentFragment extends BottomSheetDialogFragment implements ICommentCallbackListener {

    private final ICommentCallbackListener listener;
    private CommentViewModel commentViewModel;
    private AlertDialog dialog;
    private static CommentFragment instance;
    private BottomSheetCommentLayoutBinding binding;

    public CommentFragment() {
        listener = this;
    }

    public static CommentFragment getInstance() {
        if(instance == null)
            instance = new CommentFragment();
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.bottom_sheet_comment_layout, container, false);

        binding = BottomSheetCommentLayoutBinding.bind(view);
        binding.getRoot();

        initView();

        loadComments();

        commentViewModel.getCommentMutableLiveData().observe(getViewLifecycleOwner(), commentModels -> {
            CommentsAdapter adapter = new CommentsAdapter(getContext(), commentModels);
            binding.recyclerComments.setAdapter(adapter);
        });


        return view;
    }

    private void loadComments() {

        dialog.setMessage("برجاء الإنتظار...");
        dialog.show();

        FirebaseDatabase.getInstance()
                .getReference(Common.COMMENT_REF)
                .child(Common.SelectedFood.getId())
                .orderByChild("commentTimeStamp")
                .limitToLast(100)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<CommentModel> listComments = new ArrayList<>();
                        for(DataSnapshot post : snapshot.getChildren()) {

                            CommentModel model = post.getValue(CommentModel.class);
                            listComments.add(model);

                        }
                        dialog.dismiss();
                        listener.onCommentLoadSuccessful(listComments);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        dialog.dismiss();
                        listener.onCommentLoadFailed(error.getMessage());
                    }
                });

    }

    private void initView() {

        commentViewModel = new ViewModelProvider(this).get(CommentViewModel.class);
        dialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();

        binding.recyclerComments.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, true);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        binding.recyclerComments.setLayoutManager(layoutManager);
        binding.recyclerComments.addItemDecoration(new DividerItemDecoration(getContext(), layoutManager.getOrientation()));

    }

    @Override
    public void onCommentLoadSuccessful(List<CommentModel> commentModels) {
        commentViewModel.setCommentList(commentModels);
    }

    @Override
    public void onCommentLoadFailed(String message) {}


}
