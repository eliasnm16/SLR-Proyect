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
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginUsuarioRegistradoControlador {

    public static ClienteDTO usuarioActual;

    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblErrorEmail;
    @FXML private Label lblErrorPassword;

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
        // Limpiar errores previos
        limpiarErrores();
        
        String email = txtEmail.getText();
        String pass = txtPassword.getText();

        boolean valido = true;

        // Validar email
        if (email == null || email.trim().isEmpty()) {
            mostrarError(lblErrorEmail, "El email no puede estar vacío");
            valido = false;
        } else if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            mostrarError(lblErrorEmail, "Formato de correo inválido");
            valido = false;
        }

        // Validar contraseña
        if (pass == null || pass.trim().isEmpty()) {
            mostrarError(lblErrorPassword, "La contraseña no puede estar vacía");
            valido = false;
        }

        // Si hay errores de validación básica, no continuar
        if (!valido) {
            return;
        }

        email = email.trim();
        pass = pass.trim();

        // Primero verificamos si el correo existe (en cliente o admin)
        boolean correoExiste = verificarCorreoExiste(email);
        
        if (!correoExiste) {
            mostrarError(lblErrorEmail, "✗ Este correo no está registrado");
            return;
        }

        // Si el correo existe, verificamos la contraseña
        ClienteDTO cliente = buscarUsuario(email, pass);

        if (cliente == null) {
            // El correo existe pero la contraseña es incorrecta
            mostrarError(lblErrorPassword, "✗ Contraseña incorrecta");
            return;
        }

        usuarioActual = cliente;

        if (cliente.getAdmin()) {
            abrirPanelAdmin(event);
        } else {
            abrirPanelUsuario(event);
        }
    }

    /**
     * Verifica si un correo existe en la base de datos (en cliente o admin)
     */
    private boolean verificarCorreoExiste(String email) {
        // Verificar en tabla cliente
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

    /**
     * Limpia todos los mensajes de error
     */
    private void limpiarErrores() {
        lblErrorEmail.setVisible(false);
        lblErrorEmail.setText("");
        lblErrorPassword.setVisible(false);
        lblErrorPassword.setText("");
    }

    /**
     * Muestra un mensaje de error en un label específico
     */
    private void mostrarError(Label label, String mensaje) {
        label.setText(mensaje);
        label.setVisible(true);
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