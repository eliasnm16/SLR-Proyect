package controlador;

import dto.ClienteDTO;
import conexion.ConexionBD;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class LoginUsuarioRegistradoControlador {

    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;

    @FXML
    private void irARegistro(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/vista/Loginregistro.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onAcceder(ActionEvent event) {

        String email = txtEmail.getText().trim();
        String pass = txtPassword.getText().trim();

        if (email.isEmpty() || pass.isEmpty()) {
            mostrarError("Debe introducir email y contraseña");
            return;
        }

        ClienteDTO cliente = obtenerCliente(email);

        if (cliente == null) {
            mostrarError("Usuario no encontrado");
            return;
        }

        if (!cliente.getContrasena().equals(pass)) {
            mostrarError("Contraseña incorrecta");
            return;
        }

        if (cliente.getAdmin()) {
            abrirPanelAdmin(event);
        } else {
            mostrarError("En mantenimiento");
        }
    }

    private ClienteDTO obtenerCliente(String email) {
        String sql = "SELECT * FROM cliente WHERE CORREO = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                ClienteDTO c = new ClienteDTO();
                c.setIdCliente(rs.getInt("ID_CLIENTE"));
                c.setNombreCompleto(rs.getString("NOMBRE_COMPLETO"));
                c.setNif_nie(rs.getString("NIF_NIE"));
                c.setCorreo(rs.getString("CORREO"));
                c.setContrasena(rs.getString("CONTRASENA"));
                c.setCarnet(rs.getBoolean("CARNET"));
                c.setTelefono(rs.getString("TELEFONO"));

                if (rs.getDate("FECHA_REGISTRO") != null)
                    c.setFechaRegistro(rs.getDate("FECHA_REGISTRO").toLocalDate());

                c.setAdmin(rs.getBoolean("ADMIN"));
                return c;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void abrirPanelAdmin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/vista/PanelAdmin.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Panel Administrador");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}