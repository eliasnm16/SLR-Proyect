package dao;

import dto.ClienteDTO;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import conexion.ConexionBD;

public class ClienteDAO {

    // inserta un cliente nuevo en la base de datos
    public void registrarCliente(ClienteDTO cliente) {

        // SQL que añade los datos básicos del cliente
        String sql = "INSERT INTO CLIENTE (Nombre_Completo, Nif_nie, Correo, Contrasena, Carnet, Telefono) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cliente.getNombreCompleto());
            stmt.setString(2, cliente.getNif_nie());
            stmt.setString(3, cliente.getCorreo());
            stmt.setString(4, cliente.getContrasena());
            stmt.setBoolean(5, cliente.isCarnet());
            stmt.setString(6, cliente.getTelefono());

            stmt.executeUpdate();

            System.out.println("\nCliente registrado correctamente.");

        } catch (SQLException e) {
            System.err.println("\nError insertando cliente: " + e.getMessage());
        }
    }


    // actualiza los datos del cliente con su ID
    public void modificarCliente(ClienteDTO cliente, int id) {

        // SQL que actualiza todos los campos del cliente
        String sql = "UPDATE CLIENTE SET Nombre_Completo = ?, Nif_nie = ?, Correo = ?, Contrasena = ?, Carnet = ?, Telefono = ? WHERE ID_Cliente = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cliente.getNombreCompleto());
            stmt.setString(2, cliente.getNif_nie());
            stmt.setString(3, cliente.getCorreo());
            stmt.setString(4, cliente.getContrasena());
            stmt.setBoolean(5, cliente.isCarnet());
            stmt.setString(6, cliente.getTelefono());
            stmt.setInt(7, id);

            int filas = stmt.executeUpdate();

            if (filas > 0) {
                System.out.println("\nCliente modificado correctamente.");
            } else {
                System.out.println("\nEl cliente con ese ID no existe.");
            }

        } catch (SQLException e) {
            System.err.println("\nError modificando cliente: " + e.getMessage());
        }
    }


    // elimina un cliente usando su ID
    public void eliminarCliente(int id) {

        // SQL que borra un cliente existente
        String sql = "DELETE FROM CLIENTE WHERE ID_Cliente = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int filas = stmt.executeUpdate();

            if (filas > 0) {
                System.out.println("\nCliente eliminado correctamente.");
            } else {
                System.out.println("\nNo existe el cliente con ID: " + id);
            }

        } catch (SQLException e) {
            System.err.println("\nError eliminando cliente: " + e.getMessage());
        }
    }


    // devuelve una lista con todos los clientes
    public List<ClienteDTO> listarClientes() {

        List<ClienteDTO> clientes = new ArrayList<>();

        // SQL para obtener todos los clientes
        String sql = "SELECT * FROM CLIENTE";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                ClienteDTO cliente = new ClienteDTO();

                cliente.setIdCliente(rs.getInt("ID_Cliente"));
                cliente.setNombreCompleto(rs.getString("Nombre_Completo"));
                cliente.setNif_nie(rs.getString("Nif_nie"));
                cliente.setCorreo(rs.getString("Correo"));
                cliente.setContrasena(rs.getString("Contrasena"));
                cliente.setCarnet(rs.getBoolean("Carnet"));
                cliente.setTelefono(rs.getString("Telefono"));

                clientes.add(cliente);
            }

        } catch (SQLException e) {
            System.err.println("\nError listando clientes: " + e.getMessage());
        }

        return clientes;
    }

    
    // permite iniciar sesión si correo y contraseña coinciden
    public void iniciarSesion(String correo, String contrasena) {

        // SQL que comprueba si existe un usuario con esas credenciales
        String sql = "SELECT * FROM CLIENTE WHERE Correo = ? AND Contrasena = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, correo);
            stmt.setString(2, contrasena);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.println("\nInicio de sesión correcto para: " + rs.getString("Nombre_Completo"));
            } else {
                System.out.println("\nCredenciales incorrectas.");
            }

        } catch (SQLException e) {
            System.err.println("\nError iniciando sesión: " + e.getMessage());
        }
    }

 // En ClienteDAO.java - añade este método
    public boolean existeClienteCon(String nif, String correo, String telefono) {
        String sql = "SELECT COUNT(*) as count FROM cliente WHERE Nif_nie = ? OR Correo = ? OR Telefono = ?";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nif);
            stmt.setString(2, correo);
            stmt.setString(3, telefono);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error verificando duplicados: " + e.getMessage());
        }
        return false;
    }

    // También añade este método para verificar duplicados específicos
    public String obtenerCampoDuplicado(String nif, String correo, String telefono) {
        String sql = "SELECT Nif_nie, Correo, Telefono FROM cliente WHERE Nif_nie = ? OR Correo = ? OR Telefono = ? LIMIT 1";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nif);
            stmt.setString(2, correo);
            stmt.setString(3, telefono);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                if (nif.equals(rs.getString("Nif_nie"))) {
                    return "NIF/NIE";
                }
                if (correo.equals(rs.getString("Correo"))) {
                    return "correo electrónico";
                }
                if (telefono.equals(rs.getString("Telefono"))) {
                    return "teléfono";
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error verificando campo duplicado: " + e.getMessage());
        }
        return null;
    }
    
}
