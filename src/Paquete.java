/**
 * TDA genérico Paquete&lt;T&gt;.
 * T representa el tipo de contenido (puede ser String, o cualquier clase).
 */
public class Paquete<T> {

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
    public String toString() {
        return String.format("Paquete #%d | %.1f kg | Destino: %s | %s | %s",
                id, peso, destino, prioridad, contenido);
    }
}