package controlador;

import dao.ChoferDAO;
import dto.ChoferDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AñadirChoferControlador {

    @FXML private TextField txtNombreCompleto;
    @FXML private TextField txtDni;
    @FXML private TextField txtTelefono;
    @FXML private CheckBox chkDisposicion;
    @FXML private Button btnRegistrar;
    @FXML private Button btnSalir;     

    // DAO encargado de las operaciones en la tabla chofer
    private ChoferDAO choferDAO;

    
    private boolean editing = false;
    private ChoferDTO editingChofer = null;

    @FXML
    public void initialize() {
        System.out.println("Initialize ejecutado");

        // Instancia el DAO (conexión y métodos hacia la base de datos)
        this.choferDAO = new ChoferDAO();
        System.out.println("ChoferDAO instanciado");

            chkDisposicion.setSelected(true);
    }

    public void setChofer(ChoferDTO chofer) {
        if (chofer == null) return;

        System.out.println("setChofer recibido: ID=" + chofer.getId_chofer());

        // Activa modo edición
        this.editing = true;
        this.editingChofer = chofer;

        // Carga los datos en los campos
        txtNombreCompleto.setText(chofer.getNombre_completo());
        txtDni.setText(chofer.getDni());
        txtTelefono.setText(chofer.getTelefono());
        chkDisposicion.setSelected(chofer.isDisposicion());
        btnRegistrar.setText("Guardar cambios");
    }

    @FXML
    private void registrarChofer(ActionEvent event) {
        System.out.println("BOTÓN FUNCIONANDO!");

        try {
            // Obtener datos escritos por el usuario
            String nombre = txtNombreCompleto.getText().trim();
            String dni = txtDni.getText().trim().toUpperCase();
            String telefono = txtTelefono.getText().trim();
            boolean disposicion = chkDisposicion.isSelected();

            // Validaciones simples del formulario
            if (nombre.isEmpty()) {
                mostrarAlerta("Error", "El nombre es obligatorio", Alert.AlertType.WARNING);
                return;
            }
            if (dni.isEmpty()) {
                mostrarAlerta("Error", "El DNI es obligatorio", Alert.AlertType.WARNING);
                return;
            }
            if (telefono.isEmpty()) {
                mostrarAlerta("Error", "El teléfono es obligatorio", Alert.AlertType.WARNING);
                return;
            }

            // Si estamos en modo edición modifica el chofer existente
            if (editing && editingChofer != null) {

                // Actualizamos el DTO con los nuevos valores
                editingChofer.setNombre_completo(nombre);
                editingChofer.setDni(dni);
                editingChofer.setTelefono(telefono);
                editingChofer.setDisposicion(disposicion);

                System.out.println("Actualizando chofer ID=" + editingChofer.getId_chofer());

                // Llamada al DAO para guardar cambios en BD
                choferDAO.modificarChofer(editingChofer, editingChofer.getId_chofer());

                mostrarAlerta("Éxito", "Chofer actualizado correctamente", Alert.AlertType.INFORMATION);

            } else {
                // Registro de un nuevo chofer
                ChoferDTO nuevoChofer = new ChoferDTO(nombre,dni,telefono,disposicion);

                System.out.println("Creando chofer en BD...");
                choferDAO.registrarChofer(nuevoChofer);
                System.out.println("Chofer guardado en BD");

                mostrarAlerta("Éxito", "Chofer registrado correctamente en la base de datos", Alert.AlertType.INFORMATION);
            }

            // Cerrar la ventana después de guardar
            Stage stage = (Stage) btnRegistrar.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            System.err.println("Error guardando chofer: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error", "Error al guardar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void salir(ActionEvent event) {
        // Cierra la ventana sin realizar cambios
        Stage stage = (Stage) btnSalir.getScene().getWindow();
        stage.close();
    }

    // Método auxiliar para mostrar alertas de forma rápida
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    
    private void limpiarFormulario() {
        txtNombreCompleto.clear();
        txtDni.clear();
        txtTelefono.clear();
        chkDisposicion.setSelected(true);
        editing = false;
        editingChofer = null;
        btnRegistrar.setText("Registrar");
    }
}
