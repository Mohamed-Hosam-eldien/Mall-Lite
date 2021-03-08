package mohamed.parko.hosam.deliveryshop.Database.favorite;


import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import mohamed.parko.hosam.deliveryshop.Database.address.AddressItem;

public interface FavoriteDataSource {

    Completable insertOrReplaceAll(FavoriteItem food);

    Flowable<List<FavoriteItem>> getAllFavorite(String uid);

    Single<FavoriteItem> checkItemInFavorite(String foodId , String uid);

    Single<Integer> deleteFavoriteItem(FavoriteItem food);

}
