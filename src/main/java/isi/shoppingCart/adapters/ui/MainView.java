package isi.shoppingCart.adapters.ui;

import isi.shoppingCart.entities.CartItem;
import isi.shoppingCart.entities.Product;
import isi.shoppingCart.infrastructure.repositories.InMemoryCartRepository;
import isi.shoppingCart.infrastructure.repositories.InMemoryProductRepository;
import isi.shoppingCart.usecases.ports.CartRepository;
import isi.shoppingCart.usecases.ports.ProductRepository;
import isi.shoppingCart.usecases.services.AgregarProductoAlCarritoUseCase;
import isi.shoppingCart.usecases.services.ConfirmarCompraUseCase;
import isi.shoppingCart.usecases.services.ShoppingCartApp;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class MainView {
    private ShoppingCartApp shoppingCartApp;

    private VBox catalogBox;
    private VBox cartBox;
    private Label totalLabel;

    public MainView() {
        ProductRepository productRepository = new InMemoryProductRepository();
        CartRepository cartRepository = new InMemoryCartRepository(productRepository);

        AgregarProductoAlCarritoUseCase agregarProductoAlCarritoUseCase =
                new AgregarProductoAlCarritoUseCase(productRepository, cartRepository);

        ConfirmarCompraUseCase confirmarCompraUseCase =
                new ConfirmarCompraUseCase(cartRepository);

        shoppingCartApp = new ShoppingCartApp(
                productRepository,
                cartRepository,
                agregarProductoAlCarritoUseCase,
                confirmarCompraUseCase
        );

        catalogBox = new VBox(10);
        cartBox = new VBox(10);
        totalLabel = new Label("Total: $ 0.0");
    }

    public Scene createScene() {
        VBox catalogPanel = createCatalogPanel();
        VBox cartPanel = createCartPanel();

        HBox content = new HBox(20);
        content.setPadding(new Insets(15));
        content.getChildren().addAll(catalogPanel, cartPanel);

        HBox.setHgrow(catalogPanel, Priority.ALWAYS);
        HBox.setHgrow(cartPanel, Priority.ALWAYS);

        refreshCatalog();
        refreshCart();

        BorderPane root = new BorderPane();
        root.setCenter(content);

        return new Scene(root, 900, 450);
    }

    private VBox createCatalogPanel() {
        Label title = new Label("Catalogo");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        VBox panel = new VBox(10);
        panel.getChildren().addAll(title, catalogBox);
        panel.setPrefWidth(430);
        panel.setStyle("-fx-border-color: lightgray; -fx-padding: 10;");
        return panel;
    }

    private VBox createCartPanel() {
        Label title = new Label("Carrito");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button confirmButton = new Button("Confirmar compra");


        confirmButton.setOnAction(event -> {

            List<CartItem> items = shoppingCartApp.getCartItems();

            if (items.isEmpty()) {
                showError("El carrito está vacío.");
                return;
            }

            StringBuilder detalle = new StringBuilder();
            double total = 0;

            for (CartItem item : items) {
                double subtotal = item.getSubtotal();
                total += subtotal;

                detalle.append(item.getProduct().getName())
                        .append(" | Precio: $").append(item.getProduct().getPrice())
                        .append(" | Cantidad: ").append(item.getQuantity())
                        .append(" | Subtotal: $").append(subtotal)
                        .append("\n");
            }

            detalle.append("\nTOTAL: $").append(total);

            String message = shoppingCartApp.confirmPurchase();

            if (message.contains("éxito")) {
                showMessage("Compra realizada con éxito:\n\n" + detalle.toString());
            } else {
                showError(message);
            }

            refreshCatalog();
            refreshCart();
        });

        VBox panel = new VBox(10);
        panel.getChildren().addAll(title, cartBox, totalLabel, confirmButton);
        panel.setPrefWidth(430);
        panel.setStyle("-fx-border-color: lightgray; -fx-padding: 10;");
        return panel;
    }

    private void refreshCatalog() {
        catalogBox.getChildren().clear();

        List<Product> products = shoppingCartApp.getCatalogProducts();

        for (Product product : products) {
            HBox row = new HBox(10);

            Label nameLabel = new Label(product.getName());
            Label priceLabel = new Label("$ " + product.getPrice());
            Label stockLabel = new Label("Disponible: " + product.getAvailableQuantity());
            Button addButton = new Button("Agregar");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            addButton.setOnAction(event -> {
                String message = shoppingCartApp.addProductToCart(product.getId());

                if (!message.equals("")) {
                    showError(message);
                }

                refreshCatalog();
                refreshCart();
            });

            row.getChildren().addAll(nameLabel, priceLabel, stockLabel, spacer, addButton);
            row.setStyle("-fx-padding: 5; -fx-border-color: #DDDDDD;");

            catalogBox.getChildren().add(row);
        }
    }

    private void refreshCart() {
        cartBox.getChildren().clear();

        List<CartItem> items = shoppingCartApp.getCartItems();

        for (CartItem item : items) {
            HBox row = new HBox(10);

            Label nameLabel = new Label(item.getProduct().getName());
            Label quantityLabel = new Label("Cantidad: " + item.getQuantity());
            Label subtotalLabel = new Label("Subtotal: $ " + item.getSubtotal());

            row.getChildren().addAll(nameLabel, quantityLabel, subtotalLabel);
            row.setStyle("-fx-padding: 5; -fx-border-color: #DDDDDD;");

            cartBox.getChildren().add(row);
        }

        totalLabel.setText("Total: $ " + shoppingCartApp.getCartTotal());
    }

    private void showMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Mensaje");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}