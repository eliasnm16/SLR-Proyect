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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import controlador.AlertUtils;

public class LoginUsuarioRegistradoControlador implements Initializable {

    public static ClienteDTO usuarioActual;

    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnAcceder;

    @FXML private Label lblErrorEmail;
    @FXML private Label lblErrorPassword;

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

        // Limpiar errores previos
        limpiarErrores();

        String email = (txtEmail.getText() == null) ? "" : txtEmail.getText().trim();
        String pass  = (txtPassword.getText() == null) ? "" : txtPassword.getText().trim();

        boolean valido = true;

        // Validar email
        if (email.isEmpty()) {
            mostrarError(lblErrorEmail, "El email no puede estar vacío");
            valido = false;
        } else if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            mostrarError(lblErrorEmail, "Formato de correo inválido");
            valido = false;
        }

        // Validar contraseña
        if (pass.isEmpty()) {
            mostrarError(lblErrorPassword, "La contraseña no puede estar vacía");
            valido = false;
        }

        // Si hay errores de validación básica, no continuar
        if (!valido) return;

        // Primero verificamos si el correo existe (en cliente o admin)
        boolean correoExiste = verificarCorreoExiste(email);

        if (!correoExiste) {
            mostrarError(lblErrorEmail, "✗ Este correo no está registrado");
            return;
        }

        // Si el correo existe, verificamos la contraseña
        ClienteDTO cliente = buscarUsuario(email, pass);

        if (cliente == null) {
            AlertUtils.error("Acceso denegado", "Usuario o contraseña incorrectos.");
            // El correo existe pero la contraseña es incorrecta
            mostrarError(lblErrorPassword, "✗ Contraseña incorrecta");
            return;
        }

        usuarioActual = cliente;

        if (cliente.getAdmin()) abrirPanelAdmin(event);
        else abrirPanelUsuario(event);
    }

    /**
     * Verifica si un correo existe en la base de datos (en cliente o admin)
     */
    private boolean verificarCorreoExiste(String email) {
        String sqlCliente = "SELECT COUNT(*) as count FROM cliente WHERE CORREO = ?";
        String sqlAdmin = "SELECT COUNT(*) as count FROM admin WHERE CORREO = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmtCliente = conn.prepareStatement(sqlCliente);
             PreparedStatement stmtAdmin = conn.prepareStatement(sqlAdmin)) {

            // Verificar en cliente
            stmtCliente.setString(1, email);
            ResultSet rsCliente = stmtCliente.executeQuery();
            if (rsCliente.next() && rsCliente.getInt("count") > 0) {
                return true;
            }

            // Verificar en admin
            stmtAdmin.setString(1, email);
            ResultSet rsAdmin = stmtAdmin.executeQuery();
            if (rsAdmin.next() && rsAdmin.getInt("count") > 0) {
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    
    private void limpiarErrores() {
        if (lblErrorEmail != null) {
            lblErrorEmail.setVisible(false);
            lblErrorEmail.setText("");
        }
        if (lblErrorPassword != null) {
            lblErrorPassword.setVisible(false);
            lblErrorPassword.setText("");
        }
    }

    
    private void mostrarError(Label label, String mensaje) {
        if (label == null) return;
        label.setText(mensaje);
        label.setVisible(true);
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

    // Este método ya no se usa directamente, pero lo dejo por compatibilidad
    private void mostrarErrorAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
