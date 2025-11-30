package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

import conexion.ConexionBD;
import dto.AlquilerDTO;

public class AlquilerDAO {


    public int crearAlquiler(AlquilerDTO a) {
        String sql = "INSERT INTO alquiler (BASTIDOR, ID_CHOFER, NIF_NIE, FECHAINICIO, FECHAFIN, PRECIOTOTAL, ESTADO) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, a.getBastidor());

            if (a.getId_Chofer() > 0) {
                stmt.setInt(2, a.getId_Chofer());
            } else {
                stmt.setNull(2, java.sql.Types.INTEGER);
            }

            stmt.setString(3, a.getNif_nie());
            stmt.setDate(4, Date.valueOf(a.getFechaInicio()));
            stmt.setDate(5, Date.valueOf(a.getFechaFin()));
            stmt.setDouble(6, a.getPrecioTotal());
            stmt.setString(7, a.getEstado().name());

            int filas = stmt.executeUpdate();

            if (filas == 0) return -1;

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException ex) {
            System.err.println("Error creando alquiler: " + ex.getMessage());
        }

        return -1;
    }

    public void eliminarAlquiler(int idAlquiler) {
        String sql = "DELETE FROM alquiler WHERE IDALQUILER = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idAlquiler);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Error eliminando alquiler: " + ex.getMessage());
        }
    }

    public void modificarAlquiler(AlquilerDTO a) {
        String sql = "UPDATE alquiler SET BASTIDOR=?, ID_CHOFER=?, NIF_NIE=?, FECHAINICIO=?, FECHAFIN=?, PRECIOTOTAL=?, ESTADO=? WHERE IDALQUILER=?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, a.getBastidor());

            if (a.getId_Chofer() > 0) stmt.setInt(2, a.getId_Chofer());
            else stmt.setNull(2, java.sql.Types.INTEGER);

            stmt.setString(3, a.getNif_nie());
            stmt.setDate(4, Date.valueOf(a.getFechaInicio()));
            stmt.setDate(5, Date.valueOf(a.getFechaFin()));
            stmt.setDouble(6, a.getPrecioTotal());
            stmt.setString(7, a.getEstado().name());
            stmt.setInt(8, a.getIdAlquiler());

            stmt.executeUpdate();

        } catch (SQLException ex) {
            System.err.println("Error modificando alquiler: " + ex.getMessage());
        }
    }

    public AlquilerDTO buscarPorId(int idAlquiler) {
        String sql = "SELECT * FROM alquiler WHERE IDALQUILER = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idAlquiler);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    AlquilerDTO a = new AlquilerDTO(
                        rs.getInt("IDALQUILER"),
                        rs.getInt("BASTIDOR"),
                        rs.getString("NIF_NIE"),
                        rs.getDate("FECHAINICIO").toLocalDate(),
                        rs.getDate("FECHAFIN").toLocalDate(),
                        rs.getDouble("PRECIOTOTAL"),
                        AlquilerDTO.EstadoAlquiler.valueOf(rs.getString("ESTADO"))
                    );
                    a.setId_Chofer(rs.getInt("ID_CHOFER"));
                    if (rs.wasNull()) a.setId_Chofer(0);
                    return a;
                }
            }

        } catch (SQLException ex) {
            System.err.println("Error buscando alquiler: " + ex.getMessage());
        }
        return null;
    }

    public List<AlquilerDTO> listarAlquileres() {
        List<AlquilerDTO> lista = new ArrayList<>();
        String sql = "SELECT * FROM alquiler ORDER BY FECHAINICIO DESC";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                AlquilerDTO a = new AlquilerDTO(
                    rs.getInt("IDALQUILER"),
                    rs.getInt("BASTIDOR"),
                    rs.getString("NIF_NIE"),
                    rs.getDate("FECHAINICIO").toLocalDate(),
                    rs.getDate("FECHAFIN").toLocalDate(),
                    rs.getDouble("PRECIOTOTAL"),
                    AlquilerDTO.EstadoAlquiler.valueOf(rs.getString("ESTADO"))
                );
                a.setId_Chofer(rs.getInt("ID_CHOFER"));
                if (rs.wasNull()) a.setId_Chofer(0);
                lista.add(a);
            }

        } catch (SQLException ex) {
            System.err.println("Error listando alquileres: " + ex.getMessage());
        }
        return lista;
    }

       public Integer buscarChoferDisponible(LocalDate inicio, LocalDate fin) {
        String sql = "SELECT c.ID_CHOFER FROM chofer c WHERE c.ID_CHOFER NOT IN ("
                   + "  SELECT a.ID_CHOFER FROM alquiler a "
                   + "  WHERE a.ID_CHOFER IS NOT NULL "
                   + "    AND a.FECHAINICIO <= ? "   
                   + "    AND a.FECHAFIN >= ? "      
                   + ") LIMIT 1";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {


            stmt.setDate(1, Date.valueOf(fin));
            stmt.setDate(2, Date.valueOf(inicio));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("ID_CHOFER");
                }
            }

        } catch (SQLException ex) {
            System.err.println("Error buscando chofer disponible: " + ex.getMessage());
        }

        return null;
    }

    //METODO PARA LISTAR ALQUILERES POR NIF/NIE DE USUARIO
    //AÑADIDO POR FERNANDO
    
 // Agregar este método en tu clase AlquilerDAO
    public List<AlquilerDTO> listarAlquileresPorUsuario(String nifNie) {
    	
        String sql = "SELECT * FROM alquiler WHERE NIF_NIE = ? ORDER BY FECHAINICIO DESC";
        List<AlquilerDTO> lista = new ArrayList<>();

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nifNie);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                AlquilerDTO a = new AlquilerDTO(
                    rs.getInt("IDALQUILER"),
                    rs.getInt("BASTIDOR"),
                    rs.getString("NIF_NIE"),
                    rs.getDate("FECHAINICIO").toLocalDate(),
                    rs.getDate("FECHAFIN").toLocalDate(),
                    rs.getDouble("PRECIOTOTAL"),
                    AlquilerDTO.EstadoAlquiler.valueOf(rs.getString("ESTADO"))
                );
                a.setId_Chofer(rs.getInt("ID_CHOFER"));
                if (rs.wasNull()) a.setId_Chofer(0);
                lista.add(a);
            }

        } catch (SQLException ex) {
            System.err.println("Error listando alquileres por usuario: " + ex.getMessage());
        }
        return lista;
    }
}

