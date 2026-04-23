import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

/**
 * Camión que carga paquetes en modo LIFO (pila).
 * Internamente usa un ArrayDeque como pila.
 *
 * ┌────────────────────────────────────────────────────────────────────────┐
 * │  RESUMEN DE COMPLEJIDAD – Camion                                     │
 * ├──────────────────────────┬─────────────────────────────────────────────┤
 * │ cargar()                 │ O(1) amortizado – push en ArrayDeque       │
 * │ descargar()              │ O(1) – pop del tope de la pila             │
 * │ deshacerCarga()          │ O(1) – equivale a descargar()              │
 * │ verSuperior()            │ O(1) – peek                                │
 * │ estaVacio()              │ O(1) – consulta de tamaño                  │
 * │ cantidadPaquetes()       │ O(1) – size del ArrayDeque                 │
 * │ iterator()               │ O(1) creación; O(n) recorrido completo     │
 * │ toString()               │ O(1)                                       │
 * └──────────────────────────┴─────────────────────────────────────────────┘
 *
 * Nota sobre ArrayDeque:
 *   - push/pop/peek operan sobre el extremo del arreglo interno → O(1).
 *   - push puede disparar un resize (duplica capacidad), pero el costo
 *     amortizado sigue siendo O(1) por operación.
 *   - No usa nodos enlazados ⇒ mejor localidad de caché que LinkedList.
 */
public class Camion {

    private final String patente;
    private final double capacidadMaxKg;
    private final Deque<Paquete<String>> pila;   // ArrayDeque → pila LIFO ( no se usa libreria stack porque esta obsoleta, la documentacion de java sugiere usar Deque)
    private double pesoActual;

    /**
     * Constructor.
     * Complejidad: O(1) – inicializa campos y crea un ArrayDeque vacío
     * (capacidad inicial por defecto = 16 elementos).
     */
    public Camion(String patente, double capacidadMaxKg) {
        this.patente = patente;
        this.capacidadMaxKg = capacidadMaxKg;
        this.pila = new ArrayDeque<>(); // O(1)
        this.pesoActual = 0;
    }

    /**
     * Carga un paquete en el tope de la pila si no excede la capacidad.
     *
     * Complejidad: O(1) amortizado.
     *   - La verificación de peso es O(1).
     *   - pila.push() es O(1) amortizado: normalmente O(1), pero cuando
     *     el arreglo interno está lleno se duplica su tamaño (O(n) puntual).
     *     Promediando sobre n inserciones, cada una cuesta O(1).
     */
    public boolean cargar(Paquete<String> paquete) {
        if (pesoActual + paquete.getPeso() > capacidadMaxKg) { // O(1)
            System.out.println("  ✗ No se puede cargar: excede capacidad del camión.");
            return false;
        }
        pila.push(paquete);              // O(1) amortizado
        pesoActual += paquete.getPeso(); // O(1)
        return true;
    }

    /**
     * Descarga (retira) el paquete del tope de la pila.
     *
     * Complejidad: O(1).
     *   - pila.pop() retira el elemento del extremo del arreglo → O(1).
     *   - La resta del peso es O(1).
     */
    public Paquete<String> descargar() {
        if (pila.isEmpty()) return null; // O(1)
        Paquete<String> p = pila.pop();  // O(1)
        pesoActual -= p.getPeso();       // O(1)
        return p;
    }

    /**
     * Deshace la última carga: retira el último paquete agregado.
     *
     * Complejidad: O(1) – delega en descargar(), que es O(1).
     *
     * Justificación de diseño: al usar una pila (LIFO), el último cargado
     * siempre está en el tope, así que deshacerlo es simplemente un pop().
     * Si se hubiera usado una cola (FIFO), deshacer la última inserción
     * requeriría O(n) ya que el elemento estaría al final.
     */
    public Paquete<String> deshacerCarga() {
        return descargar(); // O(1)
    }

    // ── Consultas ─────────────────────────────────────────────────────

    /** O(1) – peek sobre el tope de la pila. */
    public Paquete<String> verSuperior() { return pila.peek(); }

    /** O(1) – consulta booleana sobre el tamaño interno. */
    public boolean estaVacio()           { return pila.isEmpty(); }

    /** O(1) – ArrayDeque mantiene un contador de tamaño. */
    public int cantidadPaquetes()        { return pila.size(); }

    /** O(1) – acceso a campo primitivo. */
    public double getPesoActual()        { return pesoActual; }

    /** O(1) – acceso a campo String. */
    public String getPatente()           { return patente; }

    /**
     * Retorna un iterador sobre los paquetes (del tope al fondo).
     * Complejidad de creación: O(1).
     * Complejidad de recorrido completo: O(n), donde n = cantidad de paquetes.
     */
    public Iterator<Paquete<String>> iterator() { return pila.iterator(); }

    /**
     * Complejidad: O(1) – String.format con valores de tamaño acotado.
     */
    @Override
    public String toString() {
        return String.format("Camión [%s] | %.1f / %.1f kg | %d paquetes",
                patente, pesoActual, capacidadMaxKg, pila.size()); // O(1)
    }
}