package controlador;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.ResourceBundle;

import dto.CocheDTO;
import dto.ClienteDTO;
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
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
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

    @FXML
    private MenuButton menuUsuario;  // NUEVO: MenuButton

    @FXML
    private MenuItem itemConfig;     // NUEVO: Item Config

    @FXML
    private MenuItem itemMisReservas; // NUEVO: Item Mis Reservas

    @FXML
    private MenuItem itemLogout;     // NUEVO: Item Logout

    private CocheDTO cocheSeleccionado;
    private boolean quiereChofer;
    private String nifUsuario;
    private ClienteDTO usuarioActual;  // NUEVO: Para almacenar usuario

    private static final double DESCUENTO_7 = 0.10;
    private static final double DESCUENTO_30 = 0.20;
    private static final double COSTE_CHOFER_POR_DIA = 40.0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // CARGAR USUARIO ACTUAL (igual que en las otras pantallas)
        usuarioActual = LoginUsuarioRegistradoControlador.usuarioActual;

        if (usuarioActual != null) {
            if (menuUsuario != null) {
                menuUsuario.setText(usuarioActual.getNombreCompleto());  // Mostrar nombre del usuario
            }
            if (this.nifUsuario == null || this.nifUsuario.isEmpty()) {
                this.nifUsuario = usuarioActual.getNif_nie();
            }
        }

        // CONFIGURAR MENÚ
        configurarMenu();

        if (desdePicker == null || hastaPicker == null || lblDiasSeleccionados == null || 
            lblPrecioEstimado == null || btnConfirmar == null || btnCancelar == null) {

            System.err.println("ERROR: FXML NO COINCIDE con los fx:id desdePicker/hastaPicker/etc.");
            return;
        }

        bloquearFechasPasadas();

        desdePicker.setOnAction(e -> actualizarCalculosSeguros());
        hastaPicker.setOnAction(e -> actualizarCalculosSeguros());

        btnConfirmar.setOnAction(e -> confirmarReserva());
        btnCancelar.setOnAction(e -> volverAtras());  // MODIFICADO: Ahora usa volverAtras()
    }

    // NUEVO: Configurar el menú
    private void configurarMenu() {
        itemConfig.setOnAction(e -> abrirConfig());
        itemMisReservas.setOnAction(e -> abrirMisReservas());
        itemLogout.setOnAction(e -> cerrarSesion());
    }

    // NUEVO: Abrir Configuración (igual que en PanelMainUser)
    private void abrirConfig() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/PanelConfigUser.fxml"));
            Parent rootConfig = loader.load();

            PanelConfigUserControlador controller = loader.getController();
            if (usuarioActual != null) {
                controller.cargarUsuario(usuarioActual);
            }

            // Obtener el Stage actual y cambiar la Scene
            Stage stage = (Stage) menuUsuario.getScene().getWindow();
            stage.setScene(new Scene(rootConfig));
            stage.setTitle("Configuración de Usuario");

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No se pudo abrir la configuración.");
            alert.showAndWait();
        }
    }

    // NUEVO: Abrir Mis Reservas (igual que en PanelMainUser)
    @FXML
    private void abrirMisReservas() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/PanelConfigReservaUser.fxml"));
            Parent rootReservas = loader.load();

            PanelConfigReservaUserControlador controlador = loader.getController();
            controlador.setNifUsuarioActual(this.nifUsuario);

            // Obtener el Stage actual y cambiar la Scene
            Stage stage = (Stage) menuUsuario.getScene().getWindow();
            stage.setScene(new Scene(rootReservas));
            stage.setTitle("Mis Reservas");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // NUEVO: Cerrar sesión
    @FXML
    private void cerrarSesion() {
        try {
            // Cerrar todas las ventanas
            javafx.stage.Window.getWindows().forEach(window -> {
                if (window instanceof Stage) {
                    ((Stage) window).close();
                }
            });

            // Abrir login
            Parent root = FXMLLoader.load(getClass().getResource("/vista/loginusuarioregistrado.fxml"));
            Stage loginStage = new Stage();
            loginStage.setScene(new Scene(root));
            loginStage.setTitle("Inicio de Sesión");
            loginStage.show();

            // Limpiar datos
            this.nifUsuario = null;
            this.usuarioActual = null;
            LoginUsuarioRegistradoControlador.usuarioActual = null;

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No se pudo cerrar sesión");
            alert.showAndWait();
        }
    }

    // MODIFICADO: Método para volver atrás (cierra ventana actual)
    private void volverAtras() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    // MÉTODO PARA PASAR EL USUARIO DESDE FUERA (si es necesario)
    public void setUsuarioActual(ClienteDTO usuario) {
        this.usuarioActual = usuario;
        if (usuarioActual != null && menuUsuario != null) {
            menuUsuario.setText(usuarioActual.getNombreCompleto());
            this.nifUsuario = usuarioActual.getNif_nie();
        }
    }

    public void setDatos(CocheDTO coche, boolean contratarChofer) {
        this.cocheSeleccionado = coche;
        this.quiereChofer = contratarChofer;
        actualizarCalculosSeguros();
    }

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

        // Si no viene de sesión lo pedimos
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
            Stage currentStage = (Stage) btnConfirmar.getScene().getWindow();
            currentStage.close();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error al abrir la factura:\n" + e.getMessage()).showAndWait();
        }
    }
}