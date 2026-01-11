package controlador;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import dto.CocheDTO;
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

    @FXML
    private ImageView imgCoche;

    @FXML
    private Label lblMarcaYModelo;

    @FXML
    private Label lblDescripcion;

    @FXML
    private Label lblPotencia;

    @FXML
    private Label lblMotor;

    @FXML
    private Label lblVelocidad;

    @FXML
    private Label lblPlazas;

    @FXML
    private Label lblMatricula;

    @FXML
    private Label lblPrecio;

    @FXML
    private CheckBox chkChofer;

    @FXML
    private Button btnSeleccionarDias;

    @FXML
    private MenuButton menuUsuario;

    @FXML
    private MenuItem itemConfig;

    @FXML
    private MenuItem itemMisReservas;

    @FXML
    private MenuItem itemLogout;

    private CocheDTO cocheActual;
    private SeleccionDiasHandler seleccionHandler;
    private String nifUsuarioActual;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarMenu();
        btnSeleccionarDias.setOnAction(e -> onSeleccionarDiasClicked());
    }

    private void configurarMenu() {
        itemConfig.setOnAction(e -> abrirConfig());
        itemMisReservas.setOnAction(e -> abrirMisReservas());
        itemLogout.setOnAction(e -> cerrarSesion());
    }

    private void abrirConfig() {
        System.out.println("Abrir Config desde PanelCocheUser");
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

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No se pudo cerrar sesión");
            alert.showAndWait();
        }
    }

    public void setCoche(CocheDTO coche) {
        if (coche == null) return;
        this.cocheActual = coche;

        // MODIFICACIÓN: Verificar si el coche está disponible
        if (!coche.isDisponible()) {
            // Si no está disponible, mostrar mensaje y bloquear
            lblMarcaYModelo.setText(coche.getMarca() + " · " + coche.getModelo() + " (RESERVADO)");
            lblMarcaYModelo.setStyle("-fx-text-fill: #ff5555;");
            
            lblDescripcion.setText("Este vehículo está actualmente reservado y no disponible para alquiler.");
            lblDescripcion.setStyle("-fx-text-fill: #ff5555;");
            
            lblPotencia.setText(coche.getPotencia() + " CV");
            lblMotor.setText(coche.getMotor() != null ? coche.getMotor() : "Desconocido");
            lblVelocidad.setText(coche.getVelocidadMax() + " km/h");
            lblPlazas.setText(String.valueOf(coche.getPlazas()));
            lblMatricula.setText(coche.getMatricula() != null ? coche.getMatricula() : "---");
            lblPrecio.setText((int) coche.getPrecioDiario() + "€/mes");
            lblPrecio.setStyle("-fx-text-fill: #ff5555; -fx-strikethrough: true;");
            
            // Bloquear botón
            btnSeleccionarDias.setDisable(true);
            btnSeleccionarDias.setText("RESERVADO");
            btnSeleccionarDias.setStyle("-fx-background-color: #666666; -fx-text-fill: #999999;");
            
            // Bloquear checkbox chofer
            chkChofer.setDisable(true);
            
            // Oscurecer imagen
            if (imgCoche.getImage() != null) {
                imgCoche.setOpacity(0.6);
            }
        } else {
            // Si está disponible, mostrar normal
            lblMarcaYModelo.setText(
                    (coche.getMarca() != null ? coche.getMarca() + " · " : "") + coche.getModelo()
            );
            lblMarcaYModelo.setStyle("-fx-text-fill: #f5f5f5; -fx-font-size: 26px; -fx-font-weight: bold;");
            
            lblDescripcion.setText(coche.getDescripcion() != null ? coche.getDescripcion() : "");
            lblDescripcion.setStyle("-fx-text-fill: #b5b5b5; -fx-font-size: 13px;");
            
            lblPotencia.setText(coche.getPotencia() + " CV");
            lblMotor.setText(coche.getMotor() != null ? coche.getMotor() : "Desconocido");
            lblVelocidad.setText(coche.getVelocidadMax() + " km/h");
            lblPlazas.setText(String.valueOf(coche.getPlazas()));
            lblMatricula.setText(coche.getMatricula() != null ? coche.getMatricula() : "---");
            lblPrecio.setText((int) coche.getPrecioDiario() + "€/mes");
            lblPrecio.setStyle("-fx-text-fill: #ffd666; -fx-font-size: 18px; -fx-font-weight: bold;");
            
            // Habilitar botón
            btnSeleccionarDias.setDisable(false);
            btnSeleccionarDias.setText("Seleccionar días para el alquiler");
            btnSeleccionarDias.setStyle("-fx-background-radius: 20; -fx-background-color: #ffd666; -fx-text-fill: #111111; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 26 10 26;");
            
            // Habilitar checkbox chofer
            chkChofer.setDisable(false);
            
            // Restaurar opacidad imagen
            imgCoche.setOpacity(1.0);
        }

        cargarImagen(coche.getImagenURL());
    }

    private void cargarImagen(String imagenURL) {
        if (imagenURL == null || imagenURL.trim().isEmpty()) {
            imgCoche.setImage(null);
            return;
        }

        try {

            Image img = null;

            URL urlVista = getClass().getResource("/vista/" + imagenURL);
            if (urlVista != null) {
                img = new Image(urlVista.toExternalForm());
            } else {
                URL urlImagenes = getClass().getResource("/imagenes/" + imagenURL);
                if (urlImagenes != null) {
                    img = new Image(urlImagenes.toExternalForm());
                }
            }

            if (img != null) {
                imgCoche.setImage(img);
                imgCoche.setFitWidth(520);
                imgCoche.setFitHeight(360);
                imgCoche.setPreserveRatio(false); 
                imgCoche.setSmooth(true);
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
        // MODIFICACIÓN: Verificar si el coche está disponible
        if (cocheActual == null) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Seleccionar días");
            a.setHeaderText(null);
            a.setContentText("No hay ningún coche cargado para proceder con la selección de días.");
            a.showAndWait();
            return;
        }

        // MODIFICACIÓN: Verificar si el coche NO está disponible
        if (!cocheActual.isDisponible()) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Coche reservado");
            a.setHeaderText(null);
            a.setContentText("Este coche está actualmente reservado y no disponible para alquiler.");
            a.showAndWait();
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
                Alert err = new Alert(Alert.AlertType.ERROR, "No se encontró la vista de reservas (PanelReservaUser.fxml).");
                err.showAndWait();
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            PanelReservaUserControlador controladorReserva = loader.getController();
            controladorReserva.setDatos(cocheActual, elegirChofer);

            Stage stage = new Stage();
            stage.setTitle("Seleccionar días de alquiler");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException ex) {
            ex.printStackTrace();
            Alert err = new Alert(Alert.AlertType.ERROR);
            err.setTitle("Error");
            err.setHeaderText("No se pudo abrir la ventana de reservas");
            err.setContentText("Comprueba que PanelReservaUser.fxml no tiene errores y está en /vista/PanelReservaUser.fxml.\n" + ex.getMessage());
            err.showAndWait();
        }
    }

    @FXML
    private void abrirMisReservas() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/PanelConfigReservaUser.fxml"));
            Parent rootReservas = loader.load();

            PanelConfigReservaUserControlador controlador = loader.getController();
            String nifUsuario = obtenerNifUsuarioActual();
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
}