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

    private final ClienteDAO clienteDAO = new ClienteDAO();

       private boolean editing = false;
    private ClienteDTO editingCliente = null;

   
    public void setCliente(ClienteDTO cliente) {
        if (cliente == null) return;
        this.editing = true;
        this.editingCliente = cliente;

        // Rellenar campos con los datos del cliente
        txtNombre.setText(cliente.getNombreCompleto());
        txtCorreo.setText(cliente.getCorreo());
        txtNif.setText(cliente.getNif_nie());
        txtTelefono.setText(cliente.getTelefono());
        txtPassword.setText(cliente.getContrasena());
        chkCarnet.setSelected(cliente.isCarnet());

   
        btnRegistro.setText("Guardar cambios");
    }

   
    @FXML
    private void onAcceder(ActionEvent event) {
        // Validación mínima
        String nombre = txtNombre.getText().trim();
        String correo = txtCorreo.getText().trim();
        String contrasena = txtPassword.getText();

        if (nombre.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Datos incompletos", "Nombre, correo y contraseña son obligatorios.");
            return;
        }

        if (editing && editingCliente != null) {
            // Actualizar DTO con los valores del form
            editingCliente.setNombreCompleto(nombre);
            editingCliente.setCorreo(correo);
            editingCliente.setContrasena(contrasena);
            editingCliente.setNif_nie(txtNif.getText().trim());
            editingCliente.setTelefono(txtTelefono.getText().trim());
            editingCliente.setCarnet(chkCarnet.isSelected());
            clienteDAO.modificarCliente(editingCliente, editingCliente.getIdCliente());

            mostrarAlerta(Alert.AlertType.INFORMATION, "Actualizado", "Cliente actualizado correctamente.");

            // Cerrar la ventana de edición
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();

        } else {
            // Nuevo cliente

            String nif = txtNif.getText().trim();

            // Comprobación de duplicados
            if (clienteDAO.existeNif(nif)) {
                mostrarAlerta(Alert.AlertType.WARNING,
                        "NIF duplicado",
                        "Ya existe un cliente registrado con ese NIF/NIE.");
                return;
            }

            if (clienteDAO.existeCorreo(correo)) {
                mostrarAlerta(Alert.AlertType.WARNING,
                        "Correo duplicado",
                        "Ya existe un cliente registrado con ese correo.");
                return;
            }

            ClienteDTO nuevo = new ClienteDTO();
            nuevo.setNombreCompleto(nombre);
            nuevo.setCorreo(correo);
            nuevo.setContrasena(contrasena);
            nuevo.setNif_nie(nif);
            nuevo.setTelefono(txtTelefono.getText().trim());
            nuevo.setCarnet(chkCarnet.isSelected());

            clienteDAO.registrarCliente(nuevo);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Registrado", "Cliente registrado correctamente.");

            // Redirigir al login
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