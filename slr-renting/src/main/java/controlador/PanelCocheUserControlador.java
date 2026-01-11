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

import controlador.AlertUtils;

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

    @FXML private CheckBox chkChofer;
    @FXML private Button btnSeleccionarDias;

    @FXML private MenuButton menuUsuario;
    @FXML private MenuItem itemConfig;
    @FXML private MenuItem itemMisReservas;
    @FXML private MenuItem itemLogout;

    private CocheDTO cocheActual;
    private SeleccionDiasHandler seleccionHandler;
    private String nifUsuarioActual;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarMenu();
        btnSeleccionarDias.setOnAction(e -> onSeleccionarDiasClicked());

        // Opcional: si quieres que el menú muestre el nombre del usuario si existe
        ClienteDTO u = LoginUsuarioRegistradoControlador.usuarioActual;
        if (u != null && u.getNombreCompleto() != null && !u.getNombreCompleto().isBlank()) {
            menuUsuario.setText(u.getNombreCompleto());
        } else {
            menuUsuario.setText("Usuario");
        }
    }

    private void configurarMenu() {
        itemConfig.setOnAction(e -> abrirConfig());
        itemMisReservas.setOnAction(e -> abrirMisReservas());
        itemLogout.setOnAction(e -> cerrarSesion());
    }

    private void abrirConfig() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/PanelConfigUser.fxml"));
            Parent rootConfig = loader.load();

            PanelConfigUserControlador controller = loader.getController();

            ClienteDTO usuario = LoginUsuarioRegistradoControlador.usuarioActual;
            if (usuario != null) {
                controller.cargarUsuario(usuario);
            } else {
                AlertUtils.warning("Sesión", "No hay usuario cargado para mostrar la configuración.");
            }

            // Abrimos en la MISMA ventana donde está el PanelCocheUser
            Stage stage = (Stage) menuUsuario.getScene().getWindow();
            stage.setScene(new Scene(rootConfig));
            stage.setTitle("Configuración de Usuario");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.error("Error", "No se pudo abrir la configuración del usuario.");
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

            Parent root = FXMLLoader.load(getClass().getResource("/vista/loginusuarioregistrado.fxml"));
            Stage loginStage = new Stage();
            loginStage.setScene(new Scene(root));
            loginStage.setTitle("Inicio de Sesión");
            loginStage.show();

            this.nifUsuarioActual = null;
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
        lblPrecio.setText((int) coche.getPrecioDiario() + "€/mes");

        cargarImagen(coche.getImagenURL());
    }

    private void cargarImagen(String imagenURL) {
        imgCoche.setFitWidth(520);
        imgCoche.setFitHeight(360);
        imgCoche.setPreserveRatio(true);   // 👈 más elegante visualmente
        imgCoche.setSmooth(true);

        if (imagenURL == null || imagenURL.trim().isEmpty()) {
            imgCoche.setImage(null);
            return;
        }

        try {
            Image img = null;

            // 1) /vista
            URL urlVista = getClass().getResource("/vista/" + imagenURL.trim());
            if (urlVista != null) {
                img = new Image(urlVista.toExternalForm());
            } else {
                // 2) /imagenes
                URL urlImagenes = getClass().getResource("/imagenes/" + imagenURL.trim());
                if (urlImagenes != null) {
                    img = new Image(urlImagenes.toExternalForm());
                }
            }

            if (img != null) {
                imgCoche.setImage(img);
            } else {
                System.err.println("No se encontró la imagen en /vista ni en /imagenes: " + imagenURL);
                imgCoche.setImage(null);
            }

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

        boolean elegirChofer = chkChofer.isSelected();

        if (seleccionHandler != null) {
            seleccionHandler.handle(cocheActual, elegirChofer);
            return;
        }

        try {
            URL fxmlUrl = getClass().getResource("/vista/PanelReservaUser.fxml");
            if (fxmlUrl == null) {
                System.err.println("ERROR: no se encontró /vista/PanelReservaUser.fxml");
                AlertUtils.error("Error", "No se encontró la vista de reservas (PanelReservaUser.fxml).");
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            PanelReservaUserControlador controladorReserva = loader.getController();
            controladorReserva.setDatos(cocheActual, elegirChofer);

            Stage stage = new Stage();
            stage.setTitle("Seleccionar días de alquiler");
            stage.setScene(new Scene(root));
            stage.initOwner(menuUsuario.getScene().getWindow());
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
        System.out.println("NIF del usuario: " + nif);
    }

    private String obtenerNifUsuarioActual() {
        return this.nifUsuarioActual;
    }

    public void setSeleccionDiasHandler(SeleccionDiasHandler handler) {
        this.seleccionHandler = handler;
    }

    public static interface SeleccionDiasHandler {
        void handle(CocheDTO coche, boolean contratarChofer);
    }

    private static String safe(String s, String fallback) {
        if (s == null) return fallback;
        s = s.trim();
        return s.isEmpty() ? fallback : s;
    }
}
