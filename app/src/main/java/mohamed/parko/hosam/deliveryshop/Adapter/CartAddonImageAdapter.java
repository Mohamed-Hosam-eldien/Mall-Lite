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
import com.ornach.nobobutton.NoboButton;

import java.util.List;

import mohamed.parko.hosam.deliveryshop.Callback.IRecyclerClickListener;
import mohamed.parko.hosam.deliveryshop.Model.AddonModel;
import mohamed.parko.hosam.deliveryshop.R;
import mohamed.parko.hosam.deliveryshop.databinding.FoodAddonWithImageBinding;

public class CartAddonImageAdapter extends RecyclerView.Adapter<CartAddonImageAdapter.ViewHolder> {

    private final Context context;
    private final List<AddonModel> addonList;

    public CartAddonImageAdapter(Context context, List<AddonModel> addonList) {
        this.context = context;
        this.addonList = addonList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.food_addon_with_image, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(addonList.get(position).getImage()).into(holder.binding.imageFoodAddon);

        holder.setListener((view, position1) -> {

            Dialog dialog = new Dialog(context);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.show_image_addon_layout);


            ImageView img = dialog.findViewById(R.id.img_addon);
            NoboButton btn = dialog.findViewById(R.id.btn_add_toChip_addon);
            btn.setVisibility(View.GONE);
            Glide.with(context).load(addonList.get(position).getImage()).into(img);
            dialog.show();

        });

    }

    @Override
    public int getItemCount() {
        return addonList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        FoodAddonWithImageBinding binding;

        IRecyclerClickListener listener;

        public void setListener(IRecyclerClickListener listener) {
            this.listener = listener;
        }

        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            binding = FoodAddonWithImageBinding.bind(itemView);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            listener.onItemClickListener(view, getAdapterPosition());
        }
    }
}
