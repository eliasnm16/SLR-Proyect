package controlador;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PanelAdminControlador {

    @FXML
    private void irAUsuarios(ActionEvent event) {
        cargarVista("/vista/Adminusuario.fxml", "Administración de Usuarios", event);
    }

    @FXML
    private void irAChofer(ActionEvent event) {
        cargarVista("/vista/Adminchofer.fxml", "Administración de Choferes", event);
    }

    @FXML
    private void irACoches(ActionEvent event) {
        cargarVista("/vista/Admincoche.fxml", "Administración de Coches", event);
    }

    @FXML
    private void logout(ActionEvent event) {
        cargarVista("/vista/Loginregistro.fxml", "Login", event);
    }


    private void cargarVista(String rutaFxml, String titulo, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFxml));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.setTitle(titulo);
            stage.show();

        } catch (Exception e) {
            System.out.println("ERROR cargando vista: " + rutaFxml);
            e.printStackTrace();
        }
    }
}
