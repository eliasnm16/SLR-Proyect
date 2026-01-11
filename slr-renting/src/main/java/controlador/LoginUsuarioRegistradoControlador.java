package controlador;

import conexion.ConexionBD;
import dto.ClienteDTO;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


import controlador.AlertUtils;

public class LoginUsuarioRegistradoControlador implements Initializable {

    public static ClienteDTO usuarioActual;

    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnAcceder;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        BooleanBinding camposIncompletos = txtEmail.textProperty().isEmpty()
                .or(txtPassword.textProperty().isEmpty());

        btnAcceder.disableProperty().bind(camposIncompletos);

        camposIncompletos.addListener((obs, oldV, incompleto) -> aplicarEstiloAcceder(!incompleto));
        aplicarEstiloAcceder(!(txtEmail.getText().isBlank() || txtPassword.getText().isBlank()));

        txtPassword.setOnAction(e -> {
            if (!btnAcceder.isDisabled()) btnAcceder.fire();
        });
    }

    private void aplicarEstiloAcceder(boolean activo) {
        if (btnAcceder == null) return;

        if (activo) {
            btnAcceder.setStyle(
                    "-fx-background-color: #2ecc71;" +
                    "-fx-text-fill: #111111;" +
                    "-fx-font-weight: bold;" +
                    "-fx-background-radius: 18;" +
                    "-fx-padding: 10 22 10 22;"
            );
        } else {
            btnAcceder.setStyle(
                    "-fx-background-color: #1a1a1a;" +
                    "-fx-text-fill: #777777;" +
                    "-fx-font-weight: bold;" +
                    "-fx-background-radius: 18;" +
                    "-fx-padding: 10 22 10 22;"
            );
        }
    }

    @FXML
    private void irARegistro(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/vista/Loginregistro.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

           
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.error("Error", "No se pudo abrir la pantalla de registro.");
        }
    }

    @FXML
    private void onAcceder(ActionEvent event) {
        String email = txtEmail.getText() == null ? "" : txtEmail.getText().trim();
        String pass  = txtPassword.getText() == null ? "" : txtPassword.getText().trim();

        if (email.isEmpty() || pass.isEmpty()) {
            AlertUtils.warning("Datos incompletos", "Debe introducir email y contraseña.");
            return;
        }

        ClienteDTO cliente = buscarUsuario(email, pass);

        if (cliente == null) {
            AlertUtils.error("Acceso denegado", "Usuario o contraseña incorrectos.");
            return;
        }

        usuarioActual = cliente;

        if (cliente.getAdmin()) abrirPanelAdmin(event);
        else abrirPanelUsuario(event);
    }

    private ClienteDTO buscarUsuario(String email, String pass) {
        ClienteDTO c = buscarEnTablaCliente(email, pass);
        if (c != null) return c;

        ClienteDTO a = buscarEnTablaAdmin(email, pass);
        if (a != null) {
            a.setAdmin(true);
            return a;
        }
        return null;
    }

    private ClienteDTO buscarEnTablaCliente(String email, String pass) {
        String sql = "SELECT * FROM cliente WHERE CORREO = ? AND CONTRASENA = ?";

        try (Connection conn = ConexionBD.getConnection()) {

            if (conn == null) {
                AlertUtils.error("Base de datos", "No hay conexión con la base de datos (conn = null). Revisa ConexionBD.getConnection().");
                return null;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
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

                    try { c.setAdmin(rs.getBoolean("ADMIN")); }
                    catch (Exception e) { c.setAdmin(false); }

                    return c;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.error("Error", "Error consultando la tabla cliente.");
        }

        return null;
    }

    private ClienteDTO buscarEnTablaAdmin(String email, String pass) {
        String sql = "SELECT * FROM admin WHERE CORREO = ? AND CONTRASENA = ?";

        try (Connection conn = ConexionBD.getConnection()) {

            if (conn == null) {
                AlertUtils.error("Base de datos", "No hay conexión con la base de datos (conn = null). Revisa ConexionBD.getConnection().");
                return null;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, email);
                stmt.setString(2, pass);

                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    ClienteDTO c = new ClienteDTO();

                    try { c.setIdCliente(rs.getInt("ID_CLIENTE")); }
                    catch (Exception e) { c.setIdCliente(0); }

                    try { c.setNombreCompleto(rs.getString("NOMBRE_COMPLETO")); }
                    catch (Exception ignored) {}

                    c.setCorreo(rs.getString("CORREO"));
                    c.setContrasena(rs.getString("CONTRASENA"));
                    c.setAdmin(true);

                    return c;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.error("Error", "Error consultando la tabla admin.");
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
            AlertUtils.error("Error", "No se pudo abrir el panel de administrador.");
        }
    }

    private void abrirPanelUsuario(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/PanelMainUser.fxml"));
            Parent root = loader.load();

            PanelMainUserControlador controlador = loader.getController();
            controlador.setNifUsuarioActual(usuarioActual.getNif_nie());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

         
            stage.setScene(new Scene(root));
            stage.setTitle("Panel Usuario");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.error("Error", "No se pudo abrir el panel de usuario.");
        }
    }
}
