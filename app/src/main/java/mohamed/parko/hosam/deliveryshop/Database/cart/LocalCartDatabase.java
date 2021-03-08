package mohamed.parko.hosam.deliveryshop.Database.cart;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;


public class LocalCartDatabase implements CartDataSource {

    CartDao cartDao;

    public LocalCartDatabase(CartDao cartDao) {
        this.cartDao = cartDao;
    }

    @Override
    public Flowable<List<CartItem>> getAllCart(String uid) {
        return cartDao.getAllCart(uid);
    }

    @Override
    public Single<Integer> countItemInCart(String uid) {
        return cartDao.countItemInCart(uid);
    }

    @Override
    public Single<Double> sumPriceInCart(String uid) {
        return cartDao.sumPriceInCart(uid);
    }

    @Override
    public Single<CartItem> getItemInCart(String foodId, String uid) {
        return cartDao.getItemInCart(foodId, uid);
    }


    @Override
    public Completable insertOrReplaceAll(CartItem cartItem) {
        return cartDao.insertOrReplaceAll(cartItem);
    }

    @Override
    public Single<Integer> updateCartItem(CartItem cartItem) {
        return cartDao.updateCartItem(cartItem);
    }

    @Override
    public Single<Integer> deleteCartItem(CartItem cartItem) {
        return cartDao.deleteCartItem(cartItem);
    }

    @Override
    public Single<Integer> cleanCart(String uid) {
        return cartDao.cleanCart(uid);
    }

    @Override
    public Single<CartItem> getItemWithAllOptionsInCart(String foodId, String uid, String foodAddon, String foodAddonWithPrice) {
        return cartDao.getItemWithAllOptionsInCart(foodId, uid, foodAddon, foodAddonWithPrice);
    }

    @Override
    public Single<Double> sumPriceTomorrowInCart(String uid, String state) {
        return cartDao.sumPriceTomorrowInCart(uid, state);
    }

    @Override
    public Single<Double> sumPriceAfterDaysInCart(String uid, String state) {
        return cartDao.sumPriceAfterDaysInCart(uid, state);
    }

    @Override
    public Flowable<List<CartItem>> getAllCartWithDeposit(String uid, String state) {
        return cartDao.getAllCartWithDeposit(uid, state);
    }

    @Override
    public Flowable<List<CartItem>> getAllCartTomorrow(String uid, String state) {
        return cartDao.getAllCartTomorrow(uid, state);
    }
}
