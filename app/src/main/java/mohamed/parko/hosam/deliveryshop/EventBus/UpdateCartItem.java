package mohamed.parko.hosam.deliveryshop.EventBus;

import mohamed.parko.hosam.deliveryshop.Database.cart.CartItem;

public class UpdateCartItem {

    private final CartItem cartItem;

    public UpdateCartItem(CartItem cartItem) {
        this.cartItem = cartItem;
    }

    public CartItem getCartItem() {
        return cartItem;
    }
}
