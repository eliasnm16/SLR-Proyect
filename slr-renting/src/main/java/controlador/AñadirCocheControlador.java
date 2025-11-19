package controlador;

import dao.CocheDAO;
import dto.CocheDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AñadirCocheControlador {

    @FXML private TextField txtBastidor;
    @FXML private TextField txtMarca;
    @FXML private TextField txtModelo;
    @FXML private TextField txtMatricula;
    @FXML private TextField txtPrecioDiario;
    @FXML private TextField txtDescripcion;
    @FXML private TextField txtPlazas;
    @FXML private TextField txtPotencia;
    @FXML private TextField txtMotor;
    @FXML private TextField txtVelocidadMax;
    @FXML private TextField txtImagenURL; // Agregar esto
    @FXML private CheckBox chkNuevo;
    @FXML private CheckBox chkDisponible;
    @FXML private Button btnRegistrar;
    @FXML private Button btnSalir;
    private CocheDAO cocheDAO;

    @FXML
    public void initialize() {
        System.out.println("✅ Initialize COCHE ejecutado");
        this.cocheDAO = new CocheDAO();
        System.out.println("✅ CocheDAO instanciado");
    }

    @FXML
    private void registrarCoche() {
        System.out.println("🎯 BOTÓN COCHE FUNCIONANDO!");
        
        try {
            // Mostrar datos en consola
            System.out.println("📋 Datos del formulario COCHE:");
            System.out.println("Bastidor: " + txtBastidor.getText());
            System.out.println("Marca: " + txtMarca.getText());
            System.out.println("Modelo: " + txtModelo.getText());
            System.out.println("Matrícula: " + txtMatricula.getText());
            System.out.println("Precio Diario: " + txtPrecioDiario.getText());
            System.out.println("Descripción: " + txtDescripcion.getText());
            System.out.println("Plazas: " + txtPlazas.getText());
            System.out.println("Potencia: " + txtPotencia.getText());
            System.out.println("Motor: " + txtMotor.getText());
            System.out.println("Velocidad Max: " + txtVelocidadMax.getText());
            System.out.println("Nuevo: " + chkNuevo.isSelected());
            System.out.println("Disponible: " + chkDisponible.isSelected());

            // Validaciones básicas
            if (txtBastidor.getText().trim().isEmpty()) {
                mostrarAlerta("Error", "El bastidor es obligatorio", Alert.AlertType.WARNING);
                return;
            }

            if (txtMarca.getText().trim().isEmpty()) {
                mostrarAlerta("Error", "La marca es obligatoria", Alert.AlertType.WARNING);
                return;
            }

            if (txtModelo.getText().trim().isEmpty()) {
                mostrarAlerta("Error", "El modelo es obligatorio", Alert.AlertType.WARNING);
                return;
            }

            if (txtMatricula.getText().trim().isEmpty()) {
                mostrarAlerta("Error", "La matrícula es obligatoria", Alert.AlertType.WARNING);
                return;
            }

            if (txtPrecioDiario.getText().trim().isEmpty()) {
                mostrarAlerta("Error", "El precio diario es obligatorio", Alert.AlertType.WARNING);
                return;
            }

            // Crear DTO con los datos del formulario
            CocheDTO nuevoCoche = new CocheDTO(
            	    Integer.parseInt(txtBastidor.getText().trim()),
            	    txtMarca.getText().trim(),
            	    txtModelo.getText().trim(),
            	    Double.parseDouble(txtPrecioDiario.getText().trim()),
            	    txtDescripcion.getText().trim(),
            	    Integer.parseInt(txtPlazas.getText().trim()),
            	    Integer.parseInt(txtPotencia.getText().trim()),
            	    txtMotor.getText().trim(),
            	    Integer.parseInt(txtVelocidadMax.getText().trim()),
            	    txtMatricula.getText().trim().toUpperCase(),
            	    txtImagenURL.getText().trim(), // En lugar de "" vacío
            	    chkNuevo.isSelected(),
            	    chkDisponible.isSelected()
            	);

            System.out.println("💾 Creando coche en BD...");
            
            // ESTA ES LA LÍNEA IMPORTANTE QUE GUARDA EN BD:
            cocheDAO.registrarCoche(nuevoCoche);
            
            System.out.println("✅ Coche guardado en BD");

            // Mostrar alerta de éxito
            mostrarAlerta("Éxito", "Coche registrado correctamente en la base de datos", Alert.AlertType.INFORMATION);
            
            // Limpiar formulario
            limpiarFormulario();

        } catch (NumberFormatException e) {
            System.err.println("❌ Error: Campos numéricos inválidos");
            mostrarAlerta("Error", "Verifique que los campos numéricos contengan valores válidos", Alert.AlertType.ERROR);
        } catch (Exception e) {
            System.err.println("❌ Error guardando coche: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error", "Error al guardar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void limpiarFormulario() {
        txtBastidor.clear();
        txtMarca.clear();
        txtModelo.clear();
        txtMatricula.clear();
        txtPrecioDiario.clear();
        txtDescripcion.clear();
        txtPlazas.clear();
        txtPotencia.clear();
        txtMotor.clear();
        txtVelocidadMax.clear();
        chkDisponible.setSelected(true);
        chkNuevo.setSelected(false);
        txtBastidor.requestFocus();
    }
    
    @FXML
    private void salir(ActionEvent event) {
        this.cocheDAO = null;
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