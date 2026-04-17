public class Paquete<T> implements Comparable<Paquete<T>> {

    public enum Prioridad { URGENTE, ESTANDAR }

    private final int id;
    private final double peso;
    private final String destino;
    private final T contenido;
    private Prioridad prioridad;

    public Paquete(int id, double peso, String destino, T contenido, Prioridad prioridad) {
        this.id = id;
        this.peso = peso;
        this.destino = destino;
        this.contenido = contenido;
        this.prioridad = prioridad;
    }

    public int getId()              { return id; }
    public double getPeso()         { return peso; }
    public String getDestino()      { return destino; }
    public T getContenido()         { return contenido; }
    public Prioridad getPrioridad() { return prioridad; }
    public void setPrioridad(Prioridad p) { this.prioridad = p; }

    public boolean esPrioritario() {
        return prioridad == Prioridad.URGENTE || peso > 50;
    }

    @Override
    public int compareTo(Paquete<T> otro) {
        int miNivel = this.nivelPrioridad();
        int otroNivel = otro.nivelPrioridad();

        if (miNivel != otroNivel) return Integer.compare(miNivel, otroNivel);
        return Integer.compare(this.id, otro.id);
    }

    // Menor número = mayor prioridad
    private int nivelPrioridad() {
        if (prioridad == Prioridad.URGENTE) return 0;
        if (peso > 50) return 1;
        return 2;
    }

    @Override
    public String toString() {
        return String.format("Paquete #%d | %.1f kg | Destino: %s | %s | %s",
                id, peso, destino, prioridad, contenido);
    }
}