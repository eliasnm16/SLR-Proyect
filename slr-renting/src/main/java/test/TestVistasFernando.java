// Archivo: TestVistas.java
package test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TestVistasFernando extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Probar vista de chofer
        Parent rootChofer = FXMLLoader.load(getClass().getResource("/vista/AñadirChofer.fxml"));
        Scene sceneChofer = new Scene(rootChofer);
        Stage stageChofer = new Stage();
        stageChofer.setTitle("Test - Añadir Chofer");
        stageChofer.setScene(sceneChofer);
        stageChofer.show();
        
        // Probar vista de coche
        Parent rootCoche = FXMLLoader.load(getClass().getResource("/vista/AñadirCoche.fxml"));
        Scene sceneCoche = new Scene(rootCoche);
        Stage stageCoche = new Stage();
        stageCoche.setTitle("Test - Añadir Coche");
        stageCoche.setScene(sceneCoche);
        stageCoche.setX(600); // Posicionar a la derecha
        stageCoche.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}