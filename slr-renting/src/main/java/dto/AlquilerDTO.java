package dto;

import java.time.LocalDate;

public class AlquilerDTO {

    private int idAlquiler;
    private int bastidor;
    private int idChofer;
    private String nif_nie;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private double precioTotal;
    private EstadoAlquiler estado = EstadoAlquiler.PENDIENTE;

    public enum EstadoAlquiler {
        PENDIENTE,
        CONFIRMADA,
        COMPLETADA,
        CANCELADA
    }

	public AlquilerDTO(int idAlquiler, int bastidor, String nif_nie, LocalDate fechaInicio, LocalDate fechaFin,
			double precioTotal, EstadoAlquiler estado) {
		super();
		this.idAlquiler = idAlquiler;
		this.bastidor = bastidor;
		this.nif_nie = nif_nie;
		this.fechaInicio = fechaInicio;
		this.fechaFin = fechaFin;
		this.precioTotal = precioTotal;
		this.estado = estado;
	}

	public int getIdAlquiler() {
		return idAlquiler;
	}

	public void setIdAlquiler(int idAlquiler) {
		this.idAlquiler = idAlquiler;
	}

	public int getBastidor() {
		return bastidor;
	}

	public void setBastidor(int bastidor) {
		this.bastidor = bastidor;
	}

	public int getIdChofer() {
		return idChofer;
	}

	public void setIdChofer(int idChofer) {
		this.idChofer = idChofer;
	}

	public String getNif_nie() {
		return nif_nie;
	}

	public void setNif_nie(String nif_nie) {
		this.nif_nie = nif_nie;
	}

	public LocalDate getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(LocalDate fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public LocalDate getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(LocalDate fechaFin) {
		this.fechaFin = fechaFin;
	}

	public double getPrecioTotal() {
		return precioTotal;
	}

	public void setPrecioTotal(double precioTotal) {
		this.precioTotal = precioTotal;
	}

	public EstadoAlquiler getEstado() {
		return estado;
	}

	public void setEstado(EstadoAlquiler estado) {
		this.estado = estado;
	}
	
	
    
    
    
}
