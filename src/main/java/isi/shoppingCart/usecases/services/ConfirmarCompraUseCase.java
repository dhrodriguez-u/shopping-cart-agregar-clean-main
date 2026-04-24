package isi.shoppingCart.usecases.services;

import isi.shoppingCart.entities.Cart;
import isi.shoppingCart.entities.CartItem;
import isi.shoppingCart.entities.Product;
import isi.shoppingCart.usecases.ports.CartRepository;

import java.util.List;

public class ConfirmarCompraUseCase {

    private CartRepository cartRepository;

    public ConfirmarCompraUseCase(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public String execute() {

        Cart cart = cartRepository.getCart();


        if (cart.getItems().isEmpty()) {
            return "El carrito está vacío.";
        }

        List<CartItem> items = cart.getItems();


        for (int i = 0; i < items.size(); i++) {
            CartItem item = items.get(i);
            Product product = item.getProduct();

            int j;
            for (j = 0; j < item.getQuantity(); j++) {
                if (product.hasAvailableQuantity()) {
                    product.decreaseAvailableQuantity();
                }
            }
        }

        double total = cart.getTotal();


        cart = new Cart();
        cartRepository.save(cart);

        return "Compra realizada con éxito. Total: $" + total;
    }
}
