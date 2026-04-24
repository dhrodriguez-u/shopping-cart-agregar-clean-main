package isi.shoppingCart.adapters.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    public void start(Stage stage) {
        MainView mainView = new MainView();
        Scene scene = mainView.createScene();

        stage.setTitle("Shopping Cart");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
