package mohamed.parko.hosam.deliveryshop.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import mohamed.parko.hosam.deliveryshop.ui.addresses.Addresses;
import mohamed.parko.hosam.deliveryshop.Callback.IRecyclerClickListener;
import mohamed.parko.hosam.deliveryshop.Model.MoreModel;
import mohamed.parko.hosam.deliveryshop.R;
import mohamed.parko.hosam.deliveryshop.databinding.MoreLayoutBinding;
import mohamed.parko.hosam.deliveryshop.ui.contactUs.ContactUs;
import mohamed.parko.hosam.deliveryshop.ui.discount.Discount;
import mohamed.parko.hosam.deliveryshop.ui.orders.Orders;
import mohamed.parko.hosam.deliveryshop.ui.personal.ProfilePage;

public class MoreAdapter extends RecyclerView.Adapter<MoreAdapter.ViewHolder> {

    private final Context context;
    private final List<MoreModel> moreList;

    public MoreAdapter(Context context, List<MoreModel> moreList) {
        this.context = context;
        this.moreList = moreList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.more_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.binding.txtMore.setText(moreList.get(position).getName());

        holder.binding.mainLayout.setBackgroundResource(moreList.get(position).getBackgroundColor());

        Glide.with(context).load(moreList.get(position).getImage())
                .into(holder.binding.imgMore);

        holder.setClickListener((view, pos) -> {
            switch (pos) {
                case 0:
                    context.startActivity(new Intent(context, ProfilePage.class));
                    break;
                case 1:
                    context.startActivity(new Intent(context, Orders.class));
                    break;
                case 2:
                    context.startActivity(new Intent(context, Addresses.class));
                    break;
                case 3:
                    context.startActivity(new Intent(context, Discount.class));
                    break;
                case 4:
                    context.startActivity(new Intent(context, ContactUs.class));
                    break;
            }
        });

    }

    @Override
    public int getItemCount() {
        return moreList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        MoreLayoutBinding binding;

        IRecyclerClickListener clickListener;

        public void setClickListener(IRecyclerClickListener clickListener) {
            this.clickListener = clickListener;
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = MoreLayoutBinding.bind(itemView);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClickListener(view, getAdapterPosition());
        }

    }


}
