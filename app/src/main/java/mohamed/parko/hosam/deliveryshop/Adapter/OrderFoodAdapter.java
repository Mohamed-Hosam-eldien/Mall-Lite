package mohamed.parko.hosam.deliveryshop.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import mohamed.parko.hosam.deliveryshop.Database.cart.CartItem;
import mohamed.parko.hosam.deliveryshop.Model.AddonModel;
import mohamed.parko.hosam.deliveryshop.Model.AddonWithPrice;
import mohamed.parko.hosam.deliveryshop.R;
import mohamed.parko.hosam.deliveryshop.databinding.OrderFoodLayoutBinding;


public class OrderFoodAdapter extends RecyclerView.Adapter<OrderFoodAdapter.ViewHolder> {

    private final Context context;
    private final List<CartItem> cartItemList;
    private final Gson gson;


    public OrderFoodAdapter(Context context, List<CartItem> cartItemList) {
        this.context = context;
        this.cartItemList = cartItemList;
        gson = new Gson();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.order_food_layout, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.binding.foodNameCart.setText(cartItemList.get(position).getFoodName());
        holder.binding.foodPrice.setText(new StringBuilder("السعر : ")
                .append(cartItemList.get(position).getFoodPrice()));

        holder.binding.foodtQuantity.setText(new StringBuilder("الكمية : ")
                .append(cartItemList.get(position).getFoodQuantity()));

        holder.binding.foodtTotal.setText(new StringBuilder("الإجمالي : ")
                .append(Double.parseDouble(cartItemList.get(position).getFoodQuantity())
                        * Double.parseDouble(cartItemList.get(position).getFoodPrice()))
                .append("  ج.م"));

        // for deposit
        if (!cartItemList.get(position).getFoodDeposit().isEmpty()) {
            holder.binding.txtDeposit.setText(new StringBuilder(" ")
                    .append(cartItemList.get(position).getFoodDeposit()).append(" ج.م"));
            holder.binding.layoutDeposit.setVisibility(View.VISIBLE);
        }

        // for addon with price
        if (!cartItemList.get(position).getFoodAddonWithPrice().equals("empty")) {

            AddonWithPrice addon = gson.fromJson(cartItemList.get(position).getFoodAddonWithPrice(),
                    new TypeToken<AddonWithPrice>() {
                    }.getType());
            if (addon != null) {
                holder.binding.layoutAddon.setVisibility(View.VISIBLE);
                holder.binding.txtFoodAddonWithPriceCart.setText(new StringBuilder(addon.getName()));
            }
        }

        // for addon image
        if (!cartItemList.get(position).getFoodAddon().equals("empty")) {

            List<AddonModel> addonList = gson.fromJson(cartItemList.get(position).getFoodAddon()
                    , new TypeToken<List<AddonModel>>() {
                    }.getType());

            CartAddonImageAdapter adapter = new CartAddonImageAdapter(context, addonList);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
            layoutManager.setReverseLayout(true);
            holder.binding.recyclerAddonImage.setHasFixedSize(true);
            holder.binding.recyclerAddonImage.setLayoutManager(layoutManager);
            holder.binding.recyclerAddonImage.setAdapter(adapter);

            holder.binding.recyclerAddonImage.setVisibility(View.VISIBLE);

        }


    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final OrderFoodLayoutBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = OrderFoodLayoutBinding.bind(itemView);

        }
    }


}
