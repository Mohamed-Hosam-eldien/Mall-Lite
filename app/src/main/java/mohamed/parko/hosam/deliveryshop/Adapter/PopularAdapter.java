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

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import mohamed.parko.hosam.deliveryshop.Callback.IRecyclerClickListener;
import mohamed.parko.hosam.deliveryshop.Common.Common;
import mohamed.parko.hosam.deliveryshop.Database.favorite.FavoriteDataSource;
import mohamed.parko.hosam.deliveryshop.Database.favorite.FavoriteItem;
import mohamed.parko.hosam.deliveryshop.EventBus.PopularClickEvent;
import mohamed.parko.hosam.deliveryshop.Model.PopularModel;
import mohamed.parko.hosam.deliveryshop.R;
import mohamed.parko.hosam.deliveryshop.databinding.PopularLayoutBinding;


public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.ViewHolder> {

    private final Context context;
    private final List<PopularModel> popularModels;
    private final CompositeDisposable compositeDisposable;
    private final FavoriteDataSource favoriteDataSource;

    public boolean showShimmer = true;

    public PopularAdapter(Context context, List<PopularModel> popularModels, FavoriteDataSource favoriteDataSource ,CompositeDisposable compositeDisposable) {
        this.context = context;
        this.popularModels = popularModels;
        this.compositeDisposable = compositeDisposable;
        this.favoriteDataSource = favoriteDataSource;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.popular_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if(showShimmer) {
            holder.binding.shimmerView.startShimmer();
        } else {
            holder.binding.shimmerView.stopShimmer();
            holder.binding.shimmerView.setShimmer(null);


            Glide.with(context).load(popularModels.get(position).getImage()).into(holder.binding.imagePopular);
            holder.binding.txtPopular.setText(ellipsize(popularModels.get(position).getName()));
            favoriteDataSource.checkItemInFavorite(popularModels.get(position).getFood_id(), Common.currentUser.getUid())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<FavoriteItem>() {
                        @Override
                        public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                        }

                        @Override
                        public void onSuccess(@io.reactivex.annotations.NonNull FavoriteItem favoriteItem) {
                            if (favoriteItem.getFoodId().equals(popularModels.get(position).getFood_id()))
                                holder.binding.imgFav.setChecked(true);
                        }

                        @Override
                        public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        }
                    });
            holder.setClickListener((view, pos) ->
                    EventBus.getDefault().postSticky(new PopularClickEvent(popularModels.get(pos))));
            holder.binding.imgFav.setOnClickListener(view -> {
                FavoriteItem favoriteItem = new FavoriteItem();
                favoriteItem.setFoodId(popularModels.get(position).getFood_id());
                favoriteItem.setMenuId(popularModels.get(position).getMenu_id());
                favoriteItem.setUid(Common.currentUser.getUid());
                favoriteItem.setFoodImage(popularModels.get(position).getImage());
                favoriteItem.setFoodName(popularModels.get(position).getName());

                if (holder.binding.imgFav.isChecked()) {
                    compositeDisposable.add(favoriteDataSource.insertOrReplaceAll(favoriteItem)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(() -> holder.binding.imgFav.setChecked(true)));
                } else {
                    compositeDisposable.add(favoriteDataSource.deleteFavoriteItem(favoriteItem)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe((integer, throwable) -> holder.binding.imgFav.setChecked(false)));
                }

            });
        }

    }

    @Override
    public int getItemCount() {
        return popularModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        PopularLayoutBinding binding;
        IRecyclerClickListener clickListener;

        public void setClickListener(IRecyclerClickListener listener) {
            this.clickListener = listener;
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = PopularLayoutBinding.bind(itemView);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            clickListener.onItemClickListener(view,getAdapterPosition());
        }

    }

    private String ellipsize(String input) {
        if (input == null || input.length() < 18) {
            return input;
        }
        return input.substring(0, 18) + "...";
    }

}
