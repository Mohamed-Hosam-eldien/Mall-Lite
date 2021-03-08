package mohamed.parko.hosam.deliveryshop.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.ornach.nobobutton.NoboButton;
import java.util.List;
import mohamed.parko.hosam.deliveryshop.Callback.IRecyclerClickListener;
import mohamed.parko.hosam.deliveryshop.Model.AddonModel;
import mohamed.parko.hosam.deliveryshop.Model.FoodModel;
import mohamed.parko.hosam.deliveryshop.R;
import mohamed.parko.hosam.deliveryshop.databinding.FoodAddonWithImageBinding;

public class AddonImageListAdapter extends RecyclerView.Adapter<AddonImageListAdapter.ViewHolder> {

    private final Context context;
    private final FoodModel foodModel;
    private final ChipGroup chipGroupImageAddon;
    private final List<AddonModel> userSelectedAddon;


    public AddonImageListAdapter(Context context, FoodModel foodModel, ChipGroup chipGroupImageAddon, List<AddonModel> userSelectedAddon) {
        this.context = context;
        this.foodModel = foodModel;
        this.chipGroupImageAddon = chipGroupImageAddon;
        this.userSelectedAddon = userSelectedAddon;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.food_addon_with_image2, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Glide.with(context).load(foodModel.getAddon().get(position).getImage()).into(holder.binding.imageFoodAddon);

        holder.setIsRecyclable(false);

        holder.setClickListener((view, position1) -> {
            Dialog dialog = new Dialog(context);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            dialog.setContentView(R.layout.show_image_addon_layout);

            ImageView img = dialog.findViewById(R.id.img_addon);
            NoboButton btnAdd = dialog.findViewById(R.id.btn_add_toChip_addon);

            Glide.with(context).load(foodModel.getAddon().get(position).getImage()).into(img);


            btnAdd.setOnClickListener(view1 -> {

                Chip chip = (Chip) LayoutInflater.from(context).inflate(R.layout.layout_chip_image_item, null);
                chip.setChipIcon(holder.binding.imageFoodAddon.getDrawable());
                chip.setClickable(false);

                userSelectedAddon.add(foodModel.getAddon().get(position));

                chip.setOnCloseIconClickListener(view2 -> {

                    chipGroupImageAddon.removeView(view2);
                    userSelectedAddon.remove(foodModel.getAddon().get(position));

                });

                chipGroupImageAddon.addView(chip);
                dialog.dismiss();

            });

            dialog.show();
        });


    }

    @Override
    public int getItemCount() {
        return foodModel.getAddon().size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        FoodAddonWithImageBinding binding;
        IRecyclerClickListener clickListener;

        public void setClickListener(IRecyclerClickListener clickListener) {
            this.clickListener = clickListener;
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = FoodAddonWithImageBinding.bind(itemView);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClickListener(view, getAdapterPosition());
        }
    }
}
