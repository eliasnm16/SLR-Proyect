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
    
    private ObservableList<AlquilerDTO> reservasList = FXCollections.observableArrayList(); // Lista observable para la tabla
    private AlquilerDAO alquilerDAO = new AlquilerDAO(); // DAO para acceder a los métodos de la base de datos
    private String nifUsuarioActual; 

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configurar las columnas de la tabla, vinculando cada columna con la propiedad del DTO correspondiente
        colBastidor.setCellValueFactory(new PropertyValueFactory<>("bastidor"));
        colNif.setCellValueFactory(new PropertyValueFactory<>("nif_nie"));
        colFechaInicio.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        colFechaFin.setCellValueFactory(new PropertyValueFactory<>("fechaFin"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioTotal"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        // Configurar acciones de los botones
        btnCancelar.setOnAction(event -> cancelarReserva()); // Llama al método para cancelar reserva
        btnVolver.setOnAction(event -> volver());           // Llama al método para cerrar ventana
    }

    // Establece el NIF del usuario actual y carga sus reservas
    public void setNifUsuarioActual(String nif) {
        this.nifUsuarioActual = nif;
        cargarReservas(); // Carga las reservas del usuario en la tabla
    }

    // Carga las reservas del usuario actual en la tabla
    private void cargarReservas() {
        if (nifUsuarioActual != null) { // Solo si se ha establecido el NIF
            reservasList.clear(); // Limpiar la lista antes de cargar los datos nuevos
            reservasList.addAll(alquilerDAO.listarAlquileresPorUsuario(nifUsuarioActual)); // Obtener reservas del DAO
            tblReservas.setItems(reservasList); // Asignar la lista a la tabla
        }
    }

    // Cancela la reserva seleccionada en la tabla
    private void cancelarReserva() {
        // Obtener la reserva seleccionada en la tabla
        AlquilerDTO reservaSeleccionada = tblReservas.getSelectionModel().getSelectedItem();
        if (reservaSeleccionada == null) { // Si no hay reserva seleccionada
            mostrarAlerta("Error", "Por favor, selecciona una reserva para cancelar.");
            return;
        }

        // Comprobar si la reserva ya está cancelada
        if (reservaSeleccionada.getEstado() == AlquilerDTO.EstadoAlquiler.CANCELADA) {
            mostrarAlerta("Error", "Esta reserva ya está cancelada.");
            return;
        }

        // Mostrar diálogo de confirmación antes de cancelar
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar cancelación");
        alert.setHeaderText("¿Seguro que quieres cancelar esta reserva?");
        alert.setContentText("Esta acción no se puede deshacer.");

        // Si el usuario confirma
        if (alert.showAndWait().get() == ButtonType.OK) {
            // Cambiar el estado de la reserva a CANCELADA
            reservaSeleccionada.setEstado(AlquilerDTO.EstadoAlquiler.CANCELADA);
            alquilerDAO.modificarAlquiler(reservaSeleccionada); // Actualizar en base de datos
            cargarReservas(); // Recargar tabla para reflejar el cambio
            mostrarAlerta("Éxito", "Reserva cancelada correctamente."); // Informar al usuario
        }
    }

    // Cierra la ventana actual
    private void volver() {
        Stage stage = (Stage) btnVolver.getScene().getWindow(); // Obtener la ventana actual
        stage.close(); 
    }

    
     //Método genérico para mostrar alertas
     
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION); // Tipo de alerta: información
        alert.setTitle(titulo);
        alert.setHeaderText(null); 
        alert.setContentText(mensaje);
        alert.showAndWait(); 
    }
}
