package controlador;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import dto.CocheDTO;
import dto.ClienteDTO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class PanelCocheUserControlador implements Initializable {

    @FXML private ImageView imgCoche;

    @FXML private Label lblMarcaYModelo;
    @FXML private Label lblDescripcion;
    @FXML private Label lblPotencia;
    @FXML private Label lblMotor;
    @FXML private Label lblVelocidad;
    @FXML private Label lblPlazas;
    @FXML private Label lblMatricula;
    @FXML private Label lblPrecio;

    @FXML private Button btnVolver;

    @FXML private CheckBox chkChofer;
    @FXML private Button btnSeleccionarDias;

    @FXML private MenuButton menuUsuario;
    @FXML private MenuItem itemConfig;
    @FXML private MenuItem itemMisReservas;
    @FXML private MenuItem itemLogout;

    private CocheDTO cocheActual;
    private SeleccionDiasHandler seleccionHandler;
    private String nifUsuarioActual;
    private ClienteDTO usuario;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        usuario = LoginUsuarioRegistradoControlador.usuarioActual;

        if (menuUsuario != null) {
            String nombre = (usuario != null) ? usuario.getNombreCompleto() : null;
            menuUsuario.setText(nombre != null && !nombre.isBlank() ? nombre : "Usuario");
        }

        if (usuario != null && (this.nifUsuarioActual == null || this.nifUsuarioActual.isEmpty())) {
            this.nifUsuarioActual = usuario.getNif_nie();
        }

        configurarMenu();

        if (btnSeleccionarDias != null) {
            btnSeleccionarDias.setOnAction(e -> onSeleccionarDiasClicked());
        }
        if (btnVolver != null) {
            btnVolver.setOnAction(e -> volverAtras());
        }
    }

    private void configurarMenu() {
        if (itemConfig != null) itemConfig.setOnAction(e -> abrirConfig());
        if (itemMisReservas != null) itemMisReservas.setOnAction(e -> abrirMisReservas());
        if (itemLogout != null) itemLogout.setOnAction(e -> cerrarSesion());
    }

    private void abrirConfig() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/PanelConfigUser.fxml"));
            Parent rootConfig = loader.load();

            PanelConfigUserControlador controller = loader.getController();

            ClienteDTO u = LoginUsuarioRegistradoControlador.usuarioActual;
            if (u != null) {
                controller.cargarUsuario(u);
            } else {
                AlertUtils.warning("Sesión", "No hay usuario cargado para mostrar la configuración.");
            }

            Stage stage = (Stage) menuUsuario.getScene().getWindow();
            stage.setScene(new Scene(rootConfig));
            stage.setTitle("Configuración de Usuario");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.error("Error", "No se pudo abrir la configuración del usuario.");
        }
    }

    private void volverAtras() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/PanelMainUser.fxml"));
            Parent root = loader.load();

            PanelMainUserControlador controlador = loader.getController();
            controlador.setNifUsuarioActual(this.nifUsuarioActual);

            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Catálogo de Coches");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.close();
        }
    }

    @FXML
    private void cerrarSesion() {
        try {
            List<Window> windows = new ArrayList<>(Window.getWindows());
            for (Window window : windows) {
                if (window instanceof Stage) {
                    ((Stage) window).close();
                }
            }

            Parent root = FXMLLoader.load(getClass().getResource("/vista/Loginusuarioregistrado.fxml"));
            Stage loginStage = new Stage();
            loginStage.setScene(new Scene(root));
            loginStage.setTitle("Inicio de Sesión");
            loginStage.show();

            this.nifUsuarioActual = null;
            this.usuario = null;
            LoginUsuarioRegistradoControlador.usuarioActual = null;

        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.error("Error", "No se pudo cerrar sesión.");
        }
    }

    public void setCoche(CocheDTO coche) {
        if (coche == null) return;
        this.cocheActual = coche;


        lblMarcaYModelo.setText((coche.getMarca() != null ? coche.getMarca() + " · " : "") + safe(coche.getModelo(), "Modelo"));
        lblDescripcion.setText(safe(coche.getDescripcion(), ""));
        lblPotencia.setText(coche.getPotencia() + " CV");
        lblMotor.setText(safe(coche.getMotor(), "Desconocido"));
        lblVelocidad.setText(coche.getVelocidadMax() + " km/h");
        lblPlazas.setText(String.valueOf(coche.getPlazas()));
        lblMatricula.setText(safe(coche.getMatricula(), "---"));


        lblPrecio.setText(((int) coche.getPrecioDiario()) + "€/día");

if (!coche.isDisponible()) {
            lblMarcaYModelo.setText(safe(coche.getMarca(), "") + " · " + safe(coche.getModelo(), "") + " (RESERVADO)");
            lblMarcaYModelo.setStyle("-fx-text-fill: #ff5555;");

            lblDescripcion.setText("Este vehículo está actualmente reservado y no disponible para alquiler.");
            lblDescripcion.setStyle("-fx-text-fill: #ff5555;");

            lblPrecio.setText(((int) coche.getPrecioDiario()) + "€/día");
            lblPrecio.setStyle("-fx-text-fill: #ff5555; -fx-strikethrough: true;");

            if (btnSeleccionarDias != null) {
                btnSeleccionarDias.setDisable(true);
                btnSeleccionarDias.setText("RESERVADO");
                btnSeleccionarDias.setStyle("-fx-background-color: #666666; -fx-text-fill: #999999;");
            }

            if (chkChofer != null) chkChofer.setDisable(true);

            if (imgCoche != null && imgCoche.getImage() != null) imgCoche.setOpacity(0.6);

        } else {
            lblMarcaYModelo.setStyle("-fx-text-fill: #f5f5f5; -fx-font-size: 26px; -fx-font-weight: bold;");
            lblDescripcion.setStyle("-fx-text-fill: #b5b5b5; -fx-font-size: 13px;");
            lblPrecio.setStyle("-fx-text-fill: #ffd666; -fx-font-size: 18px; -fx-font-weight: bold;");

            if (btnSeleccionarDias != null) {
                btnSeleccionarDias.setDisable(false);
                btnSeleccionarDias.setText("Seleccionar días para el alquiler");
                btnSeleccionarDias.setStyle("-fx-background-radius: 20; -fx-background-color: #ffd666; -fx-text-fill: #111111; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 26 10 26;");
            }

            if (chkChofer != null) chkChofer.setDisable(false);

            if (imgCoche != null) imgCoche.setOpacity(1.0);
        }

        cargarImagen(coche.getImagenURL());
    }

    private void cargarImagen(String imagenURL) {
        imgCoche.setFitWidth(520);
        imgCoche.setFitHeight(360);
        imgCoche.setPreserveRatio(true);
        imgCoche.setSmooth(true);

        if (imagenURL == null || imagenURL.trim().isEmpty()) {
            imgCoche.setImage(null);
            return;
        }

        try {
            Image img = null;

            URL urlVista = getClass().getResource("/vista/" + imagenURL.trim());
            if (urlVista != null) {
                img = new Image(urlVista.toExternalForm());
            } else {
                URL urlImagenes = getClass().getResource("/imagenes/" + imagenURL.trim());
                if (urlImagenes != null) {
                    img = new Image(urlImagenes.toExternalForm());
                }
            }

            imgCoche.setImage(img);

        } catch (Exception ex) {
            System.err.println("No se pudo cargar la imagen: " + imagenURL + " -> " + ex.getMessage());
            imgCoche.setImage(null);
        }
    }

    private void onSeleccionarDiasClicked() {

        if (cocheActual == null) {
            AlertUtils.warning("Seleccionar días", "No hay ningún coche cargado para proceder con la selección de días.");
            return;
        }

        if (!cocheActual.isDisponible()) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Coche reservado");
            a.setHeaderText(null);
            a.setContentText("Este coche está actualmente reservado y no disponible para alquiler.");
            a.showAndWait();
            return;
        }

        boolean elegirChofer = (chkChofer != null && chkChofer.isSelected());

        if (seleccionHandler != null) {
            seleccionHandler.handle(cocheActual, elegirChofer);
            return;
        }

        try {
            URL fxmlUrl = getClass().getResource("/vista/PanelReservaUser.fxml");
            if (fxmlUrl == null) {
                AlertUtils.error("Error", "No se encontró la vista de reservas (PanelReservaUser.fxml).");
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            PanelReservaUserControlador controladorReserva = loader.getController();
            controladorReserva.setDatos(cocheActual, elegirChofer);
            controladorReserva.setNifUsuario(obtenerNifUsuarioActual());

            Stage stage = new Stage();
            stage.setTitle("Seleccionar días de alquiler");
            stage.setScene(new Scene(root));
            stage.initOwner(menuUsuario.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.show();

        } catch (IOException ex) {
            ex.printStackTrace();
            AlertUtils.error("Error", "No se pudo abrir la ventana de reservas.");
        }
    }

    @FXML
    private void abrirMisReservas() {
        try {
            String nifUsuario = obtenerNifUsuarioActual();
            if (nifUsuario == null || nifUsuario.isBlank()) {
                AlertUtils.warning("Sesión", "No se pudo identificar el usuario actual (NIF/NIE vacío).");
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/PanelConfigReservaUser.fxml"));
            Parent rootReservas = loader.load();

            PanelConfigReservaUserControlador controlador = loader.getController();
            controlador.setNifUsuarioActual(nifUsuario);

            Stage stage = new Stage();
            stage.setScene(new Scene(rootReservas));
            stage.setTitle("Mis Reservas");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(menuUsuario.getScene().getWindow());
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.error("Error", "No se pudo abrir el panel de reservas.");
        }
    }

    public void setNifUsuarioActual(String nif) {
        this.nifUsuarioActual = nif;
    }

    private String obtenerNifUsuarioActual() {
        return this.nifUsuarioActual;
    }

    public void setSeleccionDiasHandler(SeleccionDiasHandler handler) {
        this.seleccionHandler = handler;
    }

    public interface SeleccionDiasHandler {
        void handle(CocheDTO coche, boolean contratarChofer);
    }

    private static String safe(String s, String fallback) {
        if (s == null) return fallback;
        s = s.trim();
        return s.isEmpty() ? fallback : s;
    }
}
