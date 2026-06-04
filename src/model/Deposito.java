package model;

/**
 * Representa un depósito regional con su id.
 * Implementa Comparable para poder usarse en el ABB.
 */
public class Deposito implements Comparable<Deposito> {

    private final int id;

    public Deposito(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public int compareTo(Deposito otro) {
        /* Devuelve:
            negativo → this es menor que otro
            cero → son iguales (mismo id)
            positivo → this es mayor que otro
         */
        return Integer.compare(this.id, otro.id);
    }

    @Override
    public String toString() {
        return "Deposito{id=" + id + "}";
    }
}
