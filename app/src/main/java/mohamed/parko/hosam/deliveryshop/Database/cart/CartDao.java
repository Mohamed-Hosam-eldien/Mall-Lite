package mohamed.parko.hosam.deliveryshop.Database.cart;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface CartDao {

    @Query("SELECT * FROM Cart WHERE uid=:uid")
    Flowable<List<CartItem>> getAllCart(String uid);

    // get all item in cart when state != ""
    @Query("SELECT * FROM Cart WHERE uid=:uid AND foodDeposit!=:state")
    Flowable<List<CartItem>> getAllCartWithDeposit(String uid, String state);

    // get all item in cart when state = ""
    @Query("SELECT * FROM Cart WHERE uid=:uid AND foodDeposit=:state")
    Flowable<List<CartItem>> getAllCartTomorrow(String uid, String state);

    @Query("SELECT COUNT(*) FROM cart WHERE uid=:uid")
    Single<Integer> countItemInCart(String uid);

    @Query("SELECT SUM (foodPrice * foodQuantity) FROM Cart WHERE uid=:uid")
    Single<Double> sumPriceInCart(String uid);


    @Query("SELECT * FROM Cart WHERE foodId =:foodId AND uid=:uid")
    Single<CartItem> getItemInCart(String foodId , String uid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertOrReplaceAll(CartItem cartItem);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    Single<Integer> updateCartItem(CartItem cartItem);

    @Delete
    Single<Integer> deleteCartItem(CartItem cartItem);

    @Query("DELETE FROM Cart WHERE uid=:uid")
    Single<Integer> cleanCart(String uid);

    @Query("SELECT * FROM Cart WHERE foodId =:foodId AND uid=:uid AND foodAddon=:foodAddon AND foodAddonWithPrice=:foodAddonWithPrice")
    Single<CartItem> getItemWithAllOptionsInCart(String foodId , String uid , String foodAddon, String foodAddonWithPrice);

    // state wil be empty ""
    @Query("SELECT SUM (foodPrice * foodQuantity) FROM Cart WHERE uid=:uid AND foodDeposit=:state")
    Single<Double> sumPriceTomorrowInCart(String uid, String state);

    // state wil be empty "" but '=' will be '!='
    @Query("SELECT SUM (foodPrice * foodQuantity) FROM Cart WHERE uid=:uid AND foodDeposit!=:state")
    Single<Double> sumPriceAfterDaysInCart(String uid, String state);

}
