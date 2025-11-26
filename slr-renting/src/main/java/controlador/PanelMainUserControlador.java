package controlador;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import dao.CocheDAO;
import dto.CocheDTO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class PanelMainUserControlador implements Initializable {

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


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarDestacado();
        cargarColeccion();
        configurarMenu();
    }


    private void cargarDestacado() {
        List<CocheDTO> nuevos = cocheDAO.listarCochesNuevos();
        if (nuevos.isEmpty()) return;

        CocheDTO c = nuevos.get(0);

        lblModeloDestacado.setText(c.getModelo());
        lblDescripcionDestacado.setText(c.getDescripcion());
        lblPotenciaDestacado.setText(c.getPotencia() + " CV");
        lblAceleracionDestacado.setText("2.7 s");
        lblVelocidadDestacado.setText(c.getVelocidadMax() + " km/h");
        lblTransmisionDestacado.setText(c.getMotor());
        lblPrecioDestacado.setText((int) c.getPrecioDiario() + "€/mes");

        if (c.getImagenURL() != null) {
            try {
                imgDestacado.setImage(new Image(getClass().getResourceAsStream("/imagenes/" + c.getImagenURL())));
            } catch (Exception ignored) {}
        }

        btnVerDetallesDestacado.setOnAction(e -> abrirDetalles(c));
    }


    private void cargarColeccion() {
        contenedorCoches.getChildren().clear();

        List<CocheDTO> disponibles = cocheDAO.listarCochesDisponibles();

        for (CocheDTO c : disponibles) {
            VBox card = crearCard(c);
            contenedorCoches.getChildren().add(card);
        }
    }


    private VBox crearCard(CocheDTO c) {
        VBox card = new VBox(12);
        card.setPrefWidth(360);
        card.setStyle("-fx-background-color: #121212; -fx-background-radius: 18; -fx-border-radius: 18; -fx-border-color: #3b3320;");

        ImageView img = new ImageView();
        img.setFitHeight(180);
        img.setPreserveRatio(true);

        if (c.getImagenURL() != null) {
            try {
                img.setImage(new Image(getClass().getResourceAsStream("/imagenes/" + c.getImagenURL())));
            } catch (Exception ignored) {}
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


    private void abrirDetalles(CocheDTO c) {
        System.out.println("Detalles de coche: " + c.getModelo());
    }

    private void abrirConfig() {
        System.out.println("Abrir Config");
    }

    private void abrirMisReservas() {
        System.out.println("Abrir Mis Reservas");
    }

    private void cerrarSesion() {
        System.out.println("Logout");
    }
}
