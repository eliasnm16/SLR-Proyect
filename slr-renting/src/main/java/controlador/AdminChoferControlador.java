package controlador;

import dao.ChoferDAO;
import dto.ChoferDTO;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AdminChoferControlador implements Initializable {

    @FXML private TableView<ChoferDTO> tblChofer;
    @FXML private TableColumn<ChoferDTO, Number> clmID;
    @FXML private TableColumn<ChoferDTO, String> clmNombre;
    @FXML private TableColumn<ChoferDTO, String> clmTlf;
    @FXML private TableColumn<ChoferDTO, String> clmDni;
    @FXML private TableColumn<ChoferDTO, String> clmDisp;

    @FXML private Button btnVolver;
    @FXML private Button btnAñadir;
    @FXML private Button btnBorrar;
    @FXML private Button btnEditar;

    private final ChoferDAO choferDAO = new ChoferDAO();
    private final ObservableList<ChoferDTO> listaChoferes = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarColumnas();
        cargarChoferes();

        if (btnVolver != null) btnVolver.setOnAction(e -> volver());
        if (btnAñadir != null) btnAñadir.setOnAction(e -> abrirFormulario(null));
        if (btnBorrar != null) btnBorrar.setOnAction(e -> borrarChofer());
        if (btnEditar != null) btnEditar.setOnAction(e -> editarChofer());
    }

    private void configurarColumnas() {
        // Asociar columnas con propiedades del DTO
        if (clmID != null) clmID.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getId_chofer()));
        if (clmNombre != null) clmNombre.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getNombre_completo()));
        if (clmTlf != null) clmTlf.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getTelefono()));
        if (clmDni != null) clmDni.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getDni()));
        if (clmDisp != null) clmDisp.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().isDisposicion() ? "Disponible" : "No disponible"));
    }

    private void cargarChoferes() {
        try {
            List<ChoferDTO> lista = choferDAO.listarChoferes();
            System.out.println("Número de choferes leídos: " + lista.size());
            for (ChoferDTO c : lista) {
                System.out.println("Chofer -> ID:" + c.getId_chofer() + " | " + c.getNombre_completo());
            }
            listaChoferes.setAll(lista);
            if (tblChofer != null) tblChofer.setItems(listaChoferes);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error cargando choferes: " + e.getMessage());
        }
    }

    private void volver() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/vista/Paneladmin.fxml"));
            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("No se pudo volver al panel principal.");
        }
    }

    /**
     * Abre Añadirchofer.fxml. Si se pasa un ChoferDTO, intenta invocar setChofer(ChoferDTO)
     * en el controlador del formulario para que éste rellene los campos (edición).
     */
    private void abrirFormulario(ChoferDTO choferSeleccionado) {
        try {
            URL fxmlUrl = getClass().getResource("/vista/Añadirchofer.fxml");
            if (fxmlUrl == null) {
                // fallback sin carpeta vista
                fxmlUrl = getClass().getResource("/Añadirchofer.fxml");
            }
            if (fxmlUrl == null) {
                mostrarError("No se encontró el formulario Añadirchofer.fxml");
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            // Si se está editando, pasamos el chofer al controlador del formulario (si tiene setChofer)
            if (choferSeleccionado != null) {
                Object controller = loader.getController();
                if (controller != null) {
                    try {
                        Method m = controller.getClass().getMethod("setChofer", ChoferDTO.class);
                        m.invoke(controller, choferSeleccionado);
                    } catch (NoSuchMethodException nsme) {
                        System.err.println("El controlador del formulario no tiene setChofer(ChoferDTO)");
                    }
                }
            }

            Stage ventana = new Stage();
            ventana.setScene(new Scene(root));
            ventana.initModality(Modality.APPLICATION_MODAL);
            ventana.setTitle(choferSeleccionado == null ? "Añadir chofer" : "Editar chofer");
            ventana.showAndWait();

            // Refrescar tabla después de cerrar
            cargarChoferes();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al abrir el formulario de chofer.");
        }
    }

    private void borrarChofer() {
        ChoferDTO seleccionado = tblChofer.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Selecciona un chofer", "Debes seleccionar un chofer para borrar.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText("Vas a eliminar: " + seleccionado.getNombre_completo());
        confirm.setContentText("¿Estás seguro?");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                choferDAO.eliminarChofer(seleccionado.getId_chofer());
                cargarChoferes();
            } catch (Exception e) {
                e.printStackTrace();
                mostrarError("Error al eliminar chofer: " + e.getMessage());
            }
        }
    }

    private void editarChofer() {
        ChoferDTO seleccionado = tblChofer.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Selecciona un chofer", "Debes seleccionar un chofer para editar.");
            return;
        }
        abrirFormulario(seleccionado);
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }

    private void mostrarError(String mensaje) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }
}
