package controlador;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

public class PanelAdminControlador {

    @FXML
    private ComboBox<String> comboAdmin;

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
    private void irAAlquileres(ActionEvent event) {
        cargarVista("/vista/Adminalquiler.fxml", "Administración de Alquileres", event);
    }

    @FXML
    private void logout(ActionEvent event) {
        if (comboAdmin == null) return;

        String opcion = comboAdmin.getValue();
        if (opcion == null) return;

        switch (opcion) {
            case "Perfil":
                // Cambia la ruta si tu FXML de perfil del admin se llama distinto
                cargarVista("/vista/PanelConfigUser.fxml", "Perfil", event);
                break;

            case "Logout":
                AlertUtils.warning("Cerrar sesión", "Vas a cerrar sesión y volver al login.");
                cargarVista("/vista/Loginusuarioregistrado.fxml", "Login", event);
                break;

            default:
                // Nada
                break;
        }

        // opcional: limpiar selección para que no dispare cosas raras al volver
        // comboAdmin.getSelectionModel().clearSelection();
    }

    private void cargarVista(String rutaFxml, String titulo, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFxml));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(titulo);
            stage.show();

        } catch (Exception e) {
            AlertUtils.error(
                "Error al abrir pantalla",
                "No se pudo cargar la vista:\n" + rutaFxml + "\n\nDetalle: " + e.getMessage()
            );
            System.out.println("ERROR cargando vista: " + rutaFxml);
            e.printStackTrace();
        }
    }
}
