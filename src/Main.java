import java.util.*;

public class Main {

    private static final Scanner sc = new Scanner(System.in);
    private static final List<Paquete<String>> inventario = new ArrayList<>();
    private static final CentroDistribucion centro = new CentroDistribucion();
    private static final Camion camion = new Camion("AA-123-BB", 500);
    private static int nextId = 1000;

    public static void main(String[] args) {
        System.out.println("══════════════════════════════════════════");
        System.out.println("   SISTEMA DE GESTIÓN DE CARGA Y DESPACHO");
        System.out.println("══════════════════════════════════════════");

        List<Paquete<String>> cargados = Persistencia.cargarDesdeJson("inventario.json");
        if (!cargados.isEmpty()) {
            inventario.addAll(cargados);
            nextId = cargados.stream().mapToInt(Paquete::getId).max().orElse(999) + 1;
        }

        boolean salir = false;
        while (!salir) {
            mostrarMenu();
            String opcion = sc.nextLine().trim();
            System.out.println();
            switch (opcion) {
                case "1": cargarPaqueteManual();   break;
                case "2": cargarDesdeArchivo();     break;
                case "3": mostrarInventario();      break;
                case "4": enviarAlCentro();         break;
                case "5": mostrarColaCentro();      break;
                case "6": procesarYCargarCamion();  break;
                case "7": deshacerCargaCamion();    break;
                case "8": descargarCamion();        break;
                case "9": estadoCamion();           break;
                case "0": salir = true;             break;
                default:  System.out.println("Opción no válida.");
            }
        }
        System.out.println("¡Hasta luego!");
    }

    private static void mostrarMenu() {
        System.out.println("\n─── MENÚ ──────────────────────────────");
        System.out.println(" 1. Agregar paquete manualmente");
        System.out.println(" 2. Cargar paquetes desde inventario.json");
        System.out.println(" 3. Ver inventario completo");
        System.out.println(" 4. Enviar paquete al Centro de Distribución");
        System.out.println(" 5. Ver cola del Centro de Distribución");
        System.out.println(" 6. Procesar siguiente (Centro → Camión)");
        System.out.println(" 7. Deshacer última carga del camión");
        System.out.println(" 8. Descargar paquete del camión (LIFO)");
        System.out.println(" 9. Ver estado del camión");
        System.out.println(" 0. Salir");
        System.out.print("Opción: ");
    }

    private static void cargarPaqueteManual() {
        System.out.print("Peso (kg): ");
        double peso = Double.parseDouble(sc.nextLine().trim());
        System.out.print("Destino: ");
        String destino = sc.nextLine().trim();
        System.out.print("Contenido (descripción libre): ");
        String contenido = sc.nextLine().trim();
        System.out.print("Prioridad (1=URGENTE, 2=ESTANDAR): ");
        Paquete.Prioridad prio = sc.nextLine().trim().equals("1")
                ? Paquete.Prioridad.URGENTE : Paquete.Prioridad.ESTANDAR;

        Paquete<String> p = new Paquete<>(nextId++, peso, destino, contenido, prio);
        inventario.add(p);
        System.out.println("  ✓ " + p);
    }

    private static void cargarDesdeArchivo() {
        List<Paquete<String>> cargados = Persistencia.cargarDesdeJson("inventario.json");
        for (Paquete<String> p : cargados) {
            if (inventario.stream().noneMatch(x -> x.getId() == p.getId())) {
                inventario.add(p);
            }
        }
    }

    private static void mostrarInventario() {
        if (inventario.isEmpty()) {
            System.out.println("  (Inventario vacío)");
            return;
        }
        for (Paquete<String> p : inventario) {
            System.out.println("  " + p + (p.esPrioritario() ? "  ★ PRIORITARIO" : ""));
        }
    }

    private static void enviarAlCentro() {
        if (inventario.isEmpty()) {
            System.out.println("  (No hay paquetes en inventario)");
            return;
        }
        mostrarInventario();
        System.out.print("ID del paquete a enviar al centro: ");
        int id = Integer.parseInt(sc.nextLine().trim());
        Optional<Paquete<String>> opt = inventario.stream()
                .filter(p -> p.getId() == id).findFirst();
        if (opt.isPresent()) {
            centro.recibir(opt.get());
            System.out.println("  ✓ Enviado al Centro de Distribución.");
        } else {
            System.out.println("  ✗ ID no encontrado.");
        }
    }

    private static void mostrarColaCentro() {
        System.out.println("  Cola de procesamiento (prioridad):");
        centro.mostrarCola();
    }

    private static void procesarYCargarCamion() {
        if (!centro.hayPaquetes()) {
            System.out.println("  (Centro sin paquetes pendientes)");
            return;
        }
        Paquete<String> p = centro.procesarSiguiente();
        System.out.println("  Procesado: " + p);
        if (camion.cargar(p)) {
            System.out.println("  ✓ Cargado en " + camion.getPatente());
        }
    }

    private static void deshacerCargaCamion() {
        Paquete<String> p = camion.deshacerCarga();
        if (p != null) {
            System.out.println("  ✓ Se deshizo la carga de: " + p);
            centro.recibir(p);
            System.out.println("    (Devuelto al Centro de Distribución)");
        } else {
            System.out.println("  (Camión vacío, nada que deshacer)");
        }
    }

    private static void descargarCamion() {
        Paquete<String> p = camion.descargar();
        if (p != null) {
            System.out.println("  ✓ Descargado: " + p);
        } else {
            System.out.println("  (Camión vacío)");
        }
    }

    private static void estadoCamion() {
        System.out.println("  " + camion);
        if (!camion.estaVacio()) {
            System.out.println("  Paquetes (tope → fondo):");
            Iterator<Paquete<String>> it = camion.iterator();
            while (it.hasNext()) {
                System.out.println("    · " + it.next());
            }
        }
    }
}