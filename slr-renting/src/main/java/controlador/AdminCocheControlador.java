package controlador;
import dao.CocheDAO;
import dto.CocheDTO;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleDoubleProperty;
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

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AdminCocheControlador implements Initializable {

    @FXML private TableView<CocheDTO> tblCoche;

    @FXML private TableColumn<CocheDTO, Number> clmBast;
    @FXML private TableColumn<CocheDTO, String> clmMtr;
    @FXML private TableColumn<CocheDTO, String> clmMarca;
    @FXML private TableColumn<CocheDTO, String> clmMdl;
    @FXML private TableColumn<CocheDTO, Number> clmPr;
    @FXML private TableColumn<CocheDTO, String> clmDisp;

    
    @FXML private TableColumn<CocheDTO, String> clmImg;

    @FXML private Button btnVolver;
    @FXML private Button btnAñadir;
    @FXML private Button btnBorrar;
    @FXML private Button btnEditar;

    private final CocheDAO cocheDAO = new CocheDAO();
    private final ObservableList<CocheDTO> listaCoches = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        configurarColumnas();
        cargarCoches();

        btnVolver.setOnAction(e -> volver());
        btnAñadir.setOnAction(e -> abrirFormulario(null));
        btnBorrar.setOnAction(e -> borrarCoche());
        btnEditar.setOnAction(e -> editarCoche());
    }

    private void configurarColumnas() {
        clmBast.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getBastidor()));
        clmMtr.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getMatricula()));
        clmMarca.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getMarca()));
        clmMdl.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getModelo()));
        clmPr.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getPrecioDiario()));
        clmDisp.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().isDisponible() ? "Sí" : "No"));

        if (clmImg != null) {
            clmImg.setCellValueFactory(cell ->
                    new ReadOnlyStringWrapper(cell.getValue().getImagenURL()));
        }
    }

    private void cargarCoches() {
        List<CocheDTO> lista = cocheDAO.listarCoches();

        System.out.println("RESULTADO SQL: número de coches = " + lista.size());

        for (CocheDTO c : lista) {
            System.out.println("Coche -> " + c.getBastidor() + " | " + c.getMatricula());
        }

        listaCoches.setAll(lista);
        tblCoche.setItems(listaCoches);
    }

    private void volver() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/vista/Paneladmin.fxml"));
            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            mostrarError("No se pudo volver al panel principal.");
        }
    }

    private void abrirFormulario(CocheDTO cocheSeleccionado) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/Añadircoche.fxml"));
            Parent root = loader.load();

            if (cocheSeleccionado != null) {
                try {
                    Object controller = loader.getController();
                    controller.getClass()
                            .getMethod("setCoche", CocheDTO.class)
                            .invoke(controller, cocheSeleccionado);
                } catch (NoSuchMethodException ignored) {
                    System.err.println("El formulario no tiene método setCoche(CocheDTO)");
                }
            }

            Stage ventana = new Stage();
            ventana.setScene(new Scene(root));
            ventana.initModality(Modality.APPLICATION_MODAL);
            ventana.setTitle(cocheSeleccionado == null ? "Añadir coche" : "Editar coche");
            ventana.showAndWait();

            cargarCoches();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("No se pudo abrir el formulario de coche.");
        }
    }

    private void borrarCoche() {
        CocheDTO coche = tblCoche.getSelectionModel().getSelectedItem();

        if (coche == null) {
            mostrarError("Debe seleccionar un coche para borrar.");
            return;
        }

        Alert confirmar = new Alert(Alert.AlertType.CONFIRMATION);
        confirmar.setTitle("Confirmar borrado");
        confirmar.setHeaderText("¿Eliminar el coche con bastidor " + coche.getBastidor() + "?");

        if (confirmar.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            cocheDAO.eliminarCoche(coche.getBastidor());
            cargarCoches();
        }
    }

    private void editarCoche() {
        CocheDTO coche = tblCoche.getSelectionModel().getSelectedItem();

        if (coche == null) {
            mostrarError("Debe seleccionar un coche para editar.");
            return;
        }

        abrirFormulario(coche);
    }

    private void mostrarError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(msg);
        alert.showAndWait();
    }
}
