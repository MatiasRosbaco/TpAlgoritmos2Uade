import java.util.LinkedList;

/**
 * Centro de distribución usando lista doblemente enlazada (LinkedList).
 *
 * ┌────────────────────────────────────────────────────────────────────────┐
 * │  RESUMEN DE COMPLEJIDAD – CentroDistribucion                         │
 * ├──────────────────────────┬─────────────────────────────────────────────┤
 * │ recibir()                │ O(1) – inserción en extremo de LinkedList  │
 * │ procesarSiguiente()      │ O(1) – remoción del primer nodo           │
 * │ verSiguiente()           │ O(1) – peek del primer nodo               │
 * │ hayPaquetes()            │ O(1) – consulta de tamaño                 │
 * │ cantidadPendientes()     │ O(1) – size (campo interno)               │
 * │ mostrarCola()            │ O(n) – recorre todos los elementos        │
 * └──────────────────────────┴─────────────────────────────────────────────┘
 *
 * Nota sobre LinkedList (lista doblemente enlazada):
 *   - Mantiene punteros head y tail → insertar/remover en ambos extremos
 *     es O(1) sin necesidad de recorrer la lista.
 *   - A diferencia de ArrayList, no requiere desplazar elementos al
 *     insertar/remover del principio (ArrayList.addFirst sería O(n)).
 *   - Desventaja: peor localidad de caché y mayor uso de memoria por nodo.
 *   - Para este caso de uso (cola con prioridad simple) es ideal porque
 *     todas las operaciones frecuentes son O(1).
 */
public class CentroDistribucion {

    private final LinkedList<Paquete<String>> lista;

    /**
     * Constructor.
     * Complejidad: O(1) – crea una LinkedList vacía (head = tail = null).
     */
    public CentroDistribucion() {
        this.lista = new LinkedList<>(); // O(1)
    }

    /**
     * Recibe un paquete y lo ubica según su prioridad.
     *
     * Complejidad: O(1).
     *   - esPrioritario() es O(1) (ver Paquete).
     *   - addFirst() / addLast() son O(1) en LinkedList porque solo
     *     modifican los punteros head o tail respectivamente:
     *       addFirst: nuevo.next = head; head.prev = nuevo; head = nuevo
     *       addLast:  nuevo.prev = tail; tail.next = nuevo; tail = nuevo
     *
     * Nota de diseño: esta estrategia NO garantiza orden estricto entre
     * paquetes prioritarios (varios URGENTES quedan en orden LIFO entre sí).
     * Si se necesitara orden estricto, se usaría una PriorityQueue → O(log n).
     */
    public void recibir(Paquete<String> paquete) {
        if (paquete.esPrioritario()) {
            lista.addFirst(paquete);  // O(1) – inserta al inicio
        } else {
            lista.addLast(paquete);   // O(1) – inserta al final
        }
    }

    /**
     * Retira y devuelve el próximo paquete a procesar (el primero de la lista).
     *
     * Complejidad: O(1).
     *   - removeFirst() desenlaza el nodo head:
     *       head = head.next; head.prev = null → O(1)
     *   - No requiere desplazar ningún otro elemento.
     */
    public Paquete<String> procesarSiguiente() {
        if (lista.isEmpty()) return null; // O(1)
        return lista.removeFirst();       // O(1)
    }

    /**
     * Consulta el próximo paquete sin retirarlo.
     * Complejidad: O(1) – accede directamente al nodo head.
     */
    public Paquete<String> verSiguiente() {
        return lista.peekFirst(); // O(1)
    }

    /**
     * Complejidad: O(1) – LinkedList.isEmpty() compara size con 0.
     */
    public boolean hayPaquetes() { return !lista.isEmpty(); } // O(1)

    /**
     * Complejidad: O(1) – LinkedList mantiene un campo `size` que se
     * actualiza con cada add/remove, así que no necesita recorrer la lista.
     */
    public int cantidadPendientes() { return lista.size(); } // O(1)

    /**
     * Imprime todos los paquetes pendientes en orden.
     *
     * Complejidad: O(n), donde n = cantidad de paquetes en la lista.
     *   - El for-each recorre cada nodo de head a tail siguiendo punteros next.
     *   - Cada iteración ejecuta operaciones O(1) (printf + toString del paquete).
     *   - Total: n iteraciones × O(1) = O(n).
     */
    public void mostrarCola() {
        if (lista.isEmpty()) {                        // O(1)
            System.out.println("  (Centro vacío)");
            return;
        }
        int pos = 1;
        for (Paquete<String> p : lista) {             // O(n) – recorre n nodos
            System.out.printf("  %d. %s%n", pos++, p); // O(1) por iteración
        }
    }
}