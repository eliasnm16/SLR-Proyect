package controlador;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

import conexion.ConexionBD;
import dto.ClienteDTO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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
        
        // Hacer el campo NIF de solo lectura
        txtNif.setEditable(false);
        txtNif.setStyle("-fx-background-color: #1a1a1a; -fx-text-fill: #888888;");
    }

    public void cargarUsuario(ClienteDTO u) {
        this.usuario = u;

        if (u == null) return;

        txtNombre.setText(u.getNombreCompleto());
        txtCorreo.setText(u.getCorreo());
        txtNif.setText(u.getNif_nie());
        txtTelefono.setText(u.getTelefono());
        txtPassword.setText(u.getContrasena());
        chkCarnet.setSelected(u.isCarnet());
    }

    private void guardarCambios() {
        if (usuario == null) {
            mostrarError("No hay usuario cargado.");
            return;
        }

        String nombre = txtNombre.getText().trim();
        String correo = txtCorreo.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String password = txtPassword.getText().trim();

        // Validaciones
        if (!validarNombre(nombre)) return;
        if (!validarCorreo(correo)) return;
        if (!validarTelefono(telefono)) return;
        if (!validarPassword(password)) return;

        // Verificar si el correo ya existe en otro usuario
        if (correoYaExisteEnOtroUsuario()) {
            mostrarError("El correo electrónico ya está registrado por otro usuario.");
            return;
        }

        // Verificar si el teléfono ya existe en otro usuario
        if (telefonoYaExisteEnOtroUsuario()) {
            mostrarError("El teléfono ya está registrado por otro usuario.");
            return;
        }

        // Actualizar objeto usuario (NIF NO se toca)
        usuario.setNombreCompleto(nombre);
        usuario.setCorreo(correo);
        usuario.setTelefono(telefono);
        usuario.setContrasena(password);
        usuario.setCarnet(chkCarnet.isSelected());

        if (actualizarCliente(usuario)) {
            mostrarExito("Datos actualizados correctamente.");
        }
    }


    private boolean correoYaExisteEnOtroUsuario() {
        String sql = "SELECT COUNT(*) FROM cliente WHERE CORREO = ? AND ID_CLIENTE != ?";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, txtCorreo.getText().trim());
            stmt.setInt(2, usuario.getIdCliente());
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean telefonoYaExisteEnOtroUsuario() {
        String telefono = txtTelefono.getText().trim();
        if (telefono.isEmpty()) return false;
        
        String sql = "SELECT COUNT(*) FROM cliente WHERE TELEFONO = ? AND ID_CLIENTE != ?";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, telefono);
            stmt.setInt(2, usuario.getIdCliente());
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean actualizarCliente(ClienteDTO c) {
        // IMPORTANTE: NO incluir NIF_NIE en la actualización
        String sql = "UPDATE cliente " +
                     "SET NOMBRE_COMPLETO=?, CORREO=?, TELEFONO=?, CONTRASENA=?, CARNET=? " +
                     "WHERE ID_CLIENTE=?";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, c.getNombreCompleto());
            stmt.setString(2, c.getCorreo());
            stmt.setString(3, c.getTelefono());
            stmt.setString(4, c.getContrasena());
            stmt.setBoolean(5, c.isCarnet());
            stmt.setInt(6, c.getIdCliente());
            
            int filasActualizadas = stmt.executeUpdate();
            return filasActualizadas > 0;
            
        } catch (Exception e) {
            mostrarError("Error al actualizar datos: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private boolean validarNombre(String nombre) {
        if (nombre.isEmpty()) {
            mostrarError("El nombre es obligatorio.");
            return false;
        }

        // Letras, espacios y acentos (sin números)
        if (!nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) {
            mostrarError("El nombre no puede contener números ni símbolos.");
            return false;
        }

        if (nombre.length() < 2) {
            mostrarError("El nombre es demasiado corto.");
            return false;
        }

        return true;
    }

    private boolean validarCorreo(String correo) {
        if (correo.isEmpty()) {
            mostrarError("El correo es obligatorio.");
            return false;
        }

        // Email estándar
        String regexEmail = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (!correo.matches(regexEmail)) {
            mostrarError("El formato del correo electrónico no es válido.");
            return false;
        }

        return true;
    }

    private boolean validarTelefono(String telefono) {
        if (telefono.isEmpty()) {
            mostrarError("El teléfono es obligatorio.");
            return false;
        }

        // Exactamente 9 números
        if (!telefono.matches("^\\d{9}$")) {
            mostrarError("El teléfono debe tener exactamente 9 números.");
            return false;
        }

        return true;
    }

    private boolean validarPassword(String password) {
        if (password.isEmpty()) {
            mostrarError("La contraseña es obligatoria.");
            return false;
        }

        if (password.length() < 6) {
            mostrarError("La contraseña debe tener al menos 6 caracteres.");
            return false;
        }

        return true;
    }


    // Métodos para mostrar mensajes
    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    private void mostrarExito(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void volver() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/PanelMainUser.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Panel Usuario");
            stage.show();
        } catch (Exception e) {
            mostrarError("Error al volver: " + e.getMessage());
            e.printStackTrace();
        }
    }
}