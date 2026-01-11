package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import conexion.ConexionBD;
import dto.ChoferDTO;

public class ChoferDAO {

    // inserta un chofer nuevo en la base de datos
    public void registrarChofer(ChoferDTO chofer) {

        // SQL que añade un chofer con sus datos básicos
        String sql = "INSERT INTO CHOFER (Nombre_Completo, Dni, Telefono, Disposicion) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, chofer.getNombre_completo());
            stmt.setString(2, chofer.getDni());
            stmt.setString(3, chofer.getTelefono());
            stmt.setBoolean(4, chofer.isDisposicion());

            stmt.executeUpdate();

            System.out.println("\nChofer registrado correctamente.");

        } catch (SQLException e) {
            System.err.println("\nError insertando chofer: " + e.getMessage());
        }
    }


    // modifica los datos de un chofer existente
    public void modificarChofer(ChoferDTO chofer, int id) {

        // SQL que actualiza los campos del chofer según su ID
        String sql = "UPDATE CHOFER SET Nombre_Completo = ?, Dni = ?, Telefono = ?, Disposicion = ? WHERE ID_Chofer = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, chofer.getNombre_completo());
            stmt.setString(2, chofer.getDni());
            stmt.setString(3, chofer.getTelefono());
            stmt.setBoolean(4, chofer.isDisposicion());
            stmt.setInt(5, id);

            int filas = stmt.executeUpdate();

            if (filas > 0) {
                System.out.println("\nChofer modificado correctamente.");
            } else {
                System.out.println("\nNo existe el chofer con ID: " + id);
            }

        } catch (SQLException e) {
            System.err.println("\nError modificando chofer: " + e.getMessage());
        }
    }


    // elimina un chofer usando su ID
    public void eliminarChofer(int id) {

        // SQL que borra un chofer concreto
        String sql = "DELETE FROM CHOFER WHERE ID_Chofer = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int filas = stmt.executeUpdate();

            if (filas > 0) {
                System.out.println("\nChofer eliminado correctamente.");
            } else {
                System.out.println("\nNo se encontró el chofer con ID: " + id);
            }

        } catch (SQLException e) {
            System.err.println("\nError eliminando chofer: " + e.getMessage());
        }
    }


    // devuelve una lista con todos los choferes registrados
    public List<ChoferDTO> listarChoferes() {

        List<ChoferDTO> choferes = new ArrayList<>();

        // SQL que obtiene todos los choferes
        String sql = "SELECT * FROM CHOFER";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ChoferDTO chofer = new ChoferDTO(
                        rs.getString("Nombre_Completo"),
                        rs.getString("Dni"),
                        rs.getString("Telefono"),
                        rs.getBoolean("Disposicion")
                );

                chofer.setId_chofer(rs.getInt("ID_Chofer"));
                choferes.add(chofer);
            }

        } catch (SQLException e) {
            System.err.println("\nError listando choferes: " + e.getMessage());
        }

        return choferes;
    }


    // retorna choferes que están disponibles para asignar a alquileres
    public List<ChoferDTO> listarChoferesDisponibles() {

        List<ChoferDTO> choferes = new ArrayList<>();

        // SQL que trae los choferes que tienen disposición activa
        String sql = "SELECT * FROM CHOFER WHERE Disposicion = TRUE";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ChoferDTO chofer = new ChoferDTO(
                        rs.getString("Nombre_Completo"),
                        rs.getString("Dni"),
                        rs.getString("Telefono"),
                        rs.getBoolean("Disposicion")
                );

                chofer.setId_chofer(rs.getInt("ID_Chofer"));
                choferes.add(chofer);
            }

        } catch (SQLException e) {
            System.err.println("\nError listando choferes disponibles: " + e.getMessage());
        }

        return choferes;
    }
}
