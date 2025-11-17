package vista;
import dao.ChoferDAO;
import dao.ClienteDAO;
import dao.CocheDAO;
import dto.ChoferDTO;
import dto.ClienteDTO;
import dto.CocheDTO;
import java.util.List;
import java.util.Scanner;
public class Vistaprueba {

	
	    private static Scanner scanner = new Scanner(System.in);
	    private static ChoferDAO choferDAO = new ChoferDAO();
	    private static ClienteDAO clienteDAO = new ClienteDAO();
	    private static CocheDAO cocheDAO = new CocheDAO();

	    public static void main(String[] args) {
	        System.out.println("🚗 SISTEMA SLR RENTING - CONSOLA DE PRUEBAS 🚗");
	        System.out.println("===============================================");

	        boolean salir = false;
	        while (!salir) {
	            mostrarMenuPrincipal();
	            int opcion = leerEntero("Seleccione una opción: ");

	            switch (opcion) {
	                case 1:
	                    menuGestionChoferes();
	                    break;
	                case 2:
	                    menuGestionClientes();
	                    break;
	                case 3:
	                    menuGestionCoches();
	                    break;
	                case 4:
	                    salir = true;
	                    System.out.println("¡Hasta pronto! 👋");
	                    break;
	                default:
	                    System.out.println("❌ Opción no válida. Intente nuevamente.");
	            }
	        }
	        scanner.close();
	    }

	    private static void mostrarMenuPrincipal() {
	        System.out.println("\n📋 MENÚ PRINCIPAL");
	        System.out.println("1. Gestión de Choferes");
	        System.out.println("2. Gestión de Clientes");
	        System.out.println("3. Gestión de Coches");
	        System.out.println("4. Salir");
	    }

	    // ===============================
	    // GESTIÓN DE CHOFERES
	    // ===============================
	    private static void menuGestionChoferes() {
	        boolean volver = false;
	        while (!volver) {
	            System.out.println("\n👨‍✈️ GESTIÓN DE CHOFERES");
	            System.out.println("1. Registrar chofer");
	            System.out.println("2. Modificar chofer");
	            System.out.println("3. Eliminar chofer");
	            System.out.println("4. Listar todos los choferes");
	            System.out.println("5. Listar choferes disponibles");
	            System.out.println("6. Volver al menú principal");

	            int opcion = leerEntero("Seleccione una opción: ");

	            switch (opcion) {
	                case 1:
	                    registrarChofer();
	                    break;
	                case 2:
	                    modificarChofer();
	                    break;
	                case 3:
	                    eliminarChofer();
	                    break;
	                case 4:
	                    listarChoferes();
	                    break;
	                case 5:
	                    listarChoferesDisponibles();
	                    break;
	                case 6:
	                    volver = true;
	                    break;
	                default:
	                    System.out.println("❌ Opción no válida.");
	            }
	        }
	    }

	    private static void registrarChofer() {
	        System.out.println("\n📝 REGISTRAR NUEVO CHOFER");
	        scanner.nextLine(); // Limpiar buffer

	        System.out.print("Nombre completo: ");
	        String nombre = scanner.nextLine();

	        System.out.print("DNI: ");
	        String dni = scanner.nextLine();

	        long telefono = leerEntero("Teléfono: ");
	        
	        System.out.print("¿Está disponible? (true/false): ");
	        boolean disponibilidad = scanner.nextBoolean();

	        ChoferDTO chofer = new ChoferDTO(nombre, dni, telefono, disponibilidad);
	        choferDAO.registrarChofer(chofer);
	    }

	    private static void modificarChofer() {
	        System.out.println("\n✏️ MODIFICAR CHOFER");
	        int id = leerEntero("ID del chofer a modificar: ");
	        
	        scanner.nextLine(); // Limpiar buffer
	        System.out.print("Nuevo nombre completo: ");
	        String nombre = scanner.nextLine();

	        System.out.print("Nuevo DNI: ");
	        String dni = scanner.nextLine();

	        long telefono = leerEntero("Nuevo teléfono: ");
	        
	        System.out.print("¿Está disponible? (true/false): ");
	        boolean disponibilidad = scanner.nextBoolean();

	        ChoferDTO chofer = new ChoferDTO(nombre, dni, telefono, disponibilidad);
	        choferDAO.modificarChofer(chofer, id);
	    }

	    private static void eliminarChofer() {
	        System.out.println("\n🗑️ ELIMINAR CHOFER");
	        int id = leerEntero("ID del chofer a eliminar: ");
	        choferDAO.eliminarChofer(id);
	    }

	    private static void listarChoferes() {
	        System.out.println("\n📋 LISTADO DE TODOS LOS CHOFERES");
	        List<ChoferDTO> choferes = choferDAO.listarChoferes();
	        
	        if (choferes.isEmpty()) {
	            System.out.println("No hay choferes registrados.");
	        } else {
	            for (ChoferDTO chofer : choferes) {
	                System.out.printf("ID: %d | Nombre: %s | DNI: %s | Tel: %d | Disponible: %s%n",
	                        chofer.getId_chofer(),
	                        chofer.getNombre_completo(),
	                        chofer.getDni(),
	                        chofer.getTelefono(),
	                        chofer.isDisposicion() ? "Sí" : "No");
	            }
	        }
	    }

	    private static void listarChoferesDisponibles() {
	        System.out.println("\n✅ CHOFERES DISPONIBLES");
	        List<ChoferDTO> choferes = choferDAO.listarChoferesDisponibles();
	        
	        if (choferes.isEmpty()) {
	            System.out.println("No hay choferes disponibles.");
	        } else {
	            for (ChoferDTO chofer : choferes) {
	                System.out.printf("ID: %d | Nombre: %s | DNI: %s | Tel: %d%n",
	                        chofer.getId_chofer(),
	                        chofer.getNombre_completo(),
	                        chofer.getDni(),
	                        chofer.getTelefono());
	            }
	        }
	    }

	    // ===============================
	    // GESTIÓN DE CLIENTES
	    // ===============================
	    private static void menuGestionClientes() {
	        boolean volver = false;
	        while (!volver) {
	            System.out.println("\n👥 GESTIÓN DE CLIENTES");
	            System.out.println("1. Registrar cliente");
	            System.out.println("2. Modificar cliente");
	            System.out.println("3. Eliminar cliente");
	            System.out.println("4. Listar todos los clientes");
	            System.out.println("5. Iniciar sesión");
	            System.out.println("6. Volver al menú principal");

	            int opcion = leerEntero("Seleccione una opción: ");

	            switch (opcion) {
	                case 1:
	                    registrarCliente();
	                    break;
	                case 2:
	                    modificarCliente();
	                    break;
	                case 3:
	                    eliminarCliente();
	                    break;
	                case 4:
	                    listarClientes();
	                    break;
	                case 5:
	                    iniciarSesionCliente();
	                    break;
	                case 6:
	                    volver = true;
	                    break;
	                default:
	                    System.out.println("❌ Opción no válida.");
	            }
	        }
	    }

	    private static void registrarCliente() {
	        System.out.println("\n📝 REGISTRAR NUEVO CLIENTE");
	        scanner.nextLine(); // Limpiar buffer

	        System.out.print("Nombre completo: ");
	        String nombre = scanner.nextLine();

	        System.out.print("NIF/NIE: ");
	        String nif = scanner.nextLine();

	        System.out.print("Correo: ");
	        String correo = scanner.nextLine();

	        System.out.print("Contraseña: ");
	        String contrasena = scanner.nextLine();

	        System.out.print("¿Tiene carnet? (true/false): ");
	        boolean carnet = scanner.nextBoolean();

	        long telefono = leerEntero("Teléfono: ");

	        ClienteDTO cliente = new ClienteDTO();
	        cliente.setNombreCompleto(nombre);
	        cliente.setNif_nie(nif);
	        cliente.setCorreo(correo);
	        cliente.setContrasena(contrasena);
	        cliente.setCarnet(carnet);
	        cliente.setTelefono(telefono);

	        clienteDAO.registrarCliente(cliente);
	    }

	    private static void modificarCliente() {
	        System.out.println("\n✏️ MODIFICAR CLIENTE");
	        int id = leerEntero("ID del cliente a modificar: ");
	        
	        scanner.nextLine(); // Limpiar buffer
	        System.out.print("Nuevo nombre completo: ");
	        String nombre = scanner.nextLine();

	        System.out.print("Nuevo NIF/NIE: ");
	        String nif = scanner.nextLine();

	        System.out.print("Nuevo correo: ");
	        String correo = scanner.nextLine();

	        System.out.print("Nueva contraseña: ");
	        String contrasena = scanner.nextLine();

	        System.out.print("¿Tiene carnet? (true/false): ");
	        boolean carnet = scanner.nextBoolean();

	        long telefono = leerEntero("Nuevo teléfono: ");

	        ClienteDTO cliente = new ClienteDTO();
	        cliente.setNombreCompleto(nombre);
	        cliente.setNif_nie(nif);
	        cliente.setCorreo(correo);
	        cliente.setContrasena(contrasena);
	        cliente.setCarnet(carnet);
	        cliente.setTelefono(telefono);

	        clienteDAO.modificarCliente(cliente, id);
	    }

	    private static void eliminarCliente() {
	        System.out.println("\n🗑️ ELIMINAR CLIENTE");
	        int id = leerEntero("ID del cliente a eliminar: ");
	        clienteDAO.eliminarCliente(id);
	    }

	    private static void listarClientes() {
	        System.out.println("\n📋 LISTADO DE TODOS LOS CLIENTES");
	        List<ClienteDTO> clientes = clienteDAO.listarClientes();
	        
	        if (clientes.isEmpty()) {
	            System.out.println("No hay clientes registrados.");
	        } else {
	            for (ClienteDTO cliente : clientes) {
	                System.out.printf("ID: %d | Nombre: %s | NIF: %s | Correo: %s | Carnet: %s | Tel: %d%n",
	                        cliente.getIdCliente(),
	                        cliente.getNombreCompleto(),
	                        cliente.getNif_nie(),
	                        cliente.getCorreo(),
	                        cliente.isCarnet() ? "Sí" : "No",
	                        cliente.getTelefono());
	            }
	        }
	    }

	    private static void iniciarSesionCliente() {
	        System.out.println("\n🔐 INICIAR SESIÓN");
	        scanner.nextLine(); // Limpiar buffer
	        
	        System.out.print("Correo: ");
	        String correo = scanner.nextLine();
	        
	        System.out.print("Contraseña: ");
	        String contrasena = scanner.nextLine();
	        
	        clienteDAO.iniciarSesion(correo, contrasena);
	    }

	    // ===============================
	    // GESTIÓN DE COCHES
	    // ===============================
	    private static void menuGestionCoches() {
	        boolean volver = false;
	        while (!volver) {
	            System.out.println("\n🚗 GESTIÓN DE COCHES");
	            System.out.println("1. Registrar coche");
	            System.out.println("2. Buscar coche por bastidor");
	            System.out.println("3. Modificar coche");
	            System.out.println("4. Eliminar coche");
	            System.out.println("5. Listar todos los coches");
	            System.out.println("6. Listar coches disponibles");
	            System.out.println("7. Listar coches nuevos");
	            System.out.println("8. Volver al menú principal");

	            int opcion = leerEntero("Seleccione una opción: ");

	            switch (opcion) {
	                case 1:
	                    registrarCoche();
	                    break;
	                case 2:
	                    buscarCoche();
	                    break;
	                case 3:
	                    modificarCoche();
	                    break;
	                case 4:
	                    eliminarCoche();
	                    break;
	                case 5:
	                    listarCoches();
	                    break;
	                case 6:
	                    listarCochesDisponibles();
	                    break;
	                case 7:
	                    listarCochesNuevos();
	                    break;
	                case 8:
	                    volver = true;
	                    break;
	                default:
	                    System.out.println("❌ Opción no válida.");
	            }
	        }
	    }

	    private static void registrarCoche() {
	        System.out.println("\n📝 REGISTRAR NUEVO COCHE");
	        
	        int bastidor = leerEntero("Número de bastidor: ");
	        scanner.nextLine(); // Limpiar buffer después de nextInt()
	        
	        System.out.print("Marca: ");
	        String marca = scanner.nextLine();
	        
	        System.out.print("Modelo: ");
	        String modelo = scanner.nextLine();
	        
	        double precio = leerDouble("Precio diario: ");
	        scanner.nextLine(); // ← ¡IMPORTANTE! Limpiar buffer después de nextDouble()
	        
	        System.out.print("Descripción: ");
	        String descripcion = scanner.nextLine(); // ← Ahora funcionará correctamente
	        
	        int plazas = leerEntero("Número de plazas: ");
	        scanner.nextLine(); // Limpiar buffer
	        
	        int potencia = leerEntero("Potencia (CV): ");
	        scanner.nextLine(); // Limpiar buffer
	        
	        System.out.print("Motor: ");
	        String motor = scanner.nextLine();
	        
	        int velocidadMax = leerEntero("Velocidad máxima: ");
	        scanner.nextLine(); // Limpiar buffer
	        
	        System.out.print("Matrícula: ");
	        String matricula = scanner.nextLine();
	        
	        System.out.print("URL de imagen: ");
	        String imagenURL = scanner.nextLine();
	        
	        System.out.print("¿Es nuevo? (true/false): ");
	        boolean nuevo = scanner.nextBoolean();
	        
	        System.out.print("¿Está disponible? (true/false): ");
	        boolean disponible = scanner.nextBoolean();

	        CocheDTO coche = new CocheDTO(bastidor, marca, modelo, precio, descripcion, 
	                                    plazas, potencia, motor, velocidadMax, matricula, 
	                                    imagenURL, nuevo, disponible);
	        cocheDAO.registrarCoche(coche);
	    }
	    private static void buscarCoche() {
	        System.out.println("\n🔍 BUSCAR COCHE");
	        int bastidor = leerEntero("Número de bastidor: ");
	        
	        CocheDTO coche = cocheDAO.buscarCoche(bastidor);
	        if (coche != null) {
	            System.out.println("✅ Coche encontrado:");
	            System.out.printf("Bastidor: %d | Marca: %s | Modelo: %s | Precio: %.2f€ | Disponible: %s%n",
	                    coche.getBastidor(), coche.getMarca(), coche.getModelo(),
	                    coche.getPrecioDiario(), coche.isDisponible() ? "Sí" : "No");
	        } else {
	            System.out.println("❌ No se encontró ningún coche con ese bastidor.");
	        }
	    }

	    private static void modificarCoche() {
	        System.out.println("\n✏️ MODIFICAR COCHE");
	        int bastidor = leerEntero("Número de bastidor del coche a modificar: ");
	        
	        // Primero buscamos el coche existente
	        CocheDTO cocheExistente = cocheDAO.buscarCoche(bastidor);
	        if (cocheExistente == null) {
	            System.out.println("❌ No existe un coche con ese bastidor.");
	            return;
	        }
	        
	        scanner.nextLine(); // Limpiar buffer
	        
	        System.out.print("Nueva marca (" + cocheExistente.getMarca() + "): ");
	        String marca = scanner.nextLine();
	        
	        System.out.print("Nuevo modelo (" + cocheExistente.getModelo() + "): ");
	        String modelo = scanner.nextLine();
	        
	        double precio = leerDouble("Nuevo precio diario (" + cocheExistente.getPrecioDiario() + "): ");
	        scanner.nextLine(); // Limpiar buffer

	        System.out.print("Nueva descripción (" + cocheExistente.getDescripcion() + "): ");
	        String descripcion = scanner.nextLine();
	        
	        int plazas = leerEntero("Nuevo número de plazas (" + cocheExistente.getPlazas() + "): ");
	        scanner.nextLine(); // Limpiar buffer
	        int potencia = leerEntero("Nueva potencia (" + cocheExistente.getPotencia() + "): ");
	        
	        System.out.print("Nuevo motor (" + cocheExistente.getMotor() + "): ");
	        String motor = scanner.nextLine();
	        scanner.nextLine(); // Limpiar buffer
	        
	        int velocidadMax = leerEntero("Nueva velocidad máxima (" + cocheExistente.getVelocidadMax() + "): ");
	        scanner.nextLine(); // Limpiar buffer

	        System.out.print("Nueva matrícula (" + cocheExistente.getMatricula() + "): ");
	        String matricula = scanner.nextLine();
	        
	        System.out.print("Nueva URL de imagen (" + cocheExistente.getImagenURL() + "): ");
	        String imagenURL = scanner.nextLine();
	        
	        System.out.print("¿Es nuevo? (true/false) (" + cocheExistente.isNuevo() + "): ");
	        boolean nuevo = scanner.nextBoolean();
	        
	        System.out.print("¿Está disponible? (true/false) (" + cocheExistente.isDisponible() + "): ");
	        boolean disponible = scanner.nextBoolean();

	        CocheDTO coche = new CocheDTO(bastidor, marca, modelo, precio, descripcion, 
	                                    plazas, potencia, motor, velocidadMax, matricula, 
	                                    imagenURL, nuevo, disponible);
	        cocheDAO.modificarCoche(coche);
	    }

	    private static void eliminarCoche() {
	        System.out.println("\n🗑️ ELIMINAR COCHE");
	        int bastidor = leerEntero("Número de bastidor del coche a eliminar: ");
	        cocheDAO.eliminarCoche(bastidor);
	    }

	    private static void listarCoches() {
	        System.out.println("\n📋 LISTADO DE TODOS LOS COCHES");
	        List<CocheDTO> coches = cocheDAO.listarCoches();
	        
	        if (coches.isEmpty()) {
	            System.out.println("No hay coches registrados.");
	        } else {
	            for (CocheDTO coche : coches) {
	                System.out.printf("Bastidor: %d | %s %s | %.2f€/día | Plazas: %d | Potencia: %dCV | Disponible: %s | Nuevo: %s%n",
	                        coche.getBastidor(), coche.getMarca(), coche.getModelo(),
	                        coche.getPrecioDiario(), coche.getPlazas(), coche.getPotencia(),
	                        coche.isDisponible() ? "Sí" : "No", coche.isNuevo() ? "Sí" : "No");
	            }
	        }
	    }

	    private static void listarCochesDisponibles() {
	        System.out.println("\n✅ COCHES DISPONIBLES");
	        List<CocheDTO> coches = cocheDAO.listarCochesDisponibles();
	        
	        if (coches.isEmpty()) {
	            System.out.println("No hay coches disponibles.");
	        } else {
	            for (CocheDTO coche : coches) {
	                System.out.printf("Bastidor: %d | %s %s | %.2f€/día | Plazas: %d | Potencia: %dCV%n",
	                        coche.getBastidor(), coche.getMarca(), coche.getModelo(),
	                        coche.getPrecioDiario(), coche.getPlazas(), coche.getPotencia());
	            }
	        }
	    }

	    private static void listarCochesNuevos() {
	        System.out.println("\n🆕 COCHES NUEVOS");
	        List<CocheDTO> coches = cocheDAO.listarCochesNuevos();
	        
	        if (coches.isEmpty()) {
	            System.out.println("No hay coches nuevos.");
	        } else {
	            for (CocheDTO coche : coches) {
	                System.out.printf("Bastidor: %d | %s %s | %.2f€/día | Plazas: %d | Potencia: %dCV | Disponible: %s%n",
	                        coche.getBastidor(), coche.getMarca(), coche.getModelo(),
	                        coche.getPrecioDiario(), coche.getPlazas(), coche.getPotencia(),
	                        coche.isDisponible() ? "Sí" : "No");
	            }
	        }
	    }

	    // ===============================
	    // MÉTODOS AUXILIARES
	    // ===============================
	    private static int leerEntero(String mensaje) {
	        System.out.print(mensaje);
	        while (!scanner.hasNextInt()) {
	            System.out.print("❌ Por favor, ingrese un número válido: ");
	            scanner.next();
	        }
	        return scanner.nextInt();
	    }

	    private static double leerDouble(String mensaje) {
	        System.out.print(mensaje);
	        while (!scanner.hasNextDouble()) {
	            System.out.print("❌ Por favor, ingrese un número válido: ");
	            scanner.next();
	        }
	        return scanner.nextDouble();
	    }
	}

