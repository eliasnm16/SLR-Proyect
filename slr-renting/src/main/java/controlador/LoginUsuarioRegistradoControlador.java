package controlador;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LoginUsuarioRegistradoControlador {

    @FXML
    private void irARegistro(ActionEvent event) {
        try {
            // Cambia la ruta según tu proyecto
            Parent root = FXMLLoader.load(getClass().getResource("/vista/Loginregistro.fxml"));
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void onAcceder(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/vista/PanelAdmin.fxml")
            );
            Parent root = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource())
                    .getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Panel Admin");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace(); 
        }
    } 

} 
    
    

