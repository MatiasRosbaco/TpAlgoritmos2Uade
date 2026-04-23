import java.util.*;

/**
 * Clase principal – menú interactivo del sistema de gestión de carga.
 *
 * FLUJO CORRECTO (según consigna):
 *   Inventario → Camión (pila LIFO) → Centro de Distribución (cola con prioridad)
 *
 *   1. Los paquetes se crean o cargan desde JSON al inventario.
 *   2. Desde el inventario se cargan al camión (pila LIFO).
 *   3. Se puede deshacer la última carga del camión en O(1) (vuelve al inventario).
 *   4. El camión "viaja" al centro y descarga (LIFO: último en entrar = primero en salir).
 *   5. El centro procesa los paquetes con prioridad (urgentes y >50kg primero).
 *
 * ┌──────────────────────────────────────────────────────────────────────────┐
 * │  RESUMEN DE COMPLEJIDAD – Main                                         │
 * ├──────────────────────────────┬──────────────────────────────────────────-┤
 * │ main()                       │ O(n) carga inicial + O(k) por ciclo     │
 * │ cargarPaqueteManual()        │ O(1) amortizado                         │
 * │ cargarDesdeArchivo()         │ O(n × m) – n leídos, m en inventario   │
 * │ mostrarInventario()          │ O(n) – recorre todo el inventario       │
 * │ cargarAlCamion()             │ O(n) – búsqueda lineal por ID          │
 * │ deshacerCargaCamion()        │ O(1) – pop + add al inventario         │
 * │ estadoCamion()               │ O(p) – p = paquetes en el camión       │
 * │ descargarCamionAlCentro()    │ O(1) – pop del camión + recibir        │
 * │ descargarTodoAlCentro()      │ O(p) – descarga todos los paquetes     │
 * │ mostrarColaCentro()          │ O(n) – delega en mostrarCola()         │
 * │ procesarSiguienteCentro()    │ O(1) – removeFirst de la LinkedList    │
 * └──────────────────────────────┴──────────────────────────────────────────-┘
 */
public class Main {

    private static final Scanner sc = new Scanner(System.in);
    private static final List<Paquete<String>> inventario = new ArrayList<>();
    private static final Camion camion = new Camion("AA-123-BB", 500);
    private static final CentroDistribucion centro = new CentroDistribucion();
    private static int nextId = 1000;

    public static void main(String[] args) {
        System.out.println("══════════════════════════════════════════");
        System.out.println("   SISTEMA DE GESTIÓN DE CARGA Y DESPACHO");
        System.out.println("══════════════════════════════════════════");

        // O(n) – carga inicial desde archivo JSON
        List<Paquete<String>> cargados = Persistencia.cargarDesdeJson("inventario.json");
        if (!cargados.isEmpty()) {
            inventario.addAll(cargados); // O(n)
            nextId = cargados.stream().mapToInt(Paquete::getId).max().orElse(999) + 1; // O(n)
        }

        boolean salir = false;
        while (!salir) {
            mostrarMenu();
            String opcion = sc.nextLine().trim();
            System.out.println();
            switch (opcion) {
                // ── Inventario ──
                case "1": cargarPaqueteManual();       break;
                case "2": cargarDesdeArchivo();         break;
                case "3": mostrarInventario();          break;
                // ── Camión (carga en origen) ──
                case "4": cargarAlCamion();             break;
                case "5": deshacerCargaCamion();        break;
                case "6": estadoCamion();               break;
                // ── Camión → Centro ──
                case "7": descargarCamionAlCentro();    break;
                case "8": descargarTodoAlCentro();      break;
                // ── Centro de Distribución ──
                case "9": mostrarColaCentro();          break;
                case "10": procesarSiguienteCentro();   break;
                case "0": salir = true;                 break;
                default:  System.out.println("Opción no válida.");
            }
        }
        System.out.println("¡Hasta luego!");
    }

    // ═══════════════════════════════════════════════════════════════════
    //  MENÚ
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Muestra el menú de opciones organizado por etapa del flujo.
     * Complejidad: O(1) – imprime un número fijo de líneas.
     */
    private static void mostrarMenu() {
        System.out.println("\n─── MENÚ ──────────────────────────────────");
        System.out.println("         INVENTARIO");
        System.out.println("  1. Agregar paquete manualmente");
        System.out.println("  2. Cargar paquetes desde inventario.json");
        System.out.println("  3. Ver inventario completo");
        System.out.println("         CAMIÓN (carga en origen)");
        System.out.println("  4. Cargar paquete al camión (Inventario → Camión)");
        System.out.println("  5. Deshacer última carga del camión");
        System.out.println("  6. Ver estado del camión");
        System.out.println("         DESCARGA (Camión → Centro)");
        System.out.println("  7. Descargar siguiente paquete al Centro");
        System.out.println("  8. Descargar TODO el camión al Centro");
        System.out.println("         CENTRO DE DISTRIBUCIÓN");
        System.out.println("  9. Ver cola del Centro de Distribución");
        System.out.println(" 10. Procesar siguiente paquete del Centro");
        System.out.println("  0. Salir");
        System.out.print("Opción: ");
    }

    // ═══════════════════════════════════════════════════════════════════
    //  INVENTARIO
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Agrega un paquete ingresado por teclado al inventario.
     * Complejidad: O(1) amortizado.
     */
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
        System.out.println("  ✓ Agregado al inventario: " + p);
    }

    /**
     * Carga paquetes desde JSON evitando duplicados por ID.
     * Complejidad: O(n × m).
     */
    private static void cargarDesdeArchivo() {
        List<Paquete<String>> cargados = Persistencia.cargarDesdeJson("inventario.json");
        int nuevos = 0;
        for (Paquete<String> p : cargados) {
            if (inventario.stream().noneMatch(x -> x.getId() == p.getId())) {
                inventario.add(p);
                nuevos++;
            }
        }
        System.out.println("  ✓ " + nuevos + " paquetes nuevos agregados al inventario.");
    }

    /**
     * Muestra todos los paquetes del inventario.
     * Complejidad: O(n).
     */
    private static void mostrarInventario() {
        if (inventario.isEmpty()) {
            System.out.println("  (Inventario vacío)");
            return;
        }
        System.out.println("  Inventario (" + inventario.size() + " paquetes):");
        for (Paquete<String> p : inventario) {
            System.out.println("    " + p + (p.esPrioritario() ? "  ★ PRIORITARIO" : ""));
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    //  CAMIÓN – Carga en origen (Inventario → Camión)
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Selecciona un paquete del inventario y lo carga en el camión.
     * El paquete se elimina del inventario al cargarse exitosamente.
     *
     * Complejidad: O(n), donde n = inventario.size().
     *   - mostrarInventario() → O(n).
     *   - Búsqueda por ID con stream().filter() → O(n) peor caso.
     *   - inventario.remove() → O(n) (desplaza elementos en ArrayList).
     *   - camion.cargar() → O(1) amortizado.
     */
    private static void cargarAlCamion() {
        if (inventario.isEmpty()) {
            System.out.println("  (No hay paquetes en inventario)");
            return;
        }
        mostrarInventario();
        System.out.print("  ID del paquete a cargar en el camión: ");
        int id = Integer.parseInt(sc.nextLine().trim());

        Optional<Paquete<String>> opt = inventario.stream()
                .filter(p -> p.getId() == id).findFirst();

        if (opt.isPresent()) {
            Paquete<String> p = opt.get();
            if (camion.cargar(p)) {
                inventario.remove(p);   // O(n) – se retira del inventario
                System.out.println("  ✓ Cargado en camión " + camion.getPatente() + ": " + p);
            }
            // Si no se pudo cargar (excede peso), el paquete queda en inventario
        } else {
            System.out.println("  ✗ ID no encontrado en inventario.");
        }
    }

    /**
     * Deshace la última carga del camión: retira el paquete del tope
     * y lo devuelve al inventario (origen).
     *
     * Complejidad: O(1).
     *   - camion.deshacerCarga() → O(1) (pop del tope de la pila).
     *   - inventario.add() → O(1) amortizado.
     *
     * El paquete vuelve al inventario porque el camión aún está en el
     * punto de carga (origen). Si ya hubiera viajado al centro, no tendría
     * sentido "deshacer" — se usaría la descarga normal.
     */
    private static void deshacerCargaCamion() {
        Paquete<String> p = camion.deshacerCarga();
        if (p != null) {
            inventario.add(p);   // Devuelve al inventario (origen)
            System.out.println("  ✓ Se deshizo la carga de: " + p);
            System.out.println("    (Devuelto al inventario)");
        } else {
            System.out.println("  (Camión vacío, nada que deshacer)");
        }
    }

    /**
     * Muestra el estado actual del camión y lista sus paquetes.
     * Complejidad: O(p), donde p = cantidad de paquetes en el camión.
     */
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

    // ═══════════════════════════════════════════════════════════════════
    //  DESCARGA – Camión → Centro de Distribución
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Descarga el paquete del tope del camión (LIFO) y lo envía al centro.
     * El centro lo ubicará según su prioridad (urgente/pesado al frente).
     *
     * Complejidad: O(1).
     *   - camion.descargar() → O(1) (pop del tope).
     *   - centro.recibir() → O(1) (addFirst o addLast en LinkedList).
     */
    private static void descargarCamionAlCentro() {
        if (camion.estaVacio()) {
            System.out.println("  (Camión vacío, nada que descargar)");
            return;
        }
        Paquete<String> p = camion.descargar();
        centro.recibir(p);
        System.out.println("  ✓ Descargado del camión → Centro: " + p);
        if (p.esPrioritario()) {
            System.out.println("    ★ Ubicado con prioridad en el centro.");
        }
    }

    /**
     * Descarga TODOS los paquetes del camión al centro de distribución.
     * Simula la llegada del camión al centro y la descarga completa.
     *
     * Complejidad: O(p), donde p = cantidad de paquetes en el camión.
     *   - Cada iteración: descargar() O(1) + recibir() O(1) = O(1).
     *   - Total: p iteraciones × O(1) = O(p).
     */
    private static void descargarTodoAlCentro() {
        if (camion.estaVacio()) {
            System.out.println("  (Camión vacío, nada que descargar)");
            return;
        }
        int count = 0;
        System.out.println("  Descargando camión " + camion.getPatente() + " en el Centro...");
        while (!camion.estaVacio()) {
            Paquete<String> p = camion.descargar();
            centro.recibir(p);
            count++;
            System.out.println("    → " + p + (p.esPrioritario() ? "  ★" : ""));
        }
        System.out.println("  ✓ " + count + " paquetes descargados al Centro de Distribución.");
    }

    // ═══════════════════════════════════════════════════════════════════
    //  CENTRO DE DISTRIBUCIÓN – Procesamiento con prioridad
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Muestra la cola del centro de distribución.
     * Complejidad: O(n) – delega en centro.mostrarCola().
     */
    private static void mostrarColaCentro() {
        System.out.println("  Cola de procesamiento (prioridad):");
        centro.mostrarCola();
    }

    /**
     * Procesa (retira) el siguiente paquete del centro.
     * Los paquetes prioritarios (URGENTE o >50kg) se procesan primero
     * gracias al orden de inserción del centro (addFirst para prioritarios).
     *
     * Complejidad: O(1).
     *   - centro.procesarSiguiente() → O(1) (removeFirst en LinkedList).
     */
    private static void procesarSiguienteCentro() {
        if (!centro.hayPaquetes()) {
            System.out.println("  (Centro sin paquetes pendientes)");
            return;
        }
        Paquete<String> p = centro.procesarSiguiente();
        System.out.println("  ✓ Procesado: " + p);
        System.out.println("    Paquetes restantes en centro: " + centro.cantidadPendientes());
    }
}