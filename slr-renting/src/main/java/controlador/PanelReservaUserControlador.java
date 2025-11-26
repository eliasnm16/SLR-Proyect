package controlador;

import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.ResourceBundle;

import dto.CocheDTO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

public class PanelReservaUserControlador implements Initializable {

    @FXML
    private DatePicker desdePicker;

    @FXML
    private DatePicker hastaPicker;

    @FXML
    private Label lblDiasSeleccionados;

    @FXML
    private Label lblPrecioEstimado;

    @FXML
    private Button btnConfirmar;

    @FXML
    private Button btnCancelar;

    private CocheDTO cocheSeleccionado;
    private boolean quiereChofer;
    private String nifUsuario; // NUEVO

    private static final double DESCUENTO_7 = 0.10;
    private static final double DESCUENTO_30 = 0.20;
    private static final double COSTE_CHOFER_POR_DIA = 40.0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        if (desdePicker == null || hastaPicker == null || lblDiasSeleccionados == null || 
            lblPrecioEstimado == null || btnConfirmar == null || btnCancelar == null) {

            System.err.println("ERROR: FXML NO COINCIDE con los fx:id desdePicker/hastaPicker/etc.");
            return;
        }

        bloquearFechasPasadas();

        desdePicker.setOnAction(e -> actualizarCalculosSeguros());
        hastaPicker.setOnAction(e -> actualizarCalculosSeguros());

        btnConfirmar.setOnAction(e -> confirmarReserva());
        btnCancelar.setOnAction(e -> btnCancelar.getScene().getWindow().hide());
    }

    public void setDatos(CocheDTO coche, boolean contratarChofer) {
        this.cocheSeleccionado = coche;
        this.quiereChofer = contratarChofer;
        actualizarCalculosSeguros();
    }

    // Permite pasar el NIF desde fuera
    public void setNifUsuario(String nif) {
        this.nifUsuario = nif;
    }

    private void actualizarCalculosSeguros() {
        if (desdePicker == null || hastaPicker == null || lblDiasSeleccionados == null || lblPrecioEstimado == null)
            return;
        actualizarCalculos();
    }

    private void actualizarCalculos() {
        LocalDate inicio = desdePicker.getValue();
        LocalDate fin = hastaPicker.getValue();

        if (inicio == null || fin == null) {
            lblDiasSeleccionados.setText("0");
            lblPrecioEstimado.setText("0€");
            return;
        }

        if (fin.isBefore(inicio)) {
            lblDiasSeleccionados.setText("0");
            lblPrecioEstimado.setText("0€");
            return;
        }

        long dias = ChronoUnit.DAYS.between(inicio, fin) + 1;
        lblDiasSeleccionados.setText(String.valueOf(dias));

        if (cocheSeleccionado == null) {
            lblPrecioEstimado.setText("0€");
            return;
        }

        double precioBase = cocheSeleccionado.getPrecioDiario() * dias;
        double descuento = (dias >= 30 ? DESCUENTO_30 : dias >= 7 ? DESCUENTO_7 : 0);
        double descuentoEuros = precioBase * descuento;

        double costeChofer = quiereChofer ? (COSTE_CHOFER_POR_DIA * dias) : 0;

        double total = precioBase - descuentoEuros + costeChofer;

        lblPrecioEstimado.setText(String.format("%.2f €", total));
    }

    private void bloquearFechasPasadas() {
        LocalDate hoy = LocalDate.now();

        desdePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate fecha, boolean empty) {
                super.updateItem(fecha, empty);
                if (fecha != null && fecha.isBefore(hoy)) {
                    setDisable(true);
                    setStyle("-fx-background-color: #2c2c2c;");
                }
            }
        });

        hastaPicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate fecha, boolean empty) {
                super.updateItem(fecha, empty);
                if (fecha != null && fecha.isBefore(hoy)) {
                    setDisable(true);
                    setStyle("-fx-background-color: #2c2c2c;");
                }
            }
        });
    }

    /**
     * CONFIRMAR RESERVA → Abre factura directamente y pasa todos los datos.
     */
    private void confirmarReserva() {

        if (desdePicker.getValue() == null || hastaPicker.getValue() == null) {
            new Alert(Alert.AlertType.WARNING, "Selecciona ambas fechas.").showAndWait();
            return;
        }

        if (hastaPicker.getValue().isBefore(desdePicker.getValue())) {
            new Alert(Alert.AlertType.WARNING, "Fecha fin no puede ser anterior.").showAndWait();
            return;
        }

        LocalDate inicio = desdePicker.getValue();
        LocalDate fin = hastaPicker.getValue();

        // Si no viene de sesión → lo pedimos
        if (nifUsuario == null || nifUsuario.trim().isEmpty()) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Introducir NIF/NIE");
            dialog.setHeaderText("Falta el identificador del cliente");
            dialog.setContentText("Introduce tu NIF/NIE:");

            Optional<String> res = dialog.showAndWait();
            if (res.isEmpty() || res.get().trim().isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "No se proporcionó NIF/NIE.").showAndWait();
                return;
            }

            nifUsuario = res.get().trim();
        }

        try {
            URL fxmlUrl = getClass().getResource("/vista/PanelFacturaUser.fxml");
            if (fxmlUrl == null) {
                new Alert(Alert.AlertType.ERROR, "No se encontró PanelFacturaUser.fxml").showAndWait();
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            PanelFacturaUserControlador ctrl = loader.getController();
            ctrl.setDatos(cocheSeleccionado, inicio, fin, quiereChofer, nifUsuario);

            Stage stage = new Stage();
            stage.setTitle("Factura de alquiler");
            stage.setScene(new Scene(root));
            stage.show();

            // Cerrar esta ventana
            btnConfirmar.getScene().getWindow().hide();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error al abrir la factura:\n" + e.getMessage()).showAndWait();
        }
    }
}




