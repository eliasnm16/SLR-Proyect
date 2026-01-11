package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import conexion.ConexionBD;
import dto.CocheDTO;

public class CocheDAO {

    // inserta un coche nuevo en la base de datos
    public void registrarCoche(CocheDTO coche) {

        // sentencia SQL para registrar el coche
        String sql = "INSERT INTO COCHE (Bastidor, Marca, Modelo, PrecioDiario, Descripcion, Plazas, Potencia, Motor, VelocidadMax, Matricula, ImagenURL, Nuevo, Disponible) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // se pasan todos los datos del coche
            stmt.setInt(1, coche.getBastidor());
            stmt.setString(2, coche.getMarca());
            stmt.setString(3, coche.getModelo());
            stmt.setDouble(4, coche.getPrecioDiario());
            stmt.setString(5, coche.getDescripcion());
            stmt.setInt(6, coche.getPlazas());
            stmt.setInt(7, coche.getPotencia());
            stmt.setString(8, coche.getMotor());
            stmt.setInt(9, coche.getVelocidadMax());
            stmt.setString(10, coche.getMatricula());
            stmt.setString(11, coche.getImagenURL());
            stmt.setBoolean(12, coche.isNuevo());
            stmt.setBoolean(13, coche.isDisponible());

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error registrando coche: " + e.getMessage());
        }
    }


    // busca un coche en la base de datos según su bastidor
    public CocheDTO buscarCoche(int bastidor) {

        String sql = "SELECT * FROM COCHE WHERE Bastidor = ?";
        CocheDTO coche = null;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // se pasa el bastidor a buscar
            stmt.setInt(1, bastidor);
            ResultSet rs = stmt.executeQuery();

            // si existe, se convierte en DTO
            if (rs.next()) {
                coche = new CocheDTO(
                        rs.getInt("Bastidor"),
                        rs.getString("Marca"),
                        rs.getString("Modelo"),
                        rs.getDouble("PrecioDiario"),
                        rs.getString("Descripcion"),
                        rs.getInt("Plazas"),
                        rs.getInt("Potencia"),
                        rs.getString("Motor"),
                        rs.getInt("VelocidadMax"),
                        rs.getString("Matricula"),
                        rs.getString("ImagenURL"),
                        rs.getBoolean("Nuevo"),
                        rs.getBoolean("Disponible")
                );
            }

        } catch (SQLException e) {
            System.err.println("Error buscando coche: " + e.getMessage());
        }

        return coche;
    }


    // devuelve una lista con todos los coches
    public List<CocheDTO> listarCoches() {

        String sql = "SELECT * FROM COCHE";
        List<CocheDTO> lista = new ArrayList<>();

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // se recorren los coches y se crean DTOs
            while (rs.next()) {

                CocheDTO coche = new CocheDTO(
                        rs.getInt("Bastidor"),
                        rs.getString("Marca"),
                        rs.getString("Modelo"),
                        rs.getDouble("PrecioDiario"),
                        rs.getString("Descripcion"),
                        rs.getInt("Plazas"),
                        rs.getInt("Potencia"),
                        rs.getString("Motor"),
                        rs.getInt("VelocidadMax"),
                        rs.getString("Matricula"),
                        rs.getString("ImagenURL"),
                        rs.getBoolean("Nuevo"),
                        rs.getBoolean("Disponible")
                );

                lista.add(coche);
            }

        } catch (SQLException e) {
            System.err.println("Error listando coches: " + e.getMessage());
        }

        return lista;
    }


    // actualiza los datos de un coche
    public void modificarCoche(CocheDTO coche) {

        // SQL para actualizar todos los campos
        String sql = "UPDATE COCHE SET Marca=?, Modelo=?, PrecioDiario=?, Descripcion=?, Plazas=?, Potencia=?, Motor=?, VelocidadMax=?, Matricula=?, ImagenURL=?, Nuevo=?, Disponible=? "
                   + "WHERE Bastidor=?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, coche.getMarca());
            stmt.setString(2, coche.getModelo());
            stmt.setDouble(3, coche.getPrecioDiario());
            stmt.setString(4, coche.getDescripcion());
            stmt.setInt(5, coche.getPlazas());
            stmt.setInt(6, coche.getPotencia());
            stmt.setString(7, coche.getMotor());
            stmt.setInt(8, coche.getVelocidadMax());
            stmt.setString(9, coche.getMatricula());
            stmt.setString(10, coche.getImagenURL());
            stmt.setBoolean(11, coche.isNuevo());
            stmt.setBoolean(12, coche.isDisponible());
            stmt.setInt(13, coche.getBastidor());

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error actualizando coche: " + e.getMessage());
        }
    }


    // elimina un coche según su bastidor
    public void eliminarCoche(int bastidor) {

        String sql = "DELETE FROM COCHE WHERE Bastidor = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // se indica qué coche borrar
            stmt.setInt(1, bastidor);

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error eliminando coche: " + e.getMessage());
        }
    }


    // lista los coches que están disponibles para alquilar
    public List<CocheDTO> listarCochesDisponibles() {

    	String sql = "SELECT c.* FROM COCHE c " +
                "WHERE c.Disponible = TRUE " +
                "AND c.Bastidor NOT IN (" +
                "    SELECT a.Bastidor FROM ALQUILER a " +
                "    WHERE a.ESTADO IN ('CONFIRMADA', 'COMPLETADA')" +
                "    AND CURDATE() BETWEEN a.FECHAINICIO AND a.FECHAFIN" +
                ")";
    	
        List<CocheDTO> lista = new ArrayList<>();

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                CocheDTO coche = new CocheDTO(
                        rs.getInt("Bastidor"),
                        rs.getString("Marca"),
                        rs.getString("Modelo"),
                        rs.getDouble("PrecioDiario"),
                        rs.getString("Descripcion"),
                        rs.getInt("Plazas"),
                        rs.getInt("Potencia"),
                        rs.getString("Motor"),
                        rs.getInt("VelocidadMax"),
                        rs.getString("Matricula"),
                        rs.getString("ImagenURL"),
                        rs.getBoolean("Nuevo"),
                        rs.getBoolean("Disponible")
                );

                lista.add(coche);
            }

        } catch (SQLException e) {
            System.err.println("Error listando coches disponibles: " + e.getMessage());
        }

        return lista;
    }


    // lista los coches que son nuevos
    public List<CocheDTO> listarCochesNuevos() {

        String sql = "SELECT * FROM COCHE WHERE Nuevo = TRUE";
        List<CocheDTO> lista = new ArrayList<>();

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                CocheDTO coche = new CocheDTO(
                        rs.getInt("Bastidor"),
                        rs.getString("Marca"),
                        rs.getString("Modelo"),
                        rs.getDouble("PrecioDiario"),
                        rs.getString("Descripcion"),
                        rs.getInt("Plazas"),
                        rs.getInt("Potencia"),
                        rs.getString("Motor"),
                        rs.getInt("VelocidadMax"),
                        rs.getString("Matricula"),
                        rs.getString("ImagenURL"),
                        rs.getBoolean("Nuevo"),
                        rs.getBoolean("Disponible")
                );

                lista.add(coche);
            }

        } catch (SQLException e) {
            System.err.println("Error listando coches nuevos: " + e.getMessage());
        }

        return lista;
    }

 // lista los coches que son nuevos Y están disponibles
    public List<CocheDTO> listarCochesNuevosDisponibles() {
        
    	String sql = "SELECT c.* FROM COCHE c " +
                "WHERE c.Nuevo = TRUE AND c.Disponible = TRUE " +
                "AND c.Bastidor NOT IN (" +
                "    SELECT a.Bastidor FROM ALQUILER a " +
                "    WHERE a.ESTADO IN ('CONFIRMADA', 'COMPLETADA')" +
                "    AND CURDATE() BETWEEN a.FECHAINICIO AND a.FECHAFIN" +
                ")";
    	
        List<CocheDTO> lista = new ArrayList<>();
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                CocheDTO coche = new CocheDTO(
                        rs.getInt("Bastidor"),
                        rs.getString("Marca"),
                        rs.getString("Modelo"),
                        rs.getDouble("PrecioDiario"),
                        rs.getString("Descripcion"),
                        rs.getInt("Plazas"),
                        rs.getInt("Potencia"),
                        rs.getString("Motor"),
                        rs.getInt("VelocidadMax"),
                        rs.getString("Matricula"),
                        rs.getString("ImagenURL"),
                        rs.getBoolean("Nuevo"),
                        rs.getBoolean("Disponible")
                );
                lista.add(coche);
            }
            
        } catch (SQLException e) {
            System.err.println("Error listando coches nuevos disponibles: " + e.getMessage());
        }
        
        return lista;
    }
    
 // En CocheDAO.java, añade este método:
    public boolean tieneReservasActivas(int bastidor) {
    	String sql = "SELECT COUNT(*) FROM ALQUILER WHERE BASTIDOR = ? AND ESTADO IN ('CONFIRMADA', 'COMPLETADA')";        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bastidor);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error verificando reservas activas: " + e.getMessage());
        }
        
        return false;
    }
}
