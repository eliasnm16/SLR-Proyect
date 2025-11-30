package controlador;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import dao.AlquilerDAO;
import dto.AlquilerDTO;

public class PanelConfigReservaUserControlador implements Initializable {

    @FXML
    private TableView<AlquilerDTO> tblReservas;
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
    private Button btnCancelar;
    @FXML
    private Button btnVolver;

    private ObservableList<AlquilerDTO> reservasList = FXCollections.observableArrayList();
    private AlquilerDAO alquilerDAO = new AlquilerDAO();
    private String nifUsuarioActual; // Se debe establecer desde fuera

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configurar las columnas
        colBastidor.setCellValueFactory(new PropertyValueFactory<>("bastidor"));
        colNif.setCellValueFactory(new PropertyValueFactory<>("nif_nie"));
        colFechaInicio.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        colFechaFin.setCellValueFactory(new PropertyValueFactory<>("fechaFin"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioTotal"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        // Cargar los datos de la tabla (se hará cuando se setee el nifUsuarioActual)
        // tblReservas.setItems(reservasList);

        // Configurar el botón de cancelar
        btnCancelar.setOnAction(event -> cancelarReserva());
        btnVolver.setOnAction(event -> volver());
    }

    public void setNifUsuarioActual(String nif) {
        this.nifUsuarioActual = nif;
        cargarReservas();
    }

    private void cargarReservas() {
        if (nifUsuarioActual != null) {
            reservasList.clear();
            reservasList.addAll(alquilerDAO.listarAlquileresPorUsuario(nifUsuarioActual));
            tblReservas.setItems(reservasList);
        }
    }

    private void cancelarReserva() {
        AlquilerDTO reservaSeleccionada = tblReservas.getSelectionModel().getSelectedItem();
        if (reservaSeleccionada == null) {
            mostrarAlerta("Error", "Por favor, selecciona una reserva para cancelar.");
            return;
        }

        // Comprobar si la reserva ya está cancelada
        if (reservaSeleccionada.getEstado() == AlquilerDTO.EstadoAlquiler.CANCELADA) {
            mostrarAlerta("Error", "Esta reserva ya está cancelada.");
            return;
        }

        // Confirmar cancelación
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar cancelación");
        alert.setHeaderText("¿Seguro que quieres cancelar esta reserva?");
        alert.setContentText("Esta acción no se puede deshacer.");

        if (alert.showAndWait().get() == ButtonType.OK) {
            // Actualizar el estado de la reserva a CANCELADA
            reservaSeleccionada.setEstado(AlquilerDTO.EstadoAlquiler.CANCELADA);
            alquilerDAO.modificarAlquiler(reservaSeleccionada);
            cargarReservas(); // Recargar la tabla para reflejar el cambio
            mostrarAlerta("Éxito", "Reserva cancelada correctamente.");
        }
    }

    private void volver() {
        Stage stage = (Stage) btnVolver.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}