package controlador;

import java.net.URL;
import java.util.ResourceBundle;

import conexion.ConexionBD;
import dto.ClienteDTO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class PanelConfigUserControlador implements Initializable {

    @FXML private TextField txtNombre;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtNif;
    @FXML private TextField txtTelefono;
    @FXML private PasswordField txtPassword;
    @FXML private CheckBox chkCarnet;
    @FXML private Button btnGuardar;
    @FXML private Button btnVolver;

    private ClienteDTO usuario;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnGuardar.setOnAction(e -> guardarCambios());
        btnVolver.setOnAction(e -> volver());
    }

    public void cargarUsuario(ClienteDTO u) {
        usuario = u;
        txtNombre.setText(u.getNombreCompleto());
        txtCorreo.setText(u.getCorreo());
        txtNif.setText(u.getNif_nie());
        txtTelefono.setText(u.getTelefono());
        txtPassword.setText(u.getContrasena());
        chkCarnet.setSelected(u.isCarnet());
    }

    private void guardarCambios() {
        usuario.setNombreCompleto(txtNombre.getText());
        usuario.setCorreo(txtCorreo.getText());
        usuario.setNif_nie(txtNif.getText());
        usuario.setTelefono(txtTelefono.getText());
        usuario.setContrasena(txtPassword.getText());
        usuario.setCarnet(chkCarnet.isSelected());
        actualizarCliente(usuario);
    }

    private void actualizarCliente(ClienteDTO c) {
        String sql = "UPDATE cliente SET NOMBRE_COMPLETO=?, CORREO=?, NIF_NIE=?, TELEFONO=?, CONTRASENA=?, CARNET=? WHERE ID_CLIENTE=?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, c.getNombreCompleto());
            stmt.setString(2, c.getCorreo());
            stmt.setString(3, c.getNif_nie());
            stmt.setString(4, c.getTelefono());
            stmt.setString(5, c.getContrasena());
            stmt.setBoolean(6, c.isCarnet());
            stmt.setInt(7, c.getIdCliente());

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void volver() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/PanelMainUser.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
