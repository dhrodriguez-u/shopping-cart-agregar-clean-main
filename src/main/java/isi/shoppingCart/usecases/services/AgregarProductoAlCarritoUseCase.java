package isi.shoppingCart.usecases.services;

import isi.shoppingCart.entities.Cart;
import isi.shoppingCart.entities.Product;
import isi.shoppingCart.usecases.ports.CartRepository;
import isi.shoppingCart.usecases.ports.ProductRepository;

public class AgregarProductoAlCarritoUseCase {
    private ProductRepository productRepository;
    private CartRepository cartRepository;

    public AgregarProductoAlCarritoUseCase(ProductRepository productRepository,
                                           CartRepository cartRepository) {
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
    }

    public String execute(int productId) {
        Product product = productRepository.findById(productId);

        if (product == null) {
            return "Producto no encontrado.";
        }

        Cart cart = cartRepository.getCart();
        int quantityInCart = cart.getQuantityOfProduct(productId);

        if (quantityInCart >= product.getAvailableQuantity()) {
            return "No se puede agregar más cantidad de este producto. "
                    + "En el carrito solo se permite hasta la cantidad disponible.";
        }

        cart.addProduct(product);
        cartRepository.save(cart);

        return "";
    }
}
