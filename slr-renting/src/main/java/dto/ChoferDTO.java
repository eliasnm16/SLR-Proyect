package dto;

public class ChoferDTO {
    
    private int id_chofer;
    private String nombre_completo;
    private String dni;
    private String telefono; 
    private boolean disposicion = true;
    
    public ChoferDTO(String nombre_completo, String dni, String telefono, boolean disposicion) {  
        super();
        this.nombre_completo = nombre_completo;
        this.dni = dni;
        this.telefono = telefono;  // CAMBIADO
        this.disposicion = disposicion;
    }

    // Getters y Setters (actualizar el de teléfono)
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

    public String getTelefono() {  // CAMBIADO
        return telefono;
    }

    public void setTelefono(String telefono) {  // CAMBIADO
        this.telefono = telefono;
    }

    public boolean isDisposicion() {
        return disposicion;
    }

    public void setDisposicion(boolean disposicion) {
        this.disposicion = disposicion;
    }
}