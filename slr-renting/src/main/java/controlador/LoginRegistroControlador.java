package controlador;

import dao.ClienteDAO;
import dto.ClienteDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginRegistroControlador {

    @FXML private TextField txtNombre;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtNif;
    @FXML private TextField txtTelefono;
    @FXML private PasswordField txtPassword;
    @FXML private CheckBox chkCarnet;
    @FXML private Button btnRegistro;
    @FXML private Button btnVolverLogin;
    
    // Labels de error para validación visual
    @FXML private Label lblErrorNombre;
    @FXML private Label lblErrorCorreo;
    @FXML private Label lblErrorNif;
    @FXML private Label lblErrorTelefono;
    @FXML private Label lblErrorPassword;

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private boolean editing = false;
    private ClienteDTO editingCliente = null;

    public void setCliente(ClienteDTO cliente) {
        if (cliente == null) return;
        this.editing = true;
        this.editingCliente = cliente;

        txtNombre.setText(cliente.getNombreCompleto());
        txtCorreo.setText(cliente.getCorreo());
        txtNif.setText(cliente.getNif_nie());
        txtTelefono.setText(cliente.getTelefono());
        txtPassword.setText(cliente.getContrasena());
        chkCarnet.setSelected(cliente.isCarnet());

        btnRegistro.setText("Guardar cambios");
    }

    /**
     * Limpia todos los mensajes de error
     */
    private void limpiarErrores() {
        lblErrorNombre.setVisible(false);
        lblErrorNombre.setText("");
        lblErrorCorreo.setVisible(false);
        lblErrorCorreo.setText("");
        lblErrorNif.setVisible(false);
        lblErrorNif.setText("");
        lblErrorTelefono.setVisible(false);
        lblErrorTelefono.setText("");
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

    /**
     * Valida todos los campos del formulario
     * @return true si todos los campos son válidos, false si hay errores
     */
    private boolean validarCampos() {
        boolean valido = true;
        limpiarErrores();

        String nombre = txtNombre.getText().trim();
        String correo = txtCorreo.getText().trim();
        String nif = txtNif.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String contrasena = txtPassword.getText();

        // VALIDACIÓN 1: Nombre no vacío
        if (nombre.isEmpty()) {
            mostrarError(lblErrorNombre, "El nombre no puede estar vacío");
            valido = false;
        }

        // VALIDACIÓN 2: Correo no vacío y formato válido
        if (correo.isEmpty()) {
            mostrarError(lblErrorCorreo, "El correo no puede estar vacío");
            valido = false;
        } else if (!correo.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            mostrarError(lblErrorCorreo, "Formato inválido (usuario@dominio.com)");
            valido = false;
        }

        // VALIDACIÓN 3: NIF/NIE no vacío y 9 caracteres
        if (nif.isEmpty()) {
            mostrarError(lblErrorNif, "El NIF/NIE no puede estar vacío");
            valido = false;
        } else if (nif.length() != 9) {
            mostrarError(lblErrorNif, "El NIF/NIE debe tener 9 caracteres");
            valido = false;
        }

        // VALIDACIÓN 4: Contraseña no vacía y mínimo 8 caracteres
        if (contrasena.isEmpty()) {
            mostrarError(lblErrorPassword, "La contraseña no puede estar vacía");
            valido = false;
        } else if (contrasena.length() < 8) {
            mostrarError(lblErrorPassword, "La contraseña necesita al menos 8 caracteres");
            valido = false;
        }

        // VALIDACIÓN 5: Teléfono obligatorio
        if (telefono.isEmpty()) {
            mostrarError(lblErrorTelefono, "El teléfono no puede estar vacío");
            valido = false;
        } else if (!telefono.matches("^[0-9]{9,}$")) {
            mostrarError(lblErrorTelefono, "El teléfono debe tener al menos 9 dígitos");
            valido = false;
        }

        return valido;
    }

    @FXML
    private void onAcceder(ActionEvent event) {
        // Validar todos los campos primero
        if (!validarCampos()) {
            return; // Detener si hay errores de validación
        }

        // Obtener datos ya validados
        String nombre = txtNombre.getText().trim();
        String correo = txtCorreo.getText().trim();
        String nif = txtNif.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String contrasena = txtPassword.getText();

        // VALIDACIÓN EXTRA: Verificar duplicados solo para NUEVOS registros
        if (!editing) {
            if (clienteDAO.existeClienteCon(nif, correo, telefono)) {
                String campoDuplicado = clienteDAO.obtenerCampoDuplicado(nif, correo, telefono);
                
                // Mostrar error en el campo correspondiente
                switch (campoDuplicado) {
                    case "NIF/NIE":
                        mostrarError(lblErrorNif, "Ya existe un usuario con este NIF/NIE");
                        break;
                    case "correo electrónico":
                        mostrarError(lblErrorCorreo, "Ya existe un usuario con este correo");
                        break;
                    case "teléfono":
                        mostrarError(lblErrorTelefono, "Ya existe un usuario con este teléfono");
                        break;
                }
                return;
            }
        }

        // Si estamos en modo edición
        if (editing && editingCliente != null) {
            // Actualizar DTO con los valores del form
            editingCliente.setNombreCompleto(nombre);
            editingCliente.setCorreo(correo);
            editingCliente.setContrasena(contrasena);
            editingCliente.setNif_nie(nif);
            editingCliente.setTelefono(telefono);
            editingCliente.setCarnet(chkCarnet.isSelected());
            clienteDAO.modificarCliente(editingCliente, editingCliente.getIdCliente());

            mostrarAlerta(Alert.AlertType.INFORMATION, "Actualizado", "Cliente actualizado correctamente.");

            // Cerrar la ventana de edición
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();

        } else {
            // Nuevo cliente
            ClienteDTO nuevo = new ClienteDTO();
            nuevo.setNombreCompleto(nombre);
            nuevo.setCorreo(correo);
            nuevo.setContrasena(contrasena);
            nuevo.setNif_nie(nif);
            nuevo.setTelefono(telefono);
            nuevo.setCarnet(chkCarnet.isSelected());

            try {
                clienteDAO.registrarCliente(nuevo);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Registrado", "Cliente registrado correctamente.");

                // Redirigir al login después de registro exitoso
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("/vista/loginusuarioregistrado.fxml"));
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Inicio de Sesión");
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                    mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo redirigir al login.");
                }
                
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error en el registro", 
                    "No se pudo completar el registro. Por favor, verifica los datos e intenta nuevamente.");
            }
        }
    }

    @FXML
    private void irALogin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/vista/loginusuarioregistrado.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Inicio de Sesión");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo volver al login.");
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String contenido) {
        Alert a = new Alert(tipo);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(contenido);
        a.showAndWait();
    }
}