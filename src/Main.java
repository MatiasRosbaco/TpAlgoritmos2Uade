import java.util.*;

/**
 * Clase principal – menú interactivo del sistema de gestión de carga.
 *
 * ┌──────────────────────────────────────────────────────────────────────────┐
 * │  RESUMEN DE COMPLEJIDAD – Main                                         │
 * ├────────────────────────────┬────────────────────────────────────────────-┤
 * │ main()                     │ O(n) carga inicial + O(k) por cada ciclo  │
 * │ cargarPaqueteManual()      │ O(1) amortizado                           │
 * │ cargarDesdeArchivo()       │ O(n × m) – n leídos, m en inventario     │
 * │ mostrarInventario()        │ O(n) – recorre todo el inventario         │
 * │ enviarAlCentro()           │ O(n) – búsqueda lineal por ID            │
 * │ mostrarColaCentro()        │ O(n) – delega en mostrarCola()           │
 * │ procesarYCargarCamion()    │ O(1)                                      │
 * │ deshacerCargaCamion()      │ O(1)                                      │
 * │ descargarCamion()          │ O(1)                                      │
 * │ estadoCamion()             │ O(p) – p = paquetes en el camión         │
 * └────────────────────────────┴────────────────────────────────────────────-┘
 */
public class Main {

    private static final Scanner sc = new Scanner(System.in);
    private static final List<Paquete<String>> inventario = new ArrayList<>();
    private static final CentroDistribucion centro = new CentroDistribucion();
    private static final Camion camion = new Camion("AA-123-BB", 500);
    private static int nextId = 1000;

    /**
     * Punto de entrada.
     *
     * Complejidad de la carga inicial:
     *   - cargarDesdeJson() → O(n) donde n = paquetes en el archivo.
     *   - inventario.addAll() → O(n).
     *   - stream().mapToInt().max() → O(n) – recorre todos para hallar el máximo.
     *   Total inicio: O(n).
     *
     * Bucle principal:
     *   - Cada iteración depende de la opción elegida (ver cada método).
     *   - El bucle en sí es O(1) por iteración (leer input + switch).
     */
    public static void main(String[] args) {
        System.out.println("══════════════════════════════════════════");
        System.out.println("   SISTEMA DE GESTIÓN DE CARGA Y DESPACHO");
        System.out.println("══════════════════════════════════════════");

        // O(n) – carga y parseo del archivo JSON
        List<Paquete<String>> cargados = Persistencia.cargarDesdeJson("inventario.json");
        if (!cargados.isEmpty()) {
            inventario.addAll(cargados); // O(n)
            // O(n) – recorre todos los paquetes para encontrar el ID máximo
            nextId = cargados.stream().mapToInt(Paquete::getId).max().orElse(999) + 1;
        }

        boolean salir = false;
        while (!salir) {
            mostrarMenu();                     // O(1)
            String opcion = sc.nextLine().trim(); // O(1)
            System.out.println();
            switch (opcion) {
                case "1": cargarPaqueteManual();   break; // O(1)
                case "2": cargarDesdeArchivo();     break; // O(n × m)
                case "3": mostrarInventario();      break; // O(n)
                case "4": enviarAlCentro();         break; // O(n)
                case "5": mostrarColaCentro();      break; // O(n)
                case "6": procesarYCargarCamion();  break; // O(1)
                case "7": deshacerCargaCamion();    break; // O(1)
                case "8": descargarCamion();        break; // O(1)
                case "9": estadoCamion();           break; // O(p)
                case "0": salir = true;             break;
                default:  System.out.println("Opción no válida.");
            }
        }
        System.out.println("¡Hasta luego!");
    }

    /**
     * Muestra el menú de opciones.
     * Complejidad: O(1) – imprime un número fijo de líneas.
     */
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

    /**
     * Agrega un paquete ingresado por teclado al inventario.
     *
     * Complejidad: O(1) amortizado.
     *   - Lectura de input → O(1) (longitud acotada).
     *   - new Paquete() → O(1).
     *   - inventario.add() → O(1) amortizado (ArrayList puede hacer resize
     *     cuando el arreglo interno está lleno, pero promediado es O(1)).
     */
    private static void cargarPaqueteManual() {
        System.out.print("Peso (kg): ");
        double peso = Double.parseDouble(sc.nextLine().trim());   // O(1)
        System.out.print("Destino: ");
        String destino = sc.nextLine().trim();                    // O(1)
        System.out.print("Contenido (descripción libre): ");
        String contenido = sc.nextLine().trim();                  // O(1)
        System.out.print("Prioridad (1=URGENTE, 2=ESTANDAR): ");
        Paquete.Prioridad prio = sc.nextLine().trim().equals("1")
                ? Paquete.Prioridad.URGENTE : Paquete.Prioridad.ESTANDAR; // O(1)

        Paquete<String> p = new Paquete<>(nextId++, peso, destino, contenido, prio); // O(1)
        inventario.add(p);           // O(1) amortizado
        System.out.println("  ✓ " + p);
    }

    /**
     * Carga paquetes desde JSON evitando duplicados por ID.
     *
     * Complejidad: O(n × m), donde:
     *   - n = cantidad de paquetes leídos del archivo.
     *   - m = cantidad de paquetes ya en el inventario.
     *
     * Desglose:
     *   - cargarDesdeJson() → O(n).
     *   - Por cada paquete leído (n), se hace noneMatch() sobre el inventario
     *     que en el peor caso recorre los m elementos → O(m) por paquete.
     *   - Total: O(n) + O(n × m) = O(n × m).
     *
     * Posible mejora: usar un HashSet<Integer> con los IDs existentes
     *   para verificar duplicados en O(1) → total bajaría a O(n + m).
     */
    private static void cargarDesdeArchivo() {
        List<Paquete<String>> cargados = Persistencia.cargarDesdeJson("inventario.json"); // O(n)
        for (Paquete<String> p : cargados) {                          // O(n)
            if (inventario.stream().noneMatch(x -> x.getId() == p.getId())) { // O(m) peor caso
                inventario.add(p);                                     // O(1) amort.
            }
        }
    }

    /**
     * Muestra todos los paquetes del inventario.
     *
     * Complejidad: O(n), donde n = inventario.size().
     *   - Recorre cada elemento e imprime su toString() → O(1) por elemento.
     */
    private static void mostrarInventario() {
        if (inventario.isEmpty()) {                              // O(1)
            System.out.println("  (Inventario vacío)");
            return;
        }
        for (Paquete<String> p : inventario) {                   // O(n)
            System.out.println("  " + p + (p.esPrioritario() ? "  ★ PRIORITARIO" : "")); // O(1)
        }
    }

    /**
     * Envía un paquete del inventario al Centro de Distribución.
     *
     * Complejidad: O(n), donde n = inventario.size().
     *   - mostrarInventario() → O(n).
     *   - stream().filter().findFirst() → O(n) peor caso (búsqueda lineal).
     *     En el mejor caso (ID encontrado al inicio) es O(1), pero el peor
     *     caso domina el análisis.
     *   - centro.recibir() → O(1).
     *   Total: O(n) + O(n) = O(n).
     *
     * Nota: el paquete NO se elimina del inventario, solo se envía al centro.
     * Si se quisiera eliminar, inventario.remove() en ArrayList sería O(n)
     * adicional por el desplazamiento de elementos.
     */
    private static void enviarAlCentro() {
        if (inventario.isEmpty()) {                              // O(1)
            System.out.println("  (No hay paquetes en inventario)");
            return;
        }
        mostrarInventario();                                     // O(n)
        System.out.print("ID del paquete a enviar al centro: ");
        int id = Integer.parseInt(sc.nextLine().trim());         // O(1)
        Optional<Paquete<String>> opt = inventario.stream()      // O(n) peor caso
                .filter(p -> p.getId() == id).findFirst();
        if (opt.isPresent()) {
            centro.recibir(opt.get());                           // O(1)
            System.out.println("  ✓ Enviado al Centro de Distribución.");
        } else {
            System.out.println("  ✗ ID no encontrado.");
        }
    }

    /**
     * Muestra la cola del centro de distribución.
     * Complejidad: O(n) – delega en centro.mostrarCola() que recorre la lista.
     */
    private static void mostrarColaCentro() {
        System.out.println("  Cola de procesamiento (prioridad):");
        centro.mostrarCola(); // O(n)
    }

    /**
     * Saca el siguiente paquete del centro y lo carga en el camión.
     *
     * Complejidad: O(1).
     *   - centro.procesarSiguiente() → O(1) (removeFirst en LinkedList).
     *   - camion.cargar() → O(1) amortizado (push en ArrayDeque).
     */
    private static void procesarYCargarCamion() {
        if (!centro.hayPaquetes()) {                              // O(1)
            System.out.println("  (Centro sin paquetes pendientes)");
            return;
        }
        Paquete<String> p = centro.procesarSiguiente();           // O(1)
        System.out.println("  Procesado: " + p);
        if (camion.cargar(p)) {                                   // O(1) amort.
            System.out.println("  ✓ Cargado en " + camion.getPatente());
        }
    }

    /**
     * Deshace la última carga del camión y devuelve el paquete al centro.
     *
     * Complejidad: O(1).
     *   - camion.deshacerCarga() → O(1) (pop del tope).
     *   - centro.recibir() → O(1) (addFirst o addLast en LinkedList).
     */
    private static void deshacerCargaCamion() {
        Paquete<String> p = camion.deshacerCarga();               // O(1)
        if (p != null) {
            System.out.println("  ✓ Se deshizo la carga de: " + p);
            centro.recibir(p);                                    // O(1)
            System.out.println("    (Devuelto al Centro de Distribución)");
        } else {
            System.out.println("  (Camión vacío, nada que deshacer)");
        }
    }

    /**
     * Descarga un paquete del camión (LIFO).
     *
     * Complejidad: O(1).
     *   - camion.descargar() → O(1) (pop del tope del ArrayDeque).
     */
    private static void descargarCamion() {
        Paquete<String> p = camion.descargar();                   // O(1)
        if (p != null) {
            System.out.println("  ✓ Descargado: " + p);
        } else {
            System.out.println("  (Camión vacío)");
        }
    }

    /**
     * Muestra el estado actual del camión y lista sus paquetes.
     *
     * Complejidad: O(p), donde p = cantidad de paquetes en el camión.
     *   - camion.toString() → O(1).
     *   - Iteración con iterator → O(p), visitando cada paquete una vez.
     */
    private static void estadoCamion() {
        System.out.println("  " + camion);                        // O(1)
        if (!camion.estaVacio()) {                                // O(1)
            System.out.println("  Paquetes (tope → fondo):");
            Iterator<Paquete<String>> it = camion.iterator();     // O(1)
            while (it.hasNext()) {                                // O(p)
                System.out.println("    · " + it.next());         // O(1) por iteración
            }
        }
    }
}