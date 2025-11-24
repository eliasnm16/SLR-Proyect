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
    @FXML private Button btnRegistrar; // coincide con fx:id="btnRegistrar"
    @FXML private Button btnSalir;     // IMPORTANT: asegúrate de que el FXML tenga fx:id="btnSalir"

    private ChoferDAO choferDAO;

    // Modo edición
    private boolean editing = false;
    private ChoferDTO editingChofer = null;

    @FXML
    public void initialize() {
        System.out.println("✅ Initialize ejecutado");
        this.choferDAO = new ChoferDAO();
        System.out.println("✅ ChoferDAO instanciado");

        // Opcional: valor por defecto de checkbox
        chkDisposicion.setSelected(true);
    }

    /**
     * Método que AdminChoferControlador invocará por reflexión:
     * loader.getController().getClass().getMethod("setChofer", ChoferDTO.class).invoke(controller, chofer);
     */
    public void setChofer(ChoferDTO chofer) {
        if (chofer == null) return;
        System.out.println("🔁 setChofer recibido: ID=" + chofer.getId_chofer());
        this.editing = true;
        this.editingChofer = chofer;

        // Rellenar campos con datos existentes
        txtNombreCompleto.setText(chofer.getNombre_completo());
        txtDni.setText(chofer.getDni());
        txtTelefono.setText(chofer.getTelefono());
        chkDisposicion.setSelected(chofer.isDisposicion());

        // Mejorar UX: indicar que se guardarán cambios
        btnRegistrar.setText("Guardar cambios");
    }

    @FXML
    private void registrarChofer(ActionEvent event) {
        System.out.println("🎯 BOTÓN FUNCIONANDO!");

        try {
            // Validaciones básicas
            String nombre = txtNombreCompleto.getText().trim();
            String dni = txtDni.getText().trim().toUpperCase();
            String telefono = txtTelefono.getText().trim();
            boolean disposicion = chkDisposicion.isSelected();

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

            if (editing && editingChofer != null) {
                // Actualizamos el DTO y llamamos al DAO para modificar
                editingChofer.setNombre_completo(nombre);
                editingChofer.setDni(dni);
                editingChofer.setTelefono(telefono);
                editingChofer.setDisposicion(disposicion);

                System.out.println("♻️ Actualizando chofer ID=" + editingChofer.getId_chofer());
                choferDAO.modificarChofer(editingChofer, editingChofer.getId_chofer());

                mostrarAlerta("Éxito", "Chofer actualizado correctamente", Alert.AlertType.INFORMATION);
            } else {
                // Nuevo chofer
                ChoferDTO nuevoChofer = new ChoferDTO(
                        nombre,
                        dni,
                        telefono,
                        disposicion
                );

                System.out.println("💾 Creando chofer en BD...");
                choferDAO.registrarChofer(nuevoChofer);
                System.out.println("✅ Chofer guardado en BD");

                mostrarAlerta("Éxito", "Chofer registrado correctamente en la base de datos", Alert.AlertType.INFORMATION);
            }

            // Cerrar la ventana/modal
            Stage stage = (Stage) btnRegistrar.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            System.err.println("❌ Error guardando chofer: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error", "Error al guardar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void salir(ActionEvent event) {
        // Cerrar la ventana sin guardar
        Stage stage = (Stage) btnSalir.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /** útil para limpiar si se reusa el controlador sin cerrar (no estrictamente necesario) */
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