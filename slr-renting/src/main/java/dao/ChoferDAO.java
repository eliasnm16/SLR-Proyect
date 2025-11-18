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

	//registrar chofer
	public void registrarChofer(ChoferDTO chofer) {
	 
		String sql = "INSERT INTO CHOFER (Nombre_Completo, Dni, Telefono, Disposicion) VALUES (?, ?, ?, ?)";
	   
		try (Connection conn = ConexionBD.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, chofer.getNombre_completo());
			stmt.setString(2, chofer.getDni());
			stmt.setLong(3, chofer.getTelefono()); 
			stmt.setBoolean(4, chofer.isDisposicion());
			stmt.executeUpdate(); 
			
			System.out.println("\nChofer registrado correctamente.");
			
		} catch (SQLException e) {
			System.err.println("\nError insertando chofer, " + e.getMessage());
		}
	}
	
	//modificar chofer
	
	public void modificarChofer(ChoferDTO chofer, int id) {
		
		String sql = "UPDATE CHOFER SET Nombre_Completo = ?, Dni = ?, Telefono = ?, Disposicion = ? WHERE ID_Chofer = ?";
		
		try (Connection conn = ConexionBD.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, chofer.getNombre_completo());
			stmt.setString(2, chofer.getDni());
			stmt.setLong(3, chofer.getTelefono());
			stmt.setBoolean(4, chofer.isDisposicion());
	        stmt.setInt(5, id);  // ← ¡FALTABA ESTA LÍNEA!

			int filasActualizadas = stmt.executeUpdate();
			
			if (filasActualizadas > 0) {
				System.out.println("\nChofer modificado correctamente.");
			} else {
				System.out.println("\nNo se encontró el chofer con ID: " + id);
			}
			
		} catch (SQLException e) {
			System.err.println("\nError modificando chofer, " + e.getMessage());
		}
	}
	
	//metodo eliminar chofer
	public void eliminarChofer(int id) {
		
		String sql = "DELETE FROM CHOFER WHERE ID_Chofer = ?";
		
		try (Connection conn = ConexionBD.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, id);
			int filasEliminadas = stmt.executeUpdate();
			
			if (filasEliminadas > 0) {
				System.out.println("\nChofer eliminado correctamente.");
			} else {
				System.out.println("\nNo se encontró el chofer con ID: " + id);
			}
			
		} catch (SQLException e) {
			System.err.println("\nError eliminando chofer, " + e.getMessage());
		}
	}
	
	//listar choferes
	public List<ChoferDTO> listarChoferes() {

	   		List<ChoferDTO> choferes = new ArrayList<>();
		
		String sql = "SELECT * FROM CHOFER";
		
		try (Connection conn = ConexionBD.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {
			
			while (rs.next()) {
				ChoferDTO chofer = new ChoferDTO(
					rs.getString("Nombre_Completo"),
					rs.getString("Dni"),
					rs.getLong("Telefono"),
					rs.getBoolean("Disposicion")
				);
				chofer.setId_chofer(rs.getInt("ID_Chofer"));
				choferes.add(chofer);
			}
			
		} catch (SQLException e) {
			System.err.println("\nError listando choferes, " + e.getMessage());
		}
		
		return choferes;
	}

	
	//listar chofer disponibles
	public List<ChoferDTO> listarChoferesDisponibles() {

	   		List<ChoferDTO> choferes = new ArrayList<>();
		
		String sql = "SELECT * FROM CHOFER WHERE Disposicion = TRUE";
		
		try (Connection conn = ConexionBD.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {
			
			while (rs.next()) {
				ChoferDTO chofer = new ChoferDTO(
					rs.getString("Nombre_Completo"),
					rs.getString("Dni"),
					rs.getLong("Telefono"),
					rs.getBoolean("Disposicion")
				);
				chofer.setId_chofer(rs.getInt("ID_Chofer"));
				choferes.add(chofer);
			}
			
		} catch (SQLException e) {
			System.err.println("\nError listando choferes disponibles, " + e.getMessage());
		}
		
		return choferes;
	}

}