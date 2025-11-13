package DTO;

public class ChoferDTO {
	
	private int id_chofer;
	private String nombre_completo;
	private String dni;
	private long telefono;
	private boolean disposicion = true;
	
	public ChoferDTO(int id_chofer, String nombre_completo, String dni, long telefono, boolean disposicion) {
		super();
		this.id_chofer = id_chofer;
		this.nombre_completo = nombre_completo;
		this.dni = dni;
		this.telefono = telefono;
		this.disposicion = disposicion;
	}

	public int getId_chofer() {
		return id_chofer;
	}

	public void setId_chofer(int id_chofer) {
		this.id_chofer = id_chofer;
	}

	public String getNombre_completo() {
		return nombre_completo;
	}

	public void setNombre_completo(String nombre_completo) {
		this.nombre_completo = nombre_completo;
	}

	public String getDni() {
		return dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	public long getTelefono() {
		return telefono;
	}

	public void setTelefono(long telefono) {
		this.telefono = telefono;
	}

	public boolean isDisposicion() {
		return disposicion;
	}

	public void setDisposicion(boolean disposicion) {
		this.disposicion = disposicion;
	}
	
	
	
	
	

}
