package mohamed.parko.hosam.deliveryshop.Database.cart;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public interface CartDataSource {

    Flowable<List<CartItem>> getAllCart(String uid);

    Single<Integer> countItemInCart(String uid);

    Single<Double> sumPriceInCart(String uid);


    Single<CartItem> getItemInCart(String foodId , String uid);

    Completable insertOrReplaceAll(CartItem cartItem);

    Single<Integer> updateCartItem(CartItem cartItem);

    Single<Integer> deleteCartItem(CartItem cartItem);

    Single<Integer> cleanCart(String uid);

    Single<CartItem> getItemWithAllOptionsInCart(String foodId , String uid , String foodAddon, String foodAddonWithPrice);

    // state wil be empty ""
    Single<Double> sumPriceTomorrowInCart(String uid, String state);

    // state wil be empty "" but '=' will be '!='
    Single<Double> sumPriceAfterDaysInCart(String uid, String state);

    Flowable<List<CartItem>> getAllCartWithDeposit(String uid, String state);

    Flowable<List<CartItem>> getAllCartTomorrow(String uid, String state);

}
