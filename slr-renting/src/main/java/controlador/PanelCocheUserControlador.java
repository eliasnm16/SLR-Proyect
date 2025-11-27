package controlador;

import java.io.IOException;
import java.net.URL;
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
import javafx.stage.Stage;

/**
 * Controlador para la vista PanelCocheUser.fxml
 * Rellena todos los campos con los datos de un CocheDTO y permite
 * seleccionar días para el alquiler, enviando el coche y la opción
 * "contratar chófer" a un handler externo si se ha registrado.
 */
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarMenu();

        // Acción por defecto del botón: llama al handler si existe,
        // si no, abre la ventana de selección de fechas (PanelReservaUser)
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

    private void abrirMisReservas() {
        System.out.println("Abrir Mis Reservas desde PanelCocheUser");
    }

    private void cerrarSesion() {
        System.out.println("Logout desde PanelCocheUser");
    }

    /**
     * Rellena la vista con los datos del coche dado.
     * Debe llamarse desde el código que instancie/abra este panel.
     */
    public void setCoche(CocheDTO coche) {
        if (coche == null) return;
        this.cocheActual = coche;

        lblMarcaYModelo.setText((coche.getMarca() != null ? coche.getMarca() + " · " : "") + coche.getModelo());
        lblDescripcion.setText(coche.getDescripcion() != null ? coche.getDescripcion() : "");
        lblPotencia.setText(coche.getPotencia() + " CV");
        lblMotor.setText(coche.getMotor() != null ? coche.getMotor() : "Desconocido");
        lblVelocidad.setText(coche.getVelocidadMax() + " km/h");
        lblPlazas.setText(String.valueOf(coche.getPlazas()));
        lblMatricula.setText(coche.getMatricula() != null ? coche.getMatricula() : "---");
        lblPrecio.setText((int) coche.getPrecioDiario() + "€/mes");

        cargarImagen(coche.getImagenURL());
    }

    private void cargarImagen(String imagenURL) {
        if (imagenURL == null || imagenURL.trim().isEmpty()) {
            imgCoche.setImage(null);
            return;
        }

        try {
            Image img = new Image(getClass().getResourceAsStream("/imagenes/" + imagenURL));
            imgCoche.setImage(img);
        } catch (Exception ex) {
            System.err.println("No se pudo cargar la imagen: " + imagenURL + " -> " + ex.getMessage());
            imgCoche.setImage(null);
        }
    }
    
    

    /**
     * Acción al pulsar el botón de seleccionar días.
     * Si existe un handler registrado lo invoca, si no abre el panel de reservas.
     */
    private void onSeleccionarDiasClicked() {
        if (cocheActual == null) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Seleccionar días");
            a.setHeaderText(null);
            a.setContentText("No hay ningún coche cargado para proceder con la selección de días.");
            a.showAndWait();
            return;
        }

        boolean elegirChofer = chkChofer.isSelected();

        if (seleccionHandler != null) {
            // Si alguien ha registrado un handler externo, lo usamos (flexibilidad para pruebas / integración).
            seleccionHandler.handle(cocheActual, elegirChofer);
            return;
        }

        // Si no hay handler, abrimos la ventana de reservas por defecto
        try {
            // Comprobación previa: recurso
            java.net.URL fxmlUrl = getClass().getResource("/vista/PanelReservaUser.fxml");
            if (fxmlUrl == null) {
                System.err.println("ERROR: no se encontró /vista/PanelReservaUser.fxml en el classpath. Revisa ubicación del fichero FXML.");
                Alert err = new Alert(Alert.AlertType.ERROR, "No se encontró la vista de reservas (PanelReservaUser.fxml).");
                err.showAndWait();
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            PanelReservaUserControlador controladorReserva = loader.getController();
            // Pasamos coche y si se solicitó chofer
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
            err.showAndWait(); }
        }

    /**
     * Registra un handler que será llamado cuando el usuario pulse "Seleccionar días para el alquiler".
     * El handler recibe el CocheDTO seleccionado y un boolean indicando si se ha marcado la casilla de chófer.
     */
    public void setSeleccionDiasHandler(SeleccionDiasHandler handler) {
        this.seleccionHandler = handler;
    }

    /**
     * Interfaz funcional para notificar al código que gestiona la reserva la acción del usuario.
     */
    public static interface SeleccionDiasHandler {
        void handle(CocheDTO coche, boolean contratarChofer);
    }

}


