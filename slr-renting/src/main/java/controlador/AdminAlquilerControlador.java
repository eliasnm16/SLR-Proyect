package controlador;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.ChoiceDialog;
import dao.AlquilerDAO;
import dto.AlquilerDTO;
import javafx.stage.Stage;

public class AdminAlquilerControlador implements Initializable {

    @FXML
    private TableView<AlquilerDTO> tablaAlquileres;
    @FXML
    private TableColumn<AlquilerDTO, Integer> colId;
    @FXML
    private TableColumn<AlquilerDTO, Integer> colBastidor;
    @FXML
    private TableColumn<AlquilerDTO, String> colNif;
    @FXML
    private TableColumn<AlquilerDTO, String> colFechaInicio;
    @FXML
    private TableColumn<AlquilerDTO, String> colFechaFin;
    @FXML
    private TableColumn<AlquilerDTO, Double> colPrecio;
    @FXML
    private TableColumn<AlquilerDTO, String> colEstado;
    @FXML
    private Button btnBorrar;
    @FXML
    private Button btnCambiarEstado;
    @FXML
    private Button btnVolver;

    private ObservableList<AlquilerDTO> alquileresList = FXCollections.observableArrayList();
    private AlquilerDAO alquilerDAO = new AlquilerDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configurar las columnas
        colId.setCellValueFactory(new PropertyValueFactory<>("idAlquiler"));
        colBastidor.setCellValueFactory(new PropertyValueFactory<>("bastidor"));
        colNif.setCellValueFactory(new PropertyValueFactory<>("nif_nie"));
        colFechaInicio.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        colFechaFin.setCellValueFactory(new PropertyValueFactory<>("fechaFin"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioTotal"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        // Cargar los datos
        cargarAlquileres();

        // Configurar botones
        btnBorrar.setOnAction(event -> borrarAlquiler());
        btnCambiarEstado.setOnAction(event -> cambiarEstadoAlquiler());
        btnVolver.setOnAction(event -> volver());
    }

    private void cargarAlquileres() {
        alquileresList.clear();
        alquileresList.addAll(alquilerDAO.listarAlquileres());
        tablaAlquileres.setItems(alquileresList);
    }

    private void borrarAlquiler() {
        AlquilerDTO alquilerSeleccionado = tablaAlquileres.getSelectionModel().getSelectedItem();
        if (alquilerSeleccionado == null) {
            mostrarAlerta("Error", "Por favor, selecciona un alquiler para borrar.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar borrado");
        alert.setHeaderText("¿Estás seguro de que quieres borrar este alquiler?");
        alert.setContentText("Esta acción no se puede deshacer.");

        if (alert.showAndWait().get() == ButtonType.OK) {
            alquilerDAO.eliminarAlquiler(alquilerSeleccionado.getIdAlquiler());
            cargarAlquileres();
            mostrarAlerta("Éxito", "Alquiler borrado correctamente.");
        }
    }

    private void cambiarEstadoAlquiler() {
        AlquilerDTO alquilerSeleccionado = tablaAlquileres.getSelectionModel().getSelectedItem();
        if (alquilerSeleccionado == null) {
            mostrarAlerta("Error", "Por favor, selecciona un alquiler para cambiar su estado.");
            return;
        }

        // Diálogo para seleccionar nuevo estado
        ChoiceDialog<String> dialog = new ChoiceDialog<>(
            alquilerSeleccionado.getEstado().name(), 
            "PENDIENTE", "CONFIRMADA", "COMPLETADA", "CANCELADA"
        );
        dialog.setTitle("Cambiar estado");
        dialog.setHeaderText("Selecciona el nuevo estado para el alquiler");
        dialog.setContentText("Estado:");

        String resultado = dialog.showAndWait().orElse(null);
        if (resultado != null) {
            AlquilerDTO.EstadoAlquiler nuevoEstado = AlquilerDTO.EstadoAlquiler.valueOf(resultado);

            // No se puede marcar como completado antes de la fecha de fin
            if (nuevoEstado == AlquilerDTO.EstadoAlquiler.COMPLETADA) {
                if (alquilerSeleccionado.getFechaFin().isAfter(java.time.LocalDate.now())) {
                    mostrarAlerta(
                        "Acción no permitida",
                        "No se puede marcar el alquiler como COMPLETADO antes de su fecha de finalización."
                    );
                    return;
                }
            }

            alquilerSeleccionado.setEstado(nuevoEstado);
            alquilerDAO.modificarAlquiler(alquilerSeleccionado);
            cargarAlquileres();

            mostrarAlerta("Éxito", "Estado cambiado a: " + resultado);
        }
    }

    private void volver() {
        try {
            // Cargar el panel de administración
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/PanelAdmin.fxml"));
            Parent root = loader.load();
            
            // Obtener la ventana actual
            Stage currentStage = (Stage) btnVolver.getScene().getWindow();
            
            // Reemplazar la escena actual con el panel de administración
            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Panel Administrador");
            currentStage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("No se pudo volver al panel principal.");
        }
    }

    private void mostrarError(String mensaje) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}