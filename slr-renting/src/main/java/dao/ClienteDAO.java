package dao;
import dto.ClienteDTO;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import conexion.ConexionBD;
public class ClienteDAO {

	public void registrarCliente(ClienteDTO cliente) {
		
		String sql = "INSERT INTO CLIENTE (Nombre_Completo, Nif_nie, Correo, Contrasena, Carnet, Telefono) VALUES (?, ?, ?, ?, ?, ?)";
	   
		try (Connection conn = ConexionBD.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, cliente.getNombreCompleto());
			stmt.setString(2, cliente.getNif_nie());
			stmt.setString(3, cliente.getCorreo());
			stmt.setString(4, cliente.getContrasena());
			stmt.setBoolean(5, cliente.isCarnet());
			stmt.setLong(6, cliente.getTelefono()); 
			stmt.executeUpdate(); 
			
			System.out.println("\nCliente registrado correctamente.");
			
		} catch (SQLException e) {
			System.err.println("\nError insertando cliente, " + e.getMessage());
		}
	}
	
	//modificar cliente
	public void modificarCliente(ClienteDTO cliente, int id) {
		
		String sql = "UPDATE CLIENTE SET Nombre_Completo = ?, Nif_nie = ?, Correo = ?, Contrasena = ?, Carnet = ?, Telefono = ? WHERE ID_Cliente = ?";
		
		try (Connection conn = ConexionBD.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, cliente.getNombreCompleto());
			stmt.setString(2, cliente.getNif_nie());
			stmt.setString(3, cliente.getCorreo());
			stmt.setString(4, cliente.getContrasena());
			stmt.setBoolean(5, cliente.isCarnet());
			stmt.setLong(6, cliente.getTelefono());
			stmt.setInt(7, id);
			int filasActualizadas = stmt.executeUpdate();
			
			if (filasActualizadas > 0) {
				System.out.println("\nCliente modificado correctamente.");
			} else {
				System.out.println("\nNo se encontró el cliente con ID: " + id);
			}
			
		} catch (SQLException e) {
			System.err.println("\nError modificando cliente, " + e.getMessage());
		}
	}
	
	//Eliminar cliente
	public void eliminarCliente(int id) {
		
		String sql = "DELETE FROM CLIENTE WHERE ID_Cliente = ?";
		
		try (Connection conn = ConexionBD.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, id);
			int filasEliminadas = stmt.executeUpdate();
			
			if (filasEliminadas > 0) {
				System.out.println("\nCliente eliminado correctamente.");
			} else {
				System.out.println("\nNo se encontró el cliente con ID: " + id);
			}
			
		} catch (SQLException e) {
			System.err.println("\nError eliminando cliente, " + e.getMessage());
		}
	}
	
	//listar clientes	
	public List<ClienteDTO> listarClientes() {
		List<ClienteDTO> clientes = new ArrayList<>();
		
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
				cliente.setTelefono(rs.getLong("Telefono"));
				
				clientes.add(cliente);
			}
			
		} catch (SQLException e) {
			System.err.println("\nError al listar clientes, " + e.getMessage());
		}
		
		return clientes;
	}
	//iniciar sesion
	public void iniciarSesion(String correo, String contrasena) {
		
		String sql = "SELECT * FROM CLIENTE WHERE Correo = ? AND Contrasena = ?";
		
		try (Connection conn = ConexionBD.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, correo);
			stmt.setString(2, contrasena);
			
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					System.out.println("\nInicio de sesión exitoso. Bienvenido, " + rs.getString("Nombre_Completo") + "!");
				} else {
					System.out.println("\nCorreo o contraseña incorrectos.");
				}
			}
		} catch (SQLException e) {
			System.err.println("\nError al iniciar sesión, " + e.getMessage());
		}
	}
	
	
}
