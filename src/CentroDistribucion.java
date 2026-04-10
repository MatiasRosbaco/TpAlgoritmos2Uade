import java.util.PriorityQueue;
import java.util.Comparator;

/**
 * Centro de distribución con cola de prioridad.
 * Paquetes URGENTE o peso > 50 kg se procesan primero.
 */
public class CentroDistribucion {

    private final PriorityQueue<Paquete<String>> cola;

    public CentroDistribucion() {
        this.cola = new PriorityQueue<>(new Comparator<Paquete<String>>() {
            @Override
            public int compare(Paquete<String> a, Paquete<String> b) {
                // Prioritarios van primero
                boolean aPrio = a.esPrioritario();
                boolean bPrio = b.esPrioritario();

                if (aPrio && !bPrio) return -1;   // a va primero
                if (!aPrio && bPrio) return 1;     // b va primero

                // Mismo nivel de prioridad → desempate por ID (menor ID primero)
                return Integer.compare(a.getId(), b.getId());
            }
        });
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
        // Copia con el MISMO comparador para mantener el orden
        PriorityQueue<Paquete<String>> copia = new PriorityQueue<>(cola);
        int pos = 1;
        while (!copia.isEmpty()) {
            System.out.printf("  %d. %s%n", pos++, copia.poll());
        }
    }
}