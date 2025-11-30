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
    @FXML private TextField txtImagenURL;

    @FXML private CheckBox chkNuevo;
    @FXML private CheckBox chkDisponible;

    @FXML private Button btnRegistrar;

    private CocheDTO cocheEditar = null;
      private final CocheDAO cocheDAO = new CocheDAO();

  
    public void setCoche(CocheDTO coche) {
        this.cocheEditar = coche;

        txtBastidor.setText(String.valueOf(coche.getBastidor()));
        txtMarca.setText(coche.getMarca());
        txtModelo.setText(coche.getModelo());
        txtMatricula.setText(coche.getMatricula());
        txtPrecioDiario.setText(String.valueOf(coche.getPrecioDiario()));
        txtDescripcion.setText(coche.getDescripcion());
        txtPlazas.setText(String.valueOf(coche.getPlazas()));
        txtPotencia.setText(String.valueOf(coche.getPotencia()));
        txtMotor.setText(coche.getMotor());
        txtVelocidadMax.setText(String.valueOf(coche.getVelocidadMax()));
        txtImagenURL.setText(coche.getImagenURL());

        chkNuevo.setSelected(coche.isNuevo());
        chkDisponible.setSelected(coche.isDisponible());

  
        txtBastidor.setDisable(true);

   
        btnRegistrar.setText("Actualizar");
    }

   
    @FXML
    private void registrarCoche(ActionEvent event) {
        try {

            if (txtMarca.getText().isEmpty() || txtModelo.getText().isEmpty() ||
                txtMatricula.getText().isEmpty() || txtPrecioDiario.getText().isEmpty() ||
                txtDescripcion.getText().isEmpty() || txtPlazas.getText().isEmpty() ||
                txtPotencia.getText().isEmpty() || txtMotor.getText().isEmpty() ||
                txtVelocidadMax.getText().isEmpty()) {

                mostrarAlerta("Error", "Por favor, rellene todos los campos.", Alert.AlertType.ERROR);
                return;
            }

            CocheDTO coche = new CocheDTO(
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
                    txtImagenURL.getText().trim(),
                    chkNuevo.isSelected(),
                    chkDisponible.isSelected()
            );

               if (cocheEditar != null) {
                cocheDAO.modificarCoche(coche);
                mostrarAlerta("Éxito", "Coche actualizado correctamente.", Alert.AlertType.INFORMATION);

                Stage stage = (Stage) btnRegistrar.getScene().getWindow();
                stage.close();
                return;
            }

   
            cocheDAO.registrarCoche(coche);
            mostrarAlerta("Éxito", "Coche registrado correctamente.", Alert.AlertType.INFORMATION);

            limpiarFormulario();

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Algunos campos numéricos no son válidos.", Alert.AlertType.ERROR);
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
        txtImagenURL.clear();

        chkNuevo.setSelected(false);
        chkDisponible.setSelected(false);

        txtBastidor.setDisable(false);
        btnRegistrar.setText("Registrar");
        cocheEditar = null;
    }

       @FXML
    private void salir(ActionEvent event) {
        Stage stage = (Stage) btnRegistrar.getScene().getWindow();
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