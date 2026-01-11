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
    private CocheDTO cocheDestacado;
    private ClienteDTO usuario;
    private String nifUsuarioActual;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        usuario = LoginUsuarioRegistradoControlador.usuarioActual;

        if (usuario != null) {
            if (menuUsuario != null) {
                menuUsuario.setText(usuario.getNombreCompleto());
            }
            if (this.nifUsuarioActual == null || this.nifUsuarioActual.isEmpty()) {
                this.nifUsuarioActual = usuario.getNif_nie();
            }
        }

        cargarDestacado();
        cargarColeccion();
        configurarMenu();
    }

    private void cargarDestacado() {
        // SOLO coches marcados como NUEVO en la BD
        List<CocheDTO> todosLosNuevos = cocheDAO.listarCochesNuevos();
        
        if (!todosLosNuevos.isEmpty()) {
            // Tomar el primer coche nuevo (aunque esté reservado)
            cocheDestacado = todosLosNuevos.get(0);
            CocheDTO c = cocheDestacado;

            // Mostrar datos del coche
            lblModeloDestacado.setText(c.getModelo());
            lblDescripcionDestacado.setText(c.getDescripcion());
            lblPotenciaDestacado.setText(c.getPotencia() + " CV");
            lblAceleracionDestacado.setText(c.getPlazas() + " Plazas");
            lblVelocidadDestacado.setText(c.getVelocidadMax() + " km/h");
            lblTransmisionDestacado.setText(c.getMotor());
            lblPrecioDestacado.setText((int) c.getPrecioDiario() + "€/mes");

            // Verificar si está reservado
            boolean tieneReservasActivas = cocheDAO.tieneReservasActivas(c.getBastidor());
            boolean estaDisponible = c.isDisponible() && !tieneReservasActivas;
            
            // Si está reservado, cambiar estilo
            if (!estaDisponible) {
                // Texto rojo "RESERVADO" al lado del modelo
                lblModeloDestacado.setText(c.getModelo() + "  🔴 RESERVADO");
                lblModeloDestacado.setStyle("-fx-text-fill: #ff5555; -fx-font-weight: bold;");
                
                lblPrecioDestacado.setStyle("-fx-text-fill: #ff5555; -fx-font-weight: bold; -fx-strikethrough: true;");
                
                btnVerDetallesDestacado.setDisable(true);
                btnVerDetallesDestacado.setText("RESERVADO");
                btnVerDetallesDestacado.setStyle("-fx-background-color: #666666; -fx-text-fill: #999999; -fx-font-size: 14px; -fx-font-weight: bold;");
                
                // Oscurecer imagen
                imgDestacado.setOpacity(0.6);
                
            } else {
                // Estilo normal
                lblModeloDestacado.setText(c.getModelo());
                lblModeloDestacado.setStyle("-fx-text-fill: #f5f5f5; -fx-font-weight: bold;");
                lblPrecioDestacado.setStyle("-fx-text-fill: #ffd666; -fx-font-weight: bold;");
                
                btnVerDetallesDestacado.setDisable(false);
                btnVerDetallesDestacado.setText("Ver Detalles");
                btnVerDetallesDestacado.setStyle("-fx-background-radius: 20; -fx-background-color: #ffd666; -fx-text-fill: #111111; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 26 10 26;");
                
                imgDestacado.setOpacity(1.0);
            }

            // Cargar imagen
            if (c.getImagenURL() != null && !c.getImagenURL().isEmpty()) {
                try {
                    Image img = new Image(getClass().getResourceAsStream("/vista/" + c.getImagenURL()));
                    imgDestacado.setImage(img);
                    imgDestacado.setFitWidth(420);
                    imgDestacado.setFitHeight(260);
                    imgDestacado.setPreserveRatio(false);
                    imgDestacado.setSmooth(true);
                    
                    // Oscurecer imagen si está reservado
                    imgDestacado.setOpacity(estaDisponible ? 1.0 : 0.6);
                    
                } catch (Exception ignored) {}
            }

            btnVerDetallesDestacado.setOnAction(e -> abrirDetalles(c));
            
        } else {
            // NO HAY COCHES NUEVOS en la BD
            lblModeloDestacado.setText("PRÓXIMAMENTE");
            lblModeloDestacado.setStyle("-fx-text-fill: #ffd666;");
            lblDescripcionDestacado.setText("No hay coches nuevos disponibles en este momento.");
            lblDescripcionDestacado.setStyle("-fx-text-fill: #b5b5b5;");
            
            // Limpiar otros campos
            lblPotenciaDestacado.setText("");
            lblAceleracionDestacado.setText("");
            lblVelocidadDestacado.setText("");
            lblTransmisionDestacado.setText("");
            lblPrecioDestacado.setText("");
            
            // Deshabilitar botón
            btnVerDetallesDestacado.setDisable(true);
            btnVerDetallesDestacado.setText("NO DISPONIBLE");
            btnVerDetallesDestacado.setStyle("-fx-background-color: #666666; -fx-text-fill: #999999;");
            
            // Limpiar imagen
            imgDestacado.setImage(null);
            
            cocheDestacado = null;
        }
    }

    private void cargarColeccion() {
        contenedorCoches.getChildren().clear();

        // TRAE TODOS los coches (reservados y no reservados)
        List<CocheDTO> todosLosCoches = cocheDAO.listarCoches();

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
        card.setStyle("-fx-background-color: #121212; -fx-background-radius: 18; " +
                      "-fx-border-radius: 18; -fx-border-color: #3b3320;");

        // CONTENEDOR PARA LA IMAGEN (con StackPane para superponer etiqueta)
        StackPane imagenContainer = new StackPane();
        imagenContainer.setPrefSize(360, 180);
        
        // IMAGEN DEL COCHE
        ImageView img = new ImageView();
        img.setFitWidth(360);
        img.setFitHeight(180);
        img.setPreserveRatio(false);
        img.setSmooth(true);

        // CARGAR IMAGEN
        if (c.getImagenURL() != null && !c.getImagenURL().isEmpty()) {
            try {
                Image imagen = new Image(getClass().getResourceAsStream("/vista/" + c.getImagenURL()));
                img.setImage(imagen);
            } catch (Exception ignored) {
            }
        }
        
        imagenContainer.getChildren().add(img);
        
        // VERIFICAR DISPONIBILIDAD
        boolean tieneReservasActivas = cocheDAO.tieneReservasActivas(c.getBastidor());
        boolean estaDisponible = c.isDisponible() && !tieneReservasActivas;
        
        // SI ESTÁ RESERVADO O NO DISPONIBLE
        if (tieneReservasActivas || !c.isDisponible()) {
            // 1. OSCURECER LA IMAGEN (como el MK8)
            img.setOpacity(0.6);
            
            // 2. CREAR ETIQUETA "RESERVADO" ENCIMA DE LA FOTO (EXACTO como la imagen)
            Label lblReservado = new Label("RESERVADO");
            lblReservado.setStyle(
                "-fx-background-color: rgba(255, 85, 85, 0.9); " +  // Rojo semitransparente
                "-fx-text-fill: white; " +
                "-fx-font-size: 16px; " +                          // Tamaño más grande
                "-fx-font-weight: bold; " +
                "-fx-padding: 8px 20px 8px 20px; " +               // Más padding horizontal
                "-fx-background-radius: 20px;"                     // Bordes redondeados
            );
            lblReservado.setMaxWidth(Double.MAX_VALUE);
            lblReservado.setAlignment(javafx.geometry.Pos.CENTER);
            
            // 3. POSICIONAR EN EL CENTRO (como en la imagen)
            StackPane.setAlignment(lblReservado, javafx.geometry.Pos.CENTER);
            imagenContainer.getChildren().add(lblReservado);
        }

        // NOMBRE DEL MODELO (con "RESERVADO" si corresponde)
        Label modelo = new Label(c.getModelo());
        modelo.setWrapText(true);
        
        // PRECIO
        Label lblDesde = new Label("Desde");
        lblDesde.setStyle("-fx-text-fill: #b5b5b5; -fx-font-size: 11px;");

        Label lblPrecio = new Label((int) c.getPrecioDiario() + "€/mes");
        
        // BOTÓN
        Button btn = new Button("Ver Más");
        btn.setStyle("-fx-background-radius: 16; -fx-background-color: #ffd666; " +
                     "-fx-text-fill: #111111; -fx-font-size: 12px; -fx-font-weight: bold; " +
                     "-fx-padding: 6px 16px 6px 16px;");
        btn.setOnAction(e -> abrirDetalles(c));
        
        // APLICAR ESTILOS ESPECÍFICOS PARA RESERVADOS
        if (tieneReservasActivas || !c.isDisponible()) {
            // ESTILO PARA COCHES RESERVADOS (EXACTO como la imagen):
            
            // 1. BOTÓN OSCURO CON TEXTO "RESERVADO" (como en la imagen)
            btn.setDisable(true);
            btn.setText("RESERVADO");  // Texto exacto
            btn.setStyle(
                "-fx-background-color: #666666; " +  // Gris oscuro
                "-fx-text-fill: #999999; " +         // Gris claro
                "-fx-font-size: 12px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 16px; " +
                "-fx-padding: 6px 16px 6px 16px;"
            );
            
            // 2. PRECIO TACHADO EN ROJO (como en la imagen)
            lblPrecio.setStyle(
                "-fx-text-fill: #ff5555; " +         // Rojo
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-strikethrough: true;"          // Tachado
            );
            
            // 3. NOMBRE DEL MODELO EN ROJO/GRIS (como en la imagen)
            modelo.setStyle(
                "-fx-text-fill: #ff9999; " +         // Rojo claro
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold;"
            );
            
            // 4. CAMBIAR TEXTO "Desde" A ROJO
            lblDesde.setStyle("-fx-text-fill: #ff5555; -fx-font-size: 11px;");
            
        } else {
            // ESTILO NORMAL PARA COCHES DISPONIBLES
            modelo.setStyle("-fx-text-fill: #f5f5f5; -fx-font-size: 14px; -fx-font-weight: bold;");
            lblPrecio.setStyle("-fx-text-fill: #ffd666; -fx-font-size: 14px; -fx-font-weight: bold;");
        }

        // DESCRIPCIÓN
        Label desc = new Label(c.getDescripcion());
        desc.setWrapText(true);
        desc.setStyle("-fx-text-fill: #b5b5b5; -fx-font-size: 12px;");

        // CONTENEDOR INFERIOR (precio + botón)
        HBox abajo = new HBox(12, new VBox(4, lblDesde, lblPrecio), new StackPane(), btn);
        HBox.setHgrow(abajo.getChildren().get(1), javafx.scene.layout.Priority.ALWAYS);

        // CONTENIDO TEXTO
        VBox contenido = new VBox(8, modelo, desc, abajo);
        contenido.setPadding(new Insets(0, 18, 18, 18));

        // ENSAMBLAR TARJETA COMPLETA
        card.getChildren().addAll(imagenContainer, contenido);
        return card;
    }

    private void configurarMenu() {
        itemConfig.setOnAction(e -> abrirConfig());
        itemMisReservas.setOnAction(e -> abrirMisReservas());
        itemLogout.setOnAction(e -> cerrarSesion());
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
            // 1. Cerrar la ventana actual (PanelMainUser)
            Stage stageActual = (Stage) menuUsuario.getScene().getWindow();
            stageActual.close();
            
            // 2. Abrir la nueva ventana (PanelCocheUser)
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

    private void abrirConfig() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/PanelConfigUser.fxml"));
            Parent rootConfig = loader.load();

            PanelConfigUserControlador controller = loader.getController();
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