package isi.shoppingCart.usecases.ports;

import isi.shoppingCart.entities.Cart;

public interface CartRepository {
    Cart getCart();
    void save(Cart cart);
}
