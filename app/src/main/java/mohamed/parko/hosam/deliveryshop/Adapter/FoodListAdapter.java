package mohamed.parko.hosam.deliveryshop.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

import mohamed.parko.hosam.deliveryshop.Callback.IRecyclerClickListener;
import mohamed.parko.hosam.deliveryshop.Common.Common;
import mohamed.parko.hosam.deliveryshop.EventBus.FoodItemClick;
import mohamed.parko.hosam.deliveryshop.Model.CategoryModel;
import mohamed.parko.hosam.deliveryshop.Model.FoodModel;
import mohamed.parko.hosam.deliveryshop.R;
import mohamed.parko.hosam.deliveryshop.databinding.FoodLayoutBinding;


public class FoodListAdapter extends RecyclerView.Adapter<FoodListAdapter.ViewHolder> {

    private final Context context;
    private final List<FoodModel> foodModels;
    private final boolean isDiscount;
    public boolean showShimmer = true;

    public FoodListAdapter(Context context, List<FoodModel> foodModels, boolean isDiscount) {
        this.context = context;
        this.foodModels = foodModels;
        this.isDiscount = isDiscount;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.food_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if(showShimmer) {
            holder.binding.shimmerView.startShimmer();
        } else {
            holder.binding.shimmerView.stopShimmer();
            holder.binding.shimmerView.setShimmer(null);
            holder.binding.imageFood.setBackground(null);
            holder.binding.foodName.setBackground(null);
            holder.binding.pound.setBackground(null);
            holder.binding.relative.setBackground(null);

            holder.setIsRecyclable(false);
            Glide.with(context).load(foodModels.get(position).getImage()).into(holder.binding.imageFood);
            holder.binding.foodName.setText(foodModels.get(position).getName());
            // for price && discount
            if (!foodModels.get(position).getDiscountPrice().isEmpty()
                    && !foodModels.get(position).getDiscountPrice().equals("0")) {
                holder.binding.foodCurrentPrice.setText(foodModels.get(position).getDiscountPrice());
                holder.binding.foodOldPrice.setText(foodModels.get(position).getPrice());
                holder.binding.layoutOldPrice.setVisibility(View.VISIBLE);
            } else {
                holder.binding.foodCurrentPrice.setText(foodModels.get(position).getPrice());
                holder.binding.layoutOldPrice.setVisibility(View.GONE);
            }

            // for set like count
            holder.binding.txtFoodLike.setText(String.valueOf(foodModels.get(position).getLike()));

            // for set comment count
            if (foodModels.get(position).getRatingCount() != null)
                holder.binding.txtFoodComments.setText(String.valueOf(foodModels.get(position).getRatingCount()));

            // for set rating value
            if (foodModels.get(position).getRatingValue() != null)
                holder.binding.txtFoodRating.setText((new DecimalFormat("##.#", new DecimalFormatSymbols(Locale.ENGLISH))
                        .format(foodModels.get(position).getRatingValue() / foodModels.get(position).getRatingCount())));
            else
                holder.binding.txtFoodRating.setText("4.5");

            // to gone pound text
            if (foodModels.get(position).getAddonWithPrice() != null) {
                holder.binding.pound.setVisibility(View.GONE);
                holder.binding.foodCurrentPrice.setVisibility(View.GONE);
            }
            // holder click
            holder.setClickListener((view, position1) -> {

                if (!isDiscount) {
                    Common.SelectedFood = foodModels.get(position);
                    Common.SelectedFood.setKey(String.valueOf(position));
                    EventBus.getDefault().postSticky(new FoodItemClick(true, foodModels.get(position)));
                } else
                    gotoFoodDetails(foodModels.get(position), position);

            });

        }

    }

    private void gotoFoodDetails(FoodModel foodModel, int position) {

        FirebaseDatabase.getInstance()
                .getReference(Common.CATEGORY_REF)
                .child(foodModel.getMenuId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Common.Category_Selected = snapshot.getValue(CategoryModel.class);
                            Common.SelectedFood = foodModels.get(position);
                            Common.SelectedFood.setKey(String.valueOf(position));
                            EventBus.getDefault().postSticky(new FoodItemClick(true, foodModels.get(position)));
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}});

    }


    @Override
    public int getItemCount() {
        return foodModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        FoodLayoutBinding binding;

        IRecyclerClickListener clickListener;

        public void setClickListener(IRecyclerClickListener clickListener) {
            this.clickListener = clickListener;
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = FoodLayoutBinding.bind(itemView);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClickListener(view,getAdapterPosition());
        }

    }
}
