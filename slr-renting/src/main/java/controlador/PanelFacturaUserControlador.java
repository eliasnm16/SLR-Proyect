package controlador;

import java.net.URL;
import util.FacturaPDFGenerator;
import java.time.temporal.ChronoUnit;
import java.time.LocalDate;
import java.util.ResourceBundle;

import dao.AlquilerDAO;
import dao.CocheDAO;
import dto.AlquilerDTO;
import dto.CocheDTO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;


public class PanelFacturaUserControlador implements Initializable {

    @FXML private Label lblFechaFactura;

    @FXML private Label lblMarcaModelo;
    @FXML private Label lblBastidor;
    @FXML private Label lblMatricula;

    @FXML private Label lblPrecioDia;
    @FXML private Label lblDias;
    @FXML private Label lblSubtotal;
    @FXML private Label lblDescuento;
    @FXML private Label lblChofer;
    @FXML private Label lblTotal;

    @FXML private Button btnConfirmar; 
    @FXML private Button btnCerrar;
    @FXML private Label lblMensaje; 

    private CocheDTO coche;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private boolean choferSolicitado;
    private String nifUsuario;

    private static final double DESCUENTO_7 = 0.10;
    private static final double DESCUENTO_30 = 0.20;
    private static final double COSTE_CHOFER_POR_DIA = 40.0; 

    private AlquilerDAO alquilerDAO = new AlquilerDAO();
    private CocheDAO cocheDAO = new CocheDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Botones
        btnConfirmar.setOnAction(e -> confirmarReserva());
        btnCerrar.setOnAction(e -> cerrarVentana());

        
        if (lblMensaje != null) {
            lblMensaje.setVisible(false);
        }
    }

 
    public void setDatos(CocheDTO coche, LocalDate inicio, LocalDate fin, boolean choferSolicitado, String nifUsuario) {
        this.coche = coche;
        this.fechaInicio = inicio;
        this.fechaFin = fin;
        this.choferSolicitado = choferSolicitado;
        this.nifUsuario = nifUsuario;

        rellenarVista();
    }

    private void rellenarVista() {
        if (coche == null || fechaInicio == null || fechaFin == null) return;

        lblFechaFactura.setText("Fecha: " + LocalDate.now().toString());
        lblMarcaModelo.setText((coche.getMarca() != null ? coche.getMarca() + " " : "") + coche.getModelo());
        lblBastidor.setText(String.valueOf(coche.getBastidor()));
        lblMatricula.setText(coche.getMatricula() != null ? coche.getMatricula() : "---");

        long dias = ChronoUnit.DAYS.between(fechaInicio, fechaFin) + 1;
        double precioDia = coche.getPrecioDiario();
        double subtotal = dias * precioDia;

        double descuentoPct = 0.0;
        if (dias >= 30) descuentoPct = DESCUENTO_30;
        else if (dias >= 7) descuentoPct = DESCUENTO_7;

        double descuentoEuros = subtotal * descuentoPct;

        double costeChofer = 0.0;
        if (choferSolicitado) costeChofer = COSTE_CHOFER_POR_DIA * dias;

        double total = subtotal - descuentoEuros + costeChofer;

        // Rellenar labels
        lblPrecioDia.setText(((int) precioDia) + "€");
        lblDias.setText(String.valueOf(dias));
        lblSubtotal.setText(String.format("%.2f€", subtotal));
        lblDescuento.setText(String.format("%.0f%% (%.2f€)", descuentoPct * 100, descuentoEuros));
        lblChofer.setText(choferSolicitado ? ("Sí (+ " + (int)COSTE_CHOFER_POR_DIA + "€/día)") : "No");
        lblTotal.setText(String.format("%.2f€", total));
    }

    private void confirmarReserva() {
        if (coche == null || fechaInicio == null || fechaFin == null) {
            Alert a = new Alert(AlertType.WARNING, "Faltan datos para crear la reserva.");
            a.showAndWait();
            return;
        }

        // Recalcular valores para crear DTO
        long dias = ChronoUnit.DAYS.between(fechaInicio, fechaFin) + 1;
        double precioDia = coche.getPrecioDiario();
        double subtotal = dias * precioDia;
        double descuentoPct = dias >= 30 ? DESCUENTO_30 : (dias >= 7 ? DESCUENTO_7 : 0.0);
        double descuentoEuros = subtotal * descuentoPct;
        double costeChofer = choferSolicitado ? COSTE_CHOFER_POR_DIA * dias : 0.0;
        double total = subtotal - descuentoEuros + costeChofer;

        // Intentar asignar chofer automáticamente si se solicitó
        Integer idChoferAsignado = null;
        if (choferSolicitado) {
            idChoferAsignado = alquilerDAO.buscarChoferDisponible(fechaInicio, fechaFin);
 
        }

        // Construir DTO de alquiler
        AlquilerDTO a = new AlquilerDTO(
                0,
                coche.getBastidor(),
                nifUsuario,
                fechaInicio,
                fechaFin,
                total,
                AlquilerDTO.EstadoAlquiler.PENDIENTE
        );

        if (idChoferAsignado != null) a.setId_Chofer(idChoferAsignado);

 
        int idGenerado = alquilerDAO.crearAlquiler(a);

        if (idGenerado > 0) {
        	//Pasar datos para crear PDF
        	try {
        	    FacturaPDFGenerator.generarFactura(
        	        idGenerado,
        	        a,
        	        coche,
        	        choferSolicitado,
        	        coche.getPrecioDiario(),
        	        dias,
        	        subtotal,
        	        descuentoEuros,
        	        total
        	    );
        	} catch (Exception ex) {
        	    System.err.println("Error generando PDF: " + ex.getMessage());
        	}
            // Marcar coche como no disponible
            try {
                CocheDTO cEnBd = cocheDAO.buscarCoche(coche.getBastidor());
                if (cEnBd != null) {
                    cEnBd.setDisponible(false);
                    cocheDAO.modificarCoche(cEnBd);
                }
            } catch (Exception ex) {
                System.err.println("Error actualizando disponibilidad del coche: " + ex.getMessage());
            }

            // Mostrar mensaje de confirmación en lblMensaje
            if (lblMensaje != null) {
                lblMensaje.setText("Nos pondremos en contacto con usted por correo con la resolución de su renting. Muchas gracias.");
                lblMensaje.setVisible(true);
            } else {
 
                Alert ok = new Alert(AlertType.INFORMATION);
                ok.setTitle("Reserva creada");
                ok.setHeaderText(null);
                ok.setContentText("Nos pondremos en contacto con usted por correo con la resolución de su renting. Muchas gracias.");
                ok.showAndWait();
            }

 
            btnConfirmar.setDisable(true);

        } else {
            Alert err = new Alert(AlertType.ERROR);
            err.setTitle("Error");
            err.setHeaderText("No se pudo crear la reserva");
            err.setContentText("Ha ocurrido un error al guardar la reserva en la base de datos.");
            err.showAndWait();
        }
    }

    private void cerrarVentana() {
 
        if (btnCerrar != null && btnCerrar.getScene() != null) {
            btnCerrar.getScene().getWindow().hide();
        }
    }
}

