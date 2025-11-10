package vista;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        Label lbl = new Label("Bienvenido al Renting de Coches SLR");
        Scene scene = new Scene(lbl, 400, 200);
        stage.setTitle("SLR Renting");
        stage.setScene(scene);
        stage.show();
    }
}
