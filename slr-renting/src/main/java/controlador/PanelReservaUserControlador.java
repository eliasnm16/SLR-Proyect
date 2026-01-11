package controlador;

import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.ResourceBundle;

import dto.CocheDTO;
import dto.ClienteDTO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PanelReservaUserControlador implements Initializable {

    @FXML private DatePicker desdePicker;
    @FXML private DatePicker hastaPicker;

    @FXML private Label lblDiasSeleccionados;
    @FXML private Label lblPrecioEstimado;

    @FXML private Button btnConfirmar;
    @FXML private Button btnCancelar;


    @FXML private MenuButton menuUsuario;
    @FXML private MenuItem itemConfig;
    @FXML private MenuItem itemMisReservas;
    @FXML private MenuItem itemLogout;

    private CocheDTO cocheSeleccionado;
    private boolean quiereChofer;
    private String nifUsuario;

    private static final double DESCUENTO_7 = 0.10;
    private static final double DESCUENTO_30 = 0.20;
    private static final double COSTE_CHOFER_POR_DIA = 40.0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        if (desdePicker == null || hastaPicker == null || lblDiasSeleccionados == null ||
            lblPrecioEstimado == null || btnConfirmar == null || btnCancelar == null) {

            System.err.println("ERROR: FXML NO COINCIDE con los fx:id desdePicker/hastaPicker/etc.");
            AlertUtils.error("Error FXML", "El FXML no coincide con los fx:id esperados.");
            return;
        }

        // Menú usuario
        configurarMenu();

        // Si hay usuario en sesión, usarlo para nombre y NIF
        ClienteDTO u = LoginUsuarioRegistradoControlador.usuarioActual;
        if (u != null) {
            if (menuUsuario != null) {
                String nombre = u.getNombreCompleto();
                menuUsuario.setText(nombre != null && !nombre.isBlank() ? nombre : "Usuario");
            }
            if ((nifUsuario == null || nifUsuario.isBlank()) && u.getNif_nie() != null) {
                nifUsuario = u.getNif_nie();
            }
        } else {
            if (menuUsuario != null) menuUsuario.setText("Usuario");
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

    public void setNifUsuario(String nif) {
        this.nifUsuario = nif;
    }

    private void configurarMenu() {
        if (itemConfig != null) itemConfig.setOnAction(e -> abrirConfig());
        if (itemMisReservas != null) itemMisReservas.setOnAction(e -> abrirMisReservas());
        if (itemLogout != null) itemLogout.setOnAction(e -> cerrarSesion());
    }

    private void abrirConfig() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/PanelConfigUser.fxml"));
            Parent root = loader.load();

            PanelConfigUserControlador ctrl = loader.getController();

            ClienteDTO usuario = LoginUsuarioRegistradoControlador.usuarioActual;
            if (usuario != null) {
                ctrl.cargarUsuario(usuario);
            } else {
                AlertUtils.warning("Sesión", "No hay usuario cargado para mostrar la configuración.");
            }

            Stage stage = (Stage) menuUsuario.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Configuración de Usuario");
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
            AlertUtils.error("Error", "No se pudo abrir la configuración del usuario.");
        }
    }

    private void abrirMisReservas() {
        try {
            String nif = obtenerNifUsuario();
            if (nif == null || nif.isBlank()) {
                AlertUtils.warning("Sesión", "No se pudo identificar el usuario actual (NIF/NIE vacío).");
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/PanelConfigReservaUser.fxml"));
            Parent rootReservas = loader.load();

            PanelConfigReservaUserControlador controlador = loader.getController();
            controlador.setNifUsuarioActual(nif);

            Stage stage = new Stage();
            stage.setScene(new Scene(rootReservas));
            stage.setTitle("Mis Reservas");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(menuUsuario.getScene().getWindow());
            stage.setResizable(false);
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
            AlertUtils.error("Error", "No se pudo abrir el panel de reservas.");
        }
    }

    private void cerrarSesion() {
        try {
            // Cierra solo esta ventana y vuelve al login (manteniendo tu patrón)
            Stage current = (Stage) btnCancelar.getScene().getWindow();
            current.close();

            Parent root = FXMLLoader.load(getClass().getResource("/vista/loginusuarioregistrado.fxml"));
            Stage loginStage = new Stage();
            loginStage.setScene(new Scene(root));
            loginStage.setTitle("Inicio de Sesión");
            loginStage.show();

            LoginUsuarioRegistradoControlador.usuarioActual = null;
            nifUsuario = null;

        } catch (Exception ex) {
            ex.printStackTrace();
            AlertUtils.error("Error", "No se pudo cerrar sesión.");
        }
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

    private String obtenerNifUsuario() {
        if (nifUsuario != null && !nifUsuario.isBlank()) return nifUsuario;

        ClienteDTO u = LoginUsuarioRegistradoControlador.usuarioActual;
        if (u != null && u.getNif_nie() != null && !u.getNif_nie().isBlank()) {
            nifUsuario = u.getNif_nie();
            return nifUsuario;
        }

        return null;
    }

    private void confirmarReserva() {

        if (desdePicker.getValue() == null || hastaPicker.getValue() == null) {
            AlertUtils.warning("Fechas", "Selecciona ambas fechas.");
            return;
        }

        if (hastaPicker.getValue().isBefore(desdePicker.getValue())) {
            AlertUtils.warning("Fechas", "La fecha fin no puede ser anterior a la fecha de inicio.");
            return;
        }

        LocalDate inicio = desdePicker.getValue();
        LocalDate fin = hastaPicker.getValue();

        // Si sigue sin NIF -> pedirlo
        if (obtenerNifUsuario() == null) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Introducir NIF/NIE");
            dialog.setHeaderText("Falta el identificador del cliente");
            dialog.setContentText("Introduce tu NIF/NIE:");

            Optional<String> res = dialog.showAndWait();
            if (res.isEmpty() || res.get().trim().isEmpty()) {
                AlertUtils.warning("NIF/NIE", "No se proporcionó NIF/NIE.");
                return;
            }

            nifUsuario = res.get().trim();
        }

        if (cocheSeleccionado == null) {
            AlertUtils.error("Error", "No hay coche seleccionado.");
            return;
        }

        try {
            URL fxmlUrl = getClass().getResource("/vista/PanelFacturaUser.fxml");
            if (fxmlUrl == null) {
                AlertUtils.error("Error", "No se encontró PanelFacturaUser.fxml");
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            PanelFacturaUserControlador ctrl = loader.getController();
            ctrl.setDatos(cocheSeleccionado, inicio, fin, quiereChofer, nifUsuario);

            Stage stage = new Stage();
            stage.setTitle("Factura de alquiler");
            stage.setScene(new Scene(root));
            stage.initOwner(btnConfirmar.getScene().getWindow());
            stage.show();

            // Cerrar esta ventana
            btnConfirmar.getScene().getWindow().hide();

        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.error("Error", "Error al abrir la factura:\n" + e.getMessage());
        }
    }
}
