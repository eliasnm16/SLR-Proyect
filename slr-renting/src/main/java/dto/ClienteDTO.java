package dto;

import java.time.LocalDate;

public class ClienteDTO {
	
	private int idCliente;
	private String nombreCompleto;
	private String nif_nie;
	private String correo;
	private String contrasena;
	private boolean carnet = false;
	private String telefono;
	private LocalDate fechaRegistro;
	private boolean admin = false;
	
    public ClienteDTO() {}  // ← Añadir constructor vacío

	public ClienteDTO(String nombreCompleto, String nif_nie, String correo, String contrasena, boolean carnet,
			String telefono, boolean admin) {
		super();
		this.nombreCompleto = nombreCompleto;
		this.nif_nie = nif_nie;
		this.correo = correo;
		this.contrasena = contrasena;
		this.carnet = carnet;
		this.telefono = telefono;
		this.admin = true;
	}

	public String getNombreCompleto() {
		return nombreCompleto;
	}

	public void setNombreCompleto(String nombreCompleto) {
		this.nombreCompleto = nombreCompleto;
	}

	public String getNif_nie() {
		return nif_nie;
	}

	public void setNif_nie(String nif_nie) {
		this.nif_nie = nif_nie;
	}

	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public String getContrasena() {
		return contrasena;
	}

	public void setContrasena(String contrasena) {
		this.contrasena = contrasena;
	}

	public boolean isCarnet() {
		return carnet;
	}

	public void setCarnet(boolean carnet) {
		this.carnet = carnet;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public LocalDate getFechaRegistro() {
		return fechaRegistro;
	}

	public void setFechaRegistro(LocalDate fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

	public int getIdCliente() {
		return idCliente;
	}
	
	public void setIdCliente(int idCliente) {
		this.idCliente = idCliente;
	}
	
	public boolean getAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
	
	
	
	

}
