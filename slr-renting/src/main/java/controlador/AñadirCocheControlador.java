package controlador;

import dao.CocheDAO;
import dto.CocheDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.control.Label;

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

    // Etiquetas para mostrar errores (si no las tienes en el FXML, las creamos dinámicamente)
    private Label errorBastidor = new Label();
    private Label errorMarca = new Label();
    private Label errorModelo = new Label();
    private Label errorMatricula = new Label();
    private Label errorPrecio = new Label();
    private Label errorDescripcion = new Label();
    private Label errorPlazas = new Label();
    private Label errorPotencia = new Label();
    private Label errorMotor = new Label();
    private Label errorVelocidad = new Label();
    private Label errorImagen = new Label();

    private CocheDTO cocheEditar = null;
    private final CocheDAO cocheDAO = new CocheDAO();

    @FXML
    public void initialize() {
        // Configurar estilos de error para los campos
        configurarEstilosValidacion();
        
        // Configurar listeners para limpiar errores al escribir
        configurarListeners();
    }

    private void configurarEstilosValidacion() {
        // Establecer estilo para todos los labels de error
        Label[] errorLabels = {errorBastidor, errorMarca, errorModelo, errorMatricula, errorPrecio,
                              errorDescripcion, errorPlazas, errorPotencia, errorMotor, errorVelocidad, errorImagen};
        
        for (Label label : errorLabels) {
            label.setTextFill(Color.RED);
            label.setStyle("-fx-font-size: 11px; -fx-font-weight: bold;");
        }
    }

    private void configurarListeners() {
        // Limpiar errores cuando el usuario empiece a escribir
        txtBastidor.textProperty().addListener((obs, oldVal, newVal) -> errorBastidor.setText(""));
        txtMarca.textProperty().addListener((obs, oldVal, newVal) -> errorMarca.setText(""));
        txtModelo.textProperty().addListener((obs, oldVal, newVal) -> errorModelo.setText(""));
        txtMatricula.textProperty().addListener((obs, oldVal, newVal) -> errorMatricula.setText(""));
        txtPrecioDiario.textProperty().addListener((obs, oldVal, newVal) -> errorPrecio.setText(""));
        txtDescripcion.textProperty().addListener((obs, oldVal, newVal) -> errorDescripcion.setText(""));
        txtPlazas.textProperty().addListener((obs, oldVal, newVal) -> errorPlazas.setText(""));
        txtPotencia.textProperty().addListener((obs, oldVal, newVal) -> errorPotencia.setText(""));
        txtMotor.textProperty().addListener((obs, oldVal, newVal) -> errorMotor.setText(""));
        txtVelocidadMax.textProperty().addListener((obs, oldVal, newVal) -> errorVelocidad.setText(""));
    }

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

    //registro y validación de coches
    @FXML
    private void registrarCoche(ActionEvent event) {
        // Limpiar todos los errores previos
        limpiarErrores();

        boolean hayErrores = false;

        // Validar campo por campo
        if (txtBastidor.getText().trim().isEmpty()) {
            errorBastidor.setText("El número de bastidor es obligatorio");
            txtBastidor.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            hayErrores = true;
        } else {
            try {
                int bastidor = Integer.parseInt(txtBastidor.getText().trim());
                if (bastidor <= 0) {
                    errorBastidor.setText("El bastidor debe ser un número positivo");
                    txtBastidor.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                    hayErrores = true;
                }
            } catch (NumberFormatException e) {
                errorBastidor.setText("El bastidor debe ser un número válido");
                txtBastidor.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                hayErrores = true;
            }
        }

        if (txtMarca.getText().trim().isEmpty()) {
            errorMarca.setText("La marca es obligatoria");
            txtMarca.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            hayErrores = true;
        }

        if (txtModelo.getText().trim().isEmpty()) {
            errorModelo.setText("El modelo es obligatorio");
            txtModelo.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            hayErrores = true;
        }

        if (txtMatricula.getText().trim().isEmpty()) {
            errorMatricula.setText("La matrícula es obligatoria");
            txtMatricula.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            hayErrores = true;
        } else {
            String matricula = txtMatricula.getText().trim().toUpperCase();
            if (!validarMatricula(matricula)) {
                errorMatricula.setText("Formato de matrícula inválido (ej: 1234ABC)");
                txtMatricula.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                hayErrores = true;
            }
        }

        if (txtPrecioDiario.getText().trim().isEmpty()) {
            errorPrecio.setText("El precio diario es obligatorio");
            txtPrecioDiario.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            hayErrores = true;
        } else {
            try {
                double precio = Double.parseDouble(txtPrecioDiario.getText().trim());
                if (precio <= 0) {
                    errorPrecio.setText("El precio debe ser mayor a 0");
                    txtPrecioDiario.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                    hayErrores = true;
                }
            } catch (NumberFormatException e) {
                errorPrecio.setText("El precio debe ser un número válido");
                txtPrecioDiario.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                hayErrores = true;
            }
        }

        if (txtDescripcion.getText().trim().isEmpty()) {
            errorDescripcion.setText("La descripción es obligatoria");
            txtDescripcion.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            hayErrores = true;
        }

        if (txtPlazas.getText().trim().isEmpty()) {
            errorPlazas.setText("El número de plazas es obligatorio");
            txtPlazas.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            hayErrores = true;
        } else {
            try {
                int plazas = Integer.parseInt(txtPlazas.getText().trim());
                if (plazas <= 0 || plazas > 10) {
                    errorPlazas.setText("Las plazas deben estar entre 1 y 10");
                    txtPlazas.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                    hayErrores = true;
                }
            } catch (NumberFormatException e) {
                errorPlazas.setText("Las plazas deben ser un número válido");
                txtPlazas.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                hayErrores = true;
            }
        }

        if (txtPotencia.getText().trim().isEmpty()) {
            errorPotencia.setText("La potencia es obligatoria");
            txtPotencia.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            hayErrores = true;
        } else {
            try {
                int potencia = Integer.parseInt(txtPotencia.getText().trim());
                if (potencia <= 0) {
                    errorPotencia.setText("La potencia debe ser mayor a 0");
                    txtPotencia.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                    hayErrores = true;
                }
            } catch (NumberFormatException e) {
                errorPotencia.setText("La potencia debe ser un número válido");
                txtPotencia.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                hayErrores = true;
            }
        }

        if (txtMotor.getText().trim().isEmpty()) {
            errorMotor.setText("El tipo de motor es obligatorio");
            txtMotor.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            hayErrores = true;
        }

        if (txtVelocidadMax.getText().trim().isEmpty()) {
            errorVelocidad.setText("La velocidad máxima es obligatoria");
            txtVelocidadMax.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            hayErrores = true;
        } else {
            try {
                int velocidad = Integer.parseInt(txtVelocidadMax.getText().trim());
                if (velocidad <= 0 || velocidad > 400) {
                    errorVelocidad.setText("La velocidad debe estar entre 1 y 400 km/h");
                    txtVelocidadMax.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                    hayErrores = true;
                }
            } catch (NumberFormatException e) {
                errorVelocidad.setText("La velocidad debe ser un número válido");
                txtVelocidadMax.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                hayErrores = true;
            }
        }

        // Validar URL de imagen (opcional pero si se pone debe ser válida)
        if (!txtImagenURL.getText().trim().isEmpty()) {
            String url = txtImagenURL.getText().trim();
            if (!(url.endsWith(".jpg") || url.endsWith(".png") || url.endsWith(".jpeg"))) {
                errorImagen.setText("La URL debe terminar en .jpg, .png o .jpeg");
                txtImagenURL.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                hayErrores = true;
            }
        }

        if (hayErrores) {
            return;
        }

        try {
            // Si llegamos aquí, todas las validaciones pasaron
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
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al guardar el coche: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    //validacion de matricula044
    private boolean validarMatricula(String matricula) {
        if (matricula == null || matricula.length() != 7) {
            return false;
        }

        String numeros = matricula.substring(0, 4);
        String letras = matricula.substring(4);

        return numeros.matches("\\d{4}") && letras.matches("[A-Z]{3}");
    }


    private void limpiarErrores() {
        // Limpiar textos de error
        errorBastidor.setText("");
        errorMarca.setText("");
        errorModelo.setText("");
        errorMatricula.setText("");
        errorPrecio.setText("");
        errorDescripcion.setText("");
        errorPlazas.setText("");
        errorPotencia.setText("");
        errorMotor.setText("");
        errorVelocidad.setText("");
        errorImagen.setText("");

        // Restablecer bordes
        TextField[] campos = {txtBastidor, txtMarca, txtModelo, txtMatricula, txtPrecioDiario,
                             txtDescripcion, txtPlazas, txtPotencia, txtMotor, txtVelocidadMax, txtImagenURL};
        
        for (TextField campo : campos) {
            campo.setStyle("-fx-border-color: transparent; -fx-border-width: 0;");
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
        
        limpiarErrores();
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

    // Métodos para obtener los labels de error (si los necesitas vincular en el FXML)
    public Label getErrorBastidor() { return errorBastidor; }
    public Label getErrorMarca() { return errorMarca; }
    public Label getErrorModelo() { return errorModelo; }
    public Label getErrorMatricula() { return errorMatricula; }
    public Label getErrorPrecio() { return errorPrecio; }
    public Label getErrorDescripcion() { return errorDescripcion; }
    public Label getErrorPlazas() { return errorPlazas; }
    public Label getErrorPotencia() { return errorPotencia; }
    public Label getErrorMotor() { return errorMotor; }
    public Label getErrorVelocidad() { return errorVelocidad; }
    public Label getErrorImagen() { return errorImagen; }
}