package mohamed.parko.hosam.deliveryshop.ui.favorite;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import mohamed.parko.hosam.deliveryshop.Adapter.FavoriteAdapter;
import mohamed.parko.hosam.deliveryshop.Common.Common;
import mohamed.parko.hosam.deliveryshop.Common.SpacesItemDecoration;
import mohamed.parko.hosam.deliveryshop.Database.favorite.FavoriteDataSource;
import mohamed.parko.hosam.deliveryshop.Database.favorite.FavoriteDatabase;
import mohamed.parko.hosam.deliveryshop.Database.favorite.LocalFavoriteDatabase;
import mohamed.parko.hosam.deliveryshop.R;
import mohamed.parko.hosam.deliveryshop.databinding.FragmentFavoriteBinding;

public class FavoriteFragment extends Fragment {

    private FragmentFavoriteBinding binding;
    private CompositeDisposable compositeDisposable;
    private FavoriteAdapter adapter;
    private FavoriteDataSource favoriteDataSource;

    public FavoriteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFavoriteBinding.bind(inflater.inflate(R.layout.fragment_favorite, container, false));

        init();

        return binding.getRoot();
    }

    private void init() {

        compositeDisposable = new CompositeDisposable();
        favoriteDataSource = new LocalFavoriteDatabase(FavoriteDatabase.getInstance(getContext()).favoriteDao());

        binding.recycler.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(adapter != null) {

                    switch (adapter.getItemViewType(position)) {
                        case Common.DEFAULT_COMMON_COUNT: return 1;
                        case Common.FULL_WIDTH_COLUMN : return 2;
                        default: return -1;
                    }

                }
                return -1;
            }
        });
        binding.recycler.setLayoutManager(layoutManager);
        binding.recycler.addItemDecoration(new SpacesItemDecoration(8));

        getAllFavItems();

    }

    private void getAllFavItems() {

        compositeDisposable.add(favoriteDataSource.getAllFavorite(Common.currentUser.getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(favoriteItems -> {
                    if(favoriteItems.size() > 0) {
                        adapter = new FavoriteAdapter(getContext(), favoriteItems);
                        binding.recycler.setAdapter(adapter);
                        binding.recycler.setVisibility(View.VISIBLE);
                        binding.text.setVisibility(View.GONE);
                    } else {
                        binding.recycler.setVisibility(View.GONE);
                        binding.text.setVisibility(View.VISIBLE);
                    }
                }));

    }

    @Override
    public void onStop() {
        super.onStop();
        compositeDisposable.clear();
    }

    @Override
    public void onResume() {
        super.onResume();
        getAllFavItems();
    }


}