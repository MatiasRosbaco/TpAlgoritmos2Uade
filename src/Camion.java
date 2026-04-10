import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

/**
 * Camión que carga paquetes en modo LIFO (pila).
 * deshacerCarga() retira el último paquete cargado en O(1).
 */
public class Camion {

    private final String patente;
    private final double capacidadMaxKg;
    private final Deque<Paquete<String>> pila;
    private double pesoActual;

    public Camion(String patente, double capacidadMaxKg) {
        this.patente = patente;
        this.capacidadMaxKg = capacidadMaxKg;
        this.pila = new ArrayDeque<>();
        this.pesoActual = 0;
    }

    public boolean cargar(Paquete<String> paquete) {
        if (pesoActual + paquete.getPeso() > capacidadMaxKg) {
            System.out.println("  ✗ No se puede cargar: excede capacidad del camión.");
            return false;
        }
        pila.push(paquete);
        pesoActual += paquete.getPeso();
        return true;
    }

    public Paquete<String> descargar() {
        if (pila.isEmpty()) return null;
        Paquete<String> p = pila.pop();
        pesoActual -= p.getPeso();
        return p;
    }

    /** Deshacer la última carga en O(1). */
    public Paquete<String> deshacerCarga() {
        return descargar();
    }

    public Paquete<String> verSuperior() { return pila.peek(); }
    public boolean estaVacio()           { return pila.isEmpty(); }
    public int cantidadPaquetes()        { return pila.size(); }
    public double getPesoActual()        { return pesoActual; }
    public String getPatente()           { return patente; }
    public Iterator<Paquete<String>> iterator() { return pila.iterator(); }

    @Override
    public String toString() {
        return String.format("Camión [%s] | %.1f / %.1f kg | %d paquetes",
                patente, pesoActual, capacidadMaxKg, pila.size());
    }
}