import java.util.LinkedList;

/**
 * Centro de distribución usando lista doblemente enlazada (LinkedList).
 * - Paquetes prioritarios (URGENTE o peso > 50kg) se insertan al principio.
 * - Paquetes estándar se insertan al final.
 * - Se procesa siempre desde el principio.
 */
public class CentroDistribucion {

    private final LinkedList<Paquete<String>> lista;

    public CentroDistribucion() {
        this.lista = new LinkedList<>();
    }

    public void recibir(Paquete<String> paquete) {
        if (paquete.esPrioritario()) {
            lista.addFirst(paquete);
        } else {
            lista.addLast(paquete);
        }
    }

    public Paquete<String> procesarSiguiente() {
        if (lista.isEmpty()) return null;
        return lista.removeFirst();
    }

    public Paquete<String> verSiguiente() {
        return lista.peekFirst();
    }

    public boolean hayPaquetes()      { return !lista.isEmpty(); }
    public int cantidadPendientes()   { return lista.size(); }

    public void mostrarCola() {
        if (lista.isEmpty()) {
            System.out.println("  (Centro vacío)");
            return;
        }
        int pos = 1;
        for (Paquete<String> p : lista) {
            System.out.printf("  %d. %s%n", pos++, p);
        }
    }
}