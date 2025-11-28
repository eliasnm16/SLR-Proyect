package controlador;

import javafx.stage.Window;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import dao.CocheDAO;
import dto.CocheDTO;
import dto.ClienteDTO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PanelMainUserControlador implements Initializable {

    @FXML
    private BorderPane root;

    @FXML
    private HBox contenedorCoches;

    @FXML
    private ImageView imgDestacado;

    @FXML
    private Label lblModeloDestacado;

    @FXML
    private Label lblDescripcionDestacado;

    @FXML
    private Label lblPotenciaDestacado;

    @FXML
    private Label lblAceleracionDestacado;

    @FXML
    private Label lblVelocidadDestacado;

    @FXML
    private Label lblTransmisionDestacado;

    @FXML
    private Label lblPrecioDestacado;

    @FXML
    private Button btnVerDetallesDestacado;

    @FXML
    private MenuButton menuUsuario;

    @FXML
    private MenuItem itemConfig;

    @FXML
    private MenuItem itemMisReservas;

    @FXML
    private MenuItem itemLogout;

    private CocheDAO cocheDAO = new CocheDAO();

    // Coche destacado
    private CocheDTO cocheDestacado;

    // Usuario actual
    private ClienteDTO usuario;

    // NIF del usuario (para reservas, etc.)
    private String nifUsuarioActual;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Recuperar usuario logueado desde el login
        usuario = LoginUsuarioRegistradoControlador.usuarioActual;

        if (usuario != null) {
            // Texto del menú con el nombre del usuario
            if (menuUsuario != null) {
                menuUsuario.setText(usuario.getNombreCompleto());
            }
            // Si no nos han pasado el NIF por setNifUsuarioActual, lo rellenamos
            if (this.nifUsuarioActual == null || this.nifUsuarioActual.isEmpty()) {
                this.nifUsuarioActual = usuario.getNif_nie();
            }
        }

        cargarDestacado();
        cargarColeccion();
        configurarMenu();
    }

    private void cargarDestacado() {
        List<CocheDTO> nuevos = cocheDAO.listarCochesNuevos();
        if (nuevos.isEmpty()) return;

        cocheDestacado = nuevos.get(0);
        CocheDTO c = cocheDestacado;

        lblModeloDestacado.setText(c.getModelo());
        lblDescripcionDestacado.setText(c.getDescripcion());
        lblPotenciaDestacado.setText(c.getPotencia() + " CV");
        lblAceleracionDestacado.setText(c.getPlazas() + " Plazas");
        lblVelocidadDestacado.setText(c.getVelocidadMax() + " km/h");
        lblTransmisionDestacado.setText(c.getMotor());
        lblPrecioDestacado.setText((int) c.getPrecioDiario() + "€/mes");

        if (c.getImagenURL() != null && !c.getImagenURL().isEmpty()) {
            try {
                Image img = new Image(getClass().getResourceAsStream("/vista/" + c.getImagenURL()));
                imgDestacado.setImage(img);
                imgDestacado.setFitWidth(420);
                imgDestacado.setFitHeight(260);
                imgDestacado.setPreserveRatio(false);
                imgDestacado.setSmooth(true);
            } catch (Exception ignored) {
            }
        }

        btnVerDetallesDestacado.setOnAction(e -> abrirDetalles(c));
    }

    private void cargarColeccion() {
        contenedorCoches.getChildren().clear();

        List<CocheDTO> disponibles = cocheDAO.listarCochesDisponibles();

        for (CocheDTO c : disponibles) {

            // Omitir el coche destacado en la colección
            if (cocheDestacado != null && c.getBastidor() == cocheDestacado.getBastidor()) {
                continue;
            }

            VBox card = crearCard(c);
            contenedorCoches.getChildren().add(card);
        }
    }

    private VBox crearCard(CocheDTO c) {
        VBox card = new VBox(12);
        card.setPrefWidth(360);
        card.setStyle("-fx-background-color: #121212; -fx-background-radius: 18; -fx-border-radius: 18; -fx-border-color: #3b3320;");

        ImageView img = new ImageView();
        img.setFitWidth(360);
        img.setFitHeight(180);
        img.setPreserveRatio(false);
        img.setSmooth(true);

        if (c.getImagenURL() != null && !c.getImagenURL().isEmpty()) {
            try {
                Image imagen = new Image(getClass().getResourceAsStream("/vista/" + c.getImagenURL()));
                img.setImage(imagen);
            } catch (Exception ignored) {
            }
        }

        Label modelo = new Label(c.getModelo());
        modelo.setWrapText(true);
        modelo.setStyle("-fx-text-fill: #f5f5f5; -fx-font-size: 14px; -fx-font-weight: bold;");

        Label desc = new Label(c.getDescripcion());
        desc.setWrapText(true);
        desc.setStyle("-fx-text-fill: #b5b5b5; -fx-font-size: 12px;");

        Label lblDesde = new Label("Desde");
        lblDesde.setStyle("-fx-text-fill: #b5b5b5; -fx-font-size: 11px;");

        Label lblPrecio = new Label((int) c.getPrecioDiario() + "€/mes");
        lblPrecio.setStyle("-fx-text-fill: #ffd666; -fx-font-size: 14px; -fx-font-weight: bold;");

        Button btn = new Button("Ver Más");
        btn.setStyle("-fx-background-radius: 16; -fx-background-color: #ffd666; -fx-text-fill: #111111; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 6 16 6 16;");
        btn.setOnAction(e -> abrirDetalles(c));

        HBox abajo = new HBox(12, new VBox(4, lblDesde, lblPrecio), new StackPane(), btn);
        HBox.setHgrow(abajo.getChildren().get(1), javafx.scene.layout.Priority.ALWAYS);

        VBox contenido = new VBox(8, modelo, desc, abajo);
        contenido.setPadding(new Insets(0, 18, 18, 18));

        card.getChildren().addAll(img, contenido);

        return card;
    }

    private void configurarMenu() {
        itemConfig.setOnAction(e -> abrirConfig());
        itemMisReservas.setOnAction(e -> abrirMisReservas());
        itemLogout.setOnAction(e -> cerrarSesion());
    }

    private void abrirDetalles(CocheDTO coche) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/PanelCocheUser.fxml"));
            Parent root = loader.load();

            PanelCocheUserControlador controlador = loader.getController();
            controlador.setCoche(coche);
            controlador.setNifUsuarioActual(this.nifUsuarioActual);

            Stage stage = new Stage();
            stage.setTitle("Detalles del Coche");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // -------------------- MIS RESERVAS --------------------
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

    // -------------------- CONFIG PERFIL USUARIO --------------------
    private void abrirConfig() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/PanelConfigUser.fxml"));
            Parent rootConfig = loader.load();

            PanelConfigUserControlador controller = loader.getController();

            // Pasar el usuario actual al panel de configuración
            if (usuario != null) {
                controller.cargarUsuario(usuario);
            }

            Stage stage = (Stage) menuUsuario.getScene().getWindow();
            stage.setScene(new Scene(rootConfig));
            stage.setTitle("Configuración de Usuario");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // -------------------- CERRAR SESIÓN --------------------
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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No se pudo cerrar sesión");
            alert.showAndWait();
        }
    }
}
