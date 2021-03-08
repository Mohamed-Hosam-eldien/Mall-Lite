package mohamed.parko.hosam.deliveryshop.Database.favorite;


import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import mohamed.parko.hosam.deliveryshop.Database.address.AddressItem;

public class LocalFavoriteDatabase implements FavoriteDataSource {

    private final FavoriteDao favoriteDao;

    public LocalFavoriteDatabase(FavoriteDao favoriteDao) {
        this.favoriteDao = favoriteDao;
    }


    @Override
    public Completable insertOrReplaceAll(FavoriteItem food) {
        return favoriteDao.insertOrReplaceAll(food);
    }

    @Override
    public Flowable<List<FavoriteItem>> getAllFavorite(String uid) {
        return favoriteDao.getAllFavorite(uid);
    }

    @Override
    public Single<FavoriteItem> checkItemInFavorite(String foodId, String uid) {
        return favoriteDao.checkItemInFavorite(foodId, uid);
    }

    @Override
    public Single<Integer> deleteFavoriteItem(FavoriteItem food) {
        return favoriteDao.deleteFavoriteItem(food);
    }
}
