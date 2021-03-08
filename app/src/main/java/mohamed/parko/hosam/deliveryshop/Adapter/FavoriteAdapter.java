package mohamed.parko.hosam.deliveryshop.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.List;

import dmax.dialog.SpotsDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import mohamed.parko.hosam.deliveryshop.Callback.IRecyclerClickListener;
import mohamed.parko.hosam.deliveryshop.Common.Common;
import mohamed.parko.hosam.deliveryshop.Database.favorite.FavoriteDataSource;
import mohamed.parko.hosam.deliveryshop.Database.favorite.FavoriteDatabase;
import mohamed.parko.hosam.deliveryshop.Database.favorite.FavoriteItem;
import mohamed.parko.hosam.deliveryshop.Database.favorite.LocalFavoriteDatabase;
import mohamed.parko.hosam.deliveryshop.Model.CategoryModel;
import mohamed.parko.hosam.deliveryshop.Model.FoodModel;
import mohamed.parko.hosam.deliveryshop.R;
import mohamed.parko.hosam.deliveryshop.databinding.CategoryLayoutBinding;
import mohamed.parko.hosam.deliveryshop.ui.foodDetails.FoodDetails;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {

    private final Context context;
    private final List<FavoriteItem> favoriteItemList;
    private final AlertDialog dialog;
    private final CompositeDisposable compositeDisposable;
    private final FavoriteDataSource favoriteDataSource;

    public FavoriteAdapter(Context context, List<FavoriteItem> favoriteItemList) {
        this.context = context;
        this.favoriteItemList = favoriteItemList;
        dialog = new SpotsDialog.Builder().setContext(context)
                .setCancelable(false).setMessage("برجاء الإنتظار..").build();
        compositeDisposable = new CompositeDisposable();
        favoriteDataSource = new LocalFavoriteDatabase(FavoriteDatabase.getInstance(context).favoriteDao());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.category_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.shimmerView.stopShimmer();
        holder.binding.shimmerView.setShimmer(null);
        Glide.with(context).load(favoriteItemList.get(position).getFoodImage()).into(holder.binding.imageCategory);
        holder.binding.txtCategory.setText(favoriteItemList.get(position).getFoodName());

        holder.binding.imageDeleteItem.setVisibility(View.VISIBLE);
        holder.binding.imageDeleteItem.setOnClickListener(view -> removeItemFromFav(position));

        holder.setClickListener((view, position1) -> gotoFoodDetails(position));
    }

    private void removeItemFromFav(int position) {

        compositeDisposable.add(favoriteDataSource.deleteFavoriteItem(favoriteItemList.get(position))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((integer, throwable) -> {
                    favoriteItemList.remove(position);
                    notifyItemRemoved(position);
                }));

    }

    private void gotoFoodDetails(int position) {

        dialog.show();

        FirebaseDatabase.getInstance()
                .getReference(Common.CATEGORY_REF)
                .child(favoriteItemList.get(position).getMenuId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {

                            Common.Category_Selected = snapshot.getValue(CategoryModel.class);

                            FirebaseDatabase.getInstance()
                                    .getReference(Common.CATEGORY_REF)
                                    .child(favoriteItemList.get(position).getMenuId())
                                    .child("foods")
                                    .orderByChild("id")
                                    .equalTo(favoriteItemList.get(position).getFoodId())
                                    .limitToLast(1)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                            for (DataSnapshot post : snapshot.getChildren()) {

                                                if (post.exists()) {

                                                    Common.SelectedFood = post.getValue(FoodModel.class);
                                                    Common.SelectedFood.setKey(post.getKey());
                                                    context.startActivity(new Intent(context, FoodDetails.class));

                                                } else {
                                                    Toast.makeText(context, "هذا العنصر غير متوفر", Toast.LENGTH_SHORT).show();
                                                }
                                                dialog.dismiss();
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            dialog.dismiss();
                                        }
                                    });


                        } else {
                            Toast.makeText(context, "هذا الصنف غير متوفر", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        dialog.dismiss();
                    }
                });

    }

    @Override
    public int getItemCount() {
        return favoriteItemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(favoriteItemList.size() == 1)
            return Common.DEFAULT_COMMON_COUNT;
        else {
            if(favoriteItemList.size() % 2 == 0)
                return Common.DEFAULT_COMMON_COUNT;
            else
                return (position > 1 && position == favoriteItemList.size() -1) ? Common.FULL_WIDTH_COLUMN:Common.DEFAULT_COMMON_COUNT;
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
