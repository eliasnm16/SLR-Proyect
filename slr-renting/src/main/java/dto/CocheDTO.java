package dto;

public class CocheDTO {
	
	private int bastidor;
	private String marca;
	private String modelo;
	private double precioDiario;
	private String descripcion;
	private int plazas;
	private int potencia;
	private String motor;
	private int velocidadMax;
	private String matricula;
	private String imagenURL;
	private boolean nuevo = false;
	private boolean disponible = true;
	
	public CocheDTO(int bastidor, String marca, String modelo, double precioDiario, String descripcion, int plazas,
			int potencia, String motor, int velocidadMax, String matricula, String imagenURL, boolean nuevo,
			boolean disponible) {
		super();
		this.bastidor = bastidor;
		this.marca = marca;
		this.modelo = modelo;
		this.precioDiario = precioDiario;
		this.descripcion = descripcion;
		this.plazas = plazas;
		this.potencia = potencia;
		this.motor = motor;
		this.velocidadMax = velocidadMax;
		this.matricula = matricula;
		this.imagenURL = imagenURL;
		this.nuevo = nuevo;
		this.disponible = disponible;
	}

	public int getBastidor() {
		return bastidor;
	}

	public void setBastidor(int bastidor) {
		this.bastidor = bastidor;
	}

	public String getMarca() {
		return marca;
	}

	public void setMarca(String marca) {
		this.marca = marca;
	}

	public String getModelo() {
		return modelo;
	}

	public void setModelo(String modelo) {
		this.modelo = modelo;
	}

	public double getPrecioDiario() {
		return precioDiario;
	}

	public void setPrecioDiario(double precioDiario) {
		this.precioDiario = precioDiario;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public int getPlazas() {
		return plazas;
	}

	public void setPlazas(int plazas) {
		this.plazas = plazas;
	}

	public int getPotencia() {
		return potencia;
	}

	public void setPotencia(int potencia) {
		this.potencia = potencia;
	}

	public String getMotor() {
		return motor;
	}

	public void setMotor(String motor) {
		this.motor = motor;
	}

	public int getVelocidadMax() {
		return velocidadMax;
	}

	public void setVelocidadMax(int velocidadMax) {
		this.velocidadMax = velocidadMax;
	}

	public String getMatricula() {
		return matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}

	public String getImagenURL() {
		return imagenURL;
	}

	public void setImagenURL(String imagenURL) {
		this.imagenURL = imagenURL;
	}

	public boolean isNuevo() {
		return nuevo;
	}

	public void setNuevo(boolean nuevo) {
		this.nuevo = nuevo;
	}

	public boolean isDisponible() {
		return disponible;
	}

	public void setDisponible(boolean disponible) {
		this.disponible = disponible;
	}
}
