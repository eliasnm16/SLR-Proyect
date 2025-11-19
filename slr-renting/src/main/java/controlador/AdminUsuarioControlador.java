package controlador;

import dto.ClienteDTO;
import dao.ClienteDAO;
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

public class AdminUsuarioControlador implements Initializable {

    @FXML private TableView<ClienteDTO> tblUsuarios;
    @FXML private TableColumn<ClienteDTO, Number> clmID;
    @FXML private TableColumn<ClienteDTO, String> clmNombre;
    @FXML private TableColumn<ClienteDTO, String> clmTlf;
    @FXML private TableColumn<ClienteDTO, String> clmCorreo;
    @FXML private TableColumn<ClienteDTO, String> clmNif;

    @FXML private Button btnVolver;
    @FXML private Button btnAñadir;
    @FXML private Button btnBorrar;
    @FXML private Button btnEditar;

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private ObservableList<ClienteDTO> listaObservable = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configurar columnas con lambdas (evita problemas de reflection con nombres raros)
        clmID.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getIdCliente()));
        clmNombre.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getNombreCompleto()));
        // TELEFONO en DTO es long; mostramos como texto
        clmTlf.setCellValueFactory(cell -> new ReadOnlyStringWrapper(String.valueOf(cell.getValue().getTelefono())));
        clmCorreo.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getCorreo()));
        clmNif.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getNif_nie()));

        // Cargar inicialmente
        cargarClientes();

        // Eventos botones
        btnVolver.setOnAction(e -> handleVolver());
        btnAñadir.setOnAction(e -> handleAñadir());
        btnBorrar.setOnAction(e -> handleBorrar());
        btnEditar.setOnAction(e -> handleEditar());
    }

    private void cargarClientes() {
        List<ClienteDTO> lista = clienteDAO.listarClientes();
        listaObservable.setAll(lista);
        tblUsuarios.setItems(listaObservable);
    }

    private void handleVolver() {
        Stage stage = (Stage) btnVolver.getScene().getWindow();
        stage.close();
    }

    private void handleAñadir() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Loginregistro.fxml"));
            Parent root = loader.load();

            Stage dialog = new Stage();
            dialog.setTitle("Añadir cliente");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setScene(new Scene(root));
            dialog.showAndWait(); // esperamos a que se cierre para recargar
            cargarClientes();
        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarError("Error al abrir el formulario de registro.");
        }
    }

    private void handleEditar() {
        ClienteDTO seleccionado = tblUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Selecciona un usuario", "Debes seleccionar un usuario de la tabla para editarlo.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Loginregistro.fxml"));
            Parent root = loader.load();

            // intentar pasar el cliente al controlador del formulario (si existe setCliente)
            Object controller = loader.getController();
            if (controller != null) {
                try {
                    Method m = controller.getClass().getMethod("setCliente", ClienteDTO.class);
                    m.invoke(controller, seleccionado);
                } catch (NoSuchMethodException nsme) {
                    // el controlador no tiene setCliente(ClienteDTO) → no hacemos nada
                }
            }

            Stage dialog = new Stage();
            dialog.setTitle("Editar cliente");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setScene(new Scene(root));
            dialog.showAndWait();
            cargarClientes();
        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarError("Error al abrir el formulario de edición.");
        }
    }

    private void handleBorrar() {
        ClienteDTO seleccionado = tblUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Selecciona un usuario", "Debes seleccionar un usuario de la tabla para eliminarlo.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText("Vas a eliminar el usuario: " + seleccionado.getNombreCompleto());
        confirm.setContentText("¿Estás seguro?");
        ButtonType btnYes = new ButtonType("Sí", ButtonBar.ButtonData.YES);
        ButtonType btnNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirm.getButtonTypes().setAll(btnYes, btnNo);

        confirm.showAndWait().ifPresent(response -> {
            if (response == btnYes) {
                try {
                    clienteDAO.eliminarCliente(seleccionado.getIdCliente());
                    cargarClientes();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    mostrarError("Error al eliminar el cliente.");
                }
            }
        });
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}

