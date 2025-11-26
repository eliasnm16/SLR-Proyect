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

    public static ClienteDTO usuarioActual;

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
        String email = txtEmail.getText();
        String pass = txtPassword.getText();

        if (email == null) email = "";
        if (pass == null) pass = "";

        email = email.trim();
        pass = pass.trim();

        if (email.isEmpty() || pass.isEmpty()) {
            mostrarError("Debe introducir email y contraseña");
            return;
        }

        ClienteDTO cliente = buscarUsuario(email, pass);

        if (cliente == null) {
            mostrarError("Usuario no encontrado");
            return;
        }

        usuarioActual = cliente;

        if (cliente.getAdmin()) {
            abrirPanelAdmin(event);
        } else {
            abrirPanelUsuario(event);
        }
    }

    private ClienteDTO buscarUsuario(String email, String pass) {
        ClienteDTO c = buscarEnTablaCliente(email, pass);
        if (c != null) {
            return c;
        }
        ClienteDTO a = buscarEnTablaAdmin(email, pass);
        if (a != null) {
            a.setAdmin(true);
            return a;
        }
        return null;
    }

    private ClienteDTO buscarEnTablaCliente(String email, String pass) {
        String sql = "SELECT * FROM cliente WHERE CORREO = ? AND CONTRASENA = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, pass);

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

                if (rs.getDate("FECHA_REGISTRO") != null) {
                    c.setFechaRegistro(rs.getDate("FECHA_REGISTRO").toLocalDate());
                }

                try {
                    c.setAdmin(rs.getBoolean("ADMIN"));
                } catch (Exception e) {
                    c.setAdmin(false);
                }

                return c;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private ClienteDTO buscarEnTablaAdmin(String email, String pass) {
        String sql = "SELECT * FROM admin WHERE CORREO = ? AND CONTRASENA = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, pass);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                ClienteDTO c = new ClienteDTO();

                try {
                    c.setIdCliente(rs.getInt("ID_CLIENTE"));
                } catch (Exception e) {
                    c.setIdCliente(0);
                }

                try {
                    c.setNombreCompleto(rs.getString("NOMBRE_COMPLETO"));
                } catch (Exception e) {}

                c.setCorreo(rs.getString("CORREO"));
                c.setContrasena(rs.getString("CONTRASENA"));
                c.setAdmin(true);

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

    private void abrirPanelUsuario(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/vista/PanelMainUser.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Panel Usuario");
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
