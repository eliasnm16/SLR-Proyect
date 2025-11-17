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

    public void registrarCoche(CocheDTO coche) {

        String sql = "INSERT INTO COCHE (Bastidor, Marca, Modelo, PrecioDiario, Descripcion, Plazas, Potencia, Motor, VelocidadMax, Matricula, ImagenURL, Nuevo, Disponible) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

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

            System.out.println("✔ Coche registrado correctamente.");

        } catch (SQLException e) {
            System.err.println("❌ Error registrando coche: " + e.getMessage());
        }
    }

    public CocheDTO buscarCoche(int bastidor) {

        String sql = "SELECT * FROM COCHE WHERE Bastidor = ?";
        CocheDTO coche = null;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bastidor);
            ResultSet rs = stmt.executeQuery();

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
            System.err.println("❌ Error buscando coche: " + e.getMessage());
        }

        return coche;
    }


    // ------------------------------------------------------------
    // 3. LISTAR TODOS LOS COCHES
    // ------------------------------------------------------------
    public List<CocheDTO> listarCoches() {

        String sql = "SELECT * FROM COCHE";
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
            System.err.println("❌ Error listando coches: " + e.getMessage());
        }

        return lista;
    }



    public void modificarCoche(CocheDTO coche) {

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

            int filas = stmt.executeUpdate();

            if (filas > 0) {
                System.out.println("✔ Coche actualizado correctamente.");
            } else {
                System.out.println("⚠ No existe un coche con bastidor: " + coche.getBastidor());
            }

        } catch (SQLException e) {
            System.err.println("❌ Error actualizando coche: " + e.getMessage());
        }
    }


    public void eliminarCoche(int bastidor) {

        String sql = "DELETE FROM COCHE WHERE Bastidor = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bastidor);

            int filas = stmt.executeUpdate();

            if (filas > 0) {
                System.out.println("✔ Coche eliminado correctamente.");
            } else {
                System.out.println("⚠ No existe un coche con bastidor: " + bastidor);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error eliminando coche: " + e.getMessage());
        }
    }

    public List<CocheDTO> listarCochesDisponibles() {

        String sql = "SELECT * FROM COCHE WHERE Disponible = TRUE";
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
            System.err.println("❌ Error listando coches disponibles: " + e.getMessage());
        }

        return lista;
    }


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
            System.err.println("❌ Error listando coches nuevos: " + e.getMessage());
        }

        return lista;
    }

}
