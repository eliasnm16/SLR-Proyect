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

        // sentencia SQL para insertar los datos
        String sql = "INSERT INTO alquiler (BASTIDOR, ID_CHOFER, NIF_NIE, FECHAINICIO, FECHAFIN, PRECIOTOTAL, ESTADO) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        // abre la conexión y se prepara el INSERT
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            // pasa el bastidor que es obligatorio
            stmt.setInt(1, a.getBastidor());

            // en caso de q haya chofer, se añade. Si no, se pone NULL
            if (a.getId_Chofer() > 0) {
                stmt.setInt(2, a.getId_Chofer());
            } else {
                stmt.setNull(2, java.sql.Types.INTEGER);
            }

            // se añaden los demás datos
            stmt.setString(3, a.getNif_nie());
            stmt.setDate(4, Date.valueOf(a.getFechaInicio()));
            stmt.setDate(5, Date.valueOf(a.getFechaFin()));
            stmt.setDouble(6, a.getPrecioTotal());
            stmt.setString(7, a.getEstado().name());

            // ejecuta el INSERT
            int filas = stmt.executeUpdate();

            // si no insertó nada, devolvemos -1 porque no se ha insertado nada
            if (filas == 0) return -1;

            // se obtiene el ID generado por la base de datos
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



    // metodo eliminar un alquiler por su ID
    public void eliminarAlquiler(int idAlquiler) {
        String sql = "DELETE FROM alquiler WHERE IDALQUILER = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Se indica qué alquiler borrar
            stmt.setInt(1, idAlquiler);

            stmt.executeUpdate();

        } catch (SQLException ex) {
            System.err.println("Error eliminando alquiler: " + ex.getMessage());
        }
    }



    // metodo que actualiza un alquiler existente
    public void modificarAlquiler(AlquilerDTO a) {

        // SQL para actualizar todos los campos
        String sql = "UPDATE alquiler SET BASTIDOR=?, ID_CHOFER=?, NIF_NIE=?, FECHAINICIO=?, FECHAFIN=?, PRECIOTOTAL=?, ESTADO=? WHERE IDALQUILER=?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, a.getBastidor());

            // mismo control de chofer que antes
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
            stmt.setInt(8, a.getIdAlquiler());

            stmt.executeUpdate();

        } catch (SQLException ex) {
            System.err.println("Error modificando alquiler: " + ex.getMessage());
        }
    }
    
    // Devuelve una lista de todos los alquileres
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
                
                //si no hay chofer, se pone a 0
                if (rs.wasNull()) {
                	a.setId_Chofer(0);
                }

                lista.add(a);
            }

        } catch (SQLException ex) {
            System.err.println("Error listando alquileres: " + ex.getMessage());
        }

        return lista;
    }

   

    // Busca un chofer libre que no esté ocupado entre dos fechas
    public Integer buscarChoferDisponible(LocalDate inicio, LocalDate fin) {

        // SQL que busca choferes que NO estén en ningún alquiler que coincida con ese periodo
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

            ResultSet rs = stmt.executeQuery();

            // si hay algún chofer libre, se devuelve su ID
            if (rs.next()) return rs.getInt("ID_CHOFER");

        } catch (SQLException ex) {
            System.err.println("Error buscando chofer disponible: " + ex.getMessage());
        }

        return null;
    }



    // lista todos los alquileres realizados por un usuario según su NIF/NIE
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
