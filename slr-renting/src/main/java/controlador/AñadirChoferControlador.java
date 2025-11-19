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
    private ChoferDAO choferDAO;

    @FXML
    public void initialize() {
        System.out.println("✅ Initialize ejecutado");
        this.choferDAO = new ChoferDAO();
        System.out.println("✅ ChoferDAO instanciado");
    }

    @FXML
    private void registrarChofer() {
        System.out.println("🎯 BOTÓN FUNCIONANDO!");
        
        try {
            // Mostrar datos en consola
            System.out.println("📋 Datos del formulario:");
            System.out.println("Nombre: " + txtNombreCompleto.getText());
            System.out.println("DNI: " + txtDni.getText());
            System.out.println("Teléfono: " + txtTelefono.getText());
            System.out.println("Disposición: " + chkDisposicion.isSelected());

            // Validaciones básicas
            if (txtNombreCompleto.getText().trim().isEmpty()) {
                mostrarAlerta("Error", "El nombre es obligatorio", Alert.AlertType.WARNING);
                return;
            }

            if (txtDni.getText().trim().isEmpty()) {
                mostrarAlerta("Error", "El DNI es obligatorio", Alert.AlertType.WARNING);
                return;
            }

            if (txtTelefono.getText().trim().isEmpty()) {
                mostrarAlerta("Error", "El teléfono es obligatorio", Alert.AlertType.WARNING);
                return;
            }

            

            
            // O SI tu ChoferDTO usa String para teléfono (como te sugerí):
            ChoferDTO nuevoChofer = new ChoferDTO(
                txtNombreCompleto.getText().trim(),
                txtDni.getText().trim().toUpperCase(),
                txtTelefono.getText().trim(),  // Directamente String
                chkDisposicion.isSelected()
            );
            

            System.out.println("💾 Creando chofer en BD...");
            
            // ESTA ES LA LÍNEA IMPORTANTE QUE FALTA:
            choferDAO.registrarChofer(nuevoChofer);
            
            System.out.println("✅ Chofer guardado en BD");

            // Mostrar alerta de éxito
            mostrarAlerta("Éxito", "Chofer registrado correctamente en la base de datos", Alert.AlertType.INFORMATION);
            
            // Limpiar formulario
            limpiarFormulario();

        } catch (NumberFormatException e) {
            System.err.println("❌ Error: Teléfono debe ser numérico");
            mostrarAlerta("Error", "El teléfono debe contener solo números", Alert.AlertType.ERROR);
        } catch (Exception e) {
            System.err.println("❌ Error guardando chofer: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error", "Error al guardar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void limpiarFormulario() {
        txtNombreCompleto.clear();
        txtDni.clear();
        txtTelefono.clear();
        chkDisposicion.setSelected(true);
        txtNombreCompleto.requestFocus();
    }
    
    @FXML
    private void salir(ActionEvent event) {
        this.choferDAO = null;
        // Cerramos la ventana sin guardar
        Stage stage = (Stage) this.btnSalir.getScene().getWindow();
        stage.close();
    }
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}