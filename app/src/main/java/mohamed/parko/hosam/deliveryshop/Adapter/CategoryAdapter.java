package mohamed.parko.hosam.deliveryshop.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import org.greenrobot.eventbus.EventBus;
import java.util.List;

import mohamed.parko.hosam.deliveryshop.Callback.IRecyclerClickListener;
import mohamed.parko.hosam.deliveryshop.Common.Common;
import mohamed.parko.hosam.deliveryshop.EventBus.CategoryClick;
import mohamed.parko.hosam.deliveryshop.Model.CategoryModel;
import mohamed.parko.hosam.deliveryshop.R;
import mohamed.parko.hosam.deliveryshop.databinding.CategoryLayoutBinding;


public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private final Context context;
    private final List<CategoryModel> categoryModels;
    public boolean showShimmer = true;


    public CategoryAdapter(Context context, List<CategoryModel> categoryModels) {
        this.context = context;
        this.categoryModels = categoryModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.category_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if(showShimmer) {
            holder.binding.shimmerView.startShimmer();
        } else {
            holder.binding.shimmerView.stopShimmer();
            holder.binding.shimmerView.setShimmer(null);

            Glide.with(context).load(categoryModels.get(position).getImage()).into(holder.binding.imageCategory);
            holder.binding.txtCategory.setText(categoryModels.get(position).getName());
            holder.setClickListener((view, position1) -> {
                Common.Category_Selected = categoryModels.get(position);
                EventBus.getDefault().postSticky(new CategoryClick(true, categoryModels.get(position)));
            });
        }

    }

    @Override
    public int getItemCount() {
        return categoryModels.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(categoryModels.size() == 1)
            return Common.DEFAULT_COMMON_COUNT;
        else {
            if(categoryModels.size() % 2 == 0)
                return Common.DEFAULT_COMMON_COUNT;
            else
                return (position > 1 && position == categoryModels.size() -1) ? Common.FULL_WIDTH_COLUMN:Common.DEFAULT_COMMON_COUNT;
        }

    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CategoryLayoutBinding binding;
        IRecyclerClickListener clickListener;

        public void setClickListener(IRecyclerClickListener clickListener) {
            this.clickListener = clickListener;
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = CategoryLayoutBinding.bind(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClickListener(view, getAdapterPosition());
        }
    }
}
