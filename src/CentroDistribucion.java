import java.util.PriorityQueue;

/**
 * Centro de distribución con cola de prioridad.
 * La PriorityQueue usa el compareTo() de Paquete automáticamente.
 */
public class CentroDistribucion {

    private final PriorityQueue<Paquete<String>> cola;

    public CentroDistribucion() {
        this.cola = new PriorityQueue<>();
    }

    public void recibir(Paquete<String> paquete) { cola.offer(paquete); }
    public Paquete<String> procesarSiguiente()   { return cola.poll(); }
    public Paquete<String> verSiguiente()        { return cola.peek(); }
    public boolean hayPaquetes()                 { return !cola.isEmpty(); }
    public int cantidadPendientes()              { return cola.size(); }

    public void mostrarCola() {
        if (cola.isEmpty()) {
            System.out.println("  (Centro vacío)");
            return;
        }
        PriorityQueue<Paquete<String>> copia = new PriorityQueue<>(cola);
        int pos = 1;
        while (!copia.isEmpty()) {
            System.out.printf("  %d. %s%n", pos++, copia.poll());
        }
    }
}