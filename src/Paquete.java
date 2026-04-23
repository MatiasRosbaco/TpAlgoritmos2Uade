/**
 * Clase genérica que representa un paquete en el sistema de distribución.
 *
 * ┌────────────────────────────────────────────────────────────────────┐
 * │  RESUMEN DE COMPLEJIDAD – Paquete<T>                             │
 * ├──────────────────────┬─────────────────────────────────────────────┤
 * │ Todos los métodos    │ O(1) – tiempo constante                   │
 * │                      │ Solo acceden/comparan campos primitivos    │
 * └──────────────────────┴─────────────────────────────────────────────┘
 */
public class Paquete<T> implements Comparable<Paquete<T>> {

    public enum Prioridad { URGENTE, ESTANDAR }

    private final int id;
    private final double peso;
    private final String destino;
    private final T contenido;
    private Prioridad prioridad;

    /**
     * Constructor.
     * Complejidad: O(1) – asigna referencias/valores primitivos.
     */
    public Paquete(int id, double peso, String destino, T contenido, Prioridad prioridad) {
        this.id = id;
        this.peso = peso;
        this.destino = destino;
        this.contenido = contenido;
        this.prioridad = prioridad;
    }

    // ── Getters y setter ──────────────────────────────────────────────
    // Complejidad: O(1) cada uno – acceso directo a un campo.

    public int getId()              { return id; }        // O(1)
    public double getPeso()         { return peso; }      // O(1)
    public String getDestino()      { return destino; }   // O(1)
    public T getContenido()         { return contenido; } // O(1)
    public Prioridad getPrioridad() { return prioridad; } // O(1)
    public void setPrioridad(Prioridad p) { this.prioridad = p; } // O(1)

    /**
     * Determina si el paquete es prioritario.
     * Complejidad: O(1) – dos comparaciones sobre campos primitivos/enum.
     *
     * Criterio: es prioritario si la prioridad es URGENTE **o** el peso supera 50 kg.
     */
    public boolean esPrioritario() {
        return prioridad == Prioridad.URGENTE || peso > 50; // O(1)
    }

    /**
     * Comparación natural entre paquetes (usada si se ordena una colección).
     * Complejidad: O(1) – calcula nivelPrioridad() en O(1) y compara enteros.
     *
     * Orden: primero por nivel de prioridad (menor = más urgente),
     *        luego por id (menor = más antiguo → se atiende primero).
     */
    @Override
    public int compareTo(Paquete<T> otro) {
        int miNivel = this.nivelPrioridad();       // O(1)
        int otroNivel = otro.nivelPrioridad();     // O(1)

        if (miNivel != otroNivel)
            return Integer.compare(miNivel, otroNivel); // O(1)
        return Integer.compare(this.id, otro.id);       // O(1)
    }

    /**
     * Asigna un valor numérico a la prioridad para facilitar la comparación.
     * Complejidad: O(1) – máximo dos comparaciones.
     *
     * Mapeo:  URGENTE → 0,  peso > 50 → 1,  ESTANDAR → 2
     */
    private int nivelPrioridad() {
        if (prioridad == Prioridad.URGENTE) return 0; // O(1)
        if (peso > 50) return 1;                      // O(1)
        return 2;                                     // O(1)
    }

    /**
     * Representación legible del paquete.
     * Complejidad: O(1) – String.format con campos de tamaño acotado.
     * (Técnicamente O(k) donde k es la longitud de las cadenas, pero se
     *  considera O(1) porque los campos tienen longitud prácticamente fija.)
     */
    @Override
    public String toString() {
        return String.format("Paquete #%d | %.1f kg | Destino: %s | %s | %s",
                id, peso, destino, prioridad, contenido); // O(1)
    }
}