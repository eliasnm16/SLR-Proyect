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

import controlador.AlertUtils;

public class PanelMainUserControlador implements Initializable {

    @FXML private BorderPane root;
    @FXML private HBox contenedorCoches;
    @FXML private ImageView imgDestacado;

    @FXML private Label lblModeloDestacado;
    @FXML private Label lblDescripcionDestacado;
    @FXML private Label lblPotenciaDestacado;
    @FXML private Label lblAceleracionDestacado;
    @FXML private Label lblVelocidadDestacado;
    @FXML private Label lblTransmisionDestacado;
    @FXML private Label lblPrecioDestacado;

    @FXML private Button btnVerDetallesDestacado;

    @FXML private MenuButton menuUsuario;
    @FXML private MenuItem itemConfig;
    @FXML private MenuItem itemMisReservas;
    @FXML private MenuItem itemLogout;

    private final CocheDAO cocheDAO = new CocheDAO();

    private CocheDTO cocheDestacado;
    private ClienteDTO usuario;
    private String nifUsuarioActual;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        usuario = LoginUsuarioRegistradoControlador.usuarioActual;

        if (usuario != null) {
            if (menuUsuario != null) {
                String nombre = usuario.getNombreCompleto();
                menuUsuario.setText(nombre != null && !nombre.isBlank() ? nombre : "Usuario");
            }
            if (this.nifUsuarioActual == null || this.nifUsuarioActual.isEmpty()) {
                this.nifUsuarioActual = usuario.getNif_nie();
            }
        } else {
            if (menuUsuario != null) menuUsuario.setText("Usuario");
        }

        cargarDestacado();
        cargarColeccion();
        configurarMenu();
    }

    private void cargarDestacado() {

        // SOLO coches marcados como NUEVO en la BD
        List<CocheDTO> todosLosNuevos = cocheDAO.listarCochesNuevos();

        if (todosLosNuevos == null || todosLosNuevos.isEmpty()) {
            // NO HAY COCHES NUEVOS en la BD
            lblModeloDestacado.setText("PRÓXIMAMENTE");
            lblModeloDestacado.setStyle("-fx-text-fill: #ffd666;");
            lblDescripcionDestacado.setText("No hay coches nuevos disponibles en este momento.");
            lblDescripcionDestacado.setStyle("-fx-text-fill: #b5b5b5;");

            lblPotenciaDestacado.setText("");
            lblAceleracionDestacado.setText("");
            lblVelocidadDestacado.setText("");
            lblTransmisionDestacado.setText("");
            lblPrecioDestacado.setText("");

            btnVerDetallesDestacado.setDisable(true);
            btnVerDetallesDestacado.setText("NO DISPONIBLE");
            btnVerDetallesDestacado.setStyle("-fx-background-color: #666666; -fx-text-fill: #999999;");

            imgDestacado.setImage(null);

            cocheDestacado = null;
            return;
        }

        // Tomar el primer coche nuevo (aunque esté reservado)
        cocheDestacado = todosLosNuevos.get(0);
        CocheDTO c = cocheDestacado;

        // Mostrar datos del coche
        lblModeloDestacado.setText(valorSeguro(c.getModelo(), "Modelo"));
        lblDescripcionDestacado.setText(valorSeguro(c.getDescripcion(), ""));
        lblPotenciaDestacado.setText(c.getPotencia() + " CV");
        lblAceleracionDestacado.setText(c.getPlazas() + " Plazas");
        lblVelocidadDestacado.setText(c.getVelocidadMax() + " km/h");
        lblTransmisionDestacado.setText(valorSeguro(c.getMotor(), "Desconocido"));
        lblPrecioDestacado.setText((int) c.getPrecioDiario() + "€/mes");

        // Verificar si está reservado
        boolean tieneReservasActivas = cocheDAO.tieneReservasActivas(c.getBastidor());
        boolean estaDisponible = c.isDisponible() && !tieneReservasActivas;

        if (!estaDisponible) {
            lblModeloDestacado.setText(valorSeguro(c.getModelo(), "Modelo") + "  🔴 RESERVADO");
            lblModeloDestacado.setStyle("-fx-text-fill: #ff5555; -fx-font-weight: bold;");

            lblPrecioDestacado.setStyle("-fx-text-fill: #ff5555; -fx-font-weight: bold; -fx-strikethrough: true;");

            btnVerDetallesDestacado.setDisable(true);
            btnVerDetallesDestacado.setText("RESERVADO");
            btnVerDetallesDestacado.setStyle("-fx-background-color: #666666; -fx-text-fill: #999999; -fx-font-size: 14px; -fx-font-weight: bold;");

            imgDestacado.setOpacity(0.6);

        } else {
            lblModeloDestacado.setText(valorSeguro(c.getModelo(), "Modelo"));
            lblModeloDestacado.setStyle("-fx-text-fill: #f5f5f5; -fx-font-weight: bold;");

            lblPrecioDestacado.setStyle("-fx-text-fill: #ffd666; -fx-font-weight: bold;");

            btnVerDetallesDestacado.setDisable(false);
            btnVerDetallesDestacado.setText("Ver Detalles");
            btnVerDetallesDestacado.setStyle("-fx-background-radius: 20; -fx-background-color: #ffd666; -fx-text-fill: #111111; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 26 10 26;");

            imgDestacado.setOpacity(1.0);
        }

        // Cargar imagen (usar helper)
        cargarImagenEn(imgDestacado, c.getImagenURL(), 420, 260, false);
        imgDestacado.setOpacity(estaDisponible ? 1.0 : 0.6);

        btnVerDetallesDestacado.setOnAction(e -> abrirDetalles(c));
    }

    private void cargarColeccion() {
        contenedorCoches.getChildren().clear();

        // TRAE TODOS los coches 
        List<CocheDTO> todosLosCoches = cocheDAO.listarCoches();
        if (todosLosCoches == null) return;

        for (CocheDTO c : todosLosCoches) {
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


        StackPane imagenContainer = new StackPane();
        imagenContainer.setPrefSize(360, 180);

        ImageView img = new ImageView();
        img.setFitWidth(360);
        img.setFitHeight(180);
        img.setPreserveRatio(false);
        img.setSmooth(true);

        cargarImagenEn(img, c.getImagenURL(), 360, 180, false);

        imagenContainer.getChildren().add(img);

        boolean tieneReservasActivas = cocheDAO.tieneReservasActivas(c.getBastidor());
        boolean estaDisponible = c.isDisponible() && !tieneReservasActivas;

        if (!estaDisponible) {
            img.setOpacity(0.6);

            Label lblReservado = new Label("RESERVADO");
            lblReservado.setStyle(
                "-fx-background-color: rgba(255, 85, 85, 0.9); " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 16px; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 8px 20px 8px 20px; " +
                "-fx-background-radius: 20px;"
            );
            lblReservado.setMaxWidth(Double.MAX_VALUE);
            lblReservado.setAlignment(javafx.geometry.Pos.CENTER);
            StackPane.setAlignment(lblReservado, javafx.geometry.Pos.CENTER);

            imagenContainer.getChildren().add(lblReservado);
        }

        Label modelo = new Label(valorSeguro(c.getModelo(), "Modelo"));
        modelo.setWrapText(true);

        Label desc = new Label(valorSeguro(c.getDescripcion(), ""));
        desc.setWrapText(true);
        desc.setStyle("-fx-text-fill: #b5b5b5; -fx-font-size: 12px;");

        Label lblDesde = new Label("Desde");
        lblDesde.setStyle("-fx-text-fill: #b5b5b5; -fx-font-size: 11px;");

        Label lblPrecio = new Label((int) c.getPrecioDiario() + "€/mes");

        Button btn = new Button("Ver Más");
        btn.setStyle("-fx-background-radius: 16; -fx-background-color: #ffd666; -fx-text-fill: #111111; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 6px 16px 6px 16px;");
        btn.setOnAction(e -> abrirDetalles(c));

        if (!estaDisponible) {
            btn.setDisable(true);
            btn.setText("RESERVADO");
            btn.setStyle(
                "-fx-background-color: #666666; " +
                "-fx-text-fill: #999999; " +
                "-fx-font-size: 12px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 16px; " +
                "-fx-padding: 6px 16px 6px 16px;"
            );

            lblPrecio.setStyle("-fx-text-fill: #ff5555; -fx-font-size: 14px; -fx-font-weight: bold; -fx-strikethrough: true;");
            modelo.setStyle("-fx-text-fill: #ff9999; -fx-font-size: 14px; -fx-font-weight: bold;");
            lblDesde.setStyle("-fx-text-fill: #ff5555; -fx-font-size: 11px;");
        } else {
            modelo.setStyle("-fx-text-fill: #f5f5f5; -fx-font-size: 14px; -fx-font-weight: bold;");
            lblPrecio.setStyle("-fx-text-fill: #ffd666; -fx-font-size: 14px; -fx-font-weight: bold;");
        }

        HBox abajo = new HBox(12, new VBox(4, lblDesde, lblPrecio), new StackPane(), btn);
        HBox.setHgrow(abajo.getChildren().get(1), javafx.scene.layout.Priority.ALWAYS);

        VBox contenido = new VBox(8, modelo, desc, abajo);
        contenido.setPadding(new Insets(0, 18, 18, 18));

        card.getChildren().addAll(imagenContainer, contenido);
        return card;
    }

    private void configurarMenu() {
        if (itemConfig != null) itemConfig.setOnAction(e -> abrirConfig());
        if (itemMisReservas != null) itemMisReservas.setOnAction(e -> abrirMisReservas());
        if (itemLogout != null) itemLogout.setOnAction(e -> cerrarSesion());
    }

    private void abrirDetalles(CocheDTO coche) {
        boolean tieneReservasActivas = cocheDAO.tieneReservasActivas(coche.getBastidor());
        boolean estaDisponible = coche.isDisponible() && !tieneReservasActivas;

        if (!estaDisponible) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(tieneReservasActivas ? "Coche reservado" : "Coche no disponible");
            alert.setHeaderText(null);
            alert.setContentText(tieneReservasActivas ?
                "Este coche está actualmente reservado." :
                "Este coche no está disponible para alquiler.");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/PanelCocheUser.fxml"));
            Parent root = loader.load();

            PanelCocheUserControlador controlador = loader.getController();
            controlador.setCoche(coche);
            controlador.setNifUsuarioActual(this.nifUsuarioActual);

            Stage stageNueva = new Stage();
            stageNueva.setTitle("Detalles del Coche");
            stageNueva.setScene(new Scene(root));
            stageNueva.show();

        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.error("Error", "No se pudo abrir el detalle del coche.");
        }
    }

    @FXML
    private void abrirMisReservas() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/PanelConfigReservaUser.fxml"));
            Parent rootReservas = loader.load();

            PanelConfigReservaUserControlador controlador = loader.getController();
            String nifUsuario = obtenerNifUsuarioActual();

            if (nifUsuario == null || nifUsuario.isBlank()) {
                AlertUtils.warning("Sesión", "No se pudo identificar el usuario actual.");
                return;
            }

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

    private void abrirConfig() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/PanelConfigUser.fxml"));
            Parent rootConfig = loader.load();

            PanelConfigUserControlador controller = loader.getController();

            if (usuario != null) {
                controller.cargarUsuario(usuario);
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

    
    private static String valorSeguro(String s, String fallback) {
        if (s == null) return fallback;
        s = s.trim();
        return s.isEmpty() ? fallback : s;
    }

    private void cargarImagenEn(ImageView imageView, String imagenURL, double w, double h, boolean preserveRatio) {
        imageView.setFitWidth(w);
        imageView.setFitHeight(h);
        imageView.setPreserveRatio(preserveRatio);
        imageView.setSmooth(true);

        if (imagenURL == null || imagenURL.isBlank()) {
            imageView.setImage(null);
            return;
        }

        String path = "/vista/" + imagenURL.trim();

        try {
            var is = getClass().getResourceAsStream(path);
            if (is == null) {
                System.err.println("Imagen no encontrada en classpath: " + path);
                imageView.setImage(null);
                return;
            }
            imageView.setImage(new Image(is));
        } catch (Exception ex) {
            System.err.println("Error cargando imagen: " + path + " -> " + ex.getMessage());
            imageView.setImage(null);
        }
    }
}
