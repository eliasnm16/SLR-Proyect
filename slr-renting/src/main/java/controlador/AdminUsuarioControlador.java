package controlador;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AdminUsuarioControlador {

    @FXML
    private void volverAlPanelAdmin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/vista/PanelAdmin.fxml")
            );

            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.setTitle("Panel Admin");
            stage.show();

        } catch (Exception e) {
            System.out.println("❌ ERROR al volver al panel admin");
            e.printStackTrace();
        }
    }
}

