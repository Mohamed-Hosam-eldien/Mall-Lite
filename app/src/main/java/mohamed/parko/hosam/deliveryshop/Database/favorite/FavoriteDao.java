package mohamed.parko.hosam.deliveryshop.Database.favorite;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;


@Dao
public interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertOrReplaceAll(FavoriteItem food);

    @Query("SELECT * FROM Favorite WHERE uid=:uid")
    Flowable<List<FavoriteItem>> getAllFavorite(String uid);

    @Query("SELECT * FROM Favorite WHERE foodId =:foodId AND uid=:uid")
    Single<FavoriteItem> checkItemInFavorite(String foodId , String uid);

    @Delete
    Single<Integer> deleteFavoriteItem(FavoriteItem food);

}
